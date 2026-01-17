/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.types;

import io.kaumei.jdbc.annotation.JdbcUpdate;

import java.math.BigDecimal;

public interface JdbcTypesParamSpec {

    // @formatter:off
    @JdbcUpdate("UPDATE :value") void typeBoolean(boolean value);
    @JdbcUpdate("UPDATE :value") void typeByte(byte value);
    @JdbcUpdate("UPDATE :value") void typeByteArray(byte[] value);
    @JdbcUpdate("UPDATE :value") void typeShort(short value);
    @JdbcUpdate("UPDATE :value") void typeInt(int value);
    @JdbcUpdate("UPDATE :value") void typeLong(long value);
    @JdbcUpdate("UPDATE :value") void typeFloat(float value);
    @JdbcUpdate("UPDATE :value") void typeDouble(double value);

    @JdbcUpdate("UPDATE :value") void typeBigDecimal(BigDecimal value);
    @JdbcUpdate("UPDATE :value") void typeString(String value);

    @JdbcUpdate("UPDATE :value") void typeSqlDate(java.sql.Date value);
    @JdbcUpdate("UPDATE :value") void typeSqlTime(java.sql.Time value);
    @JdbcUpdate("UPDATE :value") void typeSqlTimestamp(java.sql.Timestamp value);
    @JdbcUpdate("UPDATE :value") void typeSqlStruct(java.sql.Struct value);
    @JdbcUpdate("UPDATE :value") void typeSqlRef(java.sql.Ref value);
    @JdbcUpdate("UPDATE :value") void typeSqlBlob(java.sql.Blob value);
    @JdbcUpdate("UPDATE :value") void typeSqlClob(java.sql.Clob value);
    @JdbcUpdate("UPDATE :value") void typeSqlArray(java.sql.Array value);
    @JdbcUpdate("UPDATE :value") void typeSqlRowId(java.sql.RowId value);
    @JdbcUpdate("UPDATE :value") void typeSqlNClob(java.sql.NClob value);
    @JdbcUpdate("UPDATE :value") void typeSqlXml(java.sql.SQLXML value);

    @JdbcUpdate("SELECT :value") void typeNetUrl(java.net.URL value);
    // @formatter:on
}
