/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.JdbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NativeCodeExceptionTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeException service;

    @BeforeEach
    void beforeEach() {
        db.executeSqls("DELETE FROM db_types");
        service = new NativeCodeExceptionJdbc(db::getConnection);
    }

    // @part:spec
    @Test
    void exceptionSQLException() {
        assertThat(service.exceptionSQLException("foobar")).isEqualTo("foobar");
        assertThatThrownBy(() -> service.exceptionSQLException("SQLException"))
                .isInstanceOf(JdbcException.class)
                .cause()
                .isInstanceOf(SQLException.class)
                .hasMessage("SQLException");
    }

    @Test
    void exceptionRuntimeException() {
        assertThat(service.exceptionRuntimeException("foobar")).isEqualTo("foobar");
        assertThatThrownBy(() -> service.exceptionRuntimeException("NullPointerException"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("NullPointerException");
    }

    @Test
    void exceptionCheckedException() throws IOException {
        assertThat(service.exceptionCheckedException("foobar")).isEqualTo("foobar");
        assertThatThrownBy(() -> service.exceptionCheckedException("FileNotFoundException"))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("FileNotFoundException");
    }

    @Test
    void exceptionInvalidCheckedException() {
        kaumeiThrows(() -> service.exceptionInvalidCheckedException("foobar"))
                .annotationProcessError("Exception not compatible: java.io.IOException");
    }

    @Test
    void exceptionNotReThrown() {
        kaumeiThrows(() -> service.exceptionNotReThrown("foobar"))
                .annotationProcessError("Exception not compatible: java.io.IOException");
    }


    // @part:spec

}
