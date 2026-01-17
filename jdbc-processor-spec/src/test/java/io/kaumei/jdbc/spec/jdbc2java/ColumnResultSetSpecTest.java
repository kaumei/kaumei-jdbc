/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.spec.common.RecordInt;
import io.kaumei.jdbc.spec.common.RecordString;
import io.kaumei.jdbc.spec.common.WithString03;
import io.kaumei.jdbc.spec.common.WithString05;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.jdbc2java.ColumnFromResultSetSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnResultSetSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ColumnFromResultSetSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ColumnFromResultSetSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    // static factory methods #################################################
    @Test
    void recordInt() {
        // given
        int value = 4711;
        // when ... then
        assertThat(service.recordInt(value)).isEqualTo(new RecordInt(unique(value)));
        assertThat(service.recordInt(null)).isNull();
    }

    @Test
    void optionalRecordInt() {
        // given
        int value = 4711;
        // when ... then
        assertThat(service.optionalRecordInt(value)).isEqualTo(Optional.of(new RecordInt(unique(value))));
        assertThat(service.optionalRecordInt(null)).isEmpty();
    }

    @Test
    void optionalRecordIntWithJdbcName() {
        // given
        int value = 4711;
        // when ... then
        assertThat(service.optionalRecordIntWithJdbcName(value)).isEqualTo(Optional.of(new RecordInt(unique(value))));
        assertThat(service.optionalRecordIntWithJdbcName(null)).isEmpty();
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
        kaumeiThrows(() -> service.recordStringNonNullWithName(null))
                .resultColumnWasNullOnName("value01");
    }

    @Test
    void withRuntimeException() {
        // given
        var value = "withRuntimeException";
        // when ... then
        assertThat(service.withRuntimeException(value)).isEqualTo(new WithString03(unique(value, "withRuntimeException")));
        assertThat(service.withRuntimeException(null)).isNull();
        assertThatThrownBy(() -> service.withRuntimeException("withRuntimeExceptionToDB"))
                .isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void withFileNotFoundException() {
        kaumeiThrows(() -> service.withFileNotFoundException(null))
                .invalidConverter("ColumnFromResultSetSpec.withFileNotFoundException",
                        "method throws incompatible exceptions");
    }

    @Test
    void twoLevel() {
        // given
        var value = "withSqlException";
        // when ... then
        assertThat(service.twoLevel(value)).isEqualTo(new ColumnFromResultSetSpec.RootRecord(new WithString05(unique(value, "twoLevel"))));
        assertThat(service.twoLevel(null)).isNull();
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidVoidReturnType() {
        KaumeiAssert.kaumeiThrows(() -> service.invalidVoidReturnType(null))
                .returnTypNotSupported("void");
    }

    @Test
    void invalidNonNullReturnType() {
        kaumeiThrows(() -> service.invalidNonNullReturnType(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidNonNullReturnType",
                        "method return type must be a @Nullable or unspecific");
    }

    @Test
    void invalidOptionalReturnType() {
        kaumeiThrows(() -> service.invalidOptionalReturnType(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidOptionalReturnType",
                        "method return type must be a @Nullable or unspecific");
    }

    @Test
    void invalidTypeFirstParam() {
        kaumeiThrows(() -> service.invalidTypeFirstParam(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidTypeFirstParam",
                        "method first parameter must be a ResultSet");
    }

    @Test
    void invalidNullableFirstParam() {
        kaumeiThrows(() -> service.invalidNullableFirstParam(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidNullableFirstParam",
                        "method first parameter must be @NonNull or unspecific");
    }

    @Test
    void invalidToManyParam() {
        kaumeiThrows(() -> service.invalidToManyParam(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidToManyParam",
                        "with first ResultSet param has to many parameters");
    }

    @Test
    void invalidSecondParam() {
        kaumeiThrows(() -> service.invalidSecondParam(null))
                .invalidConverter("ColumnFromResultSetSpec.invalidSecondParam",
                        "method second parameter must be int");
    }

    @Test
    void notStatic() {
        kaumeiThrows(() -> service.notStatic(null))
                .invalidConverter("GeneralConverter.resultSetIntegerNotStatic",
                        "method must be static");
    }

    @Test
    void notVisible() {
        kaumeiThrows(() -> service.notVisible(null))
                .invalidConverter("GeneralConverter.resultSetIntegerNotVisible",
                        "method must be visible");
    }

    // record constructors ####################################################
    // class constructors #####################################################
    // @part:spec -------------------------------------------------------------

}
