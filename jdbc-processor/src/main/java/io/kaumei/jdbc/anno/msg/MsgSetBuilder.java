/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.msg;

import java.util.HashSet;
import java.util.Set;

public class MsgSetBuilder {
    private final Set<Msg> messages = new HashSet<>();

    // ------------------------------------------------------------------------

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public MsgSet build() {
        return messages.isEmpty() ? MsgSet.EMPTY : new MsgSetWithContent(messages);
    }

    // ------------------------------------------------------------------------

    public void add(Msg msg) {
        messages.add(msg);
    }

    public void add(MsgSet msg) {
        for (var m : msg) {
            messages.add(m);
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return messages.toString();
    }
}
