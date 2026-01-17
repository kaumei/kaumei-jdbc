/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import io.kaumei.jdbc.annotation.JdbcNative;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.config.JdbcConfig;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

public class ProcessorEnvironment {

    // ----- services
    private final JavaAnnoMessenger logger;

    private final Set<? extends Element> jdbcConfig;
    private final ConfigService jdbcConfigService;
    private final Set<TypeElement> jdbcInterfaces;

    ProcessorEnvironment(JavaAnnoMessenger logger, ConfigService jdbcConfigService, Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.logger = logger;
        this.jdbcConfigService = jdbcConfigService;
        var rootElements = roundEnv.getRootElements();
        this.jdbcConfig = roundEnv.getElementsAnnotatedWith(JdbcConfig.class);
        this.jdbcInterfaces = new HashSet<>();
        var jdbcNative = updateJdbcInterfaces(roundEnv.getElementsAnnotatedWith(JdbcNative.class));
        var jdbcSelect = updateJdbcInterfaces(roundEnv.getElementsAnnotatedWith(JdbcSelect.class));
        var jdbcUpdate = updateJdbcInterfaces(roundEnv.getElementsAnnotatedWith(JdbcUpdate.class));

        if (logger.isDebugEnabled()) {
            logger.debug("annotations...", annotations);
            logger.debug("rootElements..", rootElements);
            logger.debug("jdbcNative....", jdbcNative);
            logger.debug("jdbcSelect....", jdbcSelect);
            logger.debug("jdbcUpdate....", jdbcUpdate);
            logger.debug("jdbcInterfaces", jdbcInterfaces);
        } else {
            logger.allways("Kaumei JDBC input annotation types.", annotations);
            logger.allways("Kaumei JDBC found annotations.", "rootElements:", rootElements.size(),
                    "jdbcNative", jdbcNative.size(),
                    "jdbcSelect", jdbcSelect.size(),
                    "jdbcUpdate", jdbcUpdate.size(),
                    "jdbcInterfaces", jdbcInterfaces.size());
        }
    }

    private Set<? extends Element> updateJdbcInterfaces(Set<? extends Element> elements) {
        for (Element elem : elements) {
            if (elem.getKind() == ElementKind.METHOD) {
                var enclosing = elem.getEnclosingElement();
                if (enclosing != null
                        && enclosing.getKind() == ElementKind.INTERFACE
                        && enclosing instanceof TypeElement iface) {
                    jdbcInterfaces.add(iface);
                    continue;
                }
                this.logger.warn(elem, "Ignored");
            }
        }
        return elements;
    }

    public Set<? extends Element> jdbcConfig() {
        return this.jdbcConfig;
    }

    public Set<? extends Element> jdbcToJava() {
        return jdbcConfigService.jdbcToJava();
    }

    public Set<? extends Element> javaToJdbc() {
        return jdbcConfigService.javaToJdbc();
    }

    public Set<TypeElement> jdbcInterfaces() {
        return this.jdbcInterfaces;
    }

}
