/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.config;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.docs.SimpleExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static org.assertj.core.api.Assertions.assertThat;

class StatementInvalidSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();
    private final String NAME = "name";

    private StatementInvalidSpec service;

    @BeforeEach
    void beforeEach() {
        service = new StatementInvalidSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void select() {
        assertThat(service.select(NAME)).isEqualTo(0);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("select")
                .bodyDoesNotContain("setQueryTimeout");
    }

    @Test
    void selectList() {
        assertThat(service.selectList(NAME)).isEmpty();
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectList")
                .bodyDoesNotContain("setQueryTimeout");
    }


    @Test
    void selectStream() {
        KaumeiAssert.assertThat(service.selectStream(NAME)).isEmpty();
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectStream")
                .bodyDoesNotContain("setQueryTimeout");
    }

    @Test
    void insert() {
        assertThat(service.insert(NAME, SimpleExample.PricingPlan.FREE)).isEqualTo(1);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("insert")
                .bodyDoesNotContain("setQueryTimeout");
    }

    @Test
    void batch() {
        try (var batch = service.batch()) {
            batch.insert(NAME, SimpleExample.PricingPlan.FREE);
            assertSource(service.getClass())
                    .hasClass(service.getClass().getSimpleName())
                    .hasMethod("batch")
                    .bodyDoesNotContain("setQueryTimeout");
        }
    }

    // @part:spec -------------------------------------------------------------

}
