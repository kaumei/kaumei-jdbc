/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec2.converter;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcToJava;

import java.time.LocalDateTime;

public class LocalDateTimeConverter {

    @JdbcToJava
    public static LocalDateTime timestampToLocalDateTime(java.sql.Timestamp value) {
        return value.toLocalDateTime();
    }

    @JavaToJdbc
    public static java.sql.Timestamp localDateTimeToTimestamp(LocalDateTime value) {
        return java.sql.Timestamp.valueOf(value);
    }

}
