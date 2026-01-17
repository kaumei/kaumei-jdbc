/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

public class JdbcUnexpectedRowException extends JdbcException {

    public JdbcUnexpectedRowException() {
        super("ResultSet has unexpected rows.");
    }

}
