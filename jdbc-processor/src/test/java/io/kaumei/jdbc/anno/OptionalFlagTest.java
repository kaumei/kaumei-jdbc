/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import org.junit.jupiter.api.Test;

import static io.kaumei.jdbc.anno.OptionalFlag.*;
import static org.assertj.core.api.Assertions.assertThat;

class OptionalFlagTest {

    @Test
    void isAssignableTo_UNSPECIFIED() {
        assertThat(UNSPECIFIED.isAssignableTo(UNSPECIFIED)).isTrue();
        assertThat(NULLABLE.isAssignableTo(UNSPECIFIED)).isTrue();
        assertThat(NON_NULL.isAssignableTo(UNSPECIFIED)).isTrue();
        assertThat(OPTIONAL_TYPE.isAssignableTo(UNSPECIFIED)).isTrue();
    }

    @Test
    void isAssignableTo_OPTIONAL() {
        assertThat(UNSPECIFIED.isAssignableTo(NULLABLE)).isTrue();
        assertThat(NULLABLE.isAssignableTo(NULLABLE)).isTrue();
        assertThat(NON_NULL.isAssignableTo(NULLABLE)).isTrue();
        assertThat(OPTIONAL_TYPE.isAssignableTo(NULLABLE)).isTrue();
    }

    @Test
    void isAssignableTo_MANDATORY() {
        assertThat(UNSPECIFIED.isAssignableTo(NON_NULL)).isFalse();
        assertThat(NULLABLE.isAssignableTo(NON_NULL)).isFalse();
        assertThat(NON_NULL.isAssignableTo(NON_NULL)).isTrue();
        assertThat(OPTIONAL_TYPE.isAssignableTo(NON_NULL)).isFalse();
    }

    @Test
    void isAssignableTo_OPTIONAL_TYPE() {
        assertThat(UNSPECIFIED.isAssignableTo(OPTIONAL_TYPE)).isTrue();
        assertThat(NULLABLE.isAssignableTo(OPTIONAL_TYPE)).isTrue();
        assertThat(NON_NULL.isAssignableTo(OPTIONAL_TYPE)).isTrue();
        assertThat(OPTIONAL_TYPE.isAssignableTo(OPTIONAL_TYPE)).isTrue();
    }

}