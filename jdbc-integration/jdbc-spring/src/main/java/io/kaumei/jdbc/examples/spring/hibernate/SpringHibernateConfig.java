/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.hibernate;

import io.kaumei.jdbc.JdbcConnectionProvider;
import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.CombineServiceImpl;
import io.kaumei.jdbc.examples.spring.NamesService;
import io.kaumei.jdbc.examples.spring.Utils;
import io.kaumei.jdbc.examples.spring.kaumei.NamesServiceKaumeiJdbc;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
class SpringHibernateConfig {

    @Bean
    DataSource dataSource() {
        return Utils.dataSource();
    }

    // ------------------------------------------------------------------------

    @Bean
    LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        var sf = new LocalSessionFactoryBean();
        sf.setDataSource(dataSource);
        //sf.setAnnotatedClasses(ValueBudgeEntity.class,ValueBudgeId.class);
        sf.setPackagesToScan("io.kaumei.jdbc.examples.spring");
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        //properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        sf.setHibernateProperties(properties);
        return sf;
    }

    // @part:hibernate
    @Bean
    PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    JdbcConnectionProvider hibernateConnectionProvider(SessionFactory sessionFactory, DataSource dataSource) {
        return new HibernateConnectionProvider(sessionFactory, dataSource);
    }

    @Bean
    NamesService namesServiceKaumei(JdbcConnectionProvider provider) {
        return new NamesServiceKaumeiJdbc(provider);
    }
    // @part:hibernate

    @Bean
    NamesService namesServiceHibernate(SessionFactory sessionFactory) {
        return new NamesServiceHibernate(sessionFactory);
    }

    @Bean
    CombineService combineService(
            @Qualifier("namesServiceHibernate")
            NamesService a,
            @Qualifier("namesServiceKaumei")
            NamesService b) {
        return new CombineServiceImpl(a, b);
    }

}