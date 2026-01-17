/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.update;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.JdbcTestOnH2Database;
import io.kaumei.jdbc.JdbcTestOnPostgreSql;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private UpdateSpec service;

    @BeforeEach
    void beforeEach() {
        service = new UpdateSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void invalidValue() {
        kaumeiThrows(() -> service.invalidValue())
                .annotationProcessError("@JdbcUpdate must provide a SQL string");
    }

    @Test
    void invalidReturnType() {
        kaumeiThrows(() -> service.invalidReturnType())
                .returnTypNotSupported("double");
    }

    @Test
    void insertAndReturnVoid() {
        // given
        var name = "Foobar";
        // when
        service.insertAndReturnVoid(name, 1000);
        // then
        var customer = service.customers(name);
        assertThat(customer.name()).isEqualTo(name);
        assertThat(customer.budge()).isEqualTo(1000);
    }

    @Test
    void updateAndReturnInt() {
        // given
        var name = "Foobar";
        service.insertAndReturnVoid(name, 1000);
        // when ... then
        assertThat(service.updateAndReturnInt(name, 42)).isEqualTo(1);
        assertThat(service.customers(name).budge()).isEqualTo(42);
        assertThat(service.updateAndReturnInt("unknown", 42)).isEqualTo(0);
        assertThat(service.customers(name).budge()).isEqualTo(42);
    }

    @Test
    void updateAndReturnBoolean() {
        // given
        var name = "Foobar";
        service.insertAndReturnVoid(name, 1000);
        // when ... then
        assertThat(service.updateAndReturnBoolean(name, 42)).isTrue();
        assertThat(service.customers(name).budge()).isEqualTo(42);
        assertThat(service.updateAndReturnBoolean("unknown", 42)).isFalse();
        assertThat(service.customers(name).budge()).isEqualTo(42);
    }

    // ------------------------------------------------------------------------

    @Test
    void updateGeneratedKeysUnspecific_CheckCode() {
        assertSource(UpdateSpecJdbc.class)
                .hasClass("UpdateSpecJdbc")
                .hasMethod("updateGeneratedKeysUnspecific")
                .bodyContains("getGeneratedKeys");
    }

    @JdbcTestOnH2Database
    @Test
    void updateGeneratedKeysUnspecific() {
        // given
        var name = "Foobar";
        // when ... then
        var id = service.updateGeneratedKeysUnspecific(name, 42);
        var customer = service.customers(name);
        assertThat(customer.id()).isEqualTo(id);
    }

    // ------------------------------------------------------------------------

    @Test
    void returnTypeForGeneratedKeysNonNull_CheckCode() {
        assertSource(UpdateSpecJdbc.class)
                .hasClass("UpdateSpecJdbc")
                .hasMethod("returnTypeForGeneratedKeysNonNull")
                .bodyContains("getGeneratedKeys");
    }

    @JdbcTestOnH2Database
    @Test
    void returnTypeForGeneratedKeysNonNull() {
        // given
        var name = "Foobar";
        // when ... then
        var id = service.returnTypeForGeneratedKeysNonNull(name, 42);
        var customer = service.customers(name);
        assertThat(customer.id()).isEqualTo(id);
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidReturnTypeForGeneratedKeysNullable() {
        kaumeiThrows(() -> service.invalidReturnTypeForGeneratedKeysNullable())
                .returnNullnessNotSupported("nullable","non-null","unspecific");
    }

    @Test
    void invalidReturnTypeForGeneratedKeysOptional() {
        kaumeiThrows(() -> service.invalidReturnTypeForGeneratedKeysOptional())
                .returnNullnessNotSupported("Optional<.>","non-null","unspecific");
    }

    @Test
    void invalidReturnTypeForGeneratedKeysVoid() {
        kaumeiThrows(() -> service.invalidReturnTypeForGeneratedKeysVoid())
                .returnTypNotSupported("void");
    }

    @Test
    void invalidReturnTypeForGeneratedKeysCollection() {
        kaumeiThrows(() -> service.invalidReturnTypeForGeneratedKeysCollection())
                .returnTypNotSupported("java.util.List<java.lang.Long>");
    }

    @Test
    void invalidReturnTypeForGeneratedKeysJdbcBatch() {
        kaumeiThrows(() -> service.invalidReturnTypeForGeneratedKeysJdbcBatch())
                .returnTypNotSupported("io.kaumei.jdbc.JdbcBatch");
    }

    // ------------------------------------------------------------------------

    @Test
    void updateExecuteQueryUnspecific_CheckCode() {
        assertSource(UpdateSpecJdbc.class)
                .hasClass("UpdateSpecJdbc")
                .hasMethod("updateExecuteQueryUnspecific")
                .bodyContains("executeQuery");
    }

    @JdbcTestOnPostgreSql
    @Test
    void updateExecuteQueryUnspecific() {
        // given
        var name = "Foobar";
        // when ... then
        var id = service.updateExecuteQueryUnspecific(name, 42);
        var customer = service.customers(name);
        assertThat(customer.id()).isEqualTo(id.id());
        assertThat(customer.created()).isEqualTo(id.created());
    }

    // @part:spec -------------------------------------------------------------

}
