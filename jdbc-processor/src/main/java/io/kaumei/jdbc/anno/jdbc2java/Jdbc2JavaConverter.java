/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.gen.KaumeiMethodBodyBuilder;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.store.Converter;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;

public class Jdbc2JavaConverter extends Converter {

    private boolean cycleDetection;

    protected Jdbc2JavaConverter(TypeMirror type) {
        super(type);
    }

    /**
     * @param type must be @Nullable, because during look in the Store there are situations, where we do not have a type.
     */
    public Jdbc2JavaConverter(@Nullable TypeMirror type, MsgSet messages) {
        super(type, messages);
    }

    // ------------------------------------------------------------------------

    public boolean isColumn() {
        throw new ProcessorException(); // sanity-check
    }

    public CodeBlock nullCheck(String varName) {
        throw new ProcessorException(); // sanity-check
    }

    // ------------------------------------------------------------------------

    // if isColumn == true
    public final void addColumnByIndex(KaumeiMethodBodyBuilder builder, String localVarName, ColumnIndex indexVarName, OptionalFlag optional) {
        try {
            // sanity-check:on
            if (!isColumn()) {
                throw new ProcessorException("Invalid converter: " + this);
            }
            // sanity-check:off
            if (cycleDetection) {
                var msg = Msg.invalidConverter(type(), Msg.cycle());
                builder.addError(msg);
            } else {
                cycleDetection = true;
                addColumnByIndex0(builder, localVarName, indexVarName, optional);
            }
        } finally {
            cycleDetection = false;
        }
    }

    protected void addColumnByIndex0(KaumeiMethodBodyBuilder builder, String localVarName, ColumnIndex indexVarName, OptionalFlag optional) {
        throw new ProcessorException(); // sanity-check
    }

    public final void addColumnByName(KaumeiMethodBodyBuilder builder, String localVarName, String columnName, OptionalFlag optional) {
        try {
            // sanity-check:on
            if (!isColumn()) {
                throw new ProcessorException("Invalid converter: " + this);
            }
            // sanity-check:off
            if (cycleDetection) {
                var msg = Msg.invalidConverter(type(), Msg.cycle());
                builder.addError(msg);
            } else {
                cycleDetection = true;
                addColumnByName0(builder, localVarName, columnName, optional);
            }
        } finally {
            cycleDetection = false;
        }
    }

    protected void addColumnByName0(KaumeiMethodBodyBuilder builder, String localVarName, String columnName, OptionalFlag optional) {
        throw new ProcessorException(); // sanity-check
    }

    // ------------------------------------------------------------------------

    // if isColumn == false
    public final void addResultSetToRow(KaumeiMethodBodyBuilder builder, String localVarName, OptionalFlag optional) {
        try {
            // sanity-check:on
            if (isColumn()) {
                throw new ProcessorException("Invalid converter: " + this);
            }
            // sanity-check:off
            if (cycleDetection) {
                var msg = Msg.invalidConverter(type(), Msg.cycle());
                builder.addError(msg);
            } else {
                cycleDetection = true;
                addResultSetToRow0(builder, localVarName, optional);
            }
        } finally {
            cycleDetection = false;
        }
    }

    protected void addResultSetToRow0(KaumeiMethodBodyBuilder builder, String localVarName, OptionalFlag optional) {
        throw new ProcessorException(); // sanity-check
    }

}
