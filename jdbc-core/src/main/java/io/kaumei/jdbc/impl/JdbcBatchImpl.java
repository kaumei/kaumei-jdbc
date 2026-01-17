/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcBatch;
import io.kaumei.jdbc.JdbcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

public class JdbcBatchImpl implements JdbcBatch {
    private final static int[] EMPTY = new int[0];
    protected final PreparedStatement stmt;
    private final int bachSize;

    // ----- state
    private int countBatch;
    private int countAll;

    public JdbcBatchImpl(PreparedStatement stmt, int bachSize) {
        this.stmt = requireNonNull(stmt);
        this.bachSize = bachSize;
    }

    protected int[] addBatch() throws SQLException {
        this.stmt.addBatch();
        this.countBatch++;
        if (this.countBatch >= this.bachSize) {
            return executeBatch0();
        }
        return EMPTY;
    }

    // ------------------------------------------------------------------------

    @Override
    public int bachSize() {
        return this.bachSize;
    }

    @Override
    public int countBatch() {
        return this.countBatch;
    }

    @Override
    public int countAll() {
        return this.countAll;
    }

    @Override
    public void clearParameters() {
        try {
            this.stmt.clearParameters();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public void clearBatch() {
        try {
            this.stmt.clearBatch();
            this.countBatch = 0;
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    @Override
    public int[] executeBatch() {
        try {
            return this.executeBatch0();
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

    private int[] executeBatch0() throws SQLException {
        if (this.countBatch > 0) {
            this.countAll += this.countBatch;
            this.countBatch = 0;
            return this.stmt.executeBatch();
        }
        return EMPTY;
    }

    @Override
    public void close() {
        Exception ex = null;
        try {
            this.executeBatch0();
        } catch (Exception e) {
            ex = e;
        }
        try {
            stmt.close();
        } catch (Exception e) {
            if (ex == null) {
                ex = e;
            } else {
                ex.addSuppressed(e);
            }
        }
        if (ex != null) {
            throw ex instanceof RuntimeException re ? re : new JdbcException(ex.getMessage(), ex);
        }
    }

}