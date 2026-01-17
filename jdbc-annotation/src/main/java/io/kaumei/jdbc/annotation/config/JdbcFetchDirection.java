/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.annotation.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface JdbcFetchDirection {
    enum Kind {
        UNSPECIFIED(0),
        FETCH_FORWARD(ResultSet.FETCH_FORWARD),
        FETCH_REVERSE(ResultSet.FETCH_REVERSE),
        FETCH_UNKNOWN(ResultSet.FETCH_UNKNOWN);

        private final int sqlMagicNumber;

        Kind(int sqlMagicNumber) {
            this.sqlMagicNumber = sqlMagicNumber;
        }

        public int sqlMagicNumber() {
            // sanity-check:on
            if (this == UNSPECIFIED) {
                throw new RuntimeException();
            }
            // sanity-check:off
            return sqlMagicNumber;
        }

    }

    Kind value() default Kind.UNSPECIFIED;
}