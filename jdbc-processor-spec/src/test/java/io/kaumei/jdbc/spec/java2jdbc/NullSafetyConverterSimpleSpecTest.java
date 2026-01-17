/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.common.*;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.common.WithString01.WITH_STRING_1_FOOBAR;
import static io.kaumei.jdbc.spec.common.WithString01.WITH_STRING_1_NULL;
import static io.kaumei.jdbc.spec.java2jdbc.NullSafetyConverterSimpleSpec.NullSafetyLong;
import static io.kaumei.jdbc.spec.java2jdbc.NullSafetyConverterSimpleSpec.NullSafetyString;
import static org.assertj.core.api.Assertions.assertThat;

class NullSafetyConverterSimpleSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NullSafetyConverterSimpleSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new NullSafetyConverterSimpleSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void paramUnspecific() {
        assertThat(service.paramUnspecific(null)).isNull();
        assertThat(service.paramUnspecific(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramUnspecific(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    @Test
    void paramNullable() {
        assertThat(service.paramNullable(null)).isNull();
        assertThat(service.paramNullable(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramNullable(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    @Test
    void paramNonNull() {
        kaumeiThrows(() -> service.paramNonNull(null))
                .npe("value");
        assertThat(service.paramNonNull(WITH_STRING_1_FOOBAR)).isEqualTo("foobar");
        assertThat(service.paramNonNull(WITH_STRING_1_NULL)).isEqualTo("value.null");
    }

    @Test
    void paramNonNull_primitive() {
        assertThat(service.paramNonNull_primitive(1)).isEqualTo("1");
    }

    // ------------------------------------------------------------------------

    @Test
    void paramNullableBoolean() {
        assertThat(service.paramNullableBoolean(new WithString02("true"))).isEqualTo(true);
        assertThat(service.paramNullableBoolean(null)).isNull();
    }

    @Test
    void paramNullableByte() {
        assertThat(service.paramNullableByte(new WithString03("3"))).isEqualTo((byte) 3);
        assertThat(service.paramNullableByte(null)).isNull();
    }

    @Test
    void paramNullableShort() {
        assertThat(service.paramNullableShort(new WithString04("4"))).isEqualTo((short) 4);
        assertThat(service.paramNullableShort(null)).isNull();
    }

    @Test
    void paramNullableInt() {
        assertThat(service.paramNullableInt(new WithString05("5"))).isEqualTo(5);
        assertThat(service.paramNullableInt(null)).isNull();
    }

    @Test
    void paramNullableLong() {
        assertThat(service.paramNullableLong(new WithString06("6"))).isEqualTo(6);
        assertThat(service.paramNullableLong(null)).isNull();
    }

    @Test
    void paramNullableChar() {
        assertThat(service.paramNullableChar(new WithString07("1"))).isEqualTo('1');
        assertThat(service.paramNullableChar(null)).isNull();
    }

    @Test
    void paramNullableFloat() {
        assertThat(service.paramNullableFloat(new WithString08("1.2"))).isEqualTo(1.2f);
        assertThat(service.paramNullableFloat(null)).isNull();
    }

    @Test
    void paramNullableDouble() {
        assertThat(service.paramNullableDouble(new WithString09("1.1"))).isEqualTo(1.1D);
        assertThat(service.paramNullableDouble(null)).isNull();
    }

    // ------------------------------------------------------------------------

    @Test
    void nullable_object() {
        assertThat(service.nullable_object(new NullSafetyString("foobar"))).isEqualTo("NullSafety:foobar");
        assertThat(service.nullable_object(new NullSafetyString(null))).isNull();
        assertThat(service.nullable_object(null)).isNull();
    }

    @Test
    void nullable_primitive() {
        var value = new NullSafetyLong(42);
        assertThat(service.nullable_primitive(value)).isEqualTo(42);
        assertThat(service.nullable_primitive(null)).isNull();
    }

    @Test
    void nonnull_object() {
        assertThat(service.nonnull_object(new NullSafetyString("foobar"))).isEqualTo("NullSafety:foobar");
        kaumeiThrows(() -> service.nonnull_object(new NullSafetyString(null)))
                .npe("value");
        kaumeiThrows(() -> service.nonnull_object(null))
                .npe("value");
    }

    @Test
    void nonnull_primitive() {
        assertThat(service.nonnull_primitive(new NullSafetyLong(42))).isEqualTo(42);
        kaumeiThrows(() -> service.nonnull_primitive(null))
                .npe("value");
    }

    // @part:spec -------------------------------------------------------------

}
