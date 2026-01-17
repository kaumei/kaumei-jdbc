/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcIterable;
import io.kaumei.jdbc.JdbcResultSet;
import io.kaumei.jdbc.annotation.JdbcToJava;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ResultSetUtils {

    private ResultSetUtils() {
        // prevent instantiation
    }

    // ------------------------------------------------------------------------

    public static String marks(int count) {
        var sb = new StringBuilder("?");
        sb.append(",?".repeat(Math.max(0, count - 1)));
        return sb.toString();
    }

    // ------------------------------------------------------------------------

    public static <T> List<T> toList(ResultSet rs, JdbcToJava.Row<T> converter) throws SQLException {
        var list = new ArrayList<T>();
        while (rs.next()) {
            list.add(converter.mapRowToJava(rs));
        }
        return Collections.unmodifiableList(list);
    }

    // ------------------------------------------------------------------------

    public static <T> Stream<T> toStream(PreparedStatement stmt, ResultSet rs, JdbcToJava.Row<T> converter) {
        try {
            var iterator = new JdbcIterator<>(rs, converter);
            var characteristic = Spliterator.IMMUTABLE | Spliterator.NONNULL;
            var split = Spliterators.spliteratorUnknownSize(iterator, characteristic);
            return StreamSupport.stream(split, false).onClose(() -> JdbcUtils.close(stmt, rs));
        } catch (Exception e) {
            JdbcUtils.close(e, stmt, rs);
            throw e;
        }
    }

    // ------------------------------------------------------------------------

    public static <T> JdbcIterable<T> toJdbcIterable(Statement stmt, ResultSet rs, JdbcToJava.Row<T> converter) {
        return new JdbcIterableImpl<>(stmt, rs, converter);
    }

    // ------------------------------------------------------------------------

    public static <T> JdbcResultSet<T> toJdbcResultSet(Statement stmt, ResultSet rs, JdbcToJava.Row<T> converter) {
        return new JdbcResultSetImpl<>(stmt, rs, converter);
    }

}
