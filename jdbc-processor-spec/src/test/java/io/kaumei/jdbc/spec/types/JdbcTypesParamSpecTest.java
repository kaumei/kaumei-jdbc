/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTypesParamSpecTest {

    @Mock
    private Connection con;
    @Mock
    private PreparedStatement stmt;

    private JdbcTypesParamSpec service;

    @BeforeEach
    void beforeEach() throws SQLException {
        service = new JdbcTypesParamSpecJdbc(() -> con);
        when(con.prepareStatement(any())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);
    }

    @AfterEach
    void afterEach() throws SQLException {
        verify(stmt).executeUpdate();
        verify(stmt).close();
        verifyNoMoreInteractions(stmt);
        verify(con).prepareStatement(any());
        verifyNoMoreInteractions(con);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void typeBoolean() throws SQLException {
        // given
        boolean value = true;
        // when
        service.typeBoolean(value);
        // then
        verify(stmt).setBoolean(1, value);
    }

    @Test
    void typeByte() throws SQLException {
        // given
        byte value = 1;
        // when
        service.typeByte(value);
        // then
        verify(stmt).setByte(1, value);
    }


    @Test
    void typeByteArray() throws SQLException {
        typeByteArray(new byte[]{1});
    }

    @Test
    void typeByteArray_null() throws SQLException {
        typeByteArray(null);
    }

    void typeByteArray(byte[] input) throws SQLException {
        // when
        service.typeByteArray(input);
        // then
        verify(stmt).setBytes(1, input);
    }

    @Test
    void typeShort() throws SQLException {
        // given
        short value = 1;
        // when
        service.typeShort(value);
        // then
        verify(stmt).setShort(1, value);
    }

    @Test
    void typeInt() throws SQLException {
        // given
        int value = 1;
        // when
        service.typeInt(value);
        // then
        verify(stmt).setInt(1, value);
    }

    @Test
    void typeLong() throws SQLException {
        // given
        long value = 1;
        // when
        service.typeLong(value);
        // then
        verify(stmt).setLong(1, value);
    }

    @Test
    void typeFloat() throws SQLException {
        // given
        float value = 1;
        // when
        service.typeFloat(value);
        // then
        verify(stmt).setFloat(1, value);
    }

    @Test
    void typeDouble() throws SQLException {
        // given
        double value = 1;
        // when
        service.typeDouble(value);
        // then
        verify(stmt).setDouble(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeBigDecimal() throws SQLException {
        typeBigDecimal(BigDecimal.ONE);
    }

    @Test
    void typeBigDecimal_null() throws SQLException {
        typeBigDecimal(null);
    }

    void typeBigDecimal(BigDecimal value) throws SQLException {
        // when
        service.typeBigDecimal(value);
        // then
        verify(stmt).setBigDecimal(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeString() throws SQLException {
        typeString("foobar");
    }

    @Test
    void typeString_null() throws SQLException {
        typeString(null);
    }

    void typeString(String value) throws SQLException {
        // when
        service.typeString(value);
        // then
        verify(stmt).setString(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlDate() throws SQLException {
        typeSqlDate(mock(java.sql.Date.class));
    }

    @Test
    void typeSqlDate_null() throws SQLException {
        typeSqlDate(null);
    }

    void typeSqlDate(java.sql.Date value) throws SQLException {
        // when
        service.typeSqlDate(value);
        // then
        verify(stmt).setDate(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlTime() throws SQLException {
        typeSqlTime(mock(java.sql.Time.class));
    }

    @Test
    void typeSqlTime_null() throws SQLException {
        typeSqlTime(null);
    }

    void typeSqlTime(java.sql.Time value) throws SQLException {
        // when
        service.typeSqlTime(value);
        // then
        verify(stmt).setTime(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlTimestamp() throws SQLException {
        typeSqlTimestamp(mock(java.sql.Timestamp.class));
    }

    @Test
    void typeSqlTimestamp_null() throws SQLException {
        typeSqlTimestamp(null);
    }

    void typeSqlTimestamp(java.sql.Timestamp value) throws SQLException {
        // when
        service.typeSqlTimestamp(value);
        // then
        verify(stmt).setTimestamp(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlStruct() throws SQLException {
        typeSqlStruct(mock(java.sql.Struct.class));
    }

    @Test
    void typeSqlStruct_null() throws SQLException {
        typeSqlStruct(null);
    }

    void typeSqlStruct(java.sql.Struct value) throws SQLException {
        // when
        service.typeSqlStruct(value);
        // then
        verify(stmt).setObject(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlRef() throws SQLException {
        typeSqlRef(mock(java.sql.Ref.class));
    }

    @Test
    void typeSqlRef_null() throws SQLException {
        typeSqlRef(null);
    }

    void typeSqlRef(java.sql.Ref value) throws SQLException {
        // when
        service.typeSqlRef(value);
        // then
        verify(stmt).setRef(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlBlob() throws SQLException {
        typeSqlBlob(mock(java.sql.Blob.class));
    }

    @Test
    void typeSqlBlob_null() throws SQLException {
        typeSqlBlob(null);
    }

    void typeSqlBlob(java.sql.Blob value) throws SQLException {
        // when
        service.typeSqlBlob(value);
        // then
        verify(stmt).setBlob(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlClob() throws SQLException {
        typeSqlClob(mock(java.sql.Clob.class));
    }

    @Test
    void typeSqlClob_null() throws SQLException {
        typeSqlClob(null);
    }

    void typeSqlClob(java.sql.Clob value) throws SQLException {
        // when
        service.typeSqlClob(value);
        // then
        verify(stmt).setClob(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlArray() throws SQLException {
        typeSqlArray(mock(java.sql.Array.class));
    }

    @Test
    void typeSqlArray_null() throws SQLException {
        typeSqlArray(null);
    }

    void typeSqlArray(java.sql.Array value) throws SQLException {
        // when
        service.typeSqlArray(value);
        // then
        verify(stmt).setArray(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlRowId() throws SQLException {
        typeSqlRowId(mock(java.sql.RowId.class));
    }

    @Test
    void typeSqlRowId_null() throws SQLException {
        typeSqlRowId(null);
    }

    void typeSqlRowId(java.sql.RowId value) throws SQLException {
        // when
        service.typeSqlRowId(value);
        // then
        verify(stmt).setRowId(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlNClob() throws SQLException {
        typeSqlNClob(mock(java.sql.NClob.class));
    }

    @Test
    void typeSqlNClob_null() throws SQLException {
        typeSqlNClob(null);
    }

    void typeSqlNClob(java.sql.NClob value) throws SQLException {
        // when
        service.typeSqlNClob(value);
        // then
        verify(stmt).setNClob(1, value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeSqlXml() throws SQLException {
        typeSqlXml(mock(java.sql.SQLXML.class));
    }

    @Test
    void typeSqlXml_null() throws SQLException {
        typeSqlXml(null);
    }

    void typeSqlXml(java.sql.SQLXML input) throws SQLException {
        // when
        service.typeSqlXml(input);
        // then
        verify(stmt).setSQLXML(1, input);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeNetUrl() throws SQLException {
        typeNetUrl(mock(java.net.URL.class));
    }

    @Test
    void typeNetUrl_null() throws SQLException {
        typeNetUrl(null);
    }

    void typeNetUrl(java.net.URL input) throws SQLException {
        // when
        service.typeNetUrl(input);
        // then
        verify(stmt).setURL(1, input);
    }

    // @part:spec -------------------------------------------------------------

}
