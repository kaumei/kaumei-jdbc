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

class Java2JdbcLookupSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private Java2JdbcLookupSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new Java2JdbcLookupSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    @Test
    void validPrimitive() {
        assertThat(service.validPrimitive(1)).isEqualTo(1);
    }

    // ------------------------------------------------------------------------
    @Test
    void staticEnum() {
        assertThat(service.staticEnum(Java2JdbcLookupSpec.StaticEnum.B_C)).isEqualTo(2);
        assertThat(service.staticEnum(null)).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void invalidEnum() {
        kaumeiThrows(() -> service.invalidEnum(null))
                .paramInvalidConverter("value", "InvalidEnum", "To many annotations");
    }

    // @part:spec -------------------------------------------------------------

}
