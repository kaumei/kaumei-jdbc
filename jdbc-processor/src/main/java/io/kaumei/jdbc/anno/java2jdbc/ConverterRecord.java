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

class ConverterRecord extends Java2JdbcConverter {
    private final SearchKey searchKey;
    private final Name compName;

    ConverterRecord(TypeMirror type, SearchKey searchKey, Name compName) {
        super(type);
        this.searchKey = Objects.requireNonNull(searchKey);
        this.compName = Objects.requireNonNull(compName);
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterRecord c
                && Objects.equals(type, c.type)
                && Objects.equals(searchKey, c.searchKey)
                && Objects.equals(compName, c.compName);
    }

    @Override
    protected void setParameter0(KaumeiMethodBodyBuilder builder, String javaName, CodeBlock columnIndex, OptionalFlag optional) {
        // sanity-check:on
        if (optional.isOptionalType()) {
            throw new ProcessorException("Optional<> not supported");
        }
        // sanity-check:off
        builder.addComment("ConverterRecord", "type", this.type(), "optional", optional);
        var localName = tempVarName(javaName);
        var result = builder.searchJava(searchKey);
        if (result.hasMessages()) {
            builder.addError(Msg.invalidParam(javaName, type(), result, Msg.RECORD_COMPONENT_MUST_BE_VALID));
        } else if (optional.isNonNull()) {
            if (!this.searchKey.type().getKind().isPrimitive()) {
                builder.beginControlFlow("if ($L == null)", javaName);
                builder.addStatement(KaumeiLib.throwNullPointerException(javaName));
                builder.endControlFlow();
            }
            builder.addStatement("var $N = $L.$N()", localName, javaName, compName);
            result.setParameter(builder, localName, columnIndex, optional);
        } else if (this.searchKey.type().getKind().isPrimitive()) {
            builder.beginControlFlow("if ($L == null)", javaName);
            builder.addStatement(KaumeiLib.setNull(this.searchKey.type(), columnIndex));
            builder.nextControlFlow("else");
            builder.addStatement("var $N = $L.$N()", localName, javaName, compName);
            result.setParameter(builder, localName, columnIndex, OptionalFlag.NON_NULL);
            builder.endControlFlow();
        } else {
            builder.addStatement("var $N = $L == null ? null : $L.$N()", localName, javaName, javaName, compName);
            result.setParameter(builder, localName, columnIndex, optional);
        }
    }

}
