/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.kaumei.jdbc.anno.Processor.OPTION_KEY_CONFIG;
import static io.kaumei.jdbc.anno.Processor.OPTION_KEY_DEBUG_FOLDER;
import static io.kaumei.jdbc.anno.annotool.Anno.*;
import static java.util.Objects.requireNonNull;

public class ConfigService implements ProcessorSteps {

    // ----- services
    private final JavaAnnoMessenger logger;
    private final JavaAnnoElements elements;

    // ----- state
    private final @Nullable String config;
    private final @Nullable String debugFolder;

    private final Map<WithConfigValue<?, ?>, Object> anno2value = new HashMap<>();
    private final Set<Element> jdbcToJava = new HashSet<>();
    private final Set<Element> javaToJdbc = new HashSet<>();

    // ------------------------------------------------------------------------

    public ConfigService(JavaAnnoMessenger logger, JavaAnnoElements elements, Map<String, String> options) {
        this.logger = logger;
        this.elements = elements;
        this.config = options.get(OPTION_KEY_CONFIG);
        this.debugFolder = options.get(OPTION_KEY_DEBUG_FOLDER);
    }

    // ------------------------------------------------------------------------

    @Override
    public void process(ProcessorEnvironment roundEnv) {
        this.logger.info("Process Kaumei JDBC processor configuration.");

        var configByAnno = configByAnno(roundEnv);
        var configByOptions = configByOption();
        if(configByAnno != null && configByOptions != null && !configByAnno.equals(configByOptions)) {
            var msg = "Given config by annotation and by processor option are not equal.";
            this.logger.error(configByAnno, msg);
            this.logger.error(configByOptions, msg);
        }
        loadConfig(configByAnno != null ? configByAnno : configByOptions);
        this.logger.allways("anno2value: ", anno2value);
        this.logger.allways("jdbcToJava: ", jdbcToJava);
        this.logger.allways("javaToJdbc: ", javaToJdbc);
    }

    private void loadConfig(@Nullable TypeElement jdbcConfig) {
        // ----- put the default values
        anno2value.put(JDBC_BATCH_SIZE, 1000);
        anno2value.put(JDBC_NO_MORE_ROWS, JdbcNoMoreRows.Kind.THROW_EXCEPTION);
        anno2value.put(JDBC_NO_ROWS, JdbcNoRows.Kind.THROW_EXCEPTION);
        anno2value.put(JDBC_RETURN_GENERATED_VALUES, JdbcReturnGeneratedValues.Kind.GENERATED_KEYS);

        if(jdbcConfig == null) {
            this.logger.debug("No config found.");
            return;
        }
        processConfig(jdbcConfig);
    }

    private void processConfig(TypeElement configType) {
        var anno = new KaumeiAnno(configType);
        var cfg = anno.useAnnotation(JDBC_CONFIG_PROPS);
        if(cfg.parent() != null) {
            processConfig(cfg.parent());
        }
        this.logger.debug("Process config:", configType);

        // ----- by default we search for converter in the config type
        processConverter(configType);
        // ----- next we check all other referenced types
        for (var converterType : cfg.converter()) {
            processConverter(converterType);
        }

        processAnno(anno, JDBC_BATCH_SIZE);
        processAnno(anno, JDBC_FETCH_DIRECTION);
        processAnno(anno, JDBC_FETCH_SIZE);
        processAnno(anno, JDBC_MAX_ROWS);
        processAnno(anno, JDBC_NO_MORE_ROWS);
        processAnno(anno, JDBC_NO_ROWS);
        processAnno(anno, JDBC_QUERY_TIMEOUT);
        processAnno(anno, JDBC_RESULT_SET_CONCURRENCY);
        processAnno(anno, JDBC_RESULT_SET_TYPE);
        processAnno(anno, JDBC_RETURN_GENERATED_VALUES);
    }


