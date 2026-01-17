/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec;

import io.kaumei.jdbc.annotation.config.JdbcConfig;
import io.kaumei.jdbc.spec.java2jdbc.ConverterStaticInvalid;
import io.kaumei.jdbc.spec.jdbc2java.GeneralConverter;

@JdbcConfig(converter = {GeneralConverter.class, ConverterStaticInvalid.class})
public interface ConfigSpec {

}
