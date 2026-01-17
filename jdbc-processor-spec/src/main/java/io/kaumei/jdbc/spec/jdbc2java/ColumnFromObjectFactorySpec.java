/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.NoJdbcType;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.util.Optional;


public interface ColumnFromObjectFactorySpec {

    // @formatter:off
    @JdbcSelect("SELECT :value") RecordBoolean recordBoolean(Boolean value);
    @JdbcSelect("SELECT :value") RecordShort recordShort(Short value);
    @JdbcSelect("SELECT :value") RecordInt recordInt(Integer value);
    @JdbcSelect("SELECT :value") RecordLong recordLong(Long value);
    @JdbcSelect("SELECT :value") RecordFloat recordFloat(Float value);
    @JdbcSelect("SELECT :value") RecordDouble recordDouble(Double value);
    @JdbcSelect("SELECT :value") RecordChar recordChar(Character value);
    @JdbcSelect("SELECT :value") RecordCharacter recordCharacter(Character value);
    @JdbcSelect("SELECT :value") RecordString recordString(String value);
    @JdbcSelect("SELECT :value") RecordStringNonNull recordStringNonNull(String value);
    @JdbcSelect("SELECT :value") RecordStringNullable recordStringNullable(); // invalid
    // @formatter:on

    @JdbcSelect("SELECT :value")
    Optional<RecordString> optionalRecordString(String value);

    @JdbcName("value1")
    @JdbcSelect("SELECT :value as value1")
    Optional<RecordString> optionalRecordStringWithName(String value);

    // ------------------------------------------------------------------------

    record RecordRec01(String value) {
    }

    record RecordRec02(RecordRec01 value) {
    }

    @JdbcSelect("SELECT :value")
    RecordRec02 recordRec02(String value);

    // ------------------------------------------------------------------------

    record RecordNoJavaType(NoJdbcType javaType) {
    }

    @JdbcSelect("SELECT :value")
    RecordNoJavaType recordNoJavaType(String value);

    // ------------------------------------------------------------------------

    record InvalidRecordWithInvalidAnnotation(@JdbcName("name") String javaType) {
    }

    @JdbcSelect("SELECT :value")
    InvalidRecordWithInvalidAnnotation invalidRecordWithInvalidAnnotation();

    // ------------------------------------------------------------------------

    record RecordWithResultSetInt(ResultSet rs, int index) {
    }

    @JdbcSelect("SELECT :value")
    RecordWithResultSetInt invalidRecordWithResultSetInt();

    // ------------------------------------------------------------------------

    class ClassRec01 {
        String value;

        ClassRec01(String value) {
            this.value = value;
        }
    }

    class ClassRec02 {
        ClassRec01 value;

        ClassRec02(ClassRec01 value) {
            this.value = value;
        }
    }

    @JdbcSelect("SELECT :value")
    ClassRec02 classRec02(String value);

    // ------------------------------------------------------------------------

    class InvalidClassResultSetInt {
        InvalidClassResultSetInt(ResultSet rs, int index) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcSelect("SELECT :value")
    InvalidClassResultSetInt invalidClassResultSetInt();

    // ------------------------------------------------------------------------

    enum DefaultGenerateEnum {
        UPPER,
        lower, UPPERlower
    }

    @JdbcSelect("SELECT :value")
    DefaultGenerateEnum defaultGenerateEnum(String value);

    // util methods ###########################################################

    static int unique(int value) {
        return value + RowFromResultSetSpec.class.hashCode();
    }

    static String unique(@Nullable String value, String context) {
        return value + "_ColumnFromObjectFactorySpec_" + context;
    }
}
