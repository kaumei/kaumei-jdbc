/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.msg;

import io.kaumei.jdbc.anno.ProcessorException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

class MsgSetWithContent implements MsgSet {

    private final Msg[] messages;

    MsgSetWithContent(Set<Msg> messages) {
        // sanity-check:on
        if (messages.isEmpty()) {
            throw new ProcessorException("messages must not be empty");
        }
        // sanity-check:off
        this.messages = messages.toArray(new Msg[0]);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isNotEmpty() {
        return true;
    }

    @Override
    public Iterator<Msg> iterator() {
        return new LocalIterator();
    }

    @Override
    public String toString() {
        return Arrays.toString(messages);
    }

    // ------------------------------------------------------------------------

    private class LocalIterator implements Iterator<Msg> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return index < messages.length;
        }

        @Override
        public Msg next() {
            if (this.index >= messages.length) {
                throw new NoSuchElementException(this.index + " >= " + messages.length);
            }
            return messages[this.index++];
        }
    }

}
