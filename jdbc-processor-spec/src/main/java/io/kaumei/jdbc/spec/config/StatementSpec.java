/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.config;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;

public interface StatementSpec {

    // ------------------------------------------------------------------------

    @JdbcQueryTimeout(100)
    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    int jdbcQueryTimeoutMethod(String city);

    @JdbcQueryTimeout
    @JdbcSelect("SELECT 1")
    int invalidJdbcQueryTimeoutMethod();

    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    int jdbcQueryTimeoutParameter(@JdbcQueryTimeout int timeout, String city);

    @JdbcSelect("SELECT 1")
    int invalidJdbcQueryTimeoutParameter(@JdbcQueryTimeout(100) int timeout);

    // util methods ###########################################################

}
