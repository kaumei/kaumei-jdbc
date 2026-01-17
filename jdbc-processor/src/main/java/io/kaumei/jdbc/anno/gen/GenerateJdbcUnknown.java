/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.MethodSpec;
import io.kaumei.jdbc.CodeGenerationException;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;

import javax.lang.model.element.ExecutableElement;

public class GenerateJdbcUnknown implements GenerateJdbc {
    // ----- services
    private final GenerateService genService;
    // ------ state
    private final KaumeiBuilder parent;
    private final ExecutableElement method;
    private final KaumeiAnno methodAnnotations;
    private final KaumeiMethodBuilder methodBuilder;

    GenerateJdbcUnknown(GenerateService genService, KaumeiBuilder parent, ExecutableElement method, KaumeiAnno methodAnnotations) {
        this.genService = genService;
        this.parent = parent;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.methodBuilder = new KaumeiMethodBuilder(genService.logger, genService.java2JdbcService.getStoreForElement(method),
                genService.jdbc2JavaService.getStoreForElement(method));
    }

    @Override
    public MethodSpec generateMethod() {
        return MethodSpec.overriding(method)
                .addStatement("throw new $T($S)", CodeGenerationException.class, "Not processed")
                .build();

    }

}
