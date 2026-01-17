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

class ConverterJdbcStatement extends Java2JdbcConverter {

    private final boolean isPrimitive;
    private final String methodName;

    ConverterJdbcStatement(TypeMirror type, String methodName) {
        super(type);
        this.isPrimitive = type.getKind().isPrimitive();
        this.methodName = Objects.requireNonNull(methodName);
    }

    @Override
    protected void setParameter0(KaumeiMethodBodyBuilder builder, String javaName, CodeBlock columnIndex, OptionalFlag optional) {
        // sanity-check:on
        if (optional.isOptionalType()) {
            throw new ProcessorException("Optional<> not supported");
        }
        // sanity-check:off
        builder.addComment("ConverterJdbcStatement", "type", this.type(), "optional", optional);
        if (optional.isNonNull()) {
            if (isPrimitive) {
                builder.addStatement("stmt.$N($L, $L)", methodName, columnIndex, javaName);
            } else {
                builder.addStatement("stmt.$N($L, $L)", methodName, columnIndex, KaumeiLib.requireNonNull(javaName));
            }
        } else {
            // sanity-check:on
            if (isPrimitive) {
                throw new ProcessorException("primitive must always be mandatory");
                // sanity-check:off
            } else {
                builder.addStatement("stmt.$N($L, $L)", methodName, columnIndex, javaName);
            }
        }
    }

}
