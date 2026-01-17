/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcToJava;
import io.kaumei.jdbc.spec.NoJdbcType;

@JdbcToJava("ignored_name")
public interface ColumnTypeSpec {

    // @formatter:off
    @JdbcSelect("SELECT :value") boolean typePrimitiveBoolean(Boolean value);
    @JdbcSelect("SELECT :value") Boolean typeBoolean(Boolean value);
    @JdbcSelect("SELECT :value") char typePrimitiveChar(Character value);
    @JdbcSelect("SELECT :value") Character typeCharacter(Character value);
    @JdbcSelect("SELECT :value") String typeString(String value);
    @JdbcSelect("SELECT :value") NoJdbcType typeNoJdbcType();
    // @formatter:off

    // ------------------------------------------------------------------------

    class ColumnCycle01 {
        ColumnCycle01(ColumnCycle02 value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    class ColumnCycle02 {
        ColumnCycle02(ColumnCycle01 value) {
            throw new AssertionError("Method must not be called from test.");
        }
    }

    @JdbcToJava
    static ColumnCycle02 cycle01ToDB(ColumnCycle01 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcToJava
    static ColumnCycle01 cycle02ToDB(ColumnCycle02 value) {
        throw new AssertionError("Method must not be called from test.");
    }

    @JdbcSelect("SELECT 1")
    ColumnCycle01 invalidColumnCycle();

    // ------------------------------------------------------------------------

    record RecordCycle01(RecordCycle01 parent) {
    }
    @JdbcSelect("SELECT 1")
    RecordCycle01 invalidRecordCycle();

    // ------------------------------------------------------------------------

}
