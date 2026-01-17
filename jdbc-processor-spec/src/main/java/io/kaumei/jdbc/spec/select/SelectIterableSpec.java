/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.JdbcIterable;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface SelectIterableSpec {

    // ------------------------------------------------------------

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcIterable<String> columnUnspecific(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcIterable<@Nullable String> columnNullable(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcIterable<@NonNull String> columnNonNull(String city);

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    JdbcIterable<Optional<String>> columnOptional(String city);

    // ------------------------------------------------------------

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcIterable<DbAddress> rowUnspecific(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcIterable<@Nullable DbAddress> rowNullable(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcIterable<@NonNull DbAddress> rowNonNull(String city);

    @JdbcSelect("SELECT * FROM db_address where city = :city ORDER BY id")
    JdbcIterable<Optional<DbAddress>> rowOptional(String city);

    // ------------------------------------------------------------

    @JdbcName("foobar")
    @JdbcSelect("SELECT *,street as foobar FROM db_address where city = :city ORDER BY id")
    JdbcIterable<String> columnWithJdbcName(String city);

    @JdbcName("foobar")
    @JdbcSelect("SELECT 1")
    JdbcIterable<DbAddress> invalidRowWithJdbcName();

    // ------------------------------------------------------------

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT 1")
    JdbcIterable<String> invalidNoRowsAnnotation();

    // ------------------------------------------------------------

    @JdbcNoMoreRows(JdbcNoMoreRows.Kind.IGNORE)
    @JdbcSelect("SELECT 1")
    JdbcIterable<String> invalidNoMoreRows();

    // util methods ###########################################################

}
