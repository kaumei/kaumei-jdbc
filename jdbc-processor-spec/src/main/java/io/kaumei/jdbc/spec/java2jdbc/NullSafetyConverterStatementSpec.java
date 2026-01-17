/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JavaToJdbc;
import io.kaumei.jdbc.annotation.JdbcConverterName;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.common.WithString01;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface NullSafetyConverterStatementSpec {

    // ------------------------------------------------------------------------

    @JavaToJdbc("safeNull")
    static void safeNull(PreparedStatement stmt, int index, WithString01 value)
            throws SQLException, RuntimeException {
        if (value == null) {
            stmt.setString(index, "null");
        } else if (value.value() == null) {
            stmt.setString(index, "value.null");
        } else {
            stmt.setString(index, value.value());
        }
    }

    @JdbcSelect("SELECT :value")
    String paramUnspecific(@JdbcConverterName("safeNull") WithString01 value);

    @JdbcSelect("SELECT :value")
    String paramNullable(@JdbcConverterName("safeNull") @Nullable WithString01 value);

    @JdbcSelect("SELECT :value")
    String paramNonNull(@JdbcConverterName("safeNull") @NonNull WithString01 value);

    // ------------------------------------------------------------------------

}
