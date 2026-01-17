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

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class ConverterEnumSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterEnumSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterEnumSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void enumUnspecific() {
        assertThat(service.enumUnspecific(ConverterEnumSpec.SimpleEnum.d_e)).isEqualTo("d_e");
        assertThat(service.enumUnspecific(null)).isNull();
    }

    @Test
    void enumNullable() {
        assertThat(service.enumNullable(ConverterEnumSpec.SimpleEnum.A)).isEqualTo("A");
        assertThat(service.enumNullable(null)).isNull();
    }

    @Test
    void enumNonNull() {
        assertThat(service.enumNonNull(ConverterEnumSpec.SimpleEnum.B_C)).isEqualTo("B_C");
        kaumeiThrows(() -> service.enumNonNull(null))
                .npe("value");
    }

    // @part:spec -------------------------------------------------------------

}
