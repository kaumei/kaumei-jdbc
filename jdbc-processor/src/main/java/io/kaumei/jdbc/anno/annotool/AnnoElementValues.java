/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.annotool;

import io.kaumei.jdbc.anno.ProcessorException;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.kaumei.jdbc.anno.JavaAnnoTypes.EMPTY;

class AnnoElementValues {
    private final Map<String, AnnotationValue> values = new HashMap<>();

    AnnoElementValues(AnnotationMirror annoMirror) {
        for (var entry : annoMirror.getElementValues().entrySet()) {
            var annoName = entry.getKey().getSimpleName().toString();
            values.put(annoName, entry.getValue());
        }
    }

    @Nullable String getString(String key) {
        var annoValue = values.get(key);
        // sanity-check:on
        if (annoValue == null) {
            return null;
        }
        // sanity-check:off
        var value = annoValue.getValue();
        if (value instanceof String s) {
            return s;
        }
        throw new ProcessorException("Invalid type for: " + key + ":" + ((value == null) ? "null" : value.getClass().getCanonicalName())); // sanity-check
    }

    @Nullable TypeElement getTypeElement(String key) {
        var annoValue = values.get(key);
        // sanity-check:on
        if (annoValue == null) {
            return null;
        }
        // sanity-check:off
        var value = annoValue.getValue();
        if (value instanceof DeclaredType dt && dt.asElement() instanceof TypeElement elem) {
            return elem;
        }
        throw new ProcessorException("Invalid type for: " + key + ":" + ((value == null) ? "null" : value.getClass().getCanonicalName())); // sanity-check
    }

    TypeElement[] getTypeElements(String key) {
        var annoValue = values.get(key);
        if (annoValue == null) {
            return EMPTY;
        }
        var raw = annoValue.getValue();
        // sanity-check:on
        if (!(raw instanceof List<?> list)) {
            throw new ProcessorException("Invalid type for: " + key + ": expected list, got " + (raw == null ? "null" : raw.getClass().getCanonicalName()));
        }
        // sanity-check:off

        var result = new TypeElement[list.size()];
        int i = 0;

        for (var entry : list) {
            // sanity-check:on
            if (!(entry instanceof AnnotationValue av)) {
                throw new ProcessorException("Invalid annotation value entry: " + entry);
            }
            // sanity-check:off

            var v = av.getValue();
            // sanity-check:on
            if (!(v instanceof DeclaredType dt)) {
                throw new ProcessorException("Invalid element type for: " + key + ": " + (v == null ? "null" : v.getClass().getCanonicalName()));
            }
            // sanity-check:off

            var element = dt.asElement();
            // sanity-check:on
            if (!(element instanceof TypeElement te)) {
                throw new ProcessorException("DeclaredType does not resolve to TypeElement: " + element);
            }
            // sanity-check:off

            result[i++] = te;
        }

        return result;
    }

}