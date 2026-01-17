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
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTypesColumnSpecTest {

    @Mock
    private Connection con;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet resultSet;

    private JdbcTypesColumnSpec service;

    @BeforeEach
    void beforeEach() throws SQLException {
        service = new JdbcTypesColumnSpecJdbc(() -> con);
        when(con.prepareStatement(any())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
    }

    @AfterEach
    void afterEach() throws SQLException {
        verify(resultSet).close();
        verifyNoMoreInteractions(resultSet);
        verify(stmt).setFetchSize(anyInt());
        verify(stmt).setMaxRows(anyInt());
        verify(stmt).executeQuery();
        verify(stmt).close();
        verify(con).prepareStatement(any());
        verifyNoMoreInteractions(stmt, con);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void typeBoolean() throws SQLException {
        // given
        boolean value = true;
        when(resultSet.getBoolean(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeBoolean()).isEqualTo(value);
    }

    @Test
    void typeBoolean_null() throws SQLException {
        // given
        when(resultSet.getBoolean(1)).thenReturn(true);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeBoolean())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeByte() throws SQLException {
        // given
        byte value = 1;
        when(resultSet.getByte(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeByte()).isEqualTo(value);
    }

    @Test
    void typeByte_null() throws SQLException {
        // given
        byte value = 1;
        when(resultSet.getByte(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeByte())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeByteArray() throws SQLException {
        // given
        byte[] value = new byte[]{2};
        when(resultSet.getBytes(1)).thenReturn(value);
        // when
        assertThat(service.typeByteArray()).isEqualTo(value);
    }

    @Test
    void typeByteArray_null() throws SQLException {
        // given
        byte[] value = null;
        when(resultSet.getBytes(1)).thenReturn(null);
        // when
        assertThat(service.typeByteArray()).isEqualTo(value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeShort() throws SQLException {
        // given
        short value = 1;
        when(resultSet.getShort(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeShort()).isEqualTo(value);
    }

    @Test
    void typeShort_null() throws SQLException {
        // given
        short value = 1;
        when(resultSet.getShort(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeShort())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeInt() throws SQLException {
        // given
        int value = 1;
        when(resultSet.getInt(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeInt()).isEqualTo(value);
    }

    @Test
    void typeInt_null() throws SQLException {
        // given
        int value = 1;
        when(resultSet.getInt(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeInt())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeLong() throws SQLException {
        // given
        long value = 1;
        when(resultSet.getLong(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeLong()).isEqualTo(value);
    }

    @Test
    void typeLong_null() throws SQLException {
        // given
        long value = 1;
        when(resultSet.getLong(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeLong())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeFloat() throws SQLException {
        // given
        float value = 1;
        when(resultSet.getFloat(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeFloat()).isEqualTo(value);
    }

    @Test
    void typeFloat_null() throws SQLException {
        // given
        float value = 1;
        when(resultSet.getFloat(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeFloat())
                .resultColumnWasNullOnIndex("1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeDouble() throws SQLException {
        // given
        double value = 1;
        when(resultSet.getDouble(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeDouble()).isEqualTo(value);
    }

    @Test
    void typeDouble_null() throws SQLException {
        // given
        double value = 1;
        when(resultSet.getDouble(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeDouble())
                .resultColumnWasNullOnIndex("1");
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
        // given
        when(resultSet.getBigDecimal(1)).thenReturn(value);
        // when
        assertThat(service.typeBigDecimal()).isEqualTo(value);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeString() throws SQLException {
        typeString("bar");
    }

    @Test
    void typeString_null() throws SQLException {
        typeString(null);
    }

    void typeString(String value) throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn(value);
        // when
        assertThat(service.typeString()).isEqualTo(value);
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
        // given
        when(resultSet.getDate(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlDate()).isEqualTo(value);
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
        // given
        when(resultSet.getTime(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlTime()).isEqualTo(value);
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
        // given
        when(resultSet.getTimestamp(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlTimestamp()).isEqualTo(value);
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
        // given
        when(resultSet.getObject(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlStruct()).isEqualTo(value);
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
        // given
        when(resultSet.getRef(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlRef()).isEqualTo(value);
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
        // given
        when(resultSet.getBlob(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlBlob()).isEqualTo(value);
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
        // given
        when(resultSet.getClob(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlClob()).isEqualTo(value);
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
        // given
        when(resultSet.getArray(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlArray()).isEqualTo(value);
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
        // given
        when(resultSet.getRowId(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlRowId()).isEqualTo(value);
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
        // given
        when(resultSet.getNClob(1)).thenReturn(value);

        // when
        assertThat(service.typeSqlNClob()).isEqualTo(value);
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

    void typeSqlXml(java.sql.SQLXML value) throws SQLException {
        // given
        when(resultSet.getSQLXML(1)).thenReturn(value);
        // when
        assertThat(service.typeSqlXml()).isEqualTo(value);
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

    void typeNetUrl(java.net.URL value) throws SQLException {
        // given
        when(resultSet.getURL(1)).thenReturn(value);
        // when
        assertThat(service.typeNetUrl()).isEqualTo(value);
    }

    // @part:spec -------------------------------------------------------------

}
