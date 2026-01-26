/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.db;

import io.kaumei.jdbc.annotation.JdbcName;
import io.kaumei.jdbc.docs.SimpleExample;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

public record DbCustomer(
        long id,
        String name,
        @Nullable Integer budge,
        @JdbcName("pricing_plan") SimpleExample.PricingPlan plan,
        @JdbcName("created_at") LocalDateTime created) {
}