    private void processConverter(Element element) {
        if(element instanceof ExecutableElement) {
            // ok
        } else if(element instanceof TypeElement) {
            for (var child : element.getEnclosedElements()) {
                processConverter(child);
            }
        } else {
            logger.warn(element, "Unsupported element type for annotations. Annotation is ignored.", "kind", element.getKind());
        }

        var hasJdbcToJava = Anno.JDBC_TO_JAVA.hasAnno(element);
        var hasJavaToJdbc = Anno.JAVA_TO_JDBC.hasAnno(element);
        if(hasJdbcToJava && hasJavaToJdbc) {
            this.logger.error(element, "Could not have both annotations @JdbcToJava and @JavaToJdbc present at an element.");
        } else if(hasJdbcToJava) {
            jdbcToJava.add(element);
        } else if(hasJavaToJdbc) {
            javaToJdbc.add(element);
        }
    }

    private <A extends Annotation, T> void processAnno(KaumeiAnno anno, WithConfigValue<A, T> key) {
        var value = anno.useAnnotationOrUnset(key);
        if(!key.isUnset(value)) {
            anno2value.put(key, value);
        }
    }

    /**
     * Search for a @JdbcConfig annotated class. there must be none or exactly one.
     * @return found type or null
     */
    private @Nullable TypeElement configByAnno(ProcessorEnvironment roundEnv) {
        if(roundEnv.jdbcConfig().size() == 1) {
            var jdbcConfig = roundEnv.jdbcConfig().iterator().next();
            if(jdbcConfig instanceof TypeElement te) {
                return te;
            }
            this.logger.error(jdbcConfig, "Incompatible type for configuration.");
        } else if(roundEnv.jdbcConfig().size() > 1) {
            this.logger.error("To many configurations found.", "configs", roundEnv.jdbcConfig());
        }
        return null;
    }

    /**
     * Search for a @JdbcConfig defined class by processor options.
     * @return found type or null
     */
    private @Nullable TypeElement configByOption() {
        if(config != null) {
            var jdbcConfig = this.elements.getTypeElement(config);
            if(jdbcConfig == null) {
                this.logger.warn("Given config could not be found.", "config", config);
            } else if(!Anno.JDBC_CONFIG_PROPS.hasAnno(jdbcConfig)) {
                this.logger.warn(jdbcConfig, "Given config is missing @JdbcConfig annotation.");
            } else {
                return jdbcConfig;
            }
        }
        return null;

    }

    // ------------------------------------------------------------------------

    public boolean dump() {
        return debugFolder != null;
    }

    public Path dumpFolder() {
        return Path.of(requireNonNull(debugFolder));
    }

    <A extends Annotation, T> T getConfigValue(WithConfigValue<A, T> anno) {
        var value = anno2value.get(anno);
        if(value != null) {
            return anno.convertValue(value);
        }
        return anno.unsetValue();
    }

    public Set<Element> jdbcToJava() {
        return jdbcToJava;
    }

    public Set<Element> javaToJdbc() {
        return javaToJdbc;
    }

    // ------------------------------------------------------------------------

    public JdbcReturnGeneratedValues.Kind jdbcReturnGeneratedValues(KaumeiAnno methodAnno, Element parent) {
        // this search is different
        var value = methodAnno.useAnnotationOrUnset(JDBC_RETURN_GENERATED_VALUES);
        if(!JDBC_RETURN_GENERATED_VALUES.isUnset(value)) {
            return value;
        }
        value = new KaumeiAnno(parent).annotationOrUnset(JDBC_RETURN_GENERATED_VALUES);
        if(!JDBC_RETURN_GENERATED_VALUES.isUnset(value)) {
            return value;
        }
        return getConfigValue(JDBC_RETURN_GENERATED_VALUES);
    }

    // ------------------------------------------------------------------------

    public <A extends Annotation, T> T searchAnno(WithConfigValue<A, T> anno, KaumeiAnno methodAnno, Element parent) {
        T value = methodAnno.annotationOrUnset(anno);
        if(!anno.isUnset(value)) {
            return value;
        }
        value = new KaumeiAnno(parent).annotationOrUnset(anno);
        if(!anno.isUnset(value)) {
            return value;
        }
        return getConfigValue(anno);
    }

    // ------------------------------------------------------------------------

}
