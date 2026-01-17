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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Order of methods:
 * * static factory methods
 * * record constructors
 * * class constructors
 * * util methods
 */
public interface RowFromObjectsSpec {
    // static factory methods #################################################

    record StringInt(String value1, int value2) {
        @JdbcToJava
        static StringInt fromDB(String value1, int value2) {
            return new StringInt(unique(value1, "stringInt"), unique(value2));
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    StringInt stringInt(String value1, Integer value2);

    // ------------------------------------------------------------------------
    record StringIntNullable(String value1, int value2) {
        @JdbcToJava
        static StringIntNullable fromDB(@Nullable String value1, int value2) {
            return new StringIntNullable(unique(value1, "stringIntNullable"), unique(value2));
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    StringIntNullable stringIntNullable(String value1, Integer value2);

    // ------------------------------------------------------------------------
    record StringIntNonnull(String value1, int value2) {
        @JdbcToJava
        static StringIntNonnull fromDB(@NonNull String value1, int value2) {
            return new StringIntNonnull(unique(value1, "stringIntNonnull"), unique(value2));
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    StringIntNonnull stringIntNonnull(String value1, Integer value2);

    // ------------------------------------------------------------------------
    record WithNames(String value1, int value2) {
        @JdbcToJava
        static WithNames fromDB(@JdbcName("value1") String s, @JdbcName("value2") int i) {
            return new WithNames(unique(s, "withNames"), unique(i));
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    WithNames withNames(String value1, Integer value2);

    // ------------------------------------------------------------------------

    @JdbcToJava("RowFromObjectsSpec.invalidReturnTypeVoid")
    static void invalidReturnTypeVoidFromDB(String value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromObjectsSpec.invalidReturnTypeVoid")
    String invalidReturnTypeVoid();

    // ------------------------------------------------------------------------

    @JdbcToJava("RowFromObjectsSpec.invalidReturnTypeNullable")
    static @Nullable String invalidReturnTypeNullableFromDB(String value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromObjectsSpec.invalidReturnTypeNullable")
    String invalidReturnTypeNullable();

    // ------------------------------------------------------------------------

    @JdbcToJava("RowFromObjectsSpec.invalidParamOptional")
    static String invalidParamOptionalFromDB(Optional<Integer> value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromObjectsSpec.invalidParamOptional")
    String invalidParamOptional();

    // ------------------------------------------------------------------------
    @JdbcToJava("RowFromObjectsSpec.invalidParamNoJdbcType")
    static String invalidParamNoJdbcTypeFromDB(NoJdbcType value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromObjectsSpec.invalidParamNoJdbcType")
    String invalidParamNoJdbcType();

    // ------------------------------------------------------------------------
    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotStatic")
    String notStatic();

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotVisible")
    String notVisible();

    // record constructors ####################################################
    @JdbcToJava
    record RecordConverterAnnotation(String value1, int value2) {
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    RecordConverterAnnotation recordConverterAnnotation(String value1, Integer value2);

    // class constructors #####################################################

    class ClassStringInt {
        String value1;
        int value2;

        ClassStringInt() {
        }

        ClassStringInt(String value1, int value2, int value3) {
            this.value1 = unique(value1, "classStringInt");
            this.value2 = unique(value2);
        }

        @JdbcToJava
        ClassStringInt(String value1, int value2) {
            this.value1 = unique(value1, "classStringInt");
            this.value2 = unique(value2);
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    ClassStringInt classStringInt(String value1, Integer value2);

    // util methods ###########################################################

    static int unique(int value) {
        return value + RowFromResultSetSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_RowFromObjectsSpec_" + context;
    }

}
