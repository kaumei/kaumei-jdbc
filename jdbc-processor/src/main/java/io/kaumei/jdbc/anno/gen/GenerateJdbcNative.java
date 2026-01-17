/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.MethodSpec;
import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.utils.VisibleMethodVisitor;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenerateJdbcNative implements GenerateJdbc {
    // ----- services
    private final GenerateService genService;
    private final JavaAnnoMessenger logger;
    // ------ state
    private final KaumeiBuilder parent;
    private final ExecutableElement method;
    private final KaumeiAnno methodAnnotations;
    private final GenerateService.MethodParameters methodParameters;
    private final KaumeiMethodBuilder methodBuilder;

    GenerateJdbcNative(GenerateService genService, KaumeiBuilder parent, ExecutableElement method, KaumeiAnno methodAnnotations) {
        this.genService = genService;
        this.logger = genService.logger;
        this.parent = parent;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.methodParameters = genService.getParameters(method);
        this.methodBuilder = new KaumeiMethodBuilder(this.logger,
                genService.java2JdbcService.getStoreForElement(method),
                genService.jdbc2JavaService.getStoreForElement(method));
    }

    @Override
    public MethodSpec generateMethod() {
        var body = methodBuilder.body();

        var annoProperties = methodAnnotations.jdbcNative();
        this.logger.debug("---- JdbcNative ----", "iface", this.parent.type(), "method", method, "prop", annoProperties);
        var clsType = annoProperties.cls() == null
                ? (TypeElement) method.getEnclosingElement()
                : annoProperties.cls();
        var methodName = annoProperties.method() == null
                ? method.getSimpleName().toString()
                : annoProperties.method();

        var s = new Search(method, methodName);
        s.visit(clsType);


        if (s.found == null || !s.errors.isEmpty()) {
            body.addError(s.errors);
        } else {
            var otherMethod = s.found;
            body.beginControlFlow("try");
            body.addStatement("var con = supplier.getConnection()");

            List<CodeBlock> args = new ArrayList<>();
            args.add(CodeBlock.of("$L", "con"));
            var size = otherMethod.getParameters().size() - 1;
            for (int i = 0; i < size; i++) {
                var p = method.getParameters().get(i);
                args.add(CodeBlock.of("$L", p.getSimpleName())); // variable name
            }

            if (method.getReturnType().getKind() == TypeKind.VOID) {
                body.addStatement("$T.$L($L)",
                        otherMethod.getEnclosingElement(),
                        otherMethod.getSimpleName(),
                        CodeBlock.join(args, ","));
            } else {
                body.addStatement("return $T.$L($L)",
                        otherMethod.getEnclosingElement(),
                        otherMethod.getSimpleName(),
                        CodeBlock.join(args, ","));
            }

            var checkResult = genService.elements.checkExceptions(method, otherMethod);

            body.nextControlFlow("catch ($T e)", SQLException.class);
            body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
            if (!checkResult.notCovered().isEmpty()) {
                // FIXME: we catch currently to much
                body.nextControlFlow("catch ($T e)", Exception.class);
                body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
            }
            body.endControlFlow();
        }

        methodBuilder.body().processUnused(methodAnnotations, methodParameters);
        return methodBuilder.build(this.genService, this.method, annoProperties.toString());
    }

    class Search extends VisibleMethodVisitor<Void> {
        private final ExecutableElement source;
        private final String methodName;
        @Nullable ExecutableElement found = null;
        MsgSet errors = Msg.NOT_FOUND;

        Search(ExecutableElement source, String methodName) {
            this.source = source;
            this.methodName = methodName;
        }

        @Override
        public Void visitExecutable(ExecutableElement method, Void p) {
            if (methodName.equals(method.getSimpleName().toString())
                    && method.getModifiers().contains(Modifier.STATIC)) {
                if (found == null) {
                    found = method;
                    errors = genService.elements.isCallable(source, method);
                } else {
                    errors = Msg.DUPLICATE_METHOD;
                }
            }
            return null;
        }
    }
}
