/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.db.DbAddress.*;
import static org.assertj.core.api.Assertions.assertThat;

class CollectionsSpecTest {

    private final static List<Long> LIST_IDS = List.of(HAMBURG_1.id(), HAMBURG_2.id());
    private static List<Long> LIST_IDS_WITH_NULL;

    private final static Long[] ARRAY_IDS = new Long[]{HAMBURG_1.id(), HAMBURG_2.id()};
    private static final Long[] ARRAY_IDS_WITH_NULL = new Long[]{HAMBURG_1.id(), null, HAMBURG_2.id()};

    private final static long[] ARRAY_PRIMITIVE_IDS = new long[]{HAMBURG_1.id(), HAMBURG_2.id()};

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    @BeforeAll
    static void beforeAll() {
        LIST_IDS_WITH_NULL = new ArrayList<>();
        LIST_IDS_WITH_NULL.add(HAMBURG_1.id());
        LIST_IDS_WITH_NULL.add(null);
        LIST_IDS_WITH_NULL.add(HAMBURG_2.id());
    }

    private CollectionsSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new CollectionsSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void arrayParam() {
        assertThat(service.arrayParam(ALL_ID_ARRAY)).containsAll(ALL_ID_LIST);
    }

    @Test
    void listParam() {
        assertThat(service.listParam(ALL_ID_LIST)).containsAll(ALL_ID_LIST);
    }

    @Test
    void arrayAndListMixed() {

        assertThat(service.arrayAndListMixed(LONDON_1.id(), BERLIN_ID_ARRAY, LONDON_2.id(), HAMBURG_ID_LIST, PARIS.id()))
                .containsExactly(HAMBURG_1.id(), HAMBURG_2.id(), HAMBURG_3.id(), BERLIN_1.id(), BERLIN_2.id(), LONDON_1.id(), LONDON_2.id(), PARIS.id());
    }

    // ------------------------------------------------------------------------

    @Test
    void list_unspecified_unspecified() {
        assertThat(service.list_unspecified_unspecified(LIST_IDS)).containsAll(LIST_IDS);
        assertThat(service.list_unspecified_unspecified(LIST_IDS_WITH_NULL)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.list_unspecified_unspecified(null)).npe("ids");
    }

    @Test
    void list_unspecified_nullable() {
        kaumeiThrows(() -> service.array_unspecified_nullable(ARRAY_IDS))
                .annotationProcessError("Collection must not be optional");
        //assertThat(service.list_unspecified_nullable(LIST_IDS)).containsAll(LIST_IDS);
        //assertThat(service.list_unspecified_nullable(LIST_IDS_WITH_NULL)).containsAll(LIST_IDS);
        //kaumeiThrows(() -> service.list_unspecified_nullable(null)).npe("ids");
    }

