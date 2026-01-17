/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec2.config;

import io.kaumei.jdbc.annotation.config.JdbcBatchSize;
import io.kaumei.jdbc.annotation.config.JdbcConfig;
import io.kaumei.jdbc.annotation.config.JdbcQueryTimeout;
import io.kaumei.jdbc.annotation.config.JdbcReturnGeneratedValues;
import io.kaumei.jdbc.spec2.converter.JavaToJdbcConverter;
import io.kaumei.jdbc.spec2.converter.JdbcToJavaConverter;
import io.kaumei.jdbc.spec2.converter.LocalDateTimeConverter;

@JdbcBatchSize(17)
//@JdbcFetchDirection
//@JdbcFetchSize
//@JdbcMaxRows
//@JdbcNoMoreRows
//@JdbcNoRows
@JdbcQueryTimeout(19)
//@JdbcResultSetConcurrency
//@JdbcResultSetType
@JdbcReturnGeneratedValues(JdbcReturnGeneratedValues.Kind.EXECUTE_QUERY)
@JdbcConfig(converter = {
        JavaToJdbcConverter.class,
        JdbcToJavaConverter.class,
        LocalDateTimeConverter.class
})
public interface ConfigSpec2 {

}
