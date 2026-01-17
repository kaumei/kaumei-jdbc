/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.docs;

import io.kaumei.jdbc.DatasourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class NativeCodeExampleTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeExample service;

    @BeforeEach
    void beforeEach() {
        service = new NativeCodeExampleJdbc(db::getConnection);
    }

    @Test
    void getTables() {
        assertThat(service.getTables("foo")).isEmpty();
        assertThat(service.getTables("DB_ADDRESS")).containsExactly("BASE TABLE, TEST, PUBLIC, DB_ADDRESS");
    }

}
