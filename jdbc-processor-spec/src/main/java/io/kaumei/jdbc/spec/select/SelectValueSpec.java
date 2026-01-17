/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.annotation.*;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.spec.common.RecordStringInt;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface SelectValueSpec {

    // ------------------------------------------------------------

    @JdbcSelect("select :value")
    int primitiveType(int value);

    @JdbcName("value1")
    @JdbcSelect("select :value as value1")
    String stringTypeWithNames(String value);

    @JdbcName("value1")
    @JdbcSelect("select 1")
    RecordStringInt invalidTypeWithNames();

    // ------------------------------------------------------------

    @JdbcToJava("SelectValueSpec.uniqueFromDB")
    static String uniqueFromDB(String name) {
        return unique(name, "uniqueFromDB");
    }

    @JdbcSelect("select :value")
    String withNamedParamConverter(@JdbcConverterName("SelectValueSpec.uniqueToDB") String value);

    // ------------------------------------------------------------

    @JavaToJdbc("SelectValueSpec.uniqueToDB")
    static String uniqueToDB(String name) {
        return unique(name, "uniqueToDB");
    }

    @JdbcConverterName("SelectValueSpec.uniqueFromDB")
    @JdbcSelect("select :value")
    String withNamedResultConverter(String value);

    // ------------------------------------------------------------

    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    String defaultConfig(String city);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    String noRowsReturnNullAndUnspecific(String city);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    @Nullable
    String noRowsReturnNullAndNullable(String city);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    @NonNull
    String invalidNoRowsReturnNullAndNonNull(String city);

    @JdbcNoRows(JdbcNoRows.Kind.RETURN_NULL)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    Optional<String> noRowsReturnNullAndOptional(String city);

    // ------------------------------------------------------------

    @JdbcNoRows(JdbcNoRows.Kind.THROW_EXCEPTION)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    String noRowsThrow(String city);

    // ------------------------------------------------------------

    @JdbcNoMoreRows(JdbcNoMoreRows.Kind.IGNORE)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    String noMoreRowsIgnore(String city);

    @JdbcNoMoreRows(JdbcNoMoreRows.Kind.THROW_EXCEPTION)
    @JdbcSelect("SELECT street FROM db_address where city = :city ORDER BY id")
    String noMoreRowsThrow(String city);

    // util methods ###########################################################

    static String unique(@Nullable String value, String context) {
        return value + "_SelectValueSpec_" + context;
    }
}
