/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;

public interface MarkerNamesSpec {

    @JdbcSelect("SELECT :value")
    int select_param_missing();

    @JdbcSelect("SELECT :value")
    int select_ok(int value);

    @JdbcSelect("SELECT :value")
    int select_not_found(int value1);

    @JdbcSelect("SELECT :value")
    int select_sql_missing(int value, int other);

    // ------------------------------------------------------------------------

    @JdbcUpdate("UPDATE db_types SET col_int = 1 where col_int = :value")
    void update_param_missing();

    @JdbcUpdate("UPDATE db_types SET col_int = 1 where col_int = :value")
    int update_ok(int value);

    @JdbcUpdate("UPDATE db_types SET col_int = 1 where col_int = :value")
    void update_not_found(int value1);

    @JdbcUpdate("UPDATE db_types SET col_int = 1 where col_int = :value")
    void update_sql_missing(int value, int other);

}
