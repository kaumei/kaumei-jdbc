/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.config;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class StatementSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();
    private final String CITY = DbAddress.HAMBURG_1.city();

    private StatementSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new StatementSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
/*
    @Test
    void jdbcQueryTimeoutInterface() {
        assertThat(service.jdbcQueryTimeoutInterface(CITY)).isEqualTo(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("jdbcQueryTimeoutInterface")
                .bodyContains("stmt.setQueryTimeout(1000)");
    }
*/
    @Test
    void jdbcQueryTimeoutMethod() {
        assertThat(service.jdbcQueryTimeoutMethod(CITY)).isEqualTo(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("jdbcQueryTimeoutMethod")
                .bodyContains("stmt.setQueryTimeout(100)");
    }

    @Test
    void jdbcQueryTimeoutParameter() {
        assertThat(service.jdbcQueryTimeoutParameter(10, CITY)).isEqualTo(3);
        kaumeiThrows(() -> service.jdbcQueryTimeoutParameter(-1, CITY))
                .illegalArgumentException("Invalid value for timeout");
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("jdbcQueryTimeoutParameter")
                .bodyContains("timeout == -1")
                .bodyContains("setQueryTimeout(timeout)");
    }

    @Test
    void invalidJdbcQueryTimeoutMethod() {
        kaumeiThrows(() -> service.invalidJdbcQueryTimeoutMethod())
                .unusedMethodAnnotations(JdbcQueryTimeout.class);
    }

    @Test
    void invalidJdbcQueryTimeoutParameter() {
        kaumeiThrows(() -> service.invalidJdbcQueryTimeoutParameter(100))
                .unusedParameterAnnotations("timeout", JdbcQueryTimeout.class);
    }

    // @part:spec -------------------------------------------------------------

}
