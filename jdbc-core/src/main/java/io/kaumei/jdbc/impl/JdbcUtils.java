/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.impl;

import io.kaumei.jdbc.JdbcException;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.Statement;

public final class JdbcUtils {

    private JdbcUtils() {
        // prevent instantiation
    }

    // ------------------------------------------------------------------------

    /**
     * Null safe close rs and then stmt.
     */
    public static void close(@Nullable Statement stmt, @Nullable ResultSet rs) {
        Exception e = null;
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception ex) {
            e = ex;
        }
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (Exception ex) {
            if (e == null) {
                e = ex;
            } else {
                e.addSuppressed(ex);
            }
        }
        if (e != null) {
            throw e instanceof RuntimeException re ? re : new JdbcException(e.getMessage(), e);
        }
    }

    /**
     * Null safe close rs and then stmt.
     */
    public static void close(Exception e, @Nullable Statement stmt, @Nullable ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception ex) {
            e.addSuppressed(ex);
        }
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (Exception ex) {
            e.addSuppressed(ex);
        }
    }

    public static void close(Exception e, @Nullable Statement stmt) {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (Exception ex) {
            e.addSuppressed(ex);
        }
    }

    public static void close(Exception e, @Nullable ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception ex) {
            e.addSuppressed(ex);
        }
    }
}
