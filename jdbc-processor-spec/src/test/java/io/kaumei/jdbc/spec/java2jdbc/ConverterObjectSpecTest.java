/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.db.DbAddress;
import io.kaumei.jdbc.spec.java2jdbc.ConverterObjectSpec.RecordRuntimeException;
import io.kaumei.jdbc.spec.java2jdbc.ConverterObjectSpec.SimpleReturnNonNull;
import io.kaumei.jdbc.spec.java2jdbc.ConverterObjectSpec.SimpleReturnUnspecific;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ConverterObjectSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterObjectSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterObjectSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void simpleReturnUnspecific() {
        var value = new SimpleReturnUnspecific("1");
        assertThat(service.simpleReturnUnspecific(value)).isEqualTo("SimpleReturnUnspecific:1");
    }

    @Test
    void simpleReturnNullable() {
        kaumeiThrows(() -> service.simpleReturnNullable(null))
                .paramInvalidConverter("value", "SimpleReturnNullable", "return type must be @NonNull or unspecific");
    }

    @Test
    void simpleReturnNonNull() {
        var value = new SimpleReturnNonNull(1);
        assertThat(service.simpleReturnNonNull(value)).isEqualTo("SimpleReturnNonNull:1");
    }

    @Test
    void simpleReturnVoid() {
        kaumeiThrows(() -> service.simpleReturnVoid(null))
                .paramInvalidConverter("value", "SimpleReturnVoid", "Invalid return type");
    }

    @Test
    void simpleReturnNoJdbcType() {
        kaumeiThrows(() -> service.simpleReturnNoJdbcType(null))
                .paramInvalidConverter("value", "SimpleReturnNoJdbcType", "NoJdbcType");
    }

    @Test
    void simpleToManyAnnotations() {
        kaumeiThrows(() -> service.simpleToManyAnnotations(null))
                .paramInvalidConverter("value", "SimpleToManyAnnotations", "To many annotations");
    }

    @Test
    void simpleToManyParameter() {
        kaumeiThrows(() -> service.simpleToManyParameter(null))
                .paramInvalidConverter("value", "SimpleToManyParameter", "@JavaToJdbc method must have no parameters");
    }

    // ------------------------------------------------------------------------
    @Test
    void notSupported() {
        kaumeiThrows(() -> service.notSupported(null))
                .paramInvalidConverter("value", "NotSupported", "@JavaToJdbc method must have no parameters");
    }

    @Test
    void recordRuntimeException() {
        assertThat(service.recordRuntimeException(new RecordRuntimeException(null))).isNull();
        assertThat(service.recordRuntimeException(null)).isNull();

        var io1 = new IOException("test");
        assertThat(service.recordRuntimeException(new RecordRuntimeException(io1)))
                .isEqualTo("test");

        var re = new IllegalArgumentException("test");
        assertThatThrownBy(() -> service.recordRuntimeException(new RecordRuntimeException(re)))
                .isEqualTo(re);
    }

    @Test
    void recordIOException() throws IOException {
        /*
        assertThat(service.recordIOException(new RecordIOException(null))).isNull();
        assertThat(service.recordIOException(null)).isNull();

        var io1 = new IllegalArgumentException("test");
        assertThat(service.recordIOException(new RecordIOException(io1)))
                .isEqualTo("test");

        var re = new IOException("test");
        assertThatThrownBy(() -> service.recordIOException(new RecordIOException(re)))
                .isEqualTo(re);
        */
        kaumeiThrows(() -> service.recordNoIOException(null))
                .paramInvalidConverter("value", "RecordIOException", "has incompatible exceptions");
    }

    @Test
    void recordNoIOException() {
        kaumeiThrows(() -> service.recordNoIOException(null))
                .paramInvalidConverter("value", "RecordIOException", "has incompatible exceptions");
    }

    // @part:spec -------------------------------------------------------------

}
