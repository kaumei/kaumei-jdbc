/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcIterable;
import io.kaumei.jdbc.annotation.JdbcToJava;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

class JdbcIterableImpl<T> implements JdbcIterable<T> {
    private final Statement stmt;
    private final ResultSet rs;
    private final Iterator<T> iterator;

    // ----- state
    private enum State {OPEN, USED, CLOSED}

    private State state = State.OPEN;

    JdbcIterableImpl(Statement stmt, ResultSet rs, JdbcToJava.Row<T> rowGetter) {
        this.stmt = requireNonNull(stmt);
        this.rs = requireNonNull(rs);
        this.iterator = new JdbcIterator<>(rs, rowGetter);
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() {
        this.state = State.CLOSED;
        JdbcUtils.close(this.stmt, this.rs);
    }

    // --------------------------------------------------------------------

    @Override
    public Iterator<T> iterator() {
        if (this.state != State.OPEN) {
            throw new IllegalStateException("Illegal state: " + state);
        }
        this.state = State.USED;
        return this.iterator;
    }

}