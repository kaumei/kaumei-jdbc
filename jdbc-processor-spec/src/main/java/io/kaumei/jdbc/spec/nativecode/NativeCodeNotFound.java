/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.annotation.JdbcNative;

import java.sql.Connection;

public interface NativeCodeNotFound {
    @JdbcNative(cls = NativeCodeLib.class, method = "specialAgentLoadLob")
    String otherNativeCode(int id);

    @JdbcNative
    void noStaticMethodInInterface();

    default void noStaticMethodInInterface(Connection con) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcNative(cls = NativeCodeLib.class, method = "noStaticMethodInClass")
    void noStaticMethodInClass1(String param);

    @JdbcNative(cls = NativeCodeLib.class, method = "unknown")
    void noStaticMethodInClass2(String param);


}
