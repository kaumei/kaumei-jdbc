/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.hibernate;


import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.NamesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static io.kaumei.jdbc.examples.spring.Utils.test;


@SpringJUnitConfig(SpringHibernateConfig.class)
class SpringHibernateConfigTest {
    @Qualifier("namesServiceHibernate")
    @Autowired
    private NamesService namesServiceHibernate;

    @Qualifier("namesServiceKaumei")
    @Autowired
    private NamesService namesServiceKaumei;

    @Qualifier("combineService")
    @Autowired
    private CombineService combineService;

    // ------------------------------------------------------------------------

    @Test
    void test_kaumei() {
        test(namesServiceKaumei);
    }

    @Test
    void test_hibernate() {
        test(namesServiceHibernate);
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