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

class ColumnTypeSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ColumnTypeSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ColumnTypeSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    // test with real H2 JDBC driver

    @Test
    void typePrimitiveBoolean() {
        // given
        boolean value = true;
        // when ... then
        assertThat(service.typePrimitiveBoolean(value)).isEqualTo(value);
        kaumeiThrows(() -> service.typePrimitiveBoolean(null))
                .resultColumnWasNullOnIndex("1");
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
        kaumeiThrows(() -> service.typePrimitiveChar(null))
                .resultColumnWasNullOnIndex("1");
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
    void invalidColumnCycle() {
        kaumeiThrows(() -> service.invalidColumnCycle())
                .invalidConverter("ColumnCycle01", "generate cycle");
    }

    @Test
    void invalidRecordCycle() {
        kaumeiThrows(() -> service.invalidRecordCycle())
                .invalidConverter("RecordCycle01", "generate cycle");
    }


    // @part:spec -------------------------------------------------------------

}
