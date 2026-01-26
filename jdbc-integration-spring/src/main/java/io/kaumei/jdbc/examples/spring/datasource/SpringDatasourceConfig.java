/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.datasource;

import io.kaumei.jdbc.JdbcConnectionProvider;
import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.CombineServiceImpl;
import io.kaumei.jdbc.examples.spring.NamesService;
import io.kaumei.jdbc.examples.spring.Utils;
import io.kaumei.jdbc.examples.spring.kaumei.NamesServiceKaumeiJdbc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
class SpringDatasourceConfig {

    @Bean
    DataSource dataSource() {
        return Utils.dataSource();
    }

    // ------------------------------------------------------------------------

    @Bean
    NamesService namesServiceDatasource(DataSource dataSource) {
        return new NamesServiceDatasource(dataSource);
    }

    // ------------------------------------------------------------------------

    // @part:datasource
    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    JdbcConnectionProvider datasourceConnectionProvider(DataSource dataSource) {
        return new DatasourceConnectionProvider(dataSource);
    }

    @Bean
    NamesService namesServiceKaumei(JdbcConnectionProvider provider) {
        return new NamesServiceKaumeiJdbc(provider);
    }
    // @part:datasource

    @Bean
    CombineService combineService(
            @Qualifier("namesServiceDatasource")
            NamesService a,
            @Qualifier("namesServiceKaumei")
            NamesService b) {
        return new CombineServiceImpl(a, b);
    }

}