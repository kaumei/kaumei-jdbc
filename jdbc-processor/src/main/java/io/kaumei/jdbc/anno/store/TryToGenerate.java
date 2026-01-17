/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import javax.lang.model.type.TypeMirror;

public interface TryToGenerate<T extends Converter> {
    T tryToCreate(SearchKey searchKey);

    boolean checkType(TypeMirror expectedType, T entry);
}
