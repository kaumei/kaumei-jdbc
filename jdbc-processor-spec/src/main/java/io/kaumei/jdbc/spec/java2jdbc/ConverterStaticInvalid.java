/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;


/**
 * The @JavaToJdbc will trigger a search in this class, which can not be
 * achieved with the examples where converter are defined with in the interface.
 * We msut use a class.
 */
@JavaToJdbc
public class ConverterStaticInvalid {

    // ------------------------------------------------------------------------

    public static class NotStatic {
    }

    @JavaToJdbc("ConverterStaticInvalid.notStatic")
    public String notStatic(NotStatic value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------

    public static class NotStaticDefault {
    }

    public String notStaticDefault(NotStaticDefault value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    public static class NotVisible {
    }

    @JavaToJdbc
    private static String notVisible(NotVisible value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    public static class NotVisibleDefault {
    }

    private static String notVisibleDefault(NotVisibleDefault value) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    public static class WrongParameter {
    }

    public static String wrongParameter(WrongParameter value, String invalid) {
        throw new AssertionError("Method must not be called from test.");
    }

    // ------------------------------------------------------------------------
    public static class WrongParameterInner {
    }

    public static class Inner {

        /**
         * This method has a @JavaToJdbc and the class has no @JavaToJdbc and the parameter are wrong.
         */
        @JavaToJdbc
        private static String wrongParameterInner(WrongParameterInner value, String invalid) {
            throw new AssertionError("Method must not be called from test.");
        }
    }
    // ------------------------------------------------------------------------

}
