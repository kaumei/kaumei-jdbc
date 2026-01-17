/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;

class ConverterStaticInvalidSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterStaticInvalidSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterStaticInvalidSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void notStatic() {
        kaumeiThrows(() -> service.notStatic(null))
                .paramInvalidConverter("value", "ConverterStaticInvalid.NotStatic", "must have no parameters");
    }

    @Test
    void notStaticDefault() {
        kaumeiThrows(() -> service.notStaticDefault(null))
                .paramNoConverterFound("value", "ConverterStaticInvalid.NotStaticDefault");
    }

    @Test
    void notVisible() {
        kaumeiThrows(() -> service.notVisible(null))
                .paramInvalidConverter("value", "ConverterStaticInvalid.NotVisible", "Must be visible .public/package..");
    }

    @Test
    void notVisibleDefault() {
        kaumeiThrows(() -> service.notVisibleDefault(null))
                .paramNoConverterFound("value", "ConverterStaticInvalid.NotVisibleDefault");
    }

    @Test
    void wrongParameter() {
        kaumeiThrows(() -> service.wrongParameter(null))
                .paramNoConverterFound("value", "ConverterStaticInvalid.WrongParameter");
    }

    @Test
    void wrongParameterInner() {
        kaumeiThrows(() -> service.wrongParameterInner(null))
                .paramNoConverterFound("value", "ConverterStaticInvalid.WrongParameterInner");
    }

    // @part:spec -------------------------------------------------------------

}
