/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.annotool;

import io.kaumei.jdbc.anno.ProcessorException;
import org.jspecify.annotations.Nullable;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static io.kaumei.jdbc.anno.annotool.Anno.*;

public class KaumeiAnno {

    private final AnnotatedConstruct element;
    private final Map<Anno<?>, AnnotationMirror> anno2value = new HashMap<>();

    public KaumeiAnno(AnnotatedConstruct element) {
        this.element = element;
        for (var annoMirror : element.getAnnotationMirrors()) {
            var name = annoMirror.getAnnotationType().toString();
            var anno = NAME_TO_ANNO.get(name);
            if (anno != null) {
                // sanity-check:on
                if (anno2value.put(anno, annoMirror) != null) {
                    throw new ProcessorException("Duplicate annotation: " + name);
                }
                // sanity-check:off
            }
        }
    }

    @Override
    public String toString() {
        return anno2value.toString();
    }

    // --------------------------------------------------------------------------------------------

    public boolean hasUnused() {
        return !anno2value.isEmpty();
    }

    public Set<String> unused() {
        var a = new TreeSet<String>();
        for (var v : anno2value.values()) {
            a.add(v.getAnnotationType().toString());
        }
        return a;
    }

    // ------------------------------------------------------------------------

    public <A extends Annotation> boolean hasAnnotation(Anno<A> anno) {
        return anno2value.containsKey(anno);
    }

    // ------------------------------------------------------------------------

    public <A extends Annotation> boolean useAnnotation(Anno<A> anno) {
        return anno2value.remove(anno) != null;
    }

    public <A extends Annotation, T> T useAnnotation(Anno.WithValue<A, T> anno) {
        var value = anno2value.remove(anno);
        // sanity-check:on
        if (value == null) {
            throw new ProcessorException("Annotation not found: " + anno);
        }
        // sanity-check:off
        return anno.value(element, value);
    }

    public <A extends Annotation, T> @Nullable T useAnnotationOrNull(Anno.WithValue<A, T> anno) {
        var value = anno2value.remove(anno);
        return value == null ? null : anno.value(element, value);
    }

    public <A extends Annotation, T> boolean useAnnotationWithoutValue(Anno.WithValue<A, T> anno) {
        var mirror = anno2value.get(anno);
        if (mirror != null) {
            var value = anno.value(element, mirror);
            if (anno.isUnset(value)) {
                anno2value.remove(anno);
                return true;
            }
        }
        return false;
    }

    public <A extends Annotation, T> T useAnnotationOrUnset(Anno.WithValue<A, T> anno) {
        var value = anno2value.remove(anno);
        if (value == null) {
            return anno.unsetValue();
        }
        return anno.value(element, value);
    }

    // ------------------------------------------------------------------------

    public <A extends Annotation, T> T annotationOrUnset(Anno.WithValue<A, T> anno) {
        var mirror = anno2value.get(anno);
        if (mirror != null) {
            var value = anno.value(element, mirror);
            if (!anno.isUnset(value)) {
                anno2value.remove(anno);
                return value;
            }
        }
        return anno.unsetValue();
    }

    // --------------------------------------------------------------------------------------------

    public boolean hasJdbcUpdateBatch() {
        return hasAnnotation(JDBC_UPDATE_BATCH);
    }

    // ------------------------------------------------------------------------

    public String jdbcConverterName() {
        return annotationOrUnset(JDBC_CONVERTER_NAME);
    }

    // ------------------------------------------------------------------------

    public String jdbcName() {
        return annotationOrUnset(JDBC_NAME);
    }

    // ------------------------------------------------------------------------

    public boolean hasJdbcNative() {
        return hasAnnotation(JDBC_NATIVE);
    }

    public NativeProps jdbcNative() {
        return useAnnotation(JDBC_NATIVE);
    }

    // ------------------------------------------------------------------------

    public boolean hasJdbcSelect() {
        return hasAnnotation(JDBC_SELECT);
    }

    public String jdbcSelect() {
        return useAnnotation(JDBC_SELECT);
    }

    // ------------------------------------------------------------------------

    public boolean hasJdbcUpdate() {
        return hasAnnotation(JDBC_UPDATE);
    }

    public String jdbcUpdate() {
        return useAnnotation(JDBC_UPDATE);
    }

    // ------------------------------------------------------------------------

    static final Map<String, Anno<?>> NAME_TO_ANNO = new HashMap<>();

    private static void add(Anno<?> anno) {
        var key = anno.cls().getCanonicalName();
        // sanity-check:on
        if (NAME_TO_ANNO.put(key, anno) != null) {
            throw new ProcessorException("Duplicate key: " + key);
        }
        // sanity-check:off
    }

    static {
        add(JAVA_TO_JDBC);
        add(JDBC_CONVERTER_NAME);
        //add(JDBC_DEBUG);
        add(JDBC_NAME);
        add(JDBC_NATIVE);
        add(JDBC_SELECT);
        add(JDBC_TO_JAVA);
        add(JDBC_UPDATE);
        add(JDBC_UPDATE_BATCH);
        //
        add(JDBC_BATCH_SIZE);
        add(JDBC_CONFIG_PROPS);
        add(JDBC_FETCH_DIRECTION);
        add(JDBC_FETCH_SIZE);
        add(JDBC_MAX_ROWS);
        add(JDBC_NO_MORE_ROWS);
        add(JDBC_NO_ROWS);
        add(JDBC_QUERY_TIMEOUT);
        add(JDBC_RESULT_SET_CONCURRENCY);
        add(JDBC_RESULT_SET_TYPE);
        add(JDBC_RETURN_GENERATED_VALUES);
    }

}
