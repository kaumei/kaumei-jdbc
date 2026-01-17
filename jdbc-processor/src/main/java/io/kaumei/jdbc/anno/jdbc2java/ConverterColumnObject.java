/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.store.SearchKey;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

class ConverterColumnObject extends Jdbc2JavaConverter {

    private final ExecutableElement factoryMethod;
    private final SearchKey jdbcType;
    private final Element methodParent;

    public ConverterColumnObject(TypeMirror type,
                                 ExecutableElement factoryMethod,
                                 SearchKey jdbcType) {
        super(type);
        this.factoryMethod = factoryMethod;
        this.jdbcType = jdbcType;
        this.methodParent = factoryMethod.getEnclosingElement();
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
        builder.addComment("ConverterColumnObject.addColumnByIndex", "type", type(), "optional", optional);
        var tempVarName = tempVarName(localVarName);
        var jdbcToJava = builder.searchJdbc(jdbcType);
        if (jdbcToJava == null || builder.hasErrors()) {
            return;
        }
        jdbcToJava.addColumnByIndex(builder, tempVarName, index, OptionalFlag.NULLABLE);
        if (optional.isNonNull()) {
            builder.beginControlFlow("if($L)", jdbcToJava.nullCheck(tempVarName));
            builder.addThrowColumnWasNull(index);
            builder.endControlFlow();
            builder.addStatement("var $L = $L", localVarName, createValue(tempVarName));
        } else if (optional.isOptionalType()) {
            builder.addStatement("$T<$T> $L = $L ? $T.empty() : $T.of($L)",
                    Optional.class, this.type(),
                    localVarName, jdbcToJava.nullCheck(tempVarName),
                    Optional.class, Optional.class, createValue(tempVarName));
        } else {
            builder.addStatement("var $L = $L ? null : $L", localVarName, jdbcToJava.nullCheck(tempVarName), createValue(tempVarName));
        }
    }

    @Override
    protected void addColumnByName0(KaumeiMethodBodyBuilder builder, String localVarName, String columnName, OptionalFlag optional) {
        builder.addComment("ConverterColumnObject.addColumnByName", "type", type(), "optional", optional);
        var index = ColumnIndex.ofVariable(tempVarName(localVarName), columnName);
        builder.addStatement("var $N = rs.findColumn($S)", index.columnIndexVar(), index.columnName());
        // ----
        var tempVarName = tempVarName(localVarName);
        var jdbcToJava = builder.searchJdbc(this.jdbcType);
        if (jdbcToJava == null || builder.hasErrors()) {
            return;
        }
        jdbcToJava.addColumnByIndex(builder, tempVarName, index, OptionalFlag.NULLABLE);
        if (optional.isNonNull()) {
            builder.beginControlFlow("if($L)", jdbcToJava.nullCheck(tempVarName));
            builder.addThrowColumnWasNull(index);
            builder.endControlFlow();
            builder.addStatement("var $L = $L", localVarName, createValue(tempVarName));
        } else if (optional.isOptionalType()) {
            builder.addStatement("$T<$T> $L = $L ? $T.empty() : $T.of($L)",
                    Optional.class, this.type(),
                    localVarName, jdbcToJava.nullCheck(tempVarName),
                    Optional.class, Optional.class, createValue(tempVarName));
        } else {
            builder.addStatement("var $L = $L ? null : $L", localVarName, jdbcToJava.nullCheck(tempVarName), createValue(tempVarName));
        }

    }

    private CodeBlock createValue(String varName) {
        return switch (this.factoryMethod.getKind()) {
            case METHOD ->
                    CodeBlock.of("$T.$N($L)", this.methodParent, this.factoryMethod.getSimpleName(), varName);
            case CONSTRUCTOR -> CodeBlock.of("new $L($L)", this.methodParent, varName);
            default ->
                    throw new ProcessorException("Unexpected kind: " + factoryMethod.getKind()); // sanity-check
        };
    }

}
