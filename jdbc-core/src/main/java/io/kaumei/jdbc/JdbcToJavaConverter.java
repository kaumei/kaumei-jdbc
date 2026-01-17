/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import io.kaumei.jdbc.annotation.JdbcToJava;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JdbcToJavaConverter extends JdbcToJava.Marker {

    // ------------------------------------------------------------------------

    // simulate getChar with index like other primitives
    static char getChar(ResultSet rs, int index) throws SQLException {
        var value = rs.getString(index);
        if (value == null) {
            return (char) 0;
        } else if (value.length() != 1) {
            throw new JdbcException("JDBC string has wrong length: " + value.length());
        }
        return value.charAt(0);
    }

    // simulate getChar with name like other primitives
    static char getChar(ResultSet rs, String name) throws SQLException {
        var value = rs.getString(name);
        if (value == null) {
            return (char) 0;
        } else if (value.length() != 1) {
            throw new JdbcException("JDBC string has wrong length: " + value.length());
        }
        return value.charAt(0);
    }

    // ------------------------------------------------------------------------

    static java.sql.@Nullable Struct getSqlStruct(ResultSet rs, int index) throws SQLException {
        return (java.sql.Struct) rs.getObject(index);
    }

    static java.sql.@Nullable Struct getSqlStruct(ResultSet rs, String index) throws SQLException {
        return (java.sql.Struct) rs.getObject(index);
    }

    // ------------------------------------------------------------------------

    static @Nullable Boolean columnToBoolean(ResultSet rs, int index) throws SQLException {
        var value = rs.getBoolean(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Boolean columnToBoolean(ResultSet rs, String name) throws SQLException {
        var value = rs.getBoolean(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Byte columnToByte(ResultSet rs, int index) throws SQLException {
        var value = rs.getByte(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Byte columnToByte(ResultSet rs, String name) throws SQLException {
        var value = rs.getByte(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Short columnToShort(ResultSet rs, int index) throws SQLException {
        var value = rs.getShort(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Short columnToShort(ResultSet rs, String name) throws SQLException {
        var value = rs.getShort(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Integer columnToInteger(ResultSet rs, int index) throws SQLException {
        var value = rs.getInt(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Integer columnToInteger(ResultSet rs, String name) throws SQLException {
        var value = rs.getInt(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Long columnToLong(ResultSet rs, int index) throws SQLException {
        var value = rs.getLong(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Long columnToLong(ResultSet rs, String name) throws SQLException {
        var value = rs.getLong(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Float columnToFloat(ResultSet rs, int index) throws SQLException {
        var value = rs.getFloat(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Float columnToFloat(ResultSet rs, String name) throws SQLException {
        var value = rs.getFloat(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Double columnToDouble(ResultSet rs, int index) throws SQLException {
        var value = rs.getDouble(index);
        return rs.wasNull() ? null : value;
    }

    static @Nullable Double columnToDouble(ResultSet rs, String name) throws SQLException {
        var value = rs.getDouble(name);
        return rs.wasNull() ? null : value;
    }

    // ------------------------------------------------------------------------

    static @Nullable Character columnToCharacter(ResultSet rs, int index) throws SQLException {
        var value = rs.getString(index);
        if (value == null || rs.wasNull()) {
            return null;
        } else if (value.length() != 1) {
            throw new IllegalArgumentException("String has wrong length: " + value.length());
        }
        return value.charAt(0);
    }

    static @Nullable Character columnToCharacter(ResultSet rs, String name) throws SQLException {
        var value = rs.getString(name);
        if (value == null || rs.wasNull()) {
            return null;
        } else if (value.length() != 1) {
            throw new IllegalArgumentException("String has wrong length: " + value.length());
        }
        return value.charAt(0);
    }
}
