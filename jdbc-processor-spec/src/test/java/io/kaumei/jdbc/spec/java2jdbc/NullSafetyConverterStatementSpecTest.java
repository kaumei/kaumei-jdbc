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
import static io.kaumei.jdbc.spec.common.WithString01.WITH_STRING_1_FOOBAR;
import static io.kaumei.jdbc.spec.common.WithString01.WITH_STRING_1_NULL;
import static org.assertj.core.api.Assertions.assertThat;

class NullSafetyConverterStatementSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NullSafetyConverterStatementSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new NullSafetyConverterStatementSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void paramUnspecific() {
        assertThat(service.paramUnspecific(null)).isEqualTo("null");
        assertThat(service.paramUnspecific(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramUnspecific(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    @Test
    void paramNullable() {
        assertThat(service.paramNullable(null)).isEqualTo("null");
        assertThat(service.paramNullable(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramNullable(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    @Test
    void paramNonNull() {
        kaumeiThrows(() -> service.paramNonNull(null))
                .npe("value");
        assertThat(service.paramNonNull(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramNonNull(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    // @part:spec -------------------------------------------------------------

}
