/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec2.config;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static org.assertj.core.api.Assertions.assertThat;

class StatementInterfaceSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();
    private final String NAME = "name";

    private StatementInterfaceSpec service;

    @BeforeEach
    void beforeEach() {
        service = new StatementInterfaceSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void select() {
        assertThat(service.select(NAME)).isEqualTo(0);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("select")
                .bodyContains("stmt.setQueryTimeout(11)");
    }

    @Test
    void selectList() {
        assertThat(service.selectList(NAME)).isEmpty();
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectList")
                .bodyContains("stmt.setQueryTimeout(11)");
    }


    @Test
    void selectStream() {
        KaumeiAssert.assertThat(service.selectStream(NAME)).isEmpty();
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectStream")
                .bodyContains("stmt.setQueryTimeout(11)");
    }

    @Test
    void insert() {
        assertThat(service.insert(NAME)).isEqualTo(1);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("insert")
                .bodyContains("stmt.setQueryTimeout(11)");
    }

    @Test
    void batch() {
        try (var batch = service.batch()) {
            batch.insert(NAME);
            assertSource(service.getClass())
                    .hasClass(service.getClass().getSimpleName())
                    .hasMethod("batch")
                    .bodyContains("stmt.setQueryTimeout(11)");
        }
    }

    // @part:spec -------------------------------------------------------------

}
