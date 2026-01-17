/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

public class JdbcEmptyResultSetException extends JdbcException {

    public JdbcEmptyResultSetException() {
        super("ResultSet has no rows.");
    }

}