    @Test
    void list_unspecified_nonnull() {
        assertThat(service.list_unspecified_nonnull(LIST_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.list_unspecified_nonnull(LIST_IDS_WITH_NULL)).npe("idsItem");
        kaumeiThrows(() -> service.list_unspecified_nonnull(null)).npe("ids");
    }

    @Test
    void list_nullable_unspecified() {
        kaumeiThrows(() -> service.list_nullable_unspecified(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void list_nullable_nullable() {
        kaumeiThrows(() -> service.list_nullable_nullable(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void list_nullable_nonnull() {
        kaumeiThrows(() -> service.list_nullable_nonnull(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void list_nonnull_unspecified() {
        assertThat(service.list_nonnull_unspecified(LIST_IDS)).containsAll(LIST_IDS);
        assertThat(service.list_nonnull_unspecified(LIST_IDS_WITH_NULL)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.list_nonnull_unspecified(null)).npe("ids");
    }

    @Test
    void list_nonnull_nullable() {
        kaumeiThrows(() -> service.array_unspecified_nullable(ARRAY_IDS))
                .annotationProcessError("Collection must not be optional");
        //assertThat(service.list_nonnull_nullable(LIST_IDS)).containsAll(LIST_IDS);
        //assertThat(service.list_nonnull_nullable(LIST_IDS_WITH_NULL)).containsAll(LIST_IDS);
        //kaumeiThrows(() -> service.list_nonnull_nullable(null)).npe("ids");
    }

    @Test
    void list_nonnull_nonnull() {
        assertThat(service.list_nonnull_nonnull(LIST_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.list_nonnull_nonnull(LIST_IDS_WITH_NULL)).npe("idsItem");
        kaumeiThrows(() -> service.list_nonnull_nonnull(null)).npe("ids");
    }

    @Test
    void list_optional() {
        kaumeiThrows(() -> service.list_optional(null))
                .annotationProcessError("""
                        Optional<T> parameter type not supported.
                        See JavaDoc of Optional:
                        Optional is primarily intended for use as a method return ...
                        """);
    }

    // ------------------------------------------------------------------------

    @Test
    void array_unspecified_unspecified() {
        assertThat(service.array_unspecified_unspecified(ARRAY_IDS)).containsAll(LIST_IDS);
        assertThat(service.array_unspecified_unspecified(ARRAY_IDS_WITH_NULL)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_unspecified_unspecified(null)).npe("ids");
    }

    @Test
    void array_unspecified_nullable() {
        kaumeiThrows(() -> service.array_unspecified_nullable(ARRAY_IDS))
                .annotationProcessError("Collection must not be optional");
        //assertThat(service.array_unspecified_nullable(ARRAY_IDS)).containsAll(LIST_IDS);
        //assertThat(service.array_unspecified_nullable(ARRAY_IDS_WITH_NULL)).containsAll(LIST_IDS);
        //kaumeiThrows(() -> service.array_unspecified_nullable(null)).npe("ids");
    }

    @Test
    void array_unspecified_nonnull() {
        assertThat(service.array_unspecified_nonnull(ARRAY_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_unspecified_nonnull(ARRAY_IDS_WITH_NULL)).npe("idsItem");
        kaumeiThrows(() -> service.array_unspecified_nonnull(null)).npe("ids");
    }

    @Test
    void array_nullable_unspecified() {
        kaumeiThrows(() -> service.array_nullable_unspecified(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_nullable_nullable() {
        kaumeiThrows(() -> service.array_nullable_nullable(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_nullable_nonnull() {
        kaumeiThrows(() -> service.array_nullable_nonnull(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_nonnull_unspecified() {
        assertThat(service.array_nonnull_unspecified(ARRAY_IDS)).containsAll(LIST_IDS);
        assertThat(service.array_nonnull_unspecified(ARRAY_IDS_WITH_NULL)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_nonnull_unspecified(null)).npe("ids");
    }

    @Test
    void array_nonnull_nullable() {
        kaumeiThrows(() -> service.array_nonnull_nullable(ARRAY_IDS))
                .annotationProcessError("Collection must not be optional");
        //assertThat(service.array_nonnull_nullable(ARRAY_IDS)).containsAll(LIST_IDS);
        //assertThat(service.array_nonnull_nullable(ARRAY_IDS_WITH_NULL)).containsAll(LIST_IDS);
        //kaumeiThrows(() -> service.array_nonnull_nonnull(null)).npe("ids");
    }

    @Test
    void array_nonnull_nonnull() {
        assertThat(service.array_nonnull_nonnull(ARRAY_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_nonnull_nonnull(ARRAY_IDS_WITH_NULL)).npe("idsItem");
        kaumeiThrows(() -> service.array_nonnull_nonnull(null)).npe("ids");
    }

    // ------------------------------------------------------------------------

    @Test
    void array_primitive_unspecified_unspecified() {
        assertThat(service.array_primitive_unspecified_unspecified(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_unspecified_unspecified(null)).npe("ids");
    }

    @Test
    void array_primitive_unspecified_nullable() {
        assertThat(service.array_primitive_unspecified_nullable(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_unspecified_nullable(null)).npe("ids");
    }

    @Test
    void array_primitive_unspecified_nonnull() {
        assertThat(service.array_primitive_unspecified_nonnull(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_unspecified_nonnull(null)).npe("ids");
    }

    @Test
    void array_primitive_nullable_unspecified() {
        kaumeiThrows(() -> service.array_primitive_nullable_unspecified(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_primitive_nullable_nullable() {
        kaumeiThrows(() -> service.array_primitive_nullable_nullable(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_primitive_nullable_nonnull() {
        kaumeiThrows(() -> service.array_primitive_nullable_nonnull(null))
                .annotationProcessError("Collection must not be optional.");
    }

    @Test
    void array_primitive_nonnull_unspecified() {
        assertThat(service.array_primitive_nonnull_unspecified(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_nonnull_unspecified(null)).npe("ids");
    }

    @Test
    void array_primitive_nonnull_nullable() {
        assertThat(service.array_primitive_nonnull_nullable(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_nonnull_nullable(null)).npe("ids");
    }

    @Test
    void array_primitive_nonnull_nonnull() {
        assertThat(service.array_primitive_nonnull_nonnull(ARRAY_PRIMITIVE_IDS)).containsAll(LIST_IDS);
        kaumeiThrows(() -> service.array_primitive_nonnull_nonnull(null)).npe("ids");
    }

    // ------------------------------------------------------------------------

    @Test
    void array_optional() {
        kaumeiThrows(() -> service.array_optional(null))
                .annotationProcessError("""
                        Optional<T> parameter type not supported.
                        See JavaDoc of Optional:
                        Optional is primarily intended for use as a method return ...
                        """);
    }

    // @part:spec -------------------------------------------------------------

}
