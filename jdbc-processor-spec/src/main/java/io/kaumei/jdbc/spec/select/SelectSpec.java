/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.config.*;
import io.kaumei.jdbc.spec.db.DbAddress;

import java.util.List;
import java.util.stream.Stream;

public interface SelectSpec {

    // ------------------------------------------------------------

    @JdbcSelect("SELECT 1")
    void invalidReturnTypeVoid();

    // ------------------------------------------------------------------------

    @JdbcResultSetType(JdbcResultSetType.Kind.TYPE_FORWARD_ONLY)
    @JdbcResultSetConcurrency(JdbcResultSetConcurrency.Kind.CONCUR_READ_ONLY)
    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    List<Integer> resultSetTypeAndResultSetConcurrencyMethod(String city);

    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    List<Integer> resultSetTypeAndResultSetConcurrencyParameter(
            @JdbcResultSetType JdbcResultSetType.Kind resultSetType,
            @JdbcResultSetConcurrency JdbcResultSetConcurrency.Kind resultSetConcurrency,
            String city);

    @JdbcResultSetConcurrency(JdbcResultSetConcurrency.Kind.CONCUR_READ_ONLY)
    @JdbcSelect("SELECT :city")
    List<Integer> invalidSetConcurrencyMethod(String city);

    @JdbcResultSetType(JdbcResultSetType.Kind.TYPE_FORWARD_ONLY)
    @JdbcSelect("SELECT :city")
    List<Integer> invalidJdbcResultSetType(String city);

    @JdbcResultSetType(JdbcResultSetType.Kind.TYPE_FORWARD_ONLY)
    @JdbcResultSetConcurrency(JdbcResultSetConcurrency.Kind.CONCUR_READ_ONLY)
    @JdbcSelect("SELECT :city")
    int invalidResultSetTypeAndResultSetConcurrencyMethod(String city);

    @JdbcSelect("SELECT :city")
    int invalidResultSetTypeAndResultSetConcurrencyParameter(
            @JdbcResultSetType JdbcResultSetType.Kind resultSetType,
            @JdbcResultSetConcurrency JdbcResultSetConcurrency.Kind resultSetConcurrency,
            String city);

    // ------------------------------------------------------------------------

    @JdbcFetchDirection(JdbcFetchDirection.Kind.FETCH_REVERSE)
    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    int selectJdbcFetchDirectionMethod(String city);

    @JdbcFetchDirection(JdbcFetchDirection.Kind.FETCH_REVERSE)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    List<DbAddress> selectListJdbcFetchDirectionMethod(String city);

    @JdbcFetchDirection(JdbcFetchDirection.Kind.FETCH_REVERSE)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    Stream<DbAddress> selectStreamJdbcFetchDirectionMethod(String city);

    // ------------------------------------------------------------------------

    @JdbcFetchSize(10)
    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    int selectJdbcFetchSizeMethod(String city);

    @JdbcFetchSize(10)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    List<DbAddress> selectListJdbcFetchSizeMethod(String city);

    @JdbcFetchSize(10)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    Stream<DbAddress> selectStreamJdbcFetchSizeMethod(String city);

    // ------------------------------------------------------------------------

    @JdbcMaxRows(10)
    @JdbcSelect("SELECT count(*) FROM db_address WHERE city = :city")
    int selectJdbcMaxRowsMethod(String city);

    @JdbcMaxRows(10)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    List<DbAddress> selectListJdbcMaxRowsMethod(String city);

    @JdbcMaxRows(10)
    @JdbcSelect("SELECT * FROM db_address WHERE city = :city")
    Stream<DbAddress> selectStreamJdbcMaxRowsMethod(String city);

    // util methods ###########################################################

}
