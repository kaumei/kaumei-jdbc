/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

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

    // ------------------------------------------------------------------------

    @Override
    public boolean isColumn() {
        return false;
    }

    @Override
    protected void addResultSetToRow0(KaumeiMethodBodyBuilder builder, String localVarName) {
        builder.addComment("ConverterRowResultSet.addResultSetToRow", "type", type());
        if (this.isMethod) {
            builder.addStatement("var $L = $T.$N(rs)", localVarName, this.typeElement, this.methodName);
        } else {
            builder.addStatement("var $L = new $T(rs)", localVarName, this.typeElement);
        }
    }

}
