/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.ClassHierarchy;
import io.kaumei.jdbc.spec.common.WithLong01;
import io.kaumei.jdbc.spec.common.WithString01;
import io.kaumei.jdbc.spec.common.WithString03;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class ConverterSimpleSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterSimpleSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterSimpleSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void validString() {
        assertThat(service.validString(new WithString01("1"))).isEqualTo("validToDB:1");
        assertThat(service.validString(new WithString01(null))).isEqualTo("validToDB:null");
        assertThat(service.validString(null)).isNull();
    }

    @Test
    void validLong() {
        assertThat(service.validLong(new WithLong01(1))).isEqualTo(1L);
        assertThat(service.validLong(null)).isNull();
    }

    @Test
    void returnValueNullable() {
        kaumeiThrows(() -> service.returnValueNullable(null))
                .paramInvalidConverter("value", "Class01", "return type must be @NonNull or unspecific");
    }

    @Test
    void returnValueNonNull() {
        var value1 = new WithString03("1");
        assertThat(service.returnValueNonNull(value1)).isEqualTo("returnValueNonNullToDB:1");
    }

    @Test
    void returnValueVoid() {
        kaumeiThrows(() -> service.returnValueVoid(null))
                .paramInvalidConverter("value", "Class02", "Invalid return type");
    }

    @Test
    void returnValueNotJdbc() {
        kaumeiThrows(() -> service.returnValueNoJdbcType(null))
                .paramInvalidConverter("value", "Class03", "Invalid return type");
    }

    @Test
    void duplicate() {
        kaumeiThrows(() -> service.duplicate(null))
                .paramInvalidConverter("value", "Class04", "duplicate key");
    }

    @Test
    void toManyParameters() {
        kaumeiThrows(() -> service.toManyParameters(null))
                .paramNoConverterFound("value", "Class05");
    }

    @Test
    void invalidException() {
        kaumeiThrows(() -> service.invalidException(null))
                .paramInvalidConverter("value", "Class06", "has incompatible exceptions");
    }

    @Test
    void level01() {
        assertThat(service.level01(null)).isNull();
        assertThat(service.level01(new ClassHierarchy.Level01Cls(null))).isEqualTo("level01:null");
        assertThat(service.level01(new ClassHierarchy.Level01Cls("1"))).isEqualTo("level01:1");
    }

    @Test
    void level03() {
        assertThat(service.level03(null)).isNull();
        assertThat(service.level03(new ClassHierarchy.Level03Cls(null))).isEqualTo("level01:null");
        assertThat(service.level03(new ClassHierarchy.Level03Cls("3"))).isEqualTo("level01:3");
    }

    @Test
    void incompatibleType() {
        kaumeiThrows(() -> service.incompatibleType(null))
                .paramInvalidConverter("value", "String", "incompatible type");
    }

    @Test
    void invalidCycle() {
        kaumeiThrows(() -> service.invalidCycle(null))
                .paramInvalidConverter("value_jdbc", "Cycle01", "generate cycle");
    }

    // @part:spec -------------------------------------------------------------

}
