/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.general;

import io.kaumei.jdbc.annotation.JdbcConstructorAnnotations;
import io.kaumei.jdbc.annotation.JdbcNative;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@JdbcConstructorAnnotations({Inject.class, Deprecated.class})
@Singleton
public interface General {

    @Named("jdbcSelect")
    @JdbcSelect("select 1")
    int jdbcSelect();

    @Named("jdbcUpdate")
    @JdbcUpdate("update db_customers set name = 'foobar' where id = -1")
    int jdbcUpdate();

    @Named("jdbcNative")
    @JdbcNative
    void jdbcNative();

    @Named("unprocessed")
    void unprocessed();

}
