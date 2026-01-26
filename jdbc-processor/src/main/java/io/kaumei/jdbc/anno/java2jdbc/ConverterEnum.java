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

import javax.lang.model.type.TypeMirror;
import java.util.Objects;

class ConverterEnum extends Java2JdbcConverter {
    private final SearchKey searchKey;

    ConverterEnum(TypeMirror type, SearchKey searchKey) {
        super(type);
        this.searchKey = Objects.requireNonNull(searchKey);
    }

    @Override
    public boolean isSame(Converter o) {
        return o instanceof ConverterEnum c
                && Objects.equals(type, c.type)
                && Objects.equals(searchKey, c.searchKey);
    }

    @Override
    protected void setParameter0(KaumeiMethodBodyBuilder builder, String javaName, CodeBlock columnIndex, OptionalFlag optional) {
        // sanity-check:on
        if (optional.isOptionalType()) {
            throw new ProcessorException("Optional<> not supported");
        }
        // sanity-check:off
        builder.addComment("ConverterEnum ", "type", this.type(), "searchKey", this.searchKey, "optional", optional);
        var localName = tempVarName(javaName);
        var result = builder.searchJava(searchKey);
        if (result.hasMessages()) {
            builder.addError(Msg.invalidParam(javaName, type(), result, Msg.INVALID_ENUM_TYPE));
        } else if (optional.isNonNull()) {
            builder.beginControlFlow("if ($L == null)", javaName);
            builder.addStatement(KaumeiLib.throwNullPointerException(javaName));
            builder.endControlFlow();
            builder.addStatement("var $N = $L.name()", localName, javaName);
            result.setParameter(builder, localName, columnIndex, optional);
        } else {
            builder.addStatement("var $N = $L == null ? null : $L.name()", localName, javaName, javaName);
            result.setParameter(builder, localName, columnIndex, optional);
        }
    }

}
