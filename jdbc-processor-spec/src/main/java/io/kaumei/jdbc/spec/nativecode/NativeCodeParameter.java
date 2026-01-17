/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcNative;
import io.kaumei.jdbc.spec.ClassHierarchy.Level01;
import io.kaumei.jdbc.spec.ClassHierarchy.Level02;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.util.Optional;

public interface NativeCodeParameter {

    // ------------------------------------------------------------------------

    @JdbcNative
    String exactParam(int p1, String p2);

    static String exactParam(Connection con, int p1, String p2) {
        return p1 + p2;
    }

    // ------------------------------------------------------------------------

    @JdbcNative
    String lesserParam(int p1, String p2);

    static String lesserParam(Connection con, long p1) {
        return Long.toString(p1);
    }

    // ------------------------------------------------------------------------

    @JdbcNative
    String firstParamInvalid1(String p1);

    static String firstParamInvalid1(org.h2.jdbc.JdbcConnection con, String p1) {
        return p1;
    }

    @JdbcNative
    String firstParamInvalid2(String p1);

    static String firstParamInvalid2(AutoCloseable con, String p1) {
        return p1;
    }

    @JdbcNative
    String firstParamInvalid3(String p1);

    static String firstParamInvalid3(String con, String p1) {
        return p1;
    }

    // ------------------------------------------------------------------------

    @JdbcNative
    Level01 compatible(Level02 p1);

    static Level01 compatible(Connection con, Level01 p1) {
        return p1;
    }

    @JdbcNative
    Level02 compatibleInvalid(Level01 p1);

    @JavaToJdbc
    static Level02 compatibleInvalid(Connection con, Level02 p1) {
        return p1;
    }

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    String unspecified_unspecified(String param);

    @JdbcNative(method = "nativeMandatory")
    @NonNull String unspecified_mandatory(String param);

    @JdbcNative(method = "nativeNullable")
    @Nullable String unspecified_nullable(String param);

    @JdbcNative(method = "nativeOptional")
    Optional<String> unspecified_optional(String param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    String mandatory_unspecified(@NonNull String param);

    @JdbcNative(method = "nativeMandatory")
    @NonNull String mandatory_mandatory(@NonNull String param);

    @JdbcNative(method = "nativeNullable")
    @Nullable String mandatory_nullable(@NonNull String param);

    @JdbcNative(method = "nativeOptional")
    Optional<String> mandatory_optional(@NonNull String param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    String nullable_unspecified(@Nullable String param);

    @JdbcNative(method = "nativeMandatory")
    @NonNull String nullable_mandatory(@Nullable String param);

    @JdbcNative(method = "nativeNullable")
    @Nullable String nullable_nullable(@Nullable String param);

    @JdbcNative(method = "nativeOptional")
    Optional<String> nullable_optional(@Nullable String param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    String optional_unspecified(Optional<String> param);

    @JdbcNative(method = "nativeMandatory")
    @NonNull String optional_mandatory(Optional<String> param);

    @JdbcNative(method = "nativeNullable")
    @Nullable String optional_nullable(Optional<String> param);

    @JdbcNative(method = "nativeOptional")
    Optional<String> optional_optional(Optional<String> param);

    // ------------------------------------------------------------------------

    static String nativeUnspecified(Connection con, String param) {
        return param;
    }

    static @NonNull String nativeMandatory(Connection con, @NonNull String param) {
        return param;
    }

    static @Nullable String nativeNullable(Connection con, @Nullable String param) {
        return param;
    }

    static Optional<String> nativeOptional(Connection con, Optional<String> param) {
        return param;
    }

    // ------------------------------------------------------------------------

}
