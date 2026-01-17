/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;


class SelectListSpecTest {

    static final String EMPTY = DbAddress.UNKNOWN.city();
    static final String NO_NULLS = DbAddress.HAMBURG_1.city();
    static final String WITH_NULLS = DbAddress.BERLIN_1.city();

    // @formatter:off
    static final String[] NO_NULLS_STRING = new String[]{DbAddress.HAMBURG_1.street(), DbAddress.HAMBURG_2.street(), DbAddress.HAMBURG_3.street()};
    static final Optional<String>[] NO_NULLS_STRING_OPTIONAL = new Optional[]{Optional.ofNullable(DbAddress.HAMBURG_1.street()), Optional.ofNullable(DbAddress.HAMBURG_2.street()), Optional.ofNullable(DbAddress.HAMBURG_3.street())};
    static final String[] WITH_NULLS_STRING = new String[]{DbAddress.BERLIN_1.street(), DbAddress.BERLIN_2.street()};
    static final Optional<String>[] WITH_NULLS_STRING_OPTIONAL = new Optional[]{Optional.ofNullable(DbAddress.BERLIN_1.street()), Optional.ofNullable(DbAddress.BERLIN_2.street())};
    static final DbAddress[] NO_NULLS_ADDRESS = new DbAddress[]{DbAddress.HAMBURG_1, DbAddress.HAMBURG_2, DbAddress.HAMBURG_3};
    static final DbAddress[] WITH_NULLS_ADDRESS = new DbAddress[]{DbAddress.BERLIN_1, DbAddress.BERLIN_2};
    // @formatter:on

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private SelectListSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new SelectListSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    @Test
    void columnUnspecific() {
        assertThat(service.columnUnspecific(EMPTY)).isEmpty();
        assertThat(service.columnUnspecific(NO_NULLS)).containsExactly(NO_NULLS_STRING);
        assertThat(service.columnUnspecific(WITH_NULLS)).containsExactly(WITH_NULLS_STRING);
    }

    @Test
    void columnNullable() {
        assertThat(service.columnNullable(EMPTY)).isEmpty();
        assertThat(service.columnNullable(NO_NULLS)).containsExactly(NO_NULLS_STRING);
        assertThat(service.columnNullable(WITH_NULLS)).containsExactly(WITH_NULLS_STRING);
    }

    @Test
    void columnNonNull() {
        assertThat(service.columnNonNull(EMPTY)).isEmpty();
        assertThat(service.columnNonNull(NO_NULLS)).containsExactly(NO_NULLS_STRING);
        kaumeiThrows(() -> service.columnNonNull(WITH_NULLS)).resultColumnWasNullOnIndex("1");
    }

    @Test
    void columnOptional() {
        assertThat(service.columnOptional(EMPTY)).isEmpty();
        assertThat(service.columnOptional(NO_NULLS)).containsExactly(NO_NULLS_STRING_OPTIONAL);
        assertThat(service.columnOptional(WITH_NULLS)).containsExactly(WITH_NULLS_STRING_OPTIONAL);
    }

    // ------------------------------------------------------------------------
    @Test
    void rowUnspecific() {
        assertThat(service.rowUnspecific(EMPTY)).isEmpty();
        assertThat(service.rowUnspecific(NO_NULLS)).containsExactly(NO_NULLS_ADDRESS);
        assertThat(service.rowUnspecific(WITH_NULLS)).containsExactly(WITH_NULLS_ADDRESS);
    }

    @Test
    void rowNullable() {
        kaumeiThrows(() -> service.rowNullable(EMPTY))
                .annotationProcessError("component must be mandatory or unspecific");
    }

    @Test
    void rowNonNull() {
        assertThat(service.rowNonNull(EMPTY)).isEmpty();
        assertThat(service.rowNonNull(NO_NULLS)).containsExactly(NO_NULLS_ADDRESS);
        assertThat(service.rowNonNull(WITH_NULLS)).containsExactly(WITH_NULLS_ADDRESS);
    }

    @Test
    void rowOptional() {
        kaumeiThrows(() -> service.rowOptional(EMPTY))
                .annotationProcessError("Return row component must be mandatory or unspecific");
    }

    // ------------------------------------------------------------------------
    @Test
    void columnWithJdbcName() {
        assertThat(service.columnWithJdbcName(EMPTY)).isEmpty();
        assertThat(service.columnWithJdbcName(NO_NULLS)).containsExactly(NO_NULLS_STRING);
        assertThat(service.columnWithJdbcName(WITH_NULLS)).containsExactly(WITH_NULLS_STRING);
    }

    @Test
    void invalidRowWithJdbcName() {
        kaumeiThrows(() -> service.invalidRowWithJdbcName())
                .unusedMethodAnnotations(JdbcName.class);
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidNoRowsAnnotation() {
        kaumeiThrows(() -> service.invalidNoRowsAnnotation())
                .unusedMethodAnnotations(JdbcNoRows.class);
    }

    // ------------------------------------------------------------------------
    @Test
    void invalidNoMoreRows() {
        kaumeiThrows(() -> service.invalidNoMoreRows())
                .unusedMethodAnnotations(JdbcNoMoreRows.class);
    }

    // @part:spec -------------------------------------------------------------

}
