/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.kaumei.jdbc.KaumeiAssert.assertThat;
import static io.kaumei.jdbc.spec.select.SelectListSpecTest.*;

class SelectStreamSpecTest {

    static <T> KaumeiAssert.KaumeiThrows kaumeiThrows(Supplier<Stream<T>> shouldRaiseThrowable) {
        return KaumeiAssert.kaumeiThrows(() -> {
            try (var stream = shouldRaiseThrowable.get()) {
                stream.forEach(_ -> {
                });
            }
        });
    }

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private SelectStreamSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new SelectStreamSpecJdbc(db::getConnection);
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
