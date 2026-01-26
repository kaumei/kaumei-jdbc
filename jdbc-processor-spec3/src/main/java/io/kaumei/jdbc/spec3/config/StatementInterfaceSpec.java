/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec3.config;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import io.kaumei.jdbc.docs.SimpleExample;
import io.kaumei.jdbc.spec.db.DbCustomer;

import java.time.LocalDateTime;

@JdbcQueryTimeout(11)
public interface StatementInterfaceSpec {
    @JdbcSelect("SELECT * FROM db_customers WHERE id = :id")
    DbCustomer selectById(long id);

    record DbCustomerId(long id) {
    }

    @JdbcUpdate("INSERT INTO db_customers (name,pricing_plan,created_at) values (:name,:plan,:created)")
    @JdbcReturnGeneratedValues()
    DbCustomerId insert(String name, SimpleExample.PricingPlan plan, LocalDateTime created);
}
