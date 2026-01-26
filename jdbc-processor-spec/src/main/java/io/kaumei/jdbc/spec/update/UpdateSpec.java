/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.update;

import io.kaumei.jdbc.JdbcBatch;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import io.kaumei.jdbc.docs.SimpleExample;
import io.kaumei.jdbc.spec.db.DbCustomer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UpdateSpec {

    @JdbcToJava
    static LocalDateTime fromDB(java.sql.Timestamp ts) {
        return ts.toLocalDateTime();
    }

    @JdbcSelect("SELECT * FROM db_customers WHERE name = :name")
    DbCustomer customers(String name);

    // ------------------------------------------------------------
    @JdbcUpdate("")
    void invalidValue();

    @JdbcUpdate("SELECT 1")
    double invalidReturnType();

    @JdbcUpdate("INSERT INTO db_customers (name,budge,pricing_plan) values (:name,:budge,:plan)")
    void insertAndReturnVoid(String name, Integer budge, SimpleExample.PricingPlan plan);

    @JdbcUpdate("UPDATE db_customers SET budge = :budge WHERE name = :name")
    int updateAndReturnInt(String name, Integer budge);

    @JdbcUpdate("UPDATE db_customers SET budge = :budge WHERE name = :name")
    boolean updateAndReturnBoolean(String name, Integer budge);

    // ------------------------------------------------------------

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("INSERT INTO db_customers (name,budge,pricing_plan) values (:name,:budge,:plan)")
    Long updateGeneratedKeysUnspecific(String name, Integer budge, SimpleExample.PricingPlan plan);

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("INSERT INTO db_customers (name,budge,pricing_plan) values (:name,:budge,:plan)")
    @NonNull Long returnTypeForGeneratedKeysNonNull(String name, Integer budge, SimpleExample.PricingPlan plan);

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("SELECT 1")
    @Nullable Long invalidReturnTypeForGeneratedKeysNullable();

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("SELECT 1")
    Optional<Long> invalidReturnTypeForGeneratedKeysOptional();

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("SELECT 1")
    void invalidReturnTypeForGeneratedKeysVoid();

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("SELECT 1")
    List<Long> invalidReturnTypeForGeneratedKeysCollection();

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
    @JdbcUpdate("SELECT 1")
    JdbcBatch invalidReturnTypeForGeneratedKeysJdbcBatch();

    // ------------------------------------------------------------

    record GeneratedValues(long id, LocalDateTime created) {
    }

    @JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.EXECUTE_QUERY)
    @JdbcUpdate("INSERT INTO db_customers (name,budge) values (:name,:budge) returning id,created")
    GeneratedValues updateExecuteQueryUnspecific(String name, Integer budge);

    // util methods ###########################################################

}
