/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JdbcSelect;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ConverterEnumSpec {

    // ------------------------------------------------------------------------
    enum SimpleEnum {
        A, B_C, d_e
    }

    @JdbcSelect("SELECT :value")
    String enumUnspecific(SimpleEnum value);

    @JdbcSelect("SELECT :value")
    String enumNullable(@Nullable SimpleEnum value);

    @JdbcSelect("SELECT :value")
    String enumNonNull(@NonNull SimpleEnum value);

}
