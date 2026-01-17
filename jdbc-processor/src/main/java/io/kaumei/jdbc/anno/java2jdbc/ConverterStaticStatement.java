/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.java2jdbc;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.gen.KaumeiLib;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;

import javax.lang.model.type.TypeMirror;
import java.util.Objects;

class ConverterStaticStatement extends Java2JdbcConverter {
    private final String qualifiedTypeName;
    private final String methodName;

    ConverterStaticStatement(TypeMirror type, String qualifiedTypeName, String methodName) {
        super(type);
        this.qualifiedTypeName = Objects.requireNonNull(qualifiedTypeName);
        this.methodName = Objects.requireNonNull(methodName);
    }

    @Override
    protected void setParameter0(KaumeiMethodBodyBuilder builder, String javaName, CodeBlock columnIndex, OptionalFlag optional) {
        // sanity-check:on
        if (optional.isOptionalType()) {
            throw new ProcessorException("Optional<> not supported");
        }
        // sanity-check:off
        builder.addComment("ConverterStaticStatement", "type", this.type(), "optional", optional);
        builder.addStatement("$L.$N(stmt, $L, $L)",
                this.qualifiedTypeName, this.methodName, columnIndex,
                KaumeiLib.nullCheck(optional, this.type(), javaName));
    }

}
