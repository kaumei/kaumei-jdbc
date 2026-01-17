/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import java.sql.Connection;
import java.sql.SQLException;

public class NativeCodeLib {

    // @part:otherNativeCode
    static String specialAgentLoadLob(Connection con, int id) throws SQLException {
        try (var stmt = con.prepareStatement("SELECT col_bytea FROM db_types WHERE id = ?")) {
            stmt.setLong(1, id);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                var data = rs.getBytes("col_bytea");
                if (data.length > 100) {
                    throw new SQLException("bytes is too large");
                }
                return new String(data);
                // we don't care if there are other results in the result set
            }
        }
    }
    // @part:otherNativeCode

    void noStaticMethodInClass(Connection con, String param) {
        throw new AssertionError("Method must not be called from test.");
    }

}
