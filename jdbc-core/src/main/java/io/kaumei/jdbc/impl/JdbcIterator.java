/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.annotation.JdbcToJava;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

public class JdbcIterator<T> implements Iterator<T> {

    protected final ResultSet rs;
    private final JdbcToJava.Row<T> rowGetter;
    // ----- state
    private @Nullable Boolean hasNextRow;

    JdbcIterator(ResultSet rs, JdbcToJava.Row<T> rowGetter) {
        this.rs = requireNonNull(rs);
        this.rowGetter = requireNonNull(rowGetter);
    }

    @Override
    public boolean hasNext() {
        if (this.hasNextRow == null) {
            try {
                this.hasNextRow = this.rs.next();
            } catch (SQLException e) {
                throw new JdbcException(e.getMessage(), e);
            }
        }
        return this.hasNextRow;
    }

    @Override
    public @Nullable T next() {
        if (hasNext()) {
            this.hasNextRow = null;
            try {
                return this.rowGetter.mapRowToJava(rs);
            } catch (SQLException e) {
                throw new JdbcException(e.getMessage(), e);
            }
        }
        throw new NoSuchElementException();
    }

}