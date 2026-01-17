/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.db;

import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

public record DbCustomer(
        long id,
        String name,
        @Nullable Integer budge,
        LocalDateTime created) {
}
