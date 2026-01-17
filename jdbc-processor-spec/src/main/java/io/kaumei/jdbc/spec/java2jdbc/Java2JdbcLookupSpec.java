/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Java2JdbcLookupSpec {

    // ------------------------------------------------------------------------
    @JavaToJdbc("validPrimitive")
    static void validPrimitiveToDB(PreparedStatement stmt, int index, long value) throws SQLException {
        stmt.setLong(index, value);
    }

    @JdbcSelect("SELECT :value")
    long validPrimitive(@JdbcConverterName("validPrimitive") long value);


    // ------------------------------------------------------------------------
    enum StaticEnum {
        A(1), B_C(2), d_e(3);

        final int value;

        StaticEnum(int value) {
            this.value = value;
        }

        @JavaToJdbc
        static int toDB(StaticEnum e) {
            return e.value;
        }
    }

    @JdbcSelect("SELECT :value")
    Integer staticEnum(StaticEnum value);

    // ------------------------------------------------------------------------
    enum InvalidEnum {
        A, B_C, d_e;

        @JavaToJdbc
        static String method01(InvalidEnum e) {
            throw new AssertionError("Method must not be called from test.");
        }

        @JavaToJdbc
        static String method02(InvalidEnum e) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String invalidEnum(InvalidEnum value);

}
