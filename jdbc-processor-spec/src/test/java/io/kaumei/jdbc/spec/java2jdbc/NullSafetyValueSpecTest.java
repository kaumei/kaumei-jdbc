/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class NullSafetyValueSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NullSafetyValueSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new NullSafetyValueSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void paramUnspecified() {
        assertThat(service.paramUnspecified("foobar")).isEqualTo("foobar");
        assertThat(service.paramUnspecified(null)).isNull();
    }

    @Test
    void paramNullable() {
        assertThat(service.paramNullable("foobar")).isEqualTo("foobar");
        assertThat(service.paramNullable(null)).isNull();
    }

    @Test
    void paramNonNull() {
        assertThat(service.paramNonNull("foobar")).isEqualTo("foobar");
        kaumeiThrows(() -> service.paramNonNull(null)).npe("value");
    }

    // ------------------------------------------------------------------------

    @Test
    void optional_unspecified() {
        kaumeiThrows(() -> service.optional_unspecified(Optional.of("foobar")))
                .paramOptionalTypeIsInvalid();
    }

    @Test
    void optional_nullable() {
        kaumeiThrows(() -> service.optional_nullable(Optional.of("foobar")))
                .paramOptionalTypeIsInvalid();
    }

    @Test
    void optional_nonnull() {
        kaumeiThrows(() -> service.optional_nonnull(Optional.of("foobar")))
                .paramOptionalTypeIsInvalid();
    }

    // @part:spec -------------------------------------------------------------

}
