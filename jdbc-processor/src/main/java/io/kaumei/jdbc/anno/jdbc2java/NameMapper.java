/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import io.kaumei.jdbc.anno.annotool.Anno;

import javax.lang.model.element.VariableElement;

public class NameMapper {

    public static String mapName(VariableElement elem) {
        var name = Anno.JDBC_NAME.valueOrUnset(elem);
        if (name.isEmpty()) {
            return mapName(elem.getSimpleName());
        }
        return name;
    }

    public static String mapName(CharSequence javaName) {
        if (javaName.length() <= 1) {
            return javaName.toString();
        }

        var sb = new StringBuilder();
        var last = javaName.charAt(0);
        sb.append(last);
        var lastCase = Case.of(last);
        var length = javaName.length();
        for (int i = 1; i < length; i++) {
            var current = javaName.charAt(i);
            var currentCase = Case.of(current);
            if (lastCase == Case.LOWER_CASE && currentCase == Case.UPPER_CASE) {
                sb.append('_');
            }
            sb.append(current);
            lastCase = currentCase;
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------

    private enum Case {
        UNKNOWN, LOWER_CASE, UPPER_CASE;

        static Case of(char c) {
            if (Character.isLowerCase(c)) {
                return LOWER_CASE;
            } else if (Character.isUpperCase(c)) {
                return UPPER_CASE;
            }
            return UNKNOWN;
        }
    }
}
