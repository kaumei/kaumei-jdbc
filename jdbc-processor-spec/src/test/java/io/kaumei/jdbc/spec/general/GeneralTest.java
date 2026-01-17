/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.general;

import io.kaumei.jdbc.DatasourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.regex.Pattern;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneralTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private General service;

    @BeforeEach
    void beforeEach() {
        service = new GeneralJdbc(db::getConnection);
    }

    @Test
    void jdbcSelect() {
        assertThat(service.jdbcSelect()).isEqualTo(1);
    }

    @Test
    void jdbcUpdate() {
        assertThat(service.jdbcUpdate()).isEqualTo(0);
    }

    @Test
    void jdbcNative() {
        kaumeiThrows(() -> service.jdbcNative()).annotationProcessError("not found");
    }

    // @part:spec

    /**
     * class annotations are passed to the generated code
     */
    @Test
    void classAnnotation() {
        var value = "value = \"io.kaumei.jdbc.anno.JdbcProcessor\"";
        var date = "date = \"[^\"]+\"";
        var pattern = Pattern.compile("@Generated\\(" + value + ", " + date + "\\)");
        assertSource(GeneralJdbc.class)
                .hasClass("GeneralJdbc")
                .hasAnnotation("@Singleton");
    }

    /**
     * constructor annotations are taken from JdbcConstructorAnnotations
     */
    @Test
    void constructorAnnotation() {
        assertSource(GeneralJdbc.class)
                .hasClass("GeneralJdbc")
                .hasMethod("<init>")
                .hasAnnotations("@Inject", "@Deprecated");
    }

    /**
     * method annotations are passed to the generated code
     */
    @Test
    void methodAnnotation() {
        var cls = assertSource(GeneralJdbc.class).hasClass("GeneralJdbc");
        cls.hasMethod("jdbcSelect").hasAnnotations("@Override", "@Named(\"jdbcSelect\")");
        cls.hasMethod("jdbcUpdate").hasAnnotations("@Override", "@Named(\"jdbcUpdate\")");
        cls.hasMethod("jdbcNative").hasAnnotations("@Override", "@Named(\"jdbcNative\")");
    }

    /**
     * method annotations are not passed for unprocessed methods
     */
    @Test
    void methodAnnotation_for_unprocesses() {
        var cls = assertSource(GeneralJdbc.class).hasClass("GeneralJdbc");
        cls.hasMethod("unprocessed").hasAnnotations("@Override");
    }

    /**
     * unprocessed methods throw "Not processed" exception
     */
    @Test
    void unprocessed() {
        kaumeiThrows(() -> service.unprocessed()).annotationProcessError("Not processed");
    }
    // @part:spec
}
