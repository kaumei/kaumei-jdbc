/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcToJava;

import java.sql.ResultSet;

public class GeneralConverter {
    // ------------------------------------------------------------------------
    @JdbcToJava("GeneralConverter.simpleNotStatic")
    public String simpleNotStatic(Integer value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcToJava("GeneralConverter.simpleNotVisible")
    private static String simpleNotVisible(Integer value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    @JdbcToJava("GeneralConverter.resultSetIntegerNotStatic")
    public String resultSetIntegerNotStatic(ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcToJava("GeneralConverter.resultSetIntegerNotVisible")
    private static String resultSetIntegerNotVisible(ResultSet rs, int index) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    @JdbcToJava("GeneralConverter.resultSetNotStatic")
    public String resultSetNotStatic(ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcToJava("GeneralConverter.resultSetNotVisible")
    private static String resultSetNotVisible(ResultSet rs) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    @JdbcToJava("GeneralConverter.objectsSetNotStatic")
    public String objectsSetNotStatic(String value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcToJava("GeneralConverter.objectsNotVisible")
    private static String objectsNotVisible(String value1, int value2) {
        throw new AssertionError("Method must not be called from test.");
    }
    // ------------------------------------------------------------------------
}
