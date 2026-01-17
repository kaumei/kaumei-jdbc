/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.annotation.JdbcNative;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface NativeCodeException {

    // ------------------------------------------------------------------------
    // exceptions

    @JdbcNative
    String exceptionSQLException(String value);

    static String exceptionSQLException(Connection con, String value) throws SQLException {
        if (value.equals("SQLException")) {
            throw new SQLException(value);
        }
        return value;
    }

    @JdbcNative
    String exceptionRuntimeException(String value);

    static String exceptionRuntimeException(Connection con, String value) throws NullPointerException {
        if (value.equals("NullPointerException")) {
            throw new NullPointerException(value);
        }
        return value;
    }

    @JdbcNative
    String exceptionCheckedException(String value) throws IOException;

    static String exceptionCheckedException(Connection con, String value) throws FileNotFoundException {
        if (value.equals("FileNotFoundException")) {
            throw new FileNotFoundException(value);
        }
        return value;
    }

    @JdbcNative
    String exceptionInvalidCheckedException(String value) throws FileNotFoundException;

    static String exceptionInvalidCheckedException(Connection con, String value) throws IOException {
        if (value.equals("IOException")) {
            throw new IOException(value);
        }
        return value;
    }

    @JdbcNative
    String exceptionNotReThrown(String value);

    static String exceptionNotReThrown(Connection con, String value) throws IOException {
        return value;
    }
}
