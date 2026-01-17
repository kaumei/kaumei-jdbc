/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.db;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record DbTypes(
        long id,
        byte @Nullable [] col_bytea,
        @Nullable Boolean col_boolean,
        @Nullable Character col_char,
        java.sql.@Nullable Date col_date,
        @Nullable Integer col_int,
        @Nullable BigDecimal col_number,
        @Nullable String col_mood,
        @Nullable String col_text,
        java.sql.@Nullable Timestamp col_timestamp,
        @Nullable String VARCHAR,
        @Nullable UUID UUID) {
}
