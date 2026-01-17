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

import static org.assertj.core.api.Assertions.assertThat;


class RowFromObjectsFactorySpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private RowFromObjectsFactorySpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new RowFromObjectsFactorySpecJdbc(db::getConnection);
    }

    @Test
    void select() {
        assertThat(service.select()).isEqualTo("RowObjectsGeneratedSpec");
    }

    // @part:spec -------------------------------------------------------------
    // static factory methods #################################################
    // record constructors ####################################################
    // class constructors #####################################################
    // @part:spec -------------------------------------------------------------

}
