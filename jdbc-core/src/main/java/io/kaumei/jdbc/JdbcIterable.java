/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import org.jspecify.annotations.Nullable;

import java.util.Iterator;

/**
 * Keep in mind to close this `AutoCloseable`.
 */
public interface JdbcIterable<T> extends AutoCloseable, Iterable<@Nullable T> {

    /**
     * Keep in mind: this will return always the same iterator!
     * <p>
     * This is different to the implementations from the core JDK,
     * which always return a fresh iterator starting from the front
     * <p>
     * The JDK does not specify to have always a fresh iterator.
     */
    Iterator<T> iterator();

    void close(); // remove exception
}