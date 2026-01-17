/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.annotation;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface JdbcToJava {

    /**
     * If a method is annotated, you can name this converter to select it later.
     */
    String value() default "";

    // ------------------------------------------------------------------------

    // marker interface
    interface Marker {

    }

    @FunctionalInterface
    interface Column<T> {
        @Nullable
        T mapColumnToJava(ResultSet rs, int columnIndex) throws SQLException;
    }

    @FunctionalInterface
    interface Row<T> {
        T mapRowToJava(ResultSet rs) throws SQLException;
    }
}