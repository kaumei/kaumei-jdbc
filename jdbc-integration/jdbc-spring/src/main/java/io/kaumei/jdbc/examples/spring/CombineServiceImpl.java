/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring;

import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Transactional
public class CombineServiceImpl implements CombineService {

    private final NamesService a;
    private final NamesService b;

    public CombineServiceImpl(NamesService a, NamesService b) {
        this.a = Objects.requireNonNull(a);
        this.b = Objects.requireNonNull(b);
    }

    @Override
    public void testCombineAB() {
        Utils.testCombine(a, b);
    }

    @Override
    public void testCombineBA() {
        Utils.testCombine(b, a);
    }


}