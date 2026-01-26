/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.*;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import org.jspecify.annotations.Nullable;

import javax.sql.RowSet;
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
public interface RowFromResultSetSpec {
    // static factory methods #################################################

    record RecordStringString(String value1, String value2) {
        @JdbcToJava
        static RecordStringString recordStringStringFromDB(ResultSet rs) throws SQLException {
            var value1 = unique(rs.getString("value1"), "recordStringString");
            var value2 = unique(rs.getString("value2"), "recordStringString");
            return new RecordStringString(value1, value2);
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    RecordStringString recordStringString(String value1, String value2);

    @JdbcSelect("select col_varchar AS value1, col_int AS value2 from db_types WHERE col_int = :value2")
    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    Optional<RecordStringString> recordStringStringOptional(int value2);

    @JdbcSelect("select 1")
    @JdbcNoRows(JdbcNoRows.Kind.THROW_EXCEPTION)
    Optional<RecordStringString> recordStringStringOptionalInvalid();

    // ------------------------------------------------------------------------

    @JdbcToJava("RowFromResultSetSpec.invalidReturnTypeVoid")
    static void invalidReturnTypeVoidFromDB(ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidReturnTypeVoid")
    String invalidReturnTypeVoid();

    // ------------------------------------------------------------------------

    @JdbcToJava("RowFromResultSetSpec.invalidReturnTypeNullable")
    static @Nullable String invalidReturnTypeNullable(@Nullable ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidReturnTypeNullable")
    String invalidReturnTypeNullable();

    // ------------------------------------------------------------------------
    @JdbcToJava("RowFromResultSetSpec.invalidParamName")
    static String invalidParamName(@JdbcName("invalid") ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidParamName")
    String invalidParamName();

    // ------------------------------------------------------------------------
    @JdbcToJava("RowFromResultSetSpec.invalidParamTypeRowSet")
    static String invalidParamTypeRowSet(RowSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidParamTypeRowSet")
    String invalidParamTypeRowSet();

    // ------------------------------------------------------------------------
    @JdbcToJava("RowFromResultSetSpec.invalidParamNullable")
    static String invalidParamNullableFromDB(@Nullable ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidParamNullable")
    String invalidParamNullable();

    // ------------------------------------------------------------------------
    @JdbcToJava("RowFromResultSetSpec.invalidParamOptional")
    static String invalidParamOptionalFromDB(Optional<ResultSet> rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("select 1")
    @JdbcConverterName("RowFromResultSetSpec.invalidParamOptional")
    String invalidParamOptional();


    // ------------------------------------------------------------------------
    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotStatic")
    String notStatic();

    @JdbcSelect("SELECT :value")
    @JdbcConverterName("GeneralConverter.resultSetIntegerNotVisible")
    String notVisible();

    // record constructors ####################################################
    @JdbcToJava
    record InvalidRecordConverterAnnotation(ResultSet rs) {
    }

    @JdbcSelect("select 1")
    InvalidRecordConverterAnnotation invalidRecordConverterAnnotation();

    // class constructors #####################################################
    class ClassConverter {
        String value1;
        String value2;

        ClassConverter(ResultSet rs) throws SQLException {
            this.value1 = unique(rs.getString("value1"), "classConverter");
            this.value2 = unique(rs.getString("value2"), "classConverter");
        }
    }

    @JdbcSelect("select :value1 as value1, :value2 as value2")
    ClassConverter classConverter(String value1, String value2);

    // ------------------------------------------------------------------------

    class InvalidClassConverterToMany {
        InvalidClassConverterToMany(ResultSet rs) {
            throw new AssertionError("Method must not be called from test.");
        }

        InvalidClassConverterToMany(java.sql.Timestamp value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("select 1")
    InvalidClassConverterToMany invalidClassConverterToMany();

    // ------------------------------------------------------------------------
    class InvalidClassConverterDefault {
        @JdbcToJava
        InvalidClassConverterDefault() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("select 1")
    InvalidClassConverterDefault invalidClassConverterDefault();

    // ------------------------------------------------------------------------
    class InvalidClassConverterNullable {
        @JdbcToJava
        InvalidClassConverterNullable(@Nullable ResultSet rs) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("select 1")
    InvalidClassConverterNullable invalidClassConverterNullable();

    // ------------------------------------------------------------------------
    class InvalidClassConverterOptional {
        @JdbcToJava
        InvalidClassConverterOptional(Optional<ResultSet> rs) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("select 1")
    InvalidClassConverterOptional invalidClassConverterOptional();

    // util methods ###########################################################

    static int unique(int value) {
        return value + RowFromResultSetSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_RowFromResultSetSpecSpec_" + context;
    }
}
