/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JdbcSelect;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface CollectionsSpec {
    // @formatter:off
    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> arrayParam(long[] ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> listParam(List<Long> ids);

    @JdbcSelect("""
            SELECT * from db_address
            WHERE id = :id1 OR id in (:id2) OR id = :id3 OR id in (:id4) OR id = :id5
            order by id
            """)
    List<Long> arrayAndListMixed(long id1, long[] id2, long id3, List<Long> id4, long id5);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_unspecified_unspecified(List<Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nullable_unspecified(@Nullable List<Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nonnull_unspecified(@NonNull List<Long> ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_unspecified_nullable(List<@Nullable Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nullable_nullable(@Nullable List<@Nullable Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nonnull_nullable(@NonNull List<@Nullable Long> ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_unspecified_nonnull(List<@NonNull Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nullable_nonnull(@Nullable List<@NonNull Long> ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_nonnull_nonnull(@NonNull List<@NonNull Long> ids);

    // ------------------------------------------------------------------------

    // this is only for tests: Optional should not be used as parameter
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> list_optional(List<Optional<Long>> ids);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_unspecified_unspecified(Long[] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_unspecified_nullable(@Nullable Long[] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_unspecified_nonnull(@NonNull Long[] ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nullable_unspecified(Long @Nullable [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nullable_nullable(@Nullable Long @Nullable [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nullable_nonnull(@NonNull Long @Nullable [] ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nonnull_unspecified(Long @NonNull [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nonnull_nullable(@Nullable Long @NonNull [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_nonnull_nonnull(@NonNull Long @NonNull [] ids);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_unspecified_unspecified(long[] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_unspecified_nullable(@Nullable long[] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_unspecified_nonnull(@NonNull long[] ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nullable_unspecified(long @Nullable [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nullable_nullable(@Nullable long @Nullable [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nullable_nonnull(@NonNull long @Nullable [] ids);

    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nonnull_unspecified(long @NonNull [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nonnull_nullable(@Nullable long @NonNull [] ids);
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_primitive_nonnull_nonnull(@NonNull long @NonNull [] ids);

    // ------------------------------------------------------------------------

    // this is only for tests: Optional should not be used as parameter
    @JdbcSelect("SELECT id FROM db_address where id in (:ids) order by id")
    List<Long> array_optional(Optional<Long>[] ids);

    // ------------------------------------------------------------------------
}
