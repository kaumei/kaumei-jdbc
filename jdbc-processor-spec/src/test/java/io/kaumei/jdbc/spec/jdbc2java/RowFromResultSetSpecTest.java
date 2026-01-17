/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class RowFromResultSetSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private RowFromResultSetSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new RowFromResultSetSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    // static factory methods #################################################

    @Test
    void recordStringInt() {
        // when ... then
        assert_recordStringString("foo", "bar");
        assert_recordStringString(null, null);
    }

    void assert_recordStringString(String value1, String value2) {
        var expected = new RowFromResultSetSpec.RecordStringString(
                RowFromResultSetSpec.unique(value1, "recordStringString"),
                RowFromResultSetSpec.unique(value2, "recordStringString"));
        assertThat(service.recordStringString(value1, value2)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------------
    @Test
    void invalidReturnTypeVoid() {
        kaumeiThrows(() -> service.invalidReturnTypeVoid())
                .invalidConverter("RowFromResultSetSpec.invalidReturnTypeVoid", "must not return void");
    }

    @Test
    void invalidReturnTypeNullable() {
        kaumeiThrows(() -> service.invalidReturnTypeNullable())
                .invalidConverter("RowFromResultSetSpec.invalidReturnTypeNullable", " method return type must be @NonNull or unspecific");
    }

    @Test
    void invalidParamName() {
        kaumeiThrows(() -> service.invalidParamName())
                .invalidConverter("RowFromResultSetSpec.invalidParamName", "name mapping not supported for ResultSet");
    }

    @Test
    void invalidParamTypeRowSet() {
        kaumeiThrows(() -> service.invalidParamTypeRowSet())
                .invalidConverter("RowFromResultSetSpec.invalidParamTypeRowSet", "parameter must be ResultSet");
    }

    @Test
    void invalidParamNullable() {
        kaumeiThrows(() -> service.invalidParamNullable())
                .invalidConverter("RowFromResultSetSpec.invalidParamNullable", "nullable param not supported");
    }

    @Test
    void invalidParamOptional() {
        kaumeiThrows(() -> service.invalidParamOptional())
                .invalidConverter("RowFromResultSetSpec.invalidParamOptional", "nullable param not supported");
    }

    @Test
    void notStatic() {
        kaumeiThrows(() -> service.notStatic())
                .invalidConverter("GeneralConverter.resultSetIntegerNotStatic",
                        "method must be static");
    }

    @Test
    void notVisible() {
        kaumeiThrows(() -> service.notVisible())
                .invalidConverter("GeneralConverter.resultSetIntegerNotVisible",
                        "method must be visible");
    }

    // record constructors ####################################################
    @Test
    void invalidRecordConverterAnnotation() {
        kaumeiThrows(() -> service.invalidRecordConverterAnnotation())
                .invalidConverter("InvalidRecordConverterAnnotation",
                        "record constructor does not support ResultSet");
    }

    // class constructors #####################################################
    @Test
    void classConverter() {
        assert_classConverter("foo", "bar");
        assert_classConverter(null, null);
    }

    void assert_classConverter(String value1, String value2) {
        var given = service.classConverter(value1, value2);
        assertThat(given).isExactlyInstanceOf(RowFromResultSetSpec.ClassConverter.class);
        assertThat(given.value1).isEqualTo(RowFromResultSetSpec.unique(value1, "classConverter"));
        assertThat(given.value2).isEqualTo(RowFromResultSetSpec.unique(value2, "classConverter"));
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidClassConverterToMany() {
        kaumeiThrows(() -> service.invalidClassConverterToMany())
                .invalidConverter("InvalidClassConverterToMany",
                        "to many constructors");
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidClassConverterDefault() {
        kaumeiThrows(() -> service.invalidClassConverterDefault())
                .invalidConverter("InvalidClassConverterDefault", "method must have at least one parameter");
    }

    @Test
    void invalidClassConverterNullable() {
        kaumeiThrows(() -> service.invalidClassConverterNullable())
                .invalidConverter("InvalidClassConverterNullable", "nullable param not supported");
    }

    @Test
    void invalidClassConverterOptional() {
        kaumeiThrows(() -> service.invalidClassConverterOptional())
                .invalidConverter("InvalidClassConverterOptional", "nullable param not supported");
    }

    // @part:spec -------------------------------------------------------------

}
