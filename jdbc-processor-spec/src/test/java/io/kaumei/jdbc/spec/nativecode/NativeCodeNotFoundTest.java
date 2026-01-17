/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.DatasourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;

class NativeCodeNotFoundTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeNotFound service;

    @BeforeEach
    void beforeEach() {
        db.executeSqls("DELETE FROM db_types");
        service = new NativeCodeNotFoundJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void noStaticMethodInInterface() {
        kaumeiThrows(() -> service.noStaticMethodInInterface())
                .annotationProcessError("not found");
    }

    @Test
    void noStaticMethodInClass1() {
        kaumeiThrows(() -> service.noStaticMethodInClass1("foobar"))
                .annotationProcessError("not found");
    }

    @Test
    void noStaticMethodInClass2() {
        kaumeiThrows(() -> service.noStaticMethodInClass2("foobar"))
                .annotationProcessError("not found");
    }

    // @part:spec -------------------------------------------------------------

}
