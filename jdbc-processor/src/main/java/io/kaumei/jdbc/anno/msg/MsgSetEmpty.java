/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.msg;

import java.util.Iterator;
import java.util.NoSuchElementException;

class MsgSetEmpty implements MsgSet {

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isNotEmpty() {
        return false;
    }

    @Override
    public Iterator<Msg> iterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public String toString() {
        return "[]";
    }

    // ------------------------------------------------------------------------

    private static final Iterator<Msg> EMPTY_ITERATOR = new Iterator<>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Msg next() {
            throw new NoSuchElementException();
        }
    };

}
