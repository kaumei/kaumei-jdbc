/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.ClassHierarchy;
import io.kaumei.jdbc.spec.NoJdbcType;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

@JavaToJdbc
public interface ConverterSimpleSpec {

    // ------------------------------------------------------------------------
    static String validStringToDB(WithString01 value) {
        return value == null ? null : "validToDB:" + value.value();
    }

    @JdbcSelect("SELECT :value")
    String validString(WithString01 value);

    static long validLongToDB(WithLong01 value) {
        return value.value();
    }

    @JdbcSelect("SELECT :value")
    Long validLong(WithLong01 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static @Nullable String returnValueNullableToDB(Class01 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String returnValueNullable(Class01 value);

    // ------------------------------------------------------------------------
    static @NonNull String returnValueNonNullToDB(WithString03 value) {
        if (value == null) {
            return null;
        } else if (value.value() == null) {
            return null;
        }
        return "returnValueNonNullToDB:" + value.value();
    }

    @JdbcSelect("SELECT :value")
    String returnValueNonNull(@Nullable WithString03 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void returnValueVoidToDB(Class02 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String returnValueVoid(Class02 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static NoJdbcType returnValueNoJdbcTypeDB(Class03 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    int returnValueNoJdbcType(Class03 value);

    // ------------------------------------------------------------------------
    static String duplicate01ToDB(Class04 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    static String duplicate02ToDB(Class04 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String duplicate(Class04 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static String toManyParametersToDB(Class05 value1, String value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String toManyParameters(Class05 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static String invalidExceptionToDB(Class06 value1) throws IOException {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String invalidException(Class06 value);

    // ------------------------------------------------------------------------

    static String level01ToTb(ClassHierarchy.Level01 value) {
        return "level01:" + value.value();
    }

    @JdbcSelect("SELECT :value")
    String level01(ClassHierarchy.Level01Cls value);

    @JdbcSelect("SELECT :value")
    String level03(ClassHierarchy.Level03Cls value);

    // ------------------------------------------------------------------------
    @JavaToJdbc("ConverterSimpleSpec.incompatibleType")
    static String incompatibleTypeToDB(long value1) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String incompatibleType(@JdbcConverterName("ConverterSimpleSpec.incompatibleType") String value);

    // ------------------------------------------------------------------------

    class Cycle01 {
        Cycle01(Cycle02 value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    class Cycle02 {
        Cycle02(Cycle01 value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    static Cycle02 cycle01ToDB(Cycle01 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    static Cycle01 cycle02ToDB(Cycle02 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String invalidCycle(Cycle01 value);

    // ------------------------------------------------------------------------

}
