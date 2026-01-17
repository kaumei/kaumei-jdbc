/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.common.WithString01;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class ConverterNamesSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterNamesSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterNamesSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void withStringA() {

        assertThat(service.withStringA(new WithString01("a"))).isEqualTo("withStringA:a");
        assertThat(service.withStringA(new WithString01(null))).isEqualTo("withStringA:null");
        assertThat(service.withStringA(null)).isNull();
    }

    @Test
    void withStringB() {
        assertThat(service.withStringB(new WithString01("b"))).isEqualTo("withStringB:b");
        assertThat(service.withStringB(new WithString01(null))).isEqualTo("withStringB:null");
        assertThat(service.withStringB(null)).isNull();
    }

    @Test
    void withStringDuplicate() {
        kaumeiThrows(() -> service.withStringDuplicate(null))
                .paramInvalidConverter("value", "WithString01", "duplicate key");
    }


    @Test
    void withUnknown() {
        kaumeiThrows(() -> service.withUnknown(null))
                .paramInvalidConverter("value", "String", "name 'withUnknown' not found");
    }


    // @part:spec -------------------------------------------------------------

}
