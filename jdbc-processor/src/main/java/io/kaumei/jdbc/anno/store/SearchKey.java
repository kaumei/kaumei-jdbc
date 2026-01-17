/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class SearchKey {
    private final String name;
    private final TypeMirror type;

    public SearchKey(TypeMirror type) {
        this.name = "";
        this.type = type;
    }

    public SearchKey(@Nullable String name, TypeMirror type) {
        this.name = name == null ? "" : name;
        this.type = type;
    }

    public boolean hasName() {
        return !this.name.isEmpty();
    }

    public String name() {
        return this.name;
    }

    public TypeMirror type() {
        return this.type;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.name.isEmpty() ? this.type.toString() : this.name + "(" + this.type + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.name.equals(((SearchKey) o).name) && this.type.equals(((SearchKey) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }
}
