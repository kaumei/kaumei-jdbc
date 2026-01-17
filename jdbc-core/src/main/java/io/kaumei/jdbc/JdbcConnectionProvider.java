/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

// @part:JdbcConnectionProvider
@FunctionalInterface
public interface JdbcConnectionProvider {
    Connection getConnection() throws SQLException;
}
// @part:JdbcConnectionProvider
