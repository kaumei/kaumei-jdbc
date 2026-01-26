/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.store.Converter;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

class ConverterColumnNative extends Jdbc2JavaConverter {

    private final @Nullable Class<?> cls;
    private final String methodName;
    private final boolean isPrimitive;

    public ConverterColumnNative(TypeMirror type, Class<?> cls, String methodName) {
        super(type);
        this.cls = cls == ResultSet.class ? null : cls;
        this.methodName = requireNonNull(methodName);
        this.isPrimitive = this.type().getKind().isPrimitive();
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterColumnNative c
                && Objects.equals(type, c.type)
                && Objects.equals(cls, c.cls)
                && isPrimitive == c.isPrimitive
                && Objects.equals(methodName, c.methodName);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean isColumn() {
        return true;
    }

    @Override
    public CodeBlock nullCheck(String varName) {
        return this.isPrimitive
                ? CodeBlock.of("rs.wasNull()")
                : CodeBlock.of("($L == null || rs.wasNull())", varName);
    }

    @Override
    protected void addColumnByIndex0(KaumeiMethodBodyBuilder builder, String localVarName, ColumnIndex index, OptionalFlag optional) {
        builder.addComment("ConverterColumnNative.addColumnByIndex", "type", type(), "optional", optional);
        if (cls == null) {
            setLocalVar(builder, localVarName, optional, CodeBlock.of("rs.$N($L)", methodName, index.columnIndexVar()));
        } else {
            setLocalVar(builder, localVarName, optional, CodeBlock.of("$T.$N(rs, $L)", cls, methodName, index.columnIndexVar()));
        }
        if (optional.isNonNull()) {
            builder.beginControlFlow("if($L)", nullCheck(localVarName));
            builder.addThrowColumnWasNull(index);
            builder.endControlFlow();
        }
    }

    @Override
    protected void addColumnByName0(KaumeiMethodBodyBuilder builder, String localVarName, String columnName, OptionalFlag optional) {
        builder.addComment("ConverterColumnNative.addColumnByName", "type", type(), "optional", optional);
        if (cls == null) {
            setLocalVar(builder, localVarName, optional, CodeBlock.of("rs.$N($S)", methodName, columnName));
        } else {
            setLocalVar(builder, localVarName, optional, CodeBlock.of("$T.$N(rs, $S)", cls, methodName, columnName));
        }
        if (optional.isNonNull()) {
            builder.beginControlFlow("if($L)", nullCheck(localVarName));
            builder.addStatement("throw new $T($S)", NullPointerException.class, "JDBC column was null on name: " + columnName);
            builder.endControlFlow();
        }
    }

    protected void setLocalVar(KaumeiMethodBodyBuilder builder, String localVarName, OptionalFlag optional, CodeBlock code) {
        if (optional.isOptionalType()) {
            builder.addStatement("var $L = $T.ofNullable($L)", localVarName, Optional.class, code);
        } else {
            builder.addStatement("var $L = $L", localVarName, code);
        }
    }
}
