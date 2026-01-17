/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcConverter;
import io.kaumei.jdbc.anno.msg.MsgSet;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class StoreJava2Jdbc extends Store<Java2JdbcConverter> {

    public StoreJava2Jdbc(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name) {
        super(logger, javaModelUtil, name);
    }

    protected StoreJava2Jdbc(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name, StoreJava2Jdbc parent) {
        super(logger, javaModelUtil, name, parent);
    }

    @Override
    public StoreJava2Jdbc createChildStore(String name) {
        return new StoreJava2Jdbc(this.logger, javaModelUtil, name, this);
    }

    @Override
    protected Java2JdbcConverter createInvalidConverter(MsgSet messages) {
        return new Java2JdbcConverter(null, messages);
    }

    @Override
    public @Nullable Java2JdbcConverter searchByType(TypeMirror typeMirror) {
        var entry = map.get(toKey("", typeMirror));
        if (entry != null) {
            return entry;
        }
        // ---- we try the type hierarchy
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            entry = javaModelUtil.visitTypeHierarchy(typeMirror,
                    (type0) -> map.get(toKey("", type0)));
            if (entry != null) {
                return entry;
            }
        }
        // ----- search parent
        return parent == null ? null : parent.searchByType(typeMirror);
    }
}
