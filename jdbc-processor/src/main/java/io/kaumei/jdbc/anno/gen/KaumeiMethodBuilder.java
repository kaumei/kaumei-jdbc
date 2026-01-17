/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.MethodSpec;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcConverter;
import io.kaumei.jdbc.anno.jdbc2java.Jdbc2JavaConverter;
import io.kaumei.jdbc.anno.store.ConverterSearch;

import javax.lang.model.element.ExecutableElement;

public class KaumeiMethodBuilder {

    private final KaumeiMethodBodyBuilder body;

    KaumeiMethodBuilder(JavaAnnoMessenger logger, ConverterSearch<Java2JdbcConverter> searchJava, ConverterSearch<Jdbc2JavaConverter> searchJdbc) {
        body = new KaumeiMethodBodyBuilder(logger, searchJava, searchJdbc);
    }

    KaumeiMethodBodyBuilder body() {
        return this.body;
    }

    MethodSpec build(GenerateService genService, ExecutableElement method, String comment) {
        return genService.createMethodBuilder(method)
                .addCode(body.build(comment))
                .build();
    }

    // ------------------------------------------------------------------------

}