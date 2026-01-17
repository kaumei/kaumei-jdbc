/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.TypeSpec;

import javax.lang.model.element.TypeElement;

interface KaumeiBuilder {
    TypeElement type();

    TypeSpec build();
}
