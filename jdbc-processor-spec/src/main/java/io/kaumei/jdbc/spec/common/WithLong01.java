/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.common;

public record WithLong01(long value) {
    public static final WithLong01 WITH_LONG_1_42 = new WithLong01(42);
}
