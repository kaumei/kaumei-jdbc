/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JavaToJdbcConverter extends JavaToJdbc.Marker {

    static void setBoolean(PreparedStatement stmt, int index, @Nullable Boolean value) throws SQLException {
        if (value != null) {
            stmt.setBoolean(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.BOOLEAN);
        }
    }

    static void setByte(PreparedStatement stmt, int index, @Nullable Byte value) throws SQLException {
        if (value != null) {
            stmt.setByte(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.TINYINT);
        }
    }

    static void setShort(PreparedStatement stmt, int index, @Nullable Short value) throws SQLException {
        if (value != null) {
            stmt.setShort(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.SMALLINT);
        }
    }

    static void setInteger(PreparedStatement stmt, int index, @Nullable Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.INTEGER);
        }
    }

    static void setLong(PreparedStatement stmt, int index, @Nullable Long value) throws SQLException {
        if (value != null) {
            stmt.setLong(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.BIGINT);
        }
    }

    static void setFloat(PreparedStatement stmt, int index, @Nullable Float value) throws SQLException {
        if (value != null) {
            stmt.setFloat(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.REAL);
        }
    }

    static void setDouble(PreparedStatement stmt, int index, @Nullable Double value) throws SQLException {
        if (value != null) {
            stmt.setDouble(index, value);
        } else {
            stmt.setNull(index, java.sql.Types.DOUBLE);
        }
    }

    static void setChar(PreparedStatement stmt, int index, char value) throws SQLException {
        stmt.setString(index, Character.toString(value));
    }

    static void setCharacter(PreparedStatement stmt, int index, @Nullable Character value) throws SQLException {
        if (value != null) {
            stmt.setString(index, Character.toString(value));
        } else {
            stmt.setNull(index, java.sql.Types.CHAR);
        }
    }

}
