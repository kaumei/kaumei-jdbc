/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.common.*;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class ColumnFromObjectFactorySpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ColumnFromObjectFactorySpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ColumnFromObjectFactorySpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void recordBoolean() {
        // given
        boolean value = true;
        // when ... then
        assertThat(service.recordBoolean(value)).isEqualTo(new RecordBoolean(value));
        assertThat(service.recordBoolean(null)).isNull();
    }

    @Test
    void recordShort() {
        // given
        short value = 1;
        // when ... then
        assertThat(service.recordShort(value)).isEqualTo(new RecordShort(value));
        assertThat(service.recordShort(null)).isNull();
    }

    @Test
    void recordInt() {
        // given
        int value = 2;
        // when ... then
        assertThat(service.recordInt(value)).isEqualTo(new RecordInt(value));
        assertThat(service.recordInt(null)).isNull();
    }

    @Test
    void recordLong() {
        // given
        long value = 3;
        // when ... then
        assertThat(service.recordLong(value)).isEqualTo(new RecordLong(value));
        assertThat(service.recordLong(null)).isNull();
    }

    @Test
    void recordFloat() {
        // given
        float value = 4;
        // when ... then
        assertThat(service.recordFloat(value)).isEqualTo(new RecordFloat(value));
        assertThat(service.recordFloat(null)).isNull();
    }

    @Test
    void recordDouble() {
        // given
        double value = 5;
        // when ... then
        assertThat(service.recordDouble(value)).isEqualTo(new RecordDouble(value));
        assertThat(service.recordDouble(null)).isNull();
    }

    @Test
    void recordChar() {
        // given
        char value = '6';
        // when ... then
        assertThat(service.recordChar(value)).isEqualTo(new RecordChar(value));
        assertThat(service.recordChar(null)).isNull();
    }

    @Test
    void recordCharacter() {
        // given
        char value = '7';
        // when ... then
        assertThat(service.recordCharacter(value)).isEqualTo(new RecordCharacter(value));
        assertThat(service.recordCharacter(null)).isNull();
    }

    @Test
    void recordString() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.recordString(value)).isEqualTo(new RecordString(value));
        assertThat(service.recordString(null)).isNull();
    }

    @Test
    void recordStringNonNull() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.recordStringNonNull(value)).isEqualTo(new RecordStringNonNull(value));
        assertThat(service.recordStringNonNull(null)).isNull();
    }

    @Test
    void recordStringNullable() {
        // given
        String value = "foobar";
        // when ... then

        kaumeiThrows(() -> service.recordStringNullable())
                .invalidConverter("RecordStringNullable", "nullable param not supported");
    }


    // ------------------------------------------------------------------------

    @Test
    void optionalRecordString() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.optionalRecordString(value)).isEqualTo(Optional.of(new RecordString(value)));
        assertThat(service.optionalRecordString(null)).isEmpty();
    }

    @Test
    void optionalRecordStringWithName() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.optionalRecordStringWithName(value)).isEqualTo(Optional.of(new RecordString(value)));
        assertThat(service.optionalRecordStringWithName(null)).isEmpty();
    }

    // ------------------------------------------------------------------------

    @Test
    void recordRec02() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.recordRec02(value)).isEqualTo(
                new ColumnFromObjectFactorySpec.RecordRec02(new ColumnFromObjectFactorySpec.RecordRec01(value))
        );
        assertThat(service.recordRec02(null)).isNull();
    }

    @Test
    void recordNoJavaType() {
        kaumeiThrows(() -> service.recordNoJavaType(null))
                .invalidConverter("NoJdbcType",
                        "no constructor found");
    }

    @Test
    void invalidRecordWithInvalidAnnotation() {
        kaumeiThrows(() -> service.invalidRecordWithInvalidAnnotation())
                .invalidConverter("ColumnFromObjectFactorySpec.InvalidRecordWithInvalidAnnotation",
                        "name mapping not supported for one param");
    }

    @Test
    void invalidRecordWithResultSetInt() {
        kaumeiThrows(() -> service.invalidRecordWithResultSetInt())
                .invalidConverter("ColumnFromObjectFactorySpec.RecordWithResultSetInt",
                        "record constructor does not support ResultSet");
    }

    // ------------------------------------------------------------------------

    @Test
    void classRec02() {
        // given
        String value = "foobar";
        // when ... then
        var result = service.classRec02(value);
        assertThat(result).isInstanceOf(ColumnFromObjectFactorySpec.ClassRec02.class);
        assertThat(result.value).isInstanceOf(ColumnFromObjectFactorySpec.ClassRec01.class);
        assertThat(result.value.value).isEqualTo(value);
        assertThat(service.classRec02(null)).isNull();
    }

    @Test
    void classResultSetInt() {
        kaumeiThrows(() -> service.invalidClassResultSetInt())
                .invalidConverter("ColumnFromObjectFactorySpec.InvalidClassResultSetInt",
                        " class constructor does not support ResultSet,int");
    }

    @Test
    void defaultGenerateEnum() {
        // when ... then
        assertThat(service.defaultGenerateEnum("lower"))
                .isEqualTo(ColumnFromObjectFactorySpec.DefaultGenerateEnum.lower);
        assertThat(service.defaultGenerateEnum("UPPER"))
                .isEqualTo(ColumnFromObjectFactorySpec.DefaultGenerateEnum.UPPER);
        assertThat(service.defaultGenerateEnum("UPPERlower"))
                .isEqualTo(ColumnFromObjectFactorySpec.DefaultGenerateEnum.UPPERlower);
        assertThat(service.defaultGenerateEnum(null)).isNull();
    }

    // @part:spec -------------------------------------------------------------

}
