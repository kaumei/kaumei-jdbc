/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.select;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.select.SelectValueSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;

class SelectValueSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private SelectValueSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new SelectValueSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void primitiveType() {
        assertThat(service.primitiveType(1)).isEqualTo(1);
    }

    @Test
    void stringTypeWithNames() {
        assertThat(service.stringTypeWithNames("foobar")).isEqualTo("foobar");
    }

    @Test
    void invalidTypeWithNames() {
        kaumeiThrows(() -> service.invalidTypeWithNames())
                .unusedMethodAnnotations(JdbcName.class);
    }

    // ------------------------------------------------------------

    @Test
    void withNamedParamConverter() {
        var value = "foobar";
        assertThat(service.withNamedParamConverter(value)).isEqualTo(unique(value, "uniqueToDB"));
    }

    @Test
    void withNamedResultConverter() {
        var value = "foobar";
        assertThat(service.withNamedResultConverter(value)).isEqualTo(unique(value, "uniqueFromDB"));
    }
    // ------------------------------------------------------------

    private static final String NO_RESULT = DbAddress.UNKNOWN.city();
    private static final String ONE_NULL = DbAddress.MADRID.city();
    private static final String ONE_VALUE = DbAddress.PARIS.city();
    private static final String ONE_RESULT = DbAddress.PARIS.street();
    private static final String TWO_NULL = DbAddress.LONDON_1.city();
    private static final String TWO_VALUE = DbAddress.BERLIN_1.city();
    private static final String TWO_RESULT = DbAddress.BERLIN_1.street();

    @Test
    void defaultConfig() {
        assertThat(service.defaultConfig(ONE_NULL)).isNull();
        assertThat(service.defaultConfig(ONE_VALUE)).isEqualTo(ONE_RESULT);
        kaumeiThrows(() -> service.defaultConfig(NO_RESULT)).noRows();
        kaumeiThrows(() -> service.defaultConfig(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.defaultConfig(TWO_VALUE)).toManyRows();
    }

    @Test
    void noRowsReturnNullAndUnspecific() {
        assertThat(service.noRowsReturnNullAndUnspecific(ONE_NULL)).isNull();
        assertThat(service.noRowsReturnNullAndUnspecific(ONE_VALUE)).isEqualTo(ONE_RESULT);
        assertThat(service.noRowsReturnNullAndUnspecific(NO_RESULT)).isNull();
        kaumeiThrows(() -> service.noRowsReturnNullAndUnspecific(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.noRowsReturnNullAndUnspecific(TWO_VALUE)).toManyRows();
    }

    @Test
    void noRowsReturnNullAndNullable() {
        assertThat(service.noRowsReturnNullAndNullable(ONE_NULL)).isNull();
        assertThat(service.noRowsReturnNullAndNullable(ONE_VALUE)).isEqualTo(ONE_RESULT);
        assertThat(service.noRowsReturnNullAndNullable(NO_RESULT)).isNull();
        kaumeiThrows(() -> service.noRowsReturnNullAndNullable(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.noRowsReturnNullAndNullable(TWO_VALUE)).toManyRows();
    }

    @Test
    void invalidNoRowsReturnNullAndNonNull() {
        kaumeiThrows(() -> service.invalidNoRowsReturnNullAndNonNull(TWO_VALUE))
                .annotationProcessError("@JdbcSelect incompatible: RETURN_NULL and 'non-null'");
    }

    @Test
    void noRowsReturnNullAndOptional() {
        assertThat(service.noRowsReturnNullAndOptional(ONE_NULL)).isEqualTo(Optional.empty());
        assertThat(service.noRowsReturnNullAndOptional(ONE_VALUE)).isEqualTo(Optional.of(ONE_RESULT));
        assertThat(service.noRowsReturnNullAndOptional(NO_RESULT)).isEqualTo(Optional.empty());
        kaumeiThrows(() -> service.noRowsReturnNullAndOptional(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.noRowsReturnNullAndOptional(TWO_VALUE)).toManyRows();
    }

    @Test
    void noRowsThrow() {
        assertThat(service.noRowsThrow(ONE_NULL)).isNull();
        assertThat(service.noRowsThrow(ONE_VALUE)).isEqualTo(ONE_RESULT);
        kaumeiThrows(() -> service.noRowsThrow(NO_RESULT)).noRows();
        kaumeiThrows(() -> service.noRowsThrow(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.noRowsThrow(TWO_VALUE)).toManyRows();
    }

    @Test
    void noMoreRowsIgnore() {
        assertThat(service.noMoreRowsIgnore(ONE_NULL)).isNull();
        assertThat(service.noMoreRowsIgnore(ONE_VALUE)).isEqualTo(ONE_RESULT);
        kaumeiThrows(() -> service.noMoreRowsIgnore(NO_RESULT)).noRows();
        assertThat(service.noMoreRowsIgnore(TWO_NULL)).isNull();
        assertThat(service.noMoreRowsIgnore(TWO_VALUE)).isEqualTo(TWO_RESULT);
    }

    @Test
    void noMoreRowsThrow() {
        assertThat(service.noMoreRowsThrow(ONE_NULL)).isNull();
        assertThat(service.noMoreRowsThrow(ONE_VALUE)).isEqualTo(ONE_RESULT);
        kaumeiThrows(() -> service.noMoreRowsThrow(NO_RESULT)).noRows();
        kaumeiThrows(() -> service.noMoreRowsThrow(TWO_NULL)).toManyRows();
        kaumeiThrows(() -> service.noMoreRowsThrow(TWO_VALUE)).toManyRows();
    }

    // @part:spec -------------------------------------------------------------

}
