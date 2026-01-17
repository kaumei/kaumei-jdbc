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
import java.sql.SQLException;
import java.sql.Types;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcTypesAdditionalParamSpecTest {

    @Mock
    private Connection con;
    @Mock
    private PreparedStatement stmt;

    private JdbcTypesAdditionalParamSpec service;

    @BeforeEach
    void beforeEach() throws SQLException {
        service = new JdbcTypesAdditionalParamSpecJdbc(() -> con);
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
        boolean value = false;
        // when
        service.typeBoolean(value);
        // then
        verify(stmt).setBoolean(1, value);
    }

    @Test
    void typeBoolean_null() throws SQLException {
        // when
        service.typeBoolean(null);
        // then
        verify(stmt).setNull(1, Types.BOOLEAN);
    }

    // ------------------------------------------------------------------------
    @Test
    void typeByte() throws SQLException {
        // given
        byte value = 2;
        // when
        service.typeByte(value);
        // then
        verify(stmt).setByte(1, value);
    }

    @Test
    void typeByte_null() throws SQLException {
        // when
        service.typeByte(null);
        // then
        verify(stmt).setNull(1, Types.TINYINT);
    }

    // ------------------------------------------------------------------------

    @Test
    void typeShort() throws SQLException {
        // given
        short value = 2;
        // when
        service.typeShort(value);
        // then
        verify(stmt).setShort(1, value);
    }

    @Test
    void typeShort_null() throws SQLException {
        // when
        service.typeShort(null);
        // then
        verify(stmt).setNull(1, Types.SMALLINT);
    }

    // ------------------------------------------------------------------------

    @Test
    void typeInteger() throws SQLException {
        // given
        int value = 2;
        // when
        service.typeInteger(value);
        // then
        verify(stmt).setInt(1, value);
    }

    @Test
    void typeInteger_null() throws SQLException {
        // when
        service.typeInteger(null);
        // then
        verify(stmt).setNull(1, Types.INTEGER);
    }

    // ------------------------------------------------------------------------

    @Test
    void typeLong() throws SQLException {
        // given
        long value = 2;
        // when
        service.typeLong(value);
        // then
        verify(stmt).setLong(1, value);
    }

    @Test
    void typeLong_null() throws SQLException {
        // when
        service.typeLong(null);
        // then
        verify(stmt).setNull(1, Types.BIGINT);
    }

    // ------------------------------------------------------------------------

    @Test
    void typeFloat() throws SQLException {
        // given
        float value = 2;
        // when
        service.typeFloat(value);
        // then
        verify(stmt).setFloat(1, value);
    }

    @Test
    void typeFloat_null() throws SQLException {
        // when
        service.typeFloat(null);
        // then
        verify(stmt).setNull(1, Types.REAL);
    }
    // ------------------------------------------------------------------------

    @Test
    void typeDouble() throws SQLException {
        // given
        double value = 2;
        // when
        service.typeDouble(value);
        // then
        verify(stmt).setDouble(1, value);
    }

    @Test
    void typeDouble_null() throws SQLException {
        // when
        service.typeDouble(null);
        // then
        verify(stmt).setNull(1, Types.DOUBLE);
    }
    // ------------------------------------------------------------------------

    @Test
    void typeChar() throws SQLException {
        // when
        service.typeChar('1');
        // then
        verify(stmt).setString(1, "1");
    }

    // ------------------------------------------------------------------------
    @Test
    void typeCharacter() throws SQLException {
        // when
        service.typeCharacter('1');
        // then
        verify(stmt).setString(1, "1");
    }

    @Test
    void typeCharacter_null() throws SQLException {
        // when
        service.typeCharacter(null);
        // then
        verify(stmt).setNull(1, Types.CHAR);
    }

    // @part:spec -------------------------------------------------------------

}
