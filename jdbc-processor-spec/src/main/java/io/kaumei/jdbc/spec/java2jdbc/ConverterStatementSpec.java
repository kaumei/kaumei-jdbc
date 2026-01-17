/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@JavaToJdbc
public interface ConverterStatementSpec {

    // ------------------------------------------------------------------------
    static void paramUnspecificToDB(PreparedStatement stmt, int index, WithString01 value)
            throws SQLException, RuntimeException {
        if (value != null && "IllegalArgumentException".equals(value.value())) {
            throw new IllegalArgumentException();
        }
        stmt.setString(index, value == null ? null : value.value());
    }

    @JdbcSelect("SELECT :value")
    String paramUnspecific(WithString01 value);

    // ------------------------------------------------------------------------
    static void validNullableToDB(PreparedStatement stmt, int index, @Nullable WithString02 value)
            throws SQLException, RuntimeException {
        if (value != null && "IllegalArgumentException".equals(value.value())) {
            throw new IllegalArgumentException();
        }
        stmt.setString(index, value == null ? null : value.value());
    }

    @JdbcSelect("SELECT :value")
    String validNullable(WithString02 value);

    // ------------------------------------------------------------------------

    @JavaToJdbc
    static void validNonNullToDB(PreparedStatement stmt, int index, @NonNull WithString03 value) throws SQLException {
        stmt.setString(index, value == null ? null : value.value());
    }

    @JdbcSelect("SELECT :value")
    String validNonNull(WithString03 value);

    // ------------------------------------------------------------------------

    @JavaToJdbc
    static void integerParamToDB(PreparedStatement stmt, Integer index, WithString04 value) throws SQLException {
        stmt.setString(index, value == null ? null : value.value());
    }

    @JdbcSelect("SELECT :value")
    String integerParam(WithString04 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void callableStatementToDB(CallableStatement stmt, int index, Class01 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String callableStatement(Class01 value);

    // ------------------------------------------------------------------------
    static void optionalToDB(PreparedStatement stmt, int index, Optional<Class02> value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String optional(Class02 value);

    // ------------------------------------------------------------------------
    static void duplicate1(PreparedStatement stmt, int index, Class03 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    static void duplicate2(PreparedStatement stmt, int index, Class03 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String duplicate(Class03 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void ioExceptionToDB(PreparedStatement stmt, int index, Class04 value, String value2) throws IOException {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String ioException(Class04 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void toManyParametersToDB(PreparedStatement stmt, int index, Class05 value, String value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String toManyParameters(Class05 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static String invalidReturnVoidToDB(PreparedStatement stmt, int index, Class06 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String invalidReturnVoid(Class06 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void invalidExceptionDB(PreparedStatement stmt, int index, Class07 value) throws IOException {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String invalidException(Class07 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc
    static void firstNullableToDB(@Nullable PreparedStatement stmt, int index, Class08 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String firstNullable(Class08 value);

    // ------------------------------------------------------------------------
    record ObjectRecordStatic(String value) {
        @JavaToJdbc
        static void toDB(PreparedStatement stmt, int index, ObjectRecordStatic value)
                throws SQLException {
            stmt.setString(index, value == null ? null : value.value());
        }
    }

    @JdbcSelect("SELECT :value")
    String staticInObject(ObjectRecordStatic value);

    // ------------------------------------------------------------------------
    record ObjectRecordToManyAnnotations(String value) {
        @JavaToJdbc
        static void toDB(PreparedStatement stmt, int index, ObjectRecordToManyAnnotations value) throws SQLException {
            throw new AssertionError("Method must not be called from test.");
        }

        @JavaToJdbc
        public String toDB() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String objectRecordToManyAnnotations(ObjectRecordToManyAnnotations value);

    // ------------------------------------------------------------------------
    record ObjectRecordWrongType(String value) {
        @JavaToJdbc
        static void toDB(PreparedStatement stmt, int index, String value) throws SQLException {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String objectRecordWrongType(ObjectRecordWrongType value);

    // ------------------------------------------------------------------------
    record ObjectRecordWithName(String value) {
        @JavaToJdbc("invalid_name")
        static void toDB(PreparedStatement stmt, int index, ObjectRecordWithName value) throws SQLException {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String objectRecordWithName(ObjectRecordWithName value);

    // ------------------------------------------------------------------------
    record ObjectRecordWrongParameterCount(String value) {
        @JavaToJdbc
        static void toDB(PreparedStatement stmt, int index) throws SQLException {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String objectRecordWrongParameterCount(ObjectRecordWrongParameterCount value);

    // ------------------------------------------------------------------------

}
