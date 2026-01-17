/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.NoJdbcType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.sql.PreparedStatement;


public interface ConverterObjectSpec {

    // ------------------------------------------------------------------------
    record SimpleReturnUnspecific(String value) {
        @JavaToJdbc
        String toDB() {
            if (value == null) {
                return null;
            }
            return "SimpleReturnUnspecific:" + value;
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleReturnUnspecific(@Nullable SimpleReturnUnspecific value);

    // ------------------------------------------------------------------------
    record SimpleReturnNullable(long value) {
        @JavaToJdbc
        @Nullable
        String toDB() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleReturnNullable(SimpleReturnNullable value);

    // ------------------------------------------------------------------------
    record SimpleReturnNonNull(long value) {
        @JavaToJdbc
        @NonNull
        String toDB() {
            return "SimpleReturnNonNull:" + value;
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleReturnNonNull(SimpleReturnNonNull value);

    // ------------------------------------------------------------------------
    class SimpleReturnVoid {
        @JavaToJdbc
        void toDB() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleReturnVoid(SimpleReturnVoid value);


    // ------------------------------------------------------------------------
    class SimpleReturnNoJdbcType {
        @JavaToJdbc
        NoJdbcType toDB() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleReturnNoJdbcType(SimpleReturnNoJdbcType value);

    // ------------------------------------------------------------------------
    class SimpleToManyAnnotations {
        @JavaToJdbc
        String toDB1() {
            throw new AssertionError("Method must not be called from test.");
        }

        @JavaToJdbc
        String toDB2() {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleToManyAnnotations(SimpleToManyAnnotations value);

    // ------------------------------------------------------------------------
    class SimpleToManyParameter {
        @JavaToJdbc
        String toDB(String value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String simpleToManyParameter(SimpleToManyParameter value);

    // ------------------------------------------------------------------------
    class NotSupported {

        @JavaToJdbc
        String toDB(PreparedStatement stmt, int index) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    String notSupported(NotSupported value);


    // ------------------------------------------------------------------------
    record RecordRuntimeException(Throwable value) {
        @JavaToJdbc
        String toDB() throws IllegalArgumentException {
            if (value instanceof IllegalArgumentException re) {
                throw re;
            }
            return value == null ? null : value.getMessage();
        }
    }

    @JdbcSelect("SELECT :value")
    String recordRuntimeException(RecordRuntimeException value);

    // ------------------------------------------------------------------------
    record RecordIOException(Throwable value) {
        @JavaToJdbc
        String toDB() throws IOException {
            if (value instanceof IOException re) {
                throw re;
            }
            return value == null ? null : value.getMessage();
        }
    }

    @JdbcSelect("SELECT :value")
    String recordIOException(RecordIOException value) throws IOException;

    @JdbcSelect("SELECT :value")
    String recordNoIOException(RecordIOException value);

    // ------------------------------------------------------------------------

}
