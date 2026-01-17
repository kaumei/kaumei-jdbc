/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.annotation.config.*;
import io.kaumei.jdbc.spec.config.StatementSpecJdbc;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class SelectSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();
    private final String CITY = DbAddress.HAMBURG_1.city();

    private SelectSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new SelectSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void invalidReturnTypeVoid() {
        kaumeiThrows(() -> service.invalidReturnTypeVoid())
                .returnTypNotSupported("void");
    }

    // ------------------------------------------------------------

    @Test
    void resultSetTypeAndResultSetConcurrencyMethod() {
        assertThat(service.resultSetTypeAndResultSetConcurrencyMethod(CITY)).containsExactly(3);
        assertSource(StatementSpecJdbc.class)
                .hasClass("StatementSpecJdbc")
                .hasMethod("jdbcQueryTimeoutParameter")
                .bodyContains("timeout == -1")
                .bodyContains("setQueryTimeout(timeout)");
    }

    @Test
    void resultSetTypeAndResultSetConcurrencyParameter() {
        assertThat(service.resultSetTypeAndResultSetConcurrencyParameter(
                JdbcResultSetType.Kind.TYPE_SCROLL_SENSITIVE, JdbcResultSetConcurrency.Kind.CONCUR_UPDATABLE, CITY)).containsExactly(3);
        kaumeiThrows(() -> service.resultSetTypeAndResultSetConcurrencyParameter(
                JdbcResultSetType.Kind.UNSPECIFIED, JdbcResultSetConcurrency.Kind.CONCUR_UPDATABLE, CITY))
                .illegalArgumentException("Invalid value for resultSetType: UNSPECIFIE");
        kaumeiThrows(() -> service.resultSetTypeAndResultSetConcurrencyParameter(
                JdbcResultSetType.Kind.TYPE_FORWARD_ONLY, JdbcResultSetConcurrency.Kind.UNSPECIFIED, CITY))
                .illegalArgumentException("Invalid value for resultSetConcurrency: UNSPECIFIED");
        assertSource(StatementSpecJdbc.class)
                .hasClass("StatementSpecJdbc")
                .hasMethod("jdbcQueryTimeoutParameter")
                .bodyContains("timeout == -1")
                .bodyContains("setQueryTimeout(timeout)");
    }

    @Test
    void invalidSetConcurrencyMethod() {
        kaumeiThrows(() -> service.invalidSetConcurrencyMethod(CITY))
                .annotationProcessError("You must define @JdbcResultSetType and @JdbcResultSetConcurrency");
    }

    @Test
    void invalidJdbcResultSetType() {
        kaumeiThrows(() -> service.invalidJdbcResultSetType(CITY))
                .annotationProcessError("You must define @JdbcResultSetType and @JdbcResultSetConcurrency");
    }

    @Test
    void invalidResultSetTypeAndResultSetConcurrencyMethod() {
        kaumeiThrows(() -> service.invalidResultSetTypeAndResultSetConcurrencyMethod(CITY))
                .unusedMethodAnnotations(JdbcResultSetType.class, JdbcResultSetConcurrency.class);
    }

    @Test
    void invalidResultSetTypeAndResultSetConcurrencyParameter() {
        kaumeiThrows(() -> service.invalidResultSetTypeAndResultSetConcurrencyParameter(
                JdbcResultSetType.Kind.TYPE_FORWARD_ONLY, JdbcResultSetConcurrency.Kind.CONCUR_UPDATABLE, CITY))
                .unusedParameterAnnotations("resultSetType", JdbcResultSetType.class)
                .unusedParameterAnnotations("resultSetConcurrency", JdbcResultSetConcurrency.class);
    }

    // ------------------------------------------------------------------------

    @Test
    void selectJdbcFetchDirectionMethod() {
        kaumeiThrows(() -> service.selectJdbcFetchDirectionMethod(CITY))
                .unusedMethodAnnotations(JdbcFetchDirection.class);
    }

    @Test
    void selectListJdbcFetchDirectionMethod() {
        assertThat(service.selectListJdbcFetchDirectionMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectListJdbcFetchDirectionMethod")
                .bodyContains("stmt.setFetchDirection(JdbcFetchDirection.Kind.FETCH_REVERSE.sqlMagicNumber())");
    }


    @Test
    void selectStreamJdbcFetchDirectionMethod() {
        KaumeiAssert.assertThat(service.selectStreamJdbcFetchDirectionMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectStreamJdbcFetchDirectionMethod")
                .bodyContains("stmt.setFetchDirection(JdbcFetchDirection.Kind.FETCH_REVERSE.sqlMagicNumber())");
    }

    // ------------------------------------------------------------------------
    @Test
    void selectJdbcFetchSizeMethod() {
        kaumeiThrows(() -> service.selectJdbcFetchSizeMethod(CITY))
                .unusedMethodAnnotations(JdbcFetchSize.class);
    }

    @Test
    void selectListJdbcFetchSizeMethod() {
        assertThat(service.selectListJdbcFetchSizeMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectListJdbcFetchSizeMethod")
                .bodyContains("stmt.setFetchSize(10)");
    }


    @Test
    void selectStreamJdbcFetchSizeMethod() {
        KaumeiAssert.assertThat(service.selectStreamJdbcFetchSizeMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectStreamJdbcFetchSizeMethod")
                .bodyContains("stmt.setFetchSize(10)");
    }

    // ------------------------------------------------------------------------
    @Test
    void selectJdbcMaxRowsMethod() {
        kaumeiThrows(() -> service.selectJdbcMaxRowsMethod(CITY))
                .unusedMethodAnnotations(JdbcMaxRows.class);
    }

    @Test
    void selectListJdbcMaxRowsMethod() {
        assertThat(service.selectListJdbcMaxRowsMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectListJdbcMaxRowsMethod")
                .bodyContains("stmt.setMaxRows(10)");
    }


    @Test
    void selectStreamJdbcMaxRowsMethod() {
        KaumeiAssert.assertThat(service.selectStreamJdbcMaxRowsMethod(CITY)).hasSize(3);
        assertSource(service.getClass())
                .hasClass(service.getClass().getSimpleName())
                .hasMethod("selectStreamJdbcMaxRowsMethod")
                .bodyContains("stmt.setMaxRows(10)");
    }

    // @part:spec -------------------------------------------------------------

}
