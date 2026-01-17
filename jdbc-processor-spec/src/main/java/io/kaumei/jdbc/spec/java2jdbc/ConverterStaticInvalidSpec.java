/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;


/**
 * The @JavaToJdbc with value must trigger a warning in the logs
 */
@JavaToJdbc(value = "mustBeIgnored")
public interface ConverterStaticInvalidSpec {

    @JdbcSelect("SELECT :value")
    String notStatic(@JdbcConverterName("ConverterStaticInvalid.notStatic") ConverterStaticInvalid.NotStatic value);

    @JdbcSelect("SELECT :value")
    String notStaticDefault(ConverterStaticInvalid.NotStaticDefault value);

    @JdbcSelect("SELECT :value")
    String notVisible(ConverterStaticInvalid.NotVisible value);

    @JdbcSelect("SELECT :value")
    String notVisibleDefault(ConverterStaticInvalid.NotVisibleDefault value);

    @JdbcSelect("SELECT :value")
    String wrongParameter(ConverterStaticInvalid.WrongParameter value);

    @JdbcSelect("SELECT :value")
    String wrongParameterInner(ConverterStaticInvalid.WrongParameterInner value);

}
