/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.KaumeiAssert;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static io.kaumei.jdbc.spec.jdbc2java.JdbcToJavaLookupSpec.unique;
import static org.assertj.core.api.Assertions.assertThat;


class JdbcToJavaLookupSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private JdbcToJavaLookupSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new JdbcToJavaLookupSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------
    @Test
    void twoConstructors() {
        // when ... then
        assertThat(service.twoConstructors("foobar"))
                .isExactlyInstanceOf(JdbcToJavaLookupSpec.TwoConstructors.class)
                .extracting("value")
                .isEqualTo(unique("foobar", "twoConstructors"));
        assertThat(service.twoConstructors(null)).isNull();
    }

    @Test
    void invalidReturnType() {
        KaumeiAssert.kaumeiThrows(() -> service.invalidReturnType(null))
                .invalidConverter("InvalidReturnType", "Annotation has wrong type: int");
    }

    @Test
    void invalidWithAnnotationName() {
        KaumeiAssert.kaumeiThrows(() -> service.invalidWithAnnotationName(null))
                .invalidConverter("InvalidWithAnnotationName", "Annotation must not have a name");
    }

    @Test
    void invalidClassToManyAnnotatedMethods() {
        kaumeiThrows(() -> service.invalidClassToManyAnnotatedMethods())
                .invalidConverter("InvalidClassToManyAnnotatedMethods", "To many annotations");
    }

    // @part:spec -------------------------------------------------------------

}
