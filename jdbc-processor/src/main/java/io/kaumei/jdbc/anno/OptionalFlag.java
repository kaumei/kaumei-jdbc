/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import org.jspecify.annotations.Nullable;

public enum OptionalFlag {
    UNSPECIFIED("unspecific"),
    NULLABLE("nullable"),
    NON_NULL("non-null"),
    OPTIONAL_TYPE("Optional<?>");

    private final String text;

    OptionalFlag(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
    // ------------------------------------------------------------------------

    public boolean isNullable() {
        return this == NULLABLE;
    }

    public boolean isOptionalType() {
        return this == OPTIONAL_TYPE;
    }

    public boolean isNonNull() {
        return this == NON_NULL;
    }

    public boolean isUnspecified() {
        return this == UNSPECIFIED;
    }

    public boolean isAssignableTo(OptionalFlag other) {
        return other != NON_NULL || this == NON_NULL;
    }

    // ------------------------------------------------------------------------

    public boolean isNullableOrUnspecific() {
        return this == NULLABLE || this == UNSPECIFIED;
    }

    public boolean isNonNullOrUnspecific() {
        return this == NON_NULL || this == UNSPECIFIED;
    }

    public @Nullable String checkNullableOrUnspecific() {
        return this == NULLABLE || this == UNSPECIFIED
                ? null
                : "nullness '" + this + "' supported. Expected are one of 'nullable' or 'unspecific'";
    }

    public @Nullable String checkNonNullOrUnspecific() {
        return this == NON_NULL || this == UNSPECIFIED
                ? null
                : "nullness '" + this + "' supported. Expected are one of 'non-null' or 'unspecific'";
    }

}
