/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.datasource;

import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.NamesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static io.kaumei.jdbc.examples.spring.Utils.test;


@SpringJUnitConfig(SpringDatasourceConfig.class)
class SpringDatasourceConfigTest {

    @Qualifier("namesServiceDatasource")
    @Autowired
    private NamesService namesServiceDatasource;

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
    void test_datasource() {
        test(namesServiceDatasource);
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