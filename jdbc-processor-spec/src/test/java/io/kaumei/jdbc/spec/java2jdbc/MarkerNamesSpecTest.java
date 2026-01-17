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

class MarkerNamesSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private MarkerNamesSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new MarkerNamesSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void select_param_missing() {
        kaumeiThrows(() -> service.select_param_missing())
                .annotationProcessError("No method parameter with 'value' found.");
    }

    @Test
    void select_ok() {
        assertThat(service.select_ok(1)).isEqualTo(1);
    }

    @Test
    void select_not_found() {
        kaumeiThrows(() -> service.select_not_found(1))
                .annotationProcessError("No method parameter with 'value' found.");
    }

    @Test
    void select_sql_missing() {
        kaumeiThrows(() -> service.select_sql_missing(1, 2))
                .annotationProcessError("No sql named parameter marker with 'other' found.");
    }

    // ------------------------------------------------------------------------

    @Test
    void update_param_missing() {
        kaumeiThrows(() -> service.update_param_missing())
                .annotationProcessError("No method parameter with 'value' found.");
    }

    @Test
    void update_ok() {
        assertThat(service.update_ok(1)).isEqualTo(0);
    }

    @Test
    void update_not_found() {
        kaumeiThrows(() -> service.update_not_found(1))
                .annotationProcessError("No method parameter with 'value' found.");
    }

    @Test
    void update_sql_missing() {
        kaumeiThrows(() -> service.update_sql_missing(1, 2))
                .annotationProcessError("No sql named parameter marker with 'other' found.");
    }

    // @part:spec -------------------------------------------------------------

}
