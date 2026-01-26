/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.store.Converter;
import io.kaumei.jdbc.anno.store.SearchKey;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;
import java.util.Optional;

class ConverterRowObjects extends Jdbc2JavaConverter {

    private final boolean isMethod;
    private final Element typeElement;
    private final Name methodName;
    private final int paramLength;
    private final String[] jdbcNames;
    private final SearchKey[] paramSearchKeys;
    private final String[] paramNames;
    private final OptionalFlag[] isNonnull;

    public ConverterRowObjects(TypeMirror type,
                               ExecutableElement method,
                               OptionalFlag[] isNonnull,
                               String[] jdbcNames,
                               SearchKey[] paramSearchKeys) {
        super(type);
        this.isMethod = method.getKind() == ElementKind.METHOD;
        this.typeElement = method.getEnclosingElement();
        this.methodName = method.getSimpleName();

        this.jdbcNames = jdbcNames;
        this.paramSearchKeys = paramSearchKeys;
        this.isNonnull = isNonnull;
        this.paramLength = method.getParameters().size();
        this.paramNames = new String[paramLength];
        for (int i = 0; i < this.paramLength; i++) {
            this.paramNames[i] = method.getParameters().get(i).getSimpleName().toString();
        }
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterRowObjects c
                && Objects.equals(type, c.type)
                && isMethod == c.isMethod
                && Objects.equals(typeElement, c.typeElement)
                && Objects.equals(methodName, c.methodName)
                && paramLength == c.paramLength
                && Objects.deepEquals(jdbcNames, c.jdbcNames)
                && Objects.deepEquals(paramSearchKeys, c.paramSearchKeys)
                && Objects.deepEquals(paramNames, c.paramNames)
                && Objects.deepEquals(isNonnull, c.isNonnull);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isColumn() {
        return false;
    }

    @Override
    protected void addResultSetToRow0(KaumeiMethodBodyBuilder builder, String localVarName, OptionalFlag optional) {
        builder.addComment("ConverterRowObjects.addResultSetToRow", "type", type());
        CodeBlock.Builder args = CodeBlock.builder();
        for (int i = 0; i < paramLength; i++) {
            var tempVarName = tempVarName(paramNames[i]);
            var jdbcToJava = builder.searchJdbc(paramSearchKeys[i]);
            if (jdbcToJava == null || builder.hasErrors()) {
                continue;
            }
            // TODO: should we add an extra check on jdbcToJava.isColument for better debug output
            jdbcToJava.addColumnByName(builder, tempVarName, this.jdbcNames[i], this.isNonnull[i]);
            if (i == 0) {
                args.add("$L", tempVarName);
            } else {
                args.add(", $L", tempVarName);
            }
        }
        if (builder.hasErrors()) {
            builder.addError(Msg.invalidConverter(this.type()));
        } else if (isMethod) {
            if (optional.isOptionalType()) {
                builder.addStatement("var $L = $T.of($T.$N($L))",
                        localVarName, Optional.class, this.typeElement, this.methodName, args.build());
            } else {
                builder.addStatement("var $L = $T.$N($L)",
                        localVarName, this.typeElement, this.methodName, args.build());
            }
        } else {
            if (optional.isOptionalType()) {
                builder.addStatement("var $L = $T.of(new $T($L))",
                        localVarName, Optional.class, this.typeElement, args.build());
            } else {
                builder.addStatement("var $L = new $T($L)",
                        localVarName, this.typeElement, args.build());
            }
        }
    }
}
