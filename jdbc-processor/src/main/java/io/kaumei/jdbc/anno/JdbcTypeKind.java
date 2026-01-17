/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

public enum JdbcTypeKind {
    VOID,
    PRIMITIVE,
    OBJECT,
    OPTIONAL_TYPE,
    ARRAY,
    LIST,
    STREAM,
    KAUMEI_JDBC_ITERABLE,
    KAUMEI_JDBC_RESULT_SET,
    KAUMEI_JDBC_BATCH;

    // ------------------------------------------------------------------------

    public boolean isVoid() {
        return this == VOID;
    }

    public boolean isPrimitive() {
        return this == PRIMITIVE;
    }

    public boolean isObject() {
        return this == OBJECT;
    }

    public boolean isOptionalType() {
        return this == OPTIONAL_TYPE;
    }

    public boolean isArray() {
        return this == ARRAY;
    }

    public boolean isList() {
        return this == LIST;
    }

    public boolean isKaumeiJdbcBatch() {
        return this == KAUMEI_JDBC_BATCH;
    }

}
