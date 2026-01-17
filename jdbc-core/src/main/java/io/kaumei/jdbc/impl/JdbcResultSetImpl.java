/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.JdbcResultSet;
import io.kaumei.jdbc.annotation.JdbcToJava;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.util.Objects.requireNonNull;

class JdbcResultSetImpl<T> implements JdbcResultSet<T> {

    private final Statement stmt;
    private final ResultSet rs;
    private final JdbcToJava.Row<T> rowGetter;

    JdbcResultSetImpl(Statement stmt, ResultSet rs, JdbcToJava.Row<T> rowGetter) {
        this.stmt = requireNonNull(stmt);
        this.rs = requireNonNull(rs);
        this.rowGetter = requireNonNull(rowGetter);
    }

    // --------------------------------------------------------------------

    @Override
    public T getRow() {
        try {
            return requireNonNull(this.rowGetter.mapRowToJava(this.rs));
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public @Nullable T getRowOpt() {
        try {
            return this.rowGetter.mapRowToJava(this.rs);
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    // --------------------------------------------------------------------

    @Override
    public void close() {
        JdbcUtils.close(this.stmt, this.rs);
    }

    // --------------------------------------------------------------------

    @Override
    public boolean isAfterLast() {
        try {
            return this.rs.isAfterLast();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return this.rs.isBeforeFirst();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return this.rs.isFirst();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isLast() {
        try {
            return this.rs.isLast();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteRow() {
        try {
            this.rs.deleteRow();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public boolean next() {
        try {
            return this.rs.next();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public boolean previous() {
        try {
            return this.rs.previous();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

}