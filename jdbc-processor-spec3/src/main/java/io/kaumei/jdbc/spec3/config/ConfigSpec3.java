/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec3.config;

import io.kaumei.jdbc.annotation.config.JdbcConfig;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import io.kaumei.jdbc.spec2.config.ConfigSpec2;

@JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.GENERATED_KEYS)
@JdbcConfig(parent = ConfigSpec2.class)
public interface ConfigSpec3 {
}
