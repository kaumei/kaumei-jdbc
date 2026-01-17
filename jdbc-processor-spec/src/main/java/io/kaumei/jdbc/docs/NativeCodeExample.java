/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.docs;

import io.kaumei.jdbc.annotation.JdbcNative;
import io.kaumei.jdbc.impl.ResultSetUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public interface NativeCodeExample {

    // @part:nativeCode
    @JdbcNative
    List<String> getTables(String tableNamePattern);

    static List<String> getTables(Connection con, String tableNamePattern) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        try (var rs0 = md.getTables(null, "PUBLIC", tableNamePattern, new String[]{"TABLE"})) {
            return ResultSetUtils.toList(rs0, (rs) -> {
                return rs.getString("TABLE_TYPE") + ", " +
                        rs.getString("TABLE_CAT") + ", " +
                        rs.getString("TABLE_SCHEM") + ", " +
                        rs.getString("TABLE_NAME");
            });
        }
    }
    // @part:nativeCode

}