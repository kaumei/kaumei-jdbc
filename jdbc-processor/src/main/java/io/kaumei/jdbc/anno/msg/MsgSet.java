/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.msg;

import java.util.HashSet;
import java.util.Set;

public interface MsgSet extends Iterable<Msg> {

    boolean isEmpty();

    boolean isNotEmpty();

    default String withLinefeed() {
        var sb = new StringBuilder();
        for (Msg message : this) {
            if (!sb.isEmpty()) {
                sb.append('\n');
            }
            sb.append(message);
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------

    MsgSet EMPTY = new MsgSetEmpty();

    static MsgSet of(Set<Msg> messages) {
        return new MsgSetWithContent(messages);
    }

    static MsgSet merge(MsgSet msgSet1, MsgSet msgSet2, Msg msg) {
        var s = new HashSet<Msg>();
        for (var item : msgSet1) {
            s.add(item);
        }
        for (var item : msgSet2) {
            s.add(item);
        }
        s.add(msg);
        return new MsgSetWithContent(s);
    }

    static MsgSet merge(MsgSet msgSet1, MsgSet msgSet2) {
        var s = new HashSet<Msg>();
        for (var item : msgSet1) {
            s.add(item);
        }
        for (var item : msgSet2) {
            s.add(item);
        }
        return new MsgSetWithContent(s);
    }

}
