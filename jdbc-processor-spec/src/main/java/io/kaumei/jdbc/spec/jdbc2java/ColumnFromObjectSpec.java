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
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public interface ColumnFromObjectSpec {

    // ------------------------------------------------------------------------
    @JdbcToJava
    static RecordInt recordIntFromDB(int value) {
        return new RecordInt(unique(value));
    }

    @JdbcSelect("SELECT :value")
    RecordInt recordInt(Integer value);

    // ------------------------------------------------------------------------
    class ClassWithInt {
        int value;

        @JdbcToJava
        ClassWithInt(int value) {
            this.value = unique(value);
        }
    }

    @JdbcSelect("SELECT :value")
    ClassWithInt classInt(Integer value);

    // ------------------------------------------------------------------------
    @JdbcToJava
    static RecordString recordStringFromDB(String value) {
        if (value == null) throw new AssertionError();
        return new RecordString(unique(value, "recordString"));
    }

    @JdbcSelect("SELECT :value")
    RecordString recordString(String value);

    @JdbcSelect("SELECT :value as value1")
    @JdbcName("value1")
    RecordString recordStringWithName(String value);


    @JdbcSelect("SELECT :value")
    @NonNull RecordString recordStringNonNull(String value);

    @JdbcSelect("SELECT :value as value1")
    @JdbcName("value1")
    @NonNull RecordString recordStringNonNullWithName(String value);

    // ------------------------------------------------------------------------
    class ClassWithString {
        String value;

        @JdbcToJava
        ClassWithString(String value) {
            this.value = unique(value, "classString");
        }

    }

    @JdbcSelect("SELECT :value")
    ClassWithString classString(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava
    static WithString02 withSqlExceptionFromDB(String value) throws SQLException {
        if (value == null) throw new AssertionError();
        if (value.equals("SQLException")) throw new SQLException();
        return new WithString02(unique(value, "withSqlException"));
    }

    @JdbcSelect("SELECT :value")
    WithString02 withSqlException(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava
    static WithString03 withRuntimeExceptionFromDB(String value) throws RuntimeException {
        if (value == null) throw new AssertionError();
        if (value.equals("RuntimeException")) throw new RuntimeException();
        return new WithString03(unique(value, "withRuntimeException"));
    }

    @JdbcSelect("SELECT :value")
    WithString03 withRuntimeException(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.withFileNotFoundException")
    static WithString04 withFileNotFoundExceptionFromDB(String value) throws FileNotFoundException {
        if (value == null) throw new AssertionError();
        if (value.equals("FileNotFoundException")) throw new FileNotFoundException();
        return new WithString04(unique(value, "withFileNotFoundException"));
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.withFileNotFoundException")
    WithString04 withFileNotFoundException(String value) throws IOException;

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.withFileNotFoundException")
    WithString04 withIncompatibleException(String value) throws URISyntaxException;

    // ------------------------------------------------------------------------
    @JdbcToJava
    static WithString05 withString05FromDB(String value) {
        if (value == null) throw new AssertionError();
        return new WithString05(unique(value, "withString05"));
    }

    record RootRecord(WithString05 value) {
    }

    @JdbcToJava
    static RootRecord rootRecordFromDB(WithString05 value) {
        if (value == null) throw new AssertionError();
        return new RootRecord(value);
    }

    @JdbcSelect("SELECT :value")
    RootRecord twoLevel(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.invalidVoidReturnType")
    static void invalidVoidReturnTypeFromDB(String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.invalidVoidReturnType")
    void invalidVoidReturnType(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.invalidNullableReturnType")
    static @Nullable Class01 invalidNullableReturnTypeFromDB(String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.invalidNullableReturnType")
    Class01 invalidNullableReturnType(String value);

    @JdbcToJava("ColumnFromObjectSpec.invalidOptionalReturnType")
    static @Nullable String invalidOptionalReturnTypeFromDB(String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.invalidOptionalReturnType")
    String invalidOptionalReturnType(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.invalidParamNoJdbcType")
    static Class02 invalidParamNoJdbcTypeFromDB(NoJdbcType value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT 1")
    @JdbcConverterName("ColumnFromObjectSpec.invalidParamNoJdbcType")
    Class02 invalidParamNoJdbcType();

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.invalidParamNullable")
    static Class03 invalidParamNullableFromDB(@Nullable String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.invalidParamNullable")
    Class03 invalidParamNullable(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromObjectSpec.invalidParamNameAnnotation")
    static Class04 invalidParamNameAnnotationFromDB(@JdbcName("hello") String value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromObjectSpec.invalidParamNameAnnotation")
    String invalidParamNameAnnotation(String value);

    // ------------------------------------------------------------------------
    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.simpleNotStatic")
    String notStatic(String value);

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.simpleNotVisible")
    String notVisible(String value);

    // ------------------------------------------------------------------------
    static int unique(int value) {
        return value + ColumnFromObjectSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_ColumnFromObjectSpec_" + context;
    }

}
