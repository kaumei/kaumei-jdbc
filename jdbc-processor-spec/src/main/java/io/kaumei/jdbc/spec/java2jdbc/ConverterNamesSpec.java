/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.common.WithString01;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ConverterNamesSpec {
    @JavaToJdbc("withStringA")
    static String withStringAToDB(WithString01 value) {
        return value == null ? null : "withStringA:" + value.value();
    }

    @JdbcSelect("SELECT :value")
    String withStringA(@JdbcConverterName("withStringA") WithString01 value);

    // ------------------------------------------------------------------------
    @JavaToJdbc("withStringB")
    static void paramUnspecificToDB(PreparedStatement stmt, int index, WithString01 value) throws SQLException {
        stmt.setString(index, value == null ? null : "withStringB:" + value.value());
    }

    @JdbcSelect("SELECT :value")
    String withStringB(@JdbcConverterName("withStringB") WithString01 value);
    // ------------------------------------------------------------------------

    @JavaToJdbc("withStringDuplicate")
    static String withStringDuplicate01(WithString01 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    @JavaToJdbc("withStringDuplicate")
    static void withStringDuplicate02(PreparedStatement stmt, int index, WithString01 value) throws SQLException {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT :value")
    String withStringDuplicate(@JdbcConverterName("withStringDuplicate") WithString01 value);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT :value")
    String withUnknown(@JdbcConverterName("withUnknown") String value);

}
