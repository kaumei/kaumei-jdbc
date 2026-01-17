/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.spec.common.*;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.jdbc2java.ColumnFromObjectSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnFromObjectSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ColumnFromObjectSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ColumnFromObjectSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void recordInt() {
        // given
        int value = 4711;
        // when ... then
        assertThat(service.recordInt(value)).isEqualTo(new RecordInt(unique(value)));
        assertThat(service.recordInt(null)).isNull();
    }

    @Test
    void classInt() {
        // given
        int value = 4712;
        // when ... then
        assertThat(service.classInt(value).value).isEqualTo(unique(value));
        assertThat(service.classInt(null)).isNull();
    }

    @Test
    void recordString() {
        // given
        var value = "foobar";
        // when ... then
        assertThat(service.recordString(value)).isEqualTo(new RecordString(unique(value, "recordString")));
        assertThat(service.recordString(null)).isNull();
    }

    @Test
    void recordStringWithName() {
        // given
        var value = "foobar";
        // when ... then
        assertThat(service.recordStringWithName(value)).isEqualTo(new RecordString(unique(value, "recordString")));
        assertThat(service.recordStringWithName(null)).isNull();
    }

    @Test
    void recordStringNonNull() {
        // given
        var value = "foobar";
        // when ... then
        assertThat(service.recordStringNonNull(value)).isEqualTo(new RecordString(unique(value, "recordString")));
        kaumeiThrows(() -> service.recordStringNonNull(null))
                .resultColumnWasNullOnIndex("1");
    }

    @Test
    void recordStringNonNullWithName() {
        // given
        var value = "foobar";
        // when ... then
        assertThat(service.recordStringNonNullWithName(value)).isEqualTo(new RecordString(unique(value, "recordString")));
        kaumeiThrows(() -> service.recordStringNonNullWithName(null)).resultColumnWasNullOnName("value1");
    }

    @Test
    void classString() {
        // given
        var value = "foobar";
        // when ... then
        assertThat(service.classString(value).value).isEqualTo(unique(value, "classString"));
        assertThat(service.classString(null)).isNull();
    }

    @Test
    void withSqlException() {
        // given
        var value = "withSqlException";
        // when ... then
        assertThat(service.withSqlException(value)).isEqualTo(new WithString02(unique(value, "withSqlException")));
        assertThat(service.withSqlException(null)).isNull();
        assertThatThrownBy(() -> service.withSqlException("SQLException"))
                .isExactlyInstanceOf(JdbcException.class);
    }

    @Test
    void withRuntimeException() {
        // given
        var value = "withRuntimeException";
        // when ... then
        assertThat(service.withRuntimeException(value)).isEqualTo(new WithString03(unique(value, "withRuntimeException")));
        assertThat(service.withRuntimeException(null)).isNull();
        assertThatThrownBy(() -> service.withRuntimeException("RuntimeException"))
                .isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void withFileNotFoundException() {
        kaumeiThrows(() -> service.withFileNotFoundException(null))
                .invalidConverter("ColumnFromObjectSpec.withFileNotFoundException",
                        "method throws incompatible exceptions");
    }

    @Test
    void withIncompatibleException() {
        kaumeiThrows(() -> service.withIncompatibleException(null))
                .invalidConverter("ColumnFromObjectSpec.withFileNotFoundException",
                        "method throws incompatible exceptions");
    }

    @Test
    void twoLevel() {
        // given
        var value = "withSqlException";
        // when ... then
        assertThat(service.twoLevel(value)).isEqualTo(new ColumnFromObjectSpec.RootRecord(
                new WithString05(unique(value, "withString05"))));
        assertThat(service.twoLevel(null)).isNull();
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidVoidReturnType() {
        KaumeiAssert.kaumeiThrows(() -> service.invalidVoidReturnType(null))
                .returnTypNotSupported("void");
    }

    @Test
    void invalidNullableReturnType() {
        kaumeiThrows(() -> service.invalidNullableReturnType(null))
                .invalidConverter("ColumnFromObjectSpec.invalidNullableReturnType",
                        "method return type must be @NonNull or unspecific");
    }

    @Test
    void invalidOptionalReturnType() {
        kaumeiThrows(() -> service.invalidOptionalReturnType(null))
                .invalidConverter("ColumnFromObjectSpec.invalidOptionalReturnType",
                        "method return type must be @NonNull or unspecific");
    }

    @Test
    void invalidParamNoJdbcType() {
        kaumeiThrows(() -> service.invalidParamNoJdbcType())
                .invalidConverter("ColumnFromObjectSpec.invalidParamNoJdbcType",
                        "invalid converter.*NoJdbcType");
    }

    @Test
    void invalidParamNullable() {
        kaumeiThrows(() -> service.invalidParamNullable(null))
                .invalidConverter("ColumnFromObjectSpec.invalidParamNullable",
                        "nullable param not supported");
    }

    @Test
    void invalidParamNameAnnotation() {
        kaumeiThrows(() -> service.invalidParamNameAnnotation(null))
                .invalidConverter("ColumnFromObjectSpec.invalidParamNameAnnotation",
                        "name mapping not supported for one param");
    }

    @Test
    void notStatic() {
        kaumeiThrows(() -> service.notStatic(null))
                .invalidConverter("GeneralConverter.simpleNotStatic",
                        "method must be static");
    }

    @Test
    void notVisible() {
        kaumeiThrows(() -> service.notVisible(null))
                .invalidConverter("GeneralConverter.simpleNotVisible",
                        "method must be visible");
    }

    // @part:spec -------------------------------------------------------------

}
