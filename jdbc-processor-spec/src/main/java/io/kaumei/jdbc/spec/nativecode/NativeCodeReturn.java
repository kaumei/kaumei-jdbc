/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.annotation.JdbcNative;
import io.kaumei.jdbc.spec.ClassHierarchy.Level01;
import io.kaumei.jdbc.spec.ClassHierarchy.Level02;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.util.Optional;

public interface NativeCodeReturn {

    @JdbcNative
    void returnVoidValid1(String param);

    static void returnVoidValid1(Connection con, String param) {
    }


    @JdbcNative
    void returnVoidValid2(String param);

    static String returnVoidValid2(Connection con, String param) {
        return param;
    }

    @JdbcNative
    int returnVoidInvalid(String param);

    static void returnVoidInvalid(Connection con, String param) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------

    @JdbcNative
    Level01 compatible(Level02 param);

    static Level02 compatible(Connection con, Level02 param) {
        return param;
    }

    @JdbcNative
    Level02 compatibleInvalid(Level01 param);

    static Level01 compatibleInvalid(Connection con, Level01 param) {
        return param;
    }

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    String unspecified_unspecified(String param);

    @JdbcNative(method = "nativeMandatory")
    String unspecified_mandatory(@NonNull String param);

    @JdbcNative(method = "nativeNullable")
    String unspecified_nullable(@Nullable String param);

    @JdbcNative(method = "nativeOptional")
    String unspecified_optional(Optional<String> param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    @NonNull String mandatory_unspecified(String param);

    @JdbcNative(method = "nativeMandatory")
    @NonNull String mandatory_mandatory(@NonNull String param);

    @JdbcNative(method = "nativeNullable")
    @NonNull String mandatory_nullable(@Nullable String param);

    @JdbcNative(method = "nativeOptional")
    @NonNull String mandatory_optional(Optional<String> param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    @Nullable String nullable_unspecified(String param);

    @JdbcNative(method = "nativeMandatory")
    @Nullable String nullable_mandatory(@NonNull String param);

    @JdbcNative(method = "nativeNullable")
    @Nullable String nullable_nullable(@Nullable String param);

    @JdbcNative(method = "nativeOptional")
    @Nullable String nullable_optional(Optional<String> param);

    // ------------------------------------------------------------------------

    @JdbcNative(method = "nativeUnspecified")
    Optional<String> optional_unspecified(String param);

    @JdbcNative(method = "nativeMandatory")
    Optional<String> optional_mandatory(@NonNull String param);

    @JdbcNative(method = "nativeNullable")
    Optional<String> optional_nullable(@Nullable String param);

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
