/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Order of methods:
 * * static factory methods
 * * record constructors
 * * class constructors
 * * util methods
 */
public interface ColumnFromResultSetSpec {
    // static factory methods #################################################
    @JdbcToJava
    static RecordInt recordIntFromDB(ResultSet rs, int index) throws SQLException {
        var value = rs.getInt(index);
        return rs.wasNull() ? null : new RecordInt(unique(value));
    }

    @JdbcSelect("SELECT :value")
    RecordInt recordInt(Integer value);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT :value")
    Optional<RecordInt> optionalRecordInt(Integer value);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcName("value1")
    @JdbcSelect("SELECT :value as value1")
    Optional<RecordInt> optionalRecordIntWithJdbcName(Integer value);

    // ------------------------------------------------------------------------
    @JdbcToJava
    static RecordString recordStringFromDB(ResultSet rs, int index) throws SQLException {
        var value = rs.getString(index);
        return value == null ? null : new RecordString(unique(value, "recordString"));
    }

    @JdbcSelect("SELECT :value")
    RecordString recordString(String value);

    @JdbcSelect("SELECT :value as value01")
    @JdbcName("value01")
    RecordString recordStringWithName(String value);

    @JdbcSelect("SELECT :value")
    @NonNull RecordString recordStringNonNull(String value);

    @JdbcSelect("SELECT :value as value01")
    @JdbcName("value01")
    @NonNull RecordString recordStringNonNullWithName(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava
    static WithString03 withRuntimeExceptionFromDB(ResultSet rs, int index) throws RuntimeException {
        try {
            var value = rs.getString(index);
            if ("withRuntimeExceptionToDB".equals(value)) throw new RuntimeException();
            return value == null ? null : new WithString03(unique(value, "withRuntimeException"));
        } catch (SQLException e) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    WithString03 withRuntimeException(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.withFileNotFoundException")
    static WithString04 withFileNotFoundExceptionFromDB(ResultSet rs, int index) throws FileNotFoundException {
        try {
            throw new FileNotFoundException(unique(rs.getString(index), "withRuntimeException"));
        } catch (SQLException e) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.withFileNotFoundException")
    WithString04 withFileNotFoundException(String value);

    // ------------------------------------------------------------------------
    record RootRecord(WithString05 value) {
    }

    @JdbcToJava
    static @Nullable RootRecord rootRecordFromDB(ResultSet rs, int index) throws SQLException {
        var value = rs.getString(index);
        return value == null ? null : new RootRecord(new WithString05(unique(value, "twoLevel")));
    }

    @JdbcSelect("SELECT :value")
    RootRecord twoLevel(String value);


    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidVoidReturnType")
    static void invalidVoidReturnTypeFromDB(ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidVoidReturnType")
    void invalidVoidReturnType(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidNonNullReturnType")
    static @NonNull String invalidNonNullReturnTypeFromDB(ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidNonNullReturnType")
    String invalidNonNullReturnType(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidOptionalReturnType")
    static Optional<String> invalidOptionalReturnTypeFromDB(ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidOptionalReturnType")
    String invalidOptionalReturnType(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidTypeFirstParam")
    static String invalidTypeFirstParamFromDB(RowSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidTypeFirstParam")
    String invalidTypeFirstParam(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidNullableFirstParam")
    static String invalidNullableFirstParamFromDB(@Nullable ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidNullableFirstParam")
    String invalidNullableFirstParam(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidToManyParam")
    static String invalidToManyParamFromDB(ResultSet rs, int index, String name) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidToManyParam")
    String invalidToManyParam(String value);

    // ------------------------------------------------------------------------
    @JdbcToJava("ColumnFromResultSetSpec.invalidSecondParam")
    static String invalidSecondParamFromDB(ResultSet rs, long index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("ColumnFromResultSetSpec.invalidSecondParam")
    String invalidSecondParam(String value);

    // ------------------------------------------------------------------------
    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotStatic")
    String notStatic(String value);

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotVisible")
    String notVisible(String value);

    // record constructors ####################################################

    // class constructors #####################################################

    // util methods ###########################################################
    static int unique(int value) {
        return value + ColumnFromResultSetSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_ColumnFromResultSetSpec_" + context;
    }
}
