/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcDebug;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;

public interface JdbcToJavaLookupSpec {
    // ------------------------------------------------------------------------
    class TwoConstructors {
        String value;

        TwoConstructors(ResultSet rs, int index) {
            throw new AssertionError("Method must not be called from test.");
        }

        TwoConstructors(String value) {
            this.value = unique(value, "twoConstructors");
        }

        TwoConstructors(ResultSet rs, int index1, int index2, int index3) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    TwoConstructors twoConstructors(String value);

    // ------------------------------------------------------------------------
    record InvalidReturnType(String value) {
        @JdbcToJava
        static int fromDb(String value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    InvalidReturnType invalidReturnType(String value);

    // ------------------------------------------------------------------------
    record InvalidWithAnnotationName(String value) {
        @JdbcToJava("name")
        static int fromDb(String value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    InvalidWithAnnotationName invalidWithAnnotationName(String value);

    // ------------------------------------------------------------------------
    class InvalidClassToManyAnnotatedMethods {
        @JdbcToJava
        static InvalidClassToManyAnnotatedMethods method01(ResultSet rs) {
            throw new AssertionError("Method must not be called from test.");
        }

        @JdbcToJava
        static InvalidClassToManyAnnotatedMethods method02(ResultSet rs, int index) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcDebug
    @JdbcSelect("select 1")
    InvalidClassToManyAnnotatedMethods invalidClassToManyAnnotatedMethods();

    // util methods ###########################################################

    static int unique(int value) {
        return value + JdbcToJavaLookupSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_LookupSpec_" + context;
    }

}
