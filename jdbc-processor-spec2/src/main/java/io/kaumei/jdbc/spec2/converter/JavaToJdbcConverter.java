/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec2.converter;

import io.kaumei.jdbc.annotation.JavaToJdbc;

import java.time.LocalDate;
import java.time.LocalTime;

@JavaToJdbc
public class JavaToJdbcConverter {
    static java.sql.Date localDate(LocalDate value) {
        return java.sql.Date.valueOf(value);
    }

    static java.sql.Time localTime(LocalTime value) {
        return java.sql.Time.valueOf(value);
    }
}
