/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JdbcSelect;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface NullSafetyValueSpec {

    @JdbcSelect("SELECT :value")
    String paramUnspecified(String value);

    @JdbcSelect("SELECT :value")
    String paramNullable(@Nullable String value);

    @JdbcSelect("SELECT :value")
    String paramNonNull(@NonNull String value);

    // ------------------------------------------------------------------------

    // this is only for tests: Optional should not be used as parameter
    @JdbcSelect("SELECT :value")
    String optional_unspecified(Optional<String> value);

    @JdbcSelect("SELECT :value")
    String optional_nullable(@Nullable Optional<String> value);

    @JdbcSelect("SELECT :value")
    String optional_nonnull(@NonNull Optional<String> value);

}
