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
import static io.kaumei.jdbc.spec.jdbc2java.RowFromObjectsSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;

class RowFromObjectsSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private RowFromObjectsSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new RowFromObjectsSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    // static factory methods #################################################
    @Test
    void stringInt() {
        // when ... then
        assert_stringInt("foobar", 1);
        assert_stringInt(null, 2);
        kaumeiThrows(() -> service.stringInt("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_stringInt(String value1, Integer value2) {
        var expected = new RowFromObjectsSpec.StringInt(unique(value1, "stringInt"), unique(value2));
        assertThat(service.stringInt(value1, value2)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------------
    @Test
    void stringIntNullable() {
        // when ... then
        assert_stringIntNullable("foobar", 1);
        assert_stringIntNullable(null, 2);
        kaumeiThrows(() -> service.stringIntNullable("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_stringIntNullable(String value1, Integer value2) {
        var expected = new RowFromObjectsSpec.StringIntNullable(unique(value1, "stringIntNullable"), unique(value2));
        assertThat(service.stringIntNullable(value1, value2)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------------
    @Test
    void stringIntNonnull() {
        // when ... then
        assert_stringIntNonnull("foobar", 1);
        kaumeiThrows(() -> service.stringIntNonnull(null, 2))
                .npe("JDBC column was null on name: value1");
        kaumeiThrows(() -> service.stringIntNonnull("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_stringIntNonnull(String value1, Integer value2) {
        var expected = new RowFromObjectsSpec.StringIntNonnull(unique(value1, "stringIntNonnull"), unique(value2));
        assertThat(service.stringIntNonnull(value1, value2)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------------
    @Test
    void withNames() {
        // when ... then
        assert_withNames("foobar", 1);
        assert_withNames(null, 2);
        kaumeiThrows(() -> service.withNames("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_withNames(String value1, Integer value2) {
        var expected = new RowFromObjectsSpec.WithNames(unique(value1, "withNames"), unique(value2));
        assertThat(service.withNames(value1, value2)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------------
    @Test
    void invalidReturnTypeVoid() {
        kaumeiThrows(() -> service.invalidReturnTypeVoid())
                .invalidConverter("RowFromObjectsSpec.invalidReturnTypeVoid", "must not return void");
    }

    @Test
    void invalidReturnTypeNullable() {
        kaumeiThrows(() -> service.invalidReturnTypeNullable())
                .invalidConverter("RowFromObjectsSpec.invalidReturnTypeNullable", "method return type must be @NonNull or unspecific");
    }

    @Test
    void invalidParamOptional() {
        kaumeiThrows(() -> service.invalidParamOptional())
                .invalidConverter("RowFromObjectsSpec.invalidParamOptional", "parameter value1 invalid Optional");
    }

    @Test
    void invalidParamNoJdbcType() {
        kaumeiThrows(() -> service.invalidParamNoJdbcType())
                .invalidConverter("RowFromObjectsSpec.invalidParamNoJdbcType", "no constructor found");
    }

    @Test
    void invalidParamNoJdbcType2() {
        kaumeiThrows(() -> service.invalidParamNoJdbcType())
                .annotationProcessError("invalid converter", "NoJdbcType");
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
    // no test

    @Test
    void recordConverterAnnotation() {
        // when ... then
        assert_recordConverterAnnotation("foobar", 1);
        assert_recordConverterAnnotation(null, 2);
        kaumeiThrows(() -> service.recordConverterAnnotation("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_recordConverterAnnotation(String value1, Integer value2) {
        var expected = new RowFromObjectsSpec.RecordConverterAnnotation(value1, value2);
        assertThat(service.recordConverterAnnotation(value1, value2)).isEqualTo(expected);
    }

    // class constructors #####################################################

    @Test
    void classStringInt() {
        // when ... then
        assert_classStringInt("foobar", 1);
        assert_classStringInt(null, 2);
        kaumeiThrows(() -> service.classStringInt("foobar", null))
                .npe("JDBC column was null on name: value2");
    }

    void assert_classStringInt(String value1, Integer value2) {
        var result = service.classStringInt(value1, value2);
        assertThat(result.value1).isEqualTo(unique(value1, "classStringInt"));
        assertThat(result.value2).isEqualTo(unique(value2));
    }

    // @part:spec -------------------------------------------------------------

}
