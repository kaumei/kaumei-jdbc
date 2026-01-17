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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTypesAdditionalColumnSpecTest {

    @Mock
    private Connection con;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet resultSet;

    private JdbcTypesAdditionalColumnSpec service;

    @BeforeEach
    void beforeEach() throws SQLException {
        service = new JdbcTypesAdditionalColumnSpecJdbc(() -> con);
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
        boolean value = false;
        when(resultSet.getBoolean(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeBoolean()).isEqualTo(value);
    }

    @Test
    void typeBoolean_null() throws SQLException {
        // given
        when(resultSet.getBoolean(1)).thenReturn(true); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeBoolean()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeByte() throws SQLException {
        // given
        byte value = 2;
        when(resultSet.getByte(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeByte()).isEqualTo(value);
    }

    @Test
    void typeByte_null() throws SQLException {
        // given
        when(resultSet.getByte(1)).thenReturn((byte) -1); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeByte()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeShort() throws SQLException {
        // given
        short value = 2;
        when(resultSet.getShort(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeShort()).isEqualTo(value);
    }

    @Test
    void typeShort_null() throws SQLException {
        // given
        when(resultSet.getShort(1)).thenReturn((short) -1); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeShort()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeInteger() throws SQLException {
        // given
        int value = 2;
        when(resultSet.getInt(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeInteger()).isEqualTo(value);
    }

    @Test
    void typeInteger_null() throws SQLException {
        // given
        when(resultSet.getInt(1)).thenReturn(-1); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeInteger()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeLong() throws SQLException {
        // given
        long value = 2;
        when(resultSet.getLong(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeLong()).isEqualTo(value);
    }

    @Test
    void typeLong_null() throws SQLException {
        // given
        when(resultSet.getLong(1)).thenReturn(1L); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeLong()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeFloat() throws SQLException {
        // given
        float value = 2;
        when(resultSet.getFloat(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeFloat()).isEqualTo(value);
    }

    @Test
    void typeFloat_null() throws SQLException {
        // given
        when(resultSet.getFloat(1)).thenReturn((float) -1); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeFloat()).isNull();
    }

    // ------------------------------------------------------------------------
    @Test
    void typeDouble() throws SQLException {
        // given
        double value = 2;
        when(resultSet.getDouble(1)).thenReturn(value);
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeDouble()).isEqualTo(value);
    }

    @Test
    void typeDouble_null() throws SQLException {
        // given
        when(resultSet.getDouble(1)).thenReturn((double) -1); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeDouble()).isNull();
    }
    // ------------------------------------------------------------------------

    @Test
    void typeChar() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn("2");
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeChar()).isEqualTo('2');
    }

    @Test
    void typeChar_return_wasNull() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn(null); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        kaumeiThrows(() -> service.typeChar())
                .resultColumnWasNullOnIndex("1");
    }

    @Test
    void typeChar_return_to_long() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn("12"); // return value must not be used
        //when(resultSet.wasNull()).thenReturn(false);
        // when
        kaumeiThrows(() -> service.typeChar())
                .jdbcException("JDBC string has wrong length: 2");
    }

    // ------------------------------------------------------------------------

    @Test
    void typeCharacter() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn("2");
        when(resultSet.wasNull()).thenReturn(false);
        // when
        assertThat(service.typeCharacter()).isEqualTo(Character.valueOf('2'));
    }

    @Test
    void typeCharacter_null() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn(null); // return value must not be used
        // when
        assertThat(service.typeCharacter()).isNull();
    }

    @Test
    void typeCharacter_wasNull() throws SQLException {
        // given
        when(resultSet.getString(1)).thenReturn("1"); // return value must not be used
        when(resultSet.wasNull()).thenReturn(true);
        // when
        assertThat(service.typeCharacter()).isNull();
    }
    // @part:spec -------------------------------------------------------------

}
