/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec2.converter;

import io.kaumei.jdbc.annotation.JdbcToJava;

import java.time.LocalDate;
import java.time.LocalTime;

@JdbcToJava
public class JdbcToJavaConverter {
    static LocalDate localDate(java.sql.Date value) {
        return value.toLocalDate();
    }

    static LocalTime localTime(java.sql.Time value) {
        return value.toLocalTime();
    }
}
