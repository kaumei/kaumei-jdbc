/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.common.*;
import io.kaumei.jdbc.spec.db.DbAddress;
import io.kaumei.jdbc.spec.java2jdbc.ConverterRecordSpec.RecordNonnull;
import io.kaumei.jdbc.spec.java2jdbc.ConverterRecordSpec.RecordUnspecified;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class ConverterRecordSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterRecordSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterRecordSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void recordStringUnspecific() {
        assertThat(service.recordStringUnspecific(new RecordString(null))).isNull();
        assertThat(service.recordStringUnspecific(new RecordString("1"))).isEqualTo("1");
        assertThat(service.recordStringUnspecific(null)).isNull();
    }

    @Test
    void recordStringNullable() {
        assertThat(service.recordStringNullable(new RecordString(null))).isNull();
        assertThat(service.recordStringNullable(new RecordString("1"))).isEqualTo("1");
        assertThat(service.recordStringNullable(null)).isNull();
    }

    @Test
    void recordStringNonNull() {
        kaumeiThrows(() -> service.recordStringNonNull(new RecordString(null)))
                .npe("value");
        assertThat(service.recordStringNonNull(new RecordString("1"))).isEqualTo("1");
        kaumeiThrows(() -> service.recordStringNonNull(null))
                .npe("value");
    }

    // ------------------------------------------------------------------------

    @Test
    void recordBooleanUnspecific() {
        assertThat(service.recordBooleanUnspecific(new RecordBoolean(true))).isTrue();
        assertThat(service.recordBooleanUnspecific(null)).isNull();
    }

    @Test
    void recordBooleanNullable() {
        assertThat(service.recordBooleanNullable(new RecordBoolean(true))).isTrue();
        assertThat(service.recordBooleanNullable(null)).isNull();
    }

    @Test
    void recordBooleanNonNull() {
        assertThat(service.recordBooleanNonNull(new RecordBoolean(true))).isTrue();
        kaumeiThrows(() -> service.recordBooleanNonNull(null))
                .npe("value");
    }

    @Test
    void recordChar() {
        assertThat(service.recordChar(new RecordChar('a'))).isEqualTo('a');
        assertThat(service.recordChar(null)).isNull();
    }

    @Test
    void recordShort() {
        assertThat(service.recordShort(new RecordShort((short) 1))).isEqualTo((short) 1);
        assertThat(service.recordShort(null)).isNull();
    }

    @Test
    void recordInt() {
        assertThat(service.recordInt(new RecordInt(1))).isEqualTo(1);
        assertThat(service.recordInt(null)).isNull();
    }

    @Test
    void recordLong() {
        assertThat(service.recordLong(new RecordLong(1L))).isEqualTo(1L);
        assertThat(service.recordLong(null)).isNull();
    }

    @Test
    void recordFloat() {
        assertThat(service.recordFloat(new RecordFloat(1.1f))).isEqualTo(1.1f);
        assertThat(service.recordFloat(null)).isNull();
    }

    @Test
    void recordDouble() {
        assertThat(service.recordDouble(new RecordDouble(1.2d))).isEqualTo(1.2d);
        assertThat(service.recordDouble(null)).isNull();
    }

    // ------------------------------------------------------------------------

    @Test
    void recordComponentNullable() {
        kaumeiThrows(() -> service.recordComponentNullable(null))
                .paramInvalidConverter("value", "RecordStringNullable", "Record component must be 'non-null' or 'unspecific'.");
    }

    @Test
    void recordComponentNonNull() {
        assertThat(service.recordComponentNonNull(new RecordStringNonNull(null))).isNull();
        assertThat(service.recordComponentNonNull(new RecordStringNonNull("1"))).isEqualTo("1");
        assertThat(service.recordComponentNonNull(null)).isNull();
    }

    @Test
    void recordTwoComponents() {
        kaumeiThrows(() -> service.recordTwoComponents(null))
                .paramInvalidConverter("value", "RecordTwoComponents", "Record must have exact one component.");
    }

    @Test
    void recordNoJdbcType() {
        kaumeiThrows(() -> service.recordNoJdbcType(null))
                .paramInvalidConverter("value", "RecordNoJdbcType", "Record component must be a valid JDBC type.");
    }

    // ------------------------------------------------------------------------

    @Test
    void unspecified_unspecified() {
        assertThat(service.unspecified_unspecified(new RecordUnspecified("1"))).isEqualTo("1");
        assertThat(service.unspecified_unspecified(new RecordUnspecified(null))).isNull();
        assertThat(service.unspecified_unspecified(null)).isNull();
    }

    @Test
    void unspecified_nullable() {
        kaumeiThrows(() -> service.unspecified_nullable(null))
                .paramInvalidConverter("value", "RecordNullable", "Record component must be 'non-null' or 'unspecific'.");
    }

    @Test
    void unspecified_nonnull() {
        assertThat(service.unspecified_nonnull(new RecordNonnull("1"))).isEqualTo("1");
        assertThat(service.unspecified_nonnull(new RecordNonnull(null))).isNull();
        assertThat(service.unspecified_nonnull(null)).isNull();
    }

    @Test
    void nullable_unspecified() {
        assertThat(service.nullable_unspecified(new RecordUnspecified("1"))).isEqualTo("1");
        assertThat(service.nullable_unspecified(new RecordUnspecified(null))).isNull();
        assertThat(service.nullable_unspecified(null)).isNull();
    }

    @Test
    void nullable_nullable() {
        kaumeiThrows(() -> service.nullable_nullable(null))
                .paramInvalidConverter("value", "RecordNullable", "Record component must be 'non-null' or 'unspecific'.");
    }

    @Test
    void nullable_nonnull() {
        assertThat(service.nullable_nonnull(new RecordNonnull("1"))).isEqualTo("1");
        assertThat(service.nullable_nonnull(new RecordNonnull(null))).isNull();
        assertThat(service.nullable_nonnull(null)).isNull();
    }

    @Test
    void nonnull_unspecified() {
        assertThat(service.nonnull_unspecified(new RecordUnspecified("1"))).isEqualTo("1");
        kaumeiThrows(() -> service.nonnull_unspecified(new RecordUnspecified(null)))
                .npe("value");
        kaumeiThrows(() -> service.nonnull_unspecified(null))
                .npe("value");
    }

    @Test
    void nonnull_nullable() {
        kaumeiThrows(() -> service.nonnull_nullable(null))
                .paramInvalidConverter("value", "RecordNullable", "Record component must be 'non-null' or 'unspecific'.");
    }

    @Test
    void nonnull_nonnull() {
        assertThat(service.nonnull_nonnull(new RecordNonnull("1"))).isEqualTo("1");
        kaumeiThrows(() -> service.nonnull_nonnull(new RecordNonnull(null)))
                .npe("value");
        kaumeiThrows(() -> service.nonnull_nonnull(null))
                .npe("value");
    }

    // @part:spec -------------------------------------------------------------

}
