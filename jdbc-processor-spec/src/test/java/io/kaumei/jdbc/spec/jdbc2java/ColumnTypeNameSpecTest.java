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
import static io.kaumei.jdbc.spec.jdbc2java.ColumnTypeNameSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;

class ColumnTypeNameSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ColumnTypeNameSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ColumnTypeNameSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    // test with real H2 JDBC driver

    @Test
    void typePrimitiveBoolean() {
        // given
        boolean value = true;
        // when ... then
        assertThat(service.typePrimitiveBoolean(value)).isEqualTo(value);
        kaumeiThrows(() -> service.typePrimitiveBoolean(null)).resultColumnWasNullOnName("1");
    }

    @Test
    void typeBoolean() {
        // given
        boolean value = true;
        // when ... then
        assertThat(service.typeBoolean(value)).isEqualTo(value);
        assertThat(service.typeBoolean(null)).isNull();
    }

    @Test
    void typePrimitiveChar() {
        // given
        char value = 'x';
        // when ... then
        assertThat(service.typePrimitiveChar(value)).isEqualTo(value);
        kaumeiThrows(() -> service.typePrimitiveChar(null)).resultColumnWasNullOnName("1");
    }

    @Test
    void typeCharacter() {
        // given
        Character value = 'X';
        // when ... then
        assertThat(service.typeCharacter(value)).isEqualTo(value);
        assertThat(service.typeCharacter(null)).isNull();
    }


    @Test
    void typeString() {
        // given
        String value = "foobar";
        // when ... then
        assertThat(service.typeString(value)).isEqualTo(value);
        assertThat(service.typeString(null)).isNull();
    }

    @Test
    void typeNoJdbcType() {
        // when ... then
        kaumeiThrows(() -> service.typeNoJdbcType())
                .invalidConverter("NoJdbcType", "no constructor found");
    }

    // ------------------------------------------------------------------------
    @Test
    void invalidReturnTypeIncompatible() {
        // when ... then
        kaumeiThrows(() -> service.invalidReturnTypeIncompatible())
                .invalidConverter("ColumnTypeNameSpec.invalidReturnTypeIncompatible", "incompatible type");
    }

    @Test
    void compatibleReturnTypePrimitive() {
        // when ... then
        assertThat(service.compatibleReturnTypePrimitive("11")).isEqualTo(unique(11));
    }

    @Test
    void compatibleReturnTypeObject() {
        // when ... then
        assertThat(service.compatibleReturnTypeObject(47)).isEqualTo(unique("47", "compatibleReturnTypeObject"));
        assertThat(service.compatibleReturnTypeObject(null)).isNull();
    }

    // @part:spec -------------------------------------------------------------

}
