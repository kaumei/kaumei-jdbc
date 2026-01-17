/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import io.kaumei.jdbc.spec.NoJdbcType;
import org.jspecify.annotations.Nullable;

public interface ColumnTypeNameSpec {

    // @formatter:off
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") boolean typePrimitiveBoolean(Boolean value);
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") Boolean typeBoolean(Boolean value);
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") char typePrimitiveChar(Character value);
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") Character typeCharacter(Character value);
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") String typeString(String value);
    @JdbcSelect("SELECT :value as value01") @JdbcName("value01") NoJdbcType typeNoJdbcType();
    // @formatter:off

    // ------------------------------------------------------------------------

    @JdbcToJava("ColumnTypeNameSpec.invalidReturnTypeIncompatible")
    static long invalidReturnTypeIncompatibleFromDB(String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT 1")
    @JdbcConverterName("ColumnTypeNameSpec.invalidReturnTypeIncompatible")
    int invalidReturnTypeIncompatible();

    // ------------------------------------------------------------------------

    @JdbcToJava("ColumnTypeNameSpec.compatibleReturnTypePrimitive")
    static int compatibleReturnTypePrimitiveFromDB(String value) {
        return unique(Integer.parseInt(value));
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnTypeNameSpec.compatibleReturnTypePrimitive")
    long compatibleReturnTypePrimitive(String value);

    // ------------------------------------------------------------------------

    @JdbcToJava("ColumnTypeNameSpec.compatibleReturnTypeObject")
    static String compatibleReturnTypeObjectFromDB(Integer value) {
        return unique(Integer.toString(value),"compatibleReturnTypeObject");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnTypeNameSpec.compatibleReturnTypeObject")
    CharSequence compatibleReturnTypeObject(Integer value);

    // util methods ###########################################################

    static int unique(int value) {
        return value + ColumnTypeNameSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_ColumnTypeNameSpec_" + context;
    }
}
