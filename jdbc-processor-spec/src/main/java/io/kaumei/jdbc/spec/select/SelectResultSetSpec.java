/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.JdbcResultSet;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.annotation.config.JdbcResultSetConcurrency;
import io.kaumei.jdbc.annotation.config.JdbcResultSetType;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface SelectResultSetSpec {

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    @JdbcResultSetType(JdbcResultSetType.Kind.TYPE_SCROLL_INSENSITIVE)
    @JdbcResultSetConcurrency(JdbcResultSetConcurrency.Kind.CONCUR_UPDATABLE)
    JdbcResultSet<DbAddress> row(String city);

    // ------------------------------------------------------------

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<String> columnUnspecific(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<@Nullable String> columnNullable(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<@NonNull String> columnNonNull(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<Optional<String>> columnOptional(String city);

    // ------------------------------------------------------------

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<DbAddress> rowUnspecific(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<@Nullable DbAddress> rowNullable(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<@NonNull DbAddress> rowNonNull(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<Optional<DbAddress>> rowOptional(String city);

    // ------------------------------------------------------------

    @JdbcName("foobar")
    @JdbcSelect("SELECT *,street as foobar FROM db_address where city = :city ORDER BY id")
    JdbcResultSet<String> columnWithJdbcName(String city);

    @JdbcName("foobar")
    @JdbcSelect("SELECT 1")
    JdbcResultSet<DbAddress> invalidRowWithJdbcName();

    // ------------------------------------------------------------

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT 1")
    JdbcResultSet<String> invalidNoRowsAnnotation();

    // ------------------------------------------------------------

    @JdbcNoMoreRows(JdbcNoMoreRows.Kind.IGNORE)
    @JdbcSelect("SELECT 1")
    JdbcResultSet<String> invalidNoMoreRows();

    // util methods ###########################################################

}
