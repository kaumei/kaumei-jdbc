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
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.store.Converter;
import io.kaumei.jdbc.anno.store.SearchKey;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

class ConverterStaticSimple extends Java2JdbcConverter {
    private final SearchKey searchKey;
    private final Name qualifiedTypeName;
    private final Name methodName;

    ConverterStaticSimple(TypeMirror type, SearchKey searchKey,
                          Name qualifiedTypeName,
                          Name methodName) {
        super(type);
        this.searchKey = Objects.requireNonNull(searchKey);
        this.qualifiedTypeName = Objects.requireNonNull(qualifiedTypeName);
        this.methodName = Objects.requireNonNull(methodName);
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterStaticSimple c
                && Objects.equals(type, c.type)
                && Objects.equals(searchKey, c.searchKey)
                && Objects.equals(qualifiedTypeName, c.qualifiedTypeName)
                && Objects.equals(methodName, c.methodName);
    }

    @Override
    protected void setParameter0(KaumeiMethodBodyBuilder builder, String javaName, CodeBlock columnIndex, OptionalFlag optional) {
        // sanity-check:on
        if (optional.isOptionalType()) {
            throw new ProcessorException("Optional<> not supported");
        }
        // sanity-check:off
        builder.addComment("ConverterObjectSimple", "type", this.type(), "optional", optional);
        var localName = tempVarName(javaName);
        var result = builder.searchJava(searchKey);
        if (result.hasMessages()) {
            builder.addError(Msg.invalidParam(javaName, type(), result, Msg.INVALID_RETURN_TYPE));
        } else if (optional.isNonNull()) {
            if (!this.type().getKind().isPrimitive()) {
                builder.beginControlFlow("if ($L == null)", javaName);
                builder.addStatement(KaumeiLib.throwNullPointerException(javaName));
                builder.endControlFlow();
            }
            builder.addStatement("var $N = $L.$N($L)", localName, this.qualifiedTypeName, this.methodName, javaName);
            result.setParameter(builder, localName, columnIndex, optional);
        } else if (this.type().getKind().isPrimitive()) { // sanity-check
            // optional: param: primary, jdbc: *
            throw new ProcessorException("primitive type must be mandatory"); // sanity-check
        } else if (this.searchKey.type().getKind().isPrimitive()) {
            // optional: param: object, jdbc: primary
            builder.beginControlFlow("if ($L == null)", javaName);
            builder.addStatement(KaumeiLib.setNull(this.searchKey.type(), columnIndex));
            builder.nextControlFlow("else");
            builder.addStatement("var $N = $L.$N($L)", localName, this.qualifiedTypeName, this.methodName, javaName);
            result.setParameter(builder, localName, columnIndex, OptionalFlag.NON_NULL);
            builder.endControlFlow();
        } else {
            // optional: param: object, jdbc: object
            builder.addStatement("var $N = $L == null ? null : $L.$N($L)", localName, javaName, qualifiedTypeName, methodName, javaName);
            result.setParameter(builder, localName, columnIndex, optional);
        }
    }

}
