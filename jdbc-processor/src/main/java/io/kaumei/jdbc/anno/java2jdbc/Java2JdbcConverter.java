/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.java2jdbc;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.store.Converter;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;

public class Java2JdbcConverter extends Converter {

    private boolean cycleDetection;

    Java2JdbcConverter(TypeMirror type) {
        super(type);
    }

    /**
     * @param type must be @Nullable, because during look in the Store there are situations, where we do not have a type.
     */
    public Java2JdbcConverter(@Nullable TypeMirror type, MsgSet messages) {
        super(type, messages);
    }

    // ------------------------------------------------------------------------

    public final void setParameter(KaumeiMethodBodyBuilder builder, String paramName, CodeBlock columnIndex, OptionalFlag optional) {
        try {
            if (cycleDetection) {
                var msg = Msg.invalidParam(paramName, type(), this, Msg.cycle());
                builder.addError(msg);
            } else {
                cycleDetection = true;
                setParameter0(builder, paramName, columnIndex, optional);
            }
        } finally {
            cycleDetection = false;
        }
    }

    protected void setParameter0(KaumeiMethodBodyBuilder builder, String paramName, CodeBlock columnIndex, OptionalFlag optional) {
        throw new ProcessorException(); // sanity-check
    }

}
