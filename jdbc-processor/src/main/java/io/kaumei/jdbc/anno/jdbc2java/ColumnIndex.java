/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

public class ColumnIndex {
    public static ColumnIndex ofValue(int index) {
        return new ColumnIndex(Integer.toString(index), "");
    }

    public static ColumnIndex ofVariable(String indexVar) {
        return new ColumnIndex(indexVar, "");
    }

    public static ColumnIndex ofVariable(String indexVar, String columnName) {
        return new ColumnIndex(indexVar, columnName);
    }

    private final String columnIndexVar;
    private final String columnName;

    ColumnIndex(String columnIndexVar, String columnName) {
        this.columnIndexVar = columnIndexVar;
        this.columnName = columnName;
    }

    public String columnIndexVar() {
        return columnIndexVar;
    }

    public boolean hasColumnName() {
        return !columnName.isEmpty();
    }

    public String columnName() {
        return columnName;
    }

}
