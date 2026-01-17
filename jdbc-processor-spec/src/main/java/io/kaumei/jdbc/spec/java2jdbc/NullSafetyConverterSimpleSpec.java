/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface NullSafetyConverterSimpleSpec {

    // ------------------------------------------------------------------------
    @JavaToJdbc("SimpleSafeNull")
    static String simpleSafeNull(WithString01 value) {
        if (value == null) {
            return "null";
        } else if (value.value() == null) {
            return "value.null";
        }
        return value.value();
    }

    @JavaToJdbc("SimpleSafeNullPrimitive")
    static String simpleSafeNullPrimitive(long value) {
        return Long.toString(value);
    }

    @JdbcSelect("SELECT :value")
    String paramUnspecific(@JdbcConverterName("SimpleSafeNull") WithString01 value);

    @JdbcSelect("SELECT :value")
    String paramNullable(@JdbcConverterName("SimpleSafeNull") @Nullable WithString01 value);

    @JdbcSelect("SELECT :value")
    String paramNonNull(@JdbcConverterName("SimpleSafeNull") @NonNull WithString01 value);

    @JdbcSelect("SELECT :value")
    String paramNonNull_primitive(@JdbcConverterName("SimpleSafeNullPrimitive") long value);

    // ------------------------------------------------------------------------
    static boolean paramNullableBooleanToDB(WithString02 value) {
        return Boolean.parseBoolean(value.value());
    }

    @JdbcSelect("SELECT :value")
    Boolean paramNullableBoolean(@Nullable WithString02 value);

    // ------------------------------------------------------------------------
    static byte paramNullableByteToDB(WithString03 value) {
        return Byte.parseByte(value.value());
    }

    @JdbcSelect("SELECT :value")
    Byte paramNullableByte(@Nullable WithString03 value);

    // ------------------------------------------------------------------------
    static short paramNullableShortToDB(WithString04 value) {
        return Short.parseShort(value.value());
    }

    @JdbcSelect("SELECT :value")
    Short paramNullableShort(@Nullable WithString04 value);

    // ------------------------------------------------------------------------
    static int paramNullableIntToDB(WithString05 value) {
        return Integer.parseInt(value.value());
    }

    @JdbcSelect("SELECT :value")
    Integer paramNullableInt(@Nullable WithString05 value);

    // ------------------------------------------------------------------------
    static long paramNullableLongToDB(WithString06 value) {
        return Long.parseLong(value.value());
    }

    @JdbcSelect("SELECT :value")
    Long paramNullableLong(@Nullable WithString06 value);

    // ------------------------------------------------------------------------
    static char paramNullableCharToDB(WithString07 value) {
        return value.value().charAt(0);
    }

    @JdbcSelect("SELECT :value")
    Character paramNullableChar(@Nullable WithString07 value);

    // ------------------------------------------------------------------------
    static float paramNullableFloatToDB(WithString08 value) {
        return Float.parseFloat(value.value());
    }

    @JdbcSelect("SELECT :value")
    Float paramNullableFloat(@Nullable WithString08 value);

    // ------------------------------------------------------------------------
    static double paramNullableDoubleToDB(WithString09 value) {
        return Double.parseDouble(value.value());
    }

    @JdbcSelect("SELECT :value")
    Double paramNullableDouble(@Nullable WithString09 value);

    // ------------------------------------------------------------------------

    record NullSafetyString(String value) {
        @JavaToJdbc
        String toDB() {
            return value == null ? null : "NullSafety:" + value;
        }
    }

    record NullSafetyLong(long value) {
        @JavaToJdbc
        long toDB() {
            return value;
        }
    }

    @JdbcSelect("SELECT :value")
    String nullable_object(@Nullable NullSafetyString value);

    @JdbcSelect("SELECT :value")
    Long nullable_primitive(@Nullable NullSafetyLong value);

    @JdbcSelect("SELECT :value")
    String nonnull_object(@NonNull NullSafetyString value);

    @JdbcSelect("SELECT :value")
    Long nonnull_primitive(@NonNull NullSafetyLong value);

    // ------------------------------------------------------------------------

}
