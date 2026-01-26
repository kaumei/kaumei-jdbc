/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.config;

import io.kaumei.jdbc.JdbcBatch;
import io.kaumei.jdbc.annotation.JdbcDebug;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.JdbcUpdateBatch;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import io.kaumei.jdbc.docs.SimpleExample;

import java.util.List;
import java.util.stream.Stream;

@JdbcQueryTimeout
public interface StatementInvalidSpec {

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT count(*) FROM db_customers WHERE name = :name")
    int select(String name);

    @JdbcSelect("SELECT id FROM db_customers WHERE name = :name")
    List<Integer> selectList(String name);

    @JdbcSelect("SELECT id FROM db_customers WHERE name = :name")
    Stream<Integer> selectStream(String name);

    @JdbcDebug
    @JdbcUpdate("INSERT INTO db_customers (name,pricing_plan) values (:name,:plan)")
    int insert(String name, SimpleExample.PricingPlan plan);

    @JdbcUpdateBatch()
    Batch batch();

    interface Batch extends JdbcBatch {
        @JdbcUpdate("INSERT INTO db_customers (name,pricing_plan) values (:name,:plan)")
        void insert(String name, SimpleExample.PricingPlan plan);
    }

    // util methods ###########################################################

}
