/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class ConverterColumnResultSet extends Jdbc2JavaConverter {

    private final Element typeElement;
    private final Name methodName;

    public ConverterColumnResultSet(TypeMirror type, ExecutableElement method) {
        super(type);
        this.typeElement = method.getEnclosingElement();
        this.methodName = method.getSimpleName();
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isColumn() {
        return true;
    }

    @Override
    public CodeBlock nullCheck(String varName) {
        return CodeBlock.of("$L == null", varName);
    }

    @Override
    protected void addColumnByIndex0(KaumeiMethodBodyBuilder builder, String localVarName, ColumnIndex index, OptionalFlag optional) {
        builder.addComment("ConverterColumnResultSet.addColumnByIndex", "type", type(), "optional", optional);
        if (optional.isOptionalType()) {
            builder.addStatement("var $L = $T.ofNullable($T.$N(rs, $L))", localVarName,
                    Optional.class, this.typeElement, this.methodName, index.columnIndexVar());
        } else {
            builder.addStatement("var $L = $T.$N(rs, $L)", localVarName, this.typeElement, this.methodName, index.columnIndexVar());
            if (optional.isNonNull()) {
                builder.beginControlFlow("if($L)", nullCheck(localVarName));
                builder.addThrowColumnWasNull(index);
                builder.endControlFlow();
            }
        }
    }

    @Override
    protected void addColumnByName0(KaumeiMethodBodyBuilder builder, String localVarName, String columnName, OptionalFlag optional) {
        builder.addComment("ConverterColumnResultSet.addColumnByName", "type", type(), "optional", optional);
        var index = ColumnIndex.ofVariable(tempVarName(localVarName), columnName);
        builder.addStatement("var $N = rs.findColumn($S)", index.columnIndexVar(), index.columnName());
        if (optional.isOptionalType()) {
            builder.addStatement("var $L = $T.ofNullable($T.$N(rs, $L))", localVarName,
                    Optional.class, this.typeElement, this.methodName, index.columnIndexVar());
        } else {
            builder.addStatement("var $L = $T.$N(rs, $L)", localVarName, this.typeElement, this.methodName, index.columnIndexVar());
            if (optional.isNonNull()) {
                builder.beginControlFlow("if($L)", nullCheck(localVarName));
                builder.addThrowColumnWasNull(index);
                builder.endControlFlow();
            }
        }
    }

}
