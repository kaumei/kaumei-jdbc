/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.common;

public record WithString01(String value) {
    public static final WithString01 WITH_STRING_1_FOOBAR = new WithString01("foobar");
    public static final WithString01 WITH_STRING_1_NULL = new WithString01(null);
}
