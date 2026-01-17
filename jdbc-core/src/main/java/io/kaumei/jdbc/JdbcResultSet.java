/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import org.jspecify.annotations.Nullable;

/**
 * Keep in mind to close this `AutoCloseable`.
 */
public interface JdbcResultSet<T> extends AutoCloseable {

    void close(); // remove exception

    // ------------------------------------------------------------------------

    T getRow();

    @Nullable
    T getRowOpt();
    //Optional<T> getRowOptional();

    // ------------------------------------------------------------------------

    boolean next();

    boolean isAfterLast();

    boolean isBeforeFirst();

    boolean isFirst();

    boolean isLast();

    void deleteRow();

    boolean previous();

}
