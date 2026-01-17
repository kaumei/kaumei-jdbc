/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.jdbc2java.Jdbc2JavaConverter;
import io.kaumei.jdbc.anno.msg.MsgSet;

public class StoreJdbc2Java extends Store<Jdbc2JavaConverter> {

    public StoreJdbc2Java(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name) {
        super(logger, javaModelUtil, name);
    }

    public StoreJdbc2Java(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name, StoreJdbc2Java parent) {
        super(logger, javaModelUtil, name, parent);
    }

    @Override
    public StoreJdbc2Java createChildStore(String name) {
        return new StoreJdbc2Java(this.logger, this.javaModelUtil, name, this);
    }

    @Override
    protected Jdbc2JavaConverter createInvalidConverter(MsgSet messages) {
        return new Jdbc2JavaConverter(null, messages);
    }
}
