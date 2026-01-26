/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.store.Converter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;
import java.util.Optional;

class ConverterRowResultSet extends Jdbc2JavaConverter {

    private final boolean isMethod;
    private final Element typeElement;
    private final Name methodName;

    public ConverterRowResultSet(TypeMirror type, ExecutableElement method) {
        super(type);
        this.isMethod = method.getKind() == ElementKind.METHOD;
        this.typeElement = method.getEnclosingElement();
        this.methodName = method.getSimpleName();
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterRowResultSet c
                && Objects.equals(type, c.type)
                && isMethod == c.isMethod
                && Objects.equals(typeElement, c.typeElement)
                && Objects.equals(methodName, c.methodName);
    }
    // ------------------------------------------------------------------------

    @Override
    public boolean isColumn() {
        return false;
    }

    @Override
    protected void addResultSetToRow0(KaumeiMethodBodyBuilder builder, String localVarName, OptionalFlag optional) {
        builder.addComment("ConverterRowResultSet.addResultSetToRow", "type", type());
        if (this.isMethod) {
            if (optional.isOptionalType()) {
                builder.addStatement("var $L = $T.of($T.$N(rs))",
                        localVarName, Optional.class, this.typeElement, this.methodName);
            } else {
                builder.addStatement("var $L = $T.$N(rs)",
                        localVarName, this.typeElement, this.methodName);
            }
        } else {
            if (optional.isOptionalType()) {
                builder.addStatement("var $L = $T.of(new $T(rs))",
                        localVarName, Optional.class, this.typeElement);
            } else {
                builder.addStatement("var $L = new $T(rs)", localVarName, this.typeElement);
            }
        }
    }

}
