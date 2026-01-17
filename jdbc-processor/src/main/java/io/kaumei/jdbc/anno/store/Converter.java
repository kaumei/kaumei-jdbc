/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.msg.MsgSet;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;

import static java.util.Objects.requireNonNull;

public class Converter {

    private static int counter = 0;

    protected static String tempVarName(String varName) {
        int index = varName.indexOf("_jdbc");
        return (index == -1 ? varName : varName.substring(0, index)) + "_jdbc" + (counter++);
    }

    // ------------------------------------------------------------------------

    private final @Nullable TypeMirror type;
    private final MsgSet messages;

    protected Converter(TypeMirror type) {
        this(requireNonNull(type), MsgSet.EMPTY);
    }

    protected Converter(@Nullable TypeMirror type, MsgSet messages) {
        // sanity-check:on
        if (type == null && messages.isEmpty()) {
            throw new ProcessorException();
        }
        // sanity-check:off
        this.type = type;
        this.messages = messages;
    }

    public boolean hasType() {
        return this.type != null;
    }

    public TypeMirror type() {
        // sanity-check:on
        if (this.type == null) {
            throw new ProcessorException();
        }
        // sanity-check:off
        return this.type;
    }

    public boolean hasMessages() {
        return messages.isNotEmpty();
    }

    public MsgSet messages() {
        return messages;
    }

    @Override
    public String toString() {
        if (hasMessages()) {
            return getClass().getSimpleName() + "{" + this.type + "," + messages + "}";
        } else {
            return getClass().getSimpleName() + "{" + this.type + "}";
        }
    }
}
