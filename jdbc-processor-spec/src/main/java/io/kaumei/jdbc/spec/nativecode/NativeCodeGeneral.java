/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.annotation.JdbcNative;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public interface NativeCodeGeneral {

    @JdbcNative
    boolean nativeCode(int id, String someDate);

    static boolean nativeCode(Connection con, int id, String someDate) throws SQLException {
        try (var stmt = con.prepareStatement("INSERT INTO db_types (id, col_bytea) VALUES(?,?)")) {
            stmt.setInt(1, id);
            stmt.setBytes(2, someDate.getBytes(StandardCharsets.UTF_8));
            return stmt.executeUpdate() != 0;
        }
    }

    @JdbcNative(cls = NativeCodeLib.class, method = "specialAgentLoadLob")
    String otherNativeCode(int id);

}
