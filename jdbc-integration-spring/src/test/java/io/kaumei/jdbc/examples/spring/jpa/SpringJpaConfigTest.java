/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.jpa;

import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.NamesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static io.kaumei.jdbc.examples.spring.Utils.test;

@SpringJUnitConfig(SpringJpaConfig.class)
class SpringJpaConfigTest {
    @Qualifier("customerServiceJPA")
    @Autowired
    private NamesService customerServiceJPA;

    @Qualifier("customerServiceKaumei")
    @Autowired
    private NamesService customerServiceKaumei;

    @Qualifier("combineService")
    @Autowired
    private CombineService combineService;

    @Test
    void test_kaumei() {
        test(customerServiceKaumei);
    }

    @Test
    void test_hibernate() {
        test(customerServiceJPA);
    }

    @Test
    void test_combineAB() {
        combineService.testCombineAB();
    }

    @Test
    void test_combineBA() {
        combineService.testCombineBA();
    }

}