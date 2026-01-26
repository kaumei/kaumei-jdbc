/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.update;

import io.kaumei.jdbc.JdbcBatch;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.annotation.JdbcUpdateBatch;
import io.kaumei.jdbc.annotation.config.JdbcBatchSize;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import io.kaumei.jdbc.docs.SimpleExample;
import io.kaumei.jdbc.impl.JdbcBatchImpl;
import io.kaumei.jdbc.spec.db.DbCustomer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UpdateBatchSpec {

    @JdbcToJava
    static LocalDateTime fromDB(java.sql.Timestamp ts) {
        return ts.toLocalDateTime();
    }

    @JdbcSelect("SELECT * FROM db_customers WHERE name = :name")
    List<DbCustomer> customers(String name);

    // ------------------------------------------------------------------------

    interface BatchOne extends JdbcBatch {
        @JdbcUpdate("INSERT INTO db_customers (name,budge,pricing_plan) values (:name,:budge,:plan)")
        void insertAndReturnVoid(String name, Integer budge, SimpleExample.PricingPlan plan);
    }

    // ------------------------------------------------------------------------

    @JdbcUpdateBatch
    @NonNull BatchOne batchUnspecific();

    @JdbcUpdateBatch
    @NonNull BatchOne batchNonNull();

    @JdbcUpdateBatch
    @Nullable BatchOne invalidBatchNullable();

    @JdbcUpdateBatch
    Optional<BatchOne> invalidBatchOptional();

    // ------------------------------------------------------------------------

    @JdbcUpdateBatch
    int invalidReturnValue();

    @JdbcUpdateBatch
    BatchClass invalidBatchClass();

    class BatchClass extends JdbcBatchImpl {

        public BatchClass(PreparedStatement stmt, int bachSize) {
            super(stmt, bachSize);
        }
    }

    // ------------------------------------------------------------------------

    @JdbcUpdateBatch
    InvalidBatchReturnType invalidBatchReturnType();

    interface InvalidBatchReturnType extends JdbcBatch {
        @JdbcUpdate("INSERT INTO db_customers (name,budge) values (:name,:budge)")
        boolean insertAndReturnBoolean(String name, Integer budge);
    }

    @JdbcUpdateBatch
    InvalidBatchUpdateAnnotation invalidBatchUpdateAnno();

    interface InvalidBatchUpdateAnnotation extends JdbcBatch {
        @JdbcUpdate("")
        void update();
    }

    @JdbcUpdateBatch
    InvalidBatchUnusedAnnotation invalidBatchUnusedAnnotation();

    interface InvalidBatchUnusedAnnotation extends JdbcBatch {
        @JdbcQueryTimeout(10)
        @JdbcUpdate("INSERT INTO db_customers (name,budge) values (:name,:budge)")
        void insertAndReturnVoid(String name, Integer budge);
    }

    @JdbcUpdateBatch
    InvalidBatchNoMethod invalidBatchNoMethod();

    interface InvalidBatchNoMethod extends JdbcBatch {
        void insertAndReturnVoid(String name, Integer budge);
    }

    @JdbcUpdateBatch
    InvalidBatchTwoMethods invalidBatchTwoMethods();

    interface InvalidBatchTwoMethods extends JdbcBatch {
        @JdbcUpdate("INSERT INTO db_customers (name,budge) values (:name,:budge)")
        void insertAndReturnVoid1(String name, Integer budge);

        @JdbcUpdate("INSERT INTO db_customers (name,budge) values (:name,:budge)")
        void insertAndReturnVoid2(String name, Integer budge);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    @JdbcBatchSize(5)
    @JdbcUpdateBatch
    BatchOne jdbcBatchSizeAnnotationMethod();

    @JdbcUpdateBatch
    BatchOne jdbcBatchSizeAnnotationParam(@JdbcBatchSize int batchSize);

    @JdbcBatchSize
    @JdbcUpdateBatch
    BatchOne invalidJdbcBatchSizeWithoutValue();

    // util methods ###########################################################

}
