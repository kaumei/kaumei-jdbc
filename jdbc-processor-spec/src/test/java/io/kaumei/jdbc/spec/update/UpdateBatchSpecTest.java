/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.update;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.annotation.config.JdbcBatchSize;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class UpdateBatchSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private UpdateBatchSpec service;

    @BeforeEach
    void beforeEach() {
        service = new UpdateBatchSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void batchUnspecific() {
        try (var batch = service.batchUnspecific()) {
            assertThat(batch.bachSize()).isEqualTo(1000);
            assertThat(batch.countBatch()).isEqualTo(0);
            assertThat(batch.countAll()).isEqualTo(0);

            for (int i = 1; i < batch.bachSize(); i++) {
                batch.insertAndReturnVoid("a", i);
            }
            assertThat(service.customers("a")).hasSize(0);
            batch.insertAndReturnVoid("a", 0);
            assertThat(service.customers("a")).hasSize(batch.bachSize());
        }
    }

    @Test
    void batchNonNull() {
        try (var batch = service.batchNonNull()) {
            assertThat(batch.bachSize()).isEqualTo(1000);
            assertThat(batch.countBatch()).isEqualTo(0);
            assertThat(batch.countAll()).isEqualTo(0);

            for (int i = 1; i < batch.bachSize(); i++) {
                batch.insertAndReturnVoid("a", i);
            }
            assertThat(service.customers("a")).hasSize(0);
            batch.insertAndReturnVoid("a", 0);
            assertThat(service.customers("a")).hasSize(batch.bachSize());
        }
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidBatchNullable() {
        kaumeiThrows(() -> service.invalidBatchNullable())
                .returnNullnessNotSupported("nullable", "non-null", "unspecific");
    }

    @Test
    void invalidBatchOptional() {
        kaumeiThrows(() -> service.invalidBatchOptional())
                .returnNullnessNotSupported("Optional<.>", "non-null", "unspecific");
    }

    @Test
    void invalidReturnValue() {
        kaumeiThrows(() -> service.invalidReturnValue())
                .returnTypNotSupported("int");
    }

    @Test
    void invalidBatchClass() {
        kaumeiThrows(() -> service.invalidBatchClass())
                .returnTypNotSupported("io.kaumei.jdbc.spec.update.UpdateBatchSpec.BatchClass", "must be an interface");
    }

    // ------------------------------------------------------------------------

    @Test
    void invalidBatchReturnType() {
        try (var batch = service.invalidBatchReturnType()) {
            kaumeiThrows(() -> batch.insertAndReturnBoolean("a", 1))
                    .returnTypNotSupported("boolean", "must be void");
        }
    }

    @Test
    void invalidBatchUpdateAnno() {
        try (var batch = service.invalidBatchUpdateAnno()) {
            kaumeiThrows(batch::update)
                    .annotationProcessError("@JdbcUpdate must provide a SQL string");
        }
    }

    @Test
    void invalidBatchUnusedAnnotation() {
        kaumeiThrows(() -> service.invalidBatchUnusedAnnotation())
                .unusedMethodAnnotations(JdbcQueryTimeout.class);
    }

    @Test
    void invalidBatchNoMethod() {
        kaumeiThrows(() -> service.invalidBatchNoMethod())
                .annotationProcessError("@JdbcUpdateBatch: Batch interface must have exactly one update method");
    }

    @Test
    void invalidBatchTwoMethods() {
        kaumeiThrows(() -> service.invalidBatchTwoMethods())
                .annotationProcessError("@JdbcUpdateBatch: Batch interface must have exactly one update method");
    }

    // ------------------------------------------------------------------------

    @Test
    void jdbcBatchSizeAnnotationMethod() {
        try (var batch = service.jdbcBatchSizeAnnotationMethod()) {
            assertThat(batch.bachSize()).isEqualTo(5);
            assertThat(batch.countBatch()).isEqualTo(0);
            assertThat(batch.countAll()).isEqualTo(0);

            for (int i = 1; i < batch.bachSize(); i++) {
                batch.insertAndReturnVoid("a", i);
            }
            assertThat(service.customers("a")).hasSize(0);
            batch.insertAndReturnVoid("a", 0);
            assertThat(service.customers("a")).hasSize(batch.bachSize());
        }
    }

    @Test
    void jdbcBatchSizeAnnotationParam() {
        try (var batch = service.jdbcBatchSizeAnnotationParam(7)) {
            assertThat(batch.bachSize()).isEqualTo(7);
            assertThat(batch.countBatch()).isEqualTo(0);
            assertThat(batch.countAll()).isEqualTo(0);

            for (int i = 1; i < batch.bachSize(); i++) {
                batch.insertAndReturnVoid("a", i);
            }
            assertThat(service.customers("a")).hasSize(0);
            batch.insertAndReturnVoid("a", 0);
            assertThat(service.customers("a")).hasSize(batch.bachSize());
        }
    }

    @Test
    void invalidJdbcBatchSizeWithoutValue() {
        kaumeiThrows(() -> service.invalidJdbcBatchSizeWithoutValue())
                .unusedMethodAnnotations(JdbcBatchSize.class);
    }


    // @part:spec -------------------------------------------------------------

}
