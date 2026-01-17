/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.annotation.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface JdbcLogLevel {
    enum LogLevel {
        ERROR(0), WARN(1), INFO(2), DEBUG(4);
        private final int value;

        LogLevel(int value) {
            this.value = value;
        }

        public boolean isEnabled(LogLevel level) {
            return value <= level.value;
        }
    }

    LogLevel value() default LogLevel.ERROR;
}