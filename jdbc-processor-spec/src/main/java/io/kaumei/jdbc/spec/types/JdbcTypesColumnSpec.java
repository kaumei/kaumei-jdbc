/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.types;

import io.kaumei.jdbc.annotation.JdbcSelect;

import java.math.BigDecimal;

public interface JdbcTypesColumnSpec {

    // @formatter:off
    @JdbcSelect("SELECT 1") boolean typeBoolean();
    @JdbcSelect("SELECT 1") byte    typeByte();
    @JdbcSelect("SELECT 1") byte[]  typeByteArray();
    @JdbcSelect("SELECT 1") short   typeShort();
    @JdbcSelect("SELECT 1") int     typeInt();
    @JdbcSelect("SELECT 1") long    typeLong();
    @JdbcSelect("SELECT 1") float   typeFloat();
    @JdbcSelect("SELECT 1") double  typeDouble();

    @JdbcSelect("SELECT 1") BigDecimal typeBigDecimal();
    @JdbcSelect("SELECT 1") String     typeString();

    @JdbcSelect("SELECT 1") java.sql.Date typeSqlDate();
    @JdbcSelect("SELECT 1") java.sql.Time typeSqlTime();
    @JdbcSelect("SELECT 1") java.sql.Timestamp typeSqlTimestamp();
    @JdbcSelect("SELECT 1") java.sql.Struct typeSqlStruct();
    @JdbcSelect("SELECT 1") java.sql.Ref typeSqlRef();
    @JdbcSelect("SELECT 1") java.sql.Blob typeSqlBlob();
    @JdbcSelect("SELECT 1") java.sql.Clob typeSqlClob();
    @JdbcSelect("SELECT 1") java.sql.Array typeSqlArray();
    @JdbcSelect("SELECT 1") java.sql.RowId typeSqlRowId();
    @JdbcSelect("SELECT 1") java.sql.NClob typeSqlNClob();
    @JdbcSelect("SELECT 1") java.sql.SQLXML typeSqlXml();

    @JdbcSelect("SELECT 1") java.net.URL typeNetUrl();
    // @formatter:on
}
