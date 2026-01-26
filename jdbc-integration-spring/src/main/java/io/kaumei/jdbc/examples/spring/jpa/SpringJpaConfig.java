/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.jpa;

import io.kaumei.jdbc.JdbcConnectionProvider;
import io.kaumei.jdbc.examples.spring.CombineService;
import io.kaumei.jdbc.examples.spring.CombineServiceImpl;
import io.kaumei.jdbc.examples.spring.NamesService;
import io.kaumei.jdbc.examples.spring.Utils;
import io.kaumei.jdbc.examples.spring.kaumei.NamesServiceKaumeiJdbc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
class SpringJpaConfig {

    @Bean
    DataSource dataSource() {
        return Utils.dataSource();
    }

    // ------------------------------------------------------------------------

    // @part:jpa
    @Bean
    AbstractEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaDialect(new HibernateJpaDialect());
        em.setDataSource(dataSource);
        em.setPersistenceProvider(new HibernatePersistenceProvider());
        // @part:jpa
        em.setPackagesToScan("io.kaumei.jdbc.examples.spring");
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        em.setJpaProperties(properties);
        // @part:jpa
        return em;
    }

    @Bean
    PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
        var transactionManager = new JpaTransactionManager(entityManagerFactory);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    JdbcConnectionProvider jpaConnectionProvider(EntityManager entityManager, DataSource dataSource) {
        return new JpaConnectionProvider(entityManager, dataSource);
    }

    @Bean
    NamesService customerServiceKaumei(JdbcConnectionProvider provider) {
        return new NamesServiceKaumeiJdbc(provider);
    }
    // @part:jpa

    @Bean
    NamesService customerServiceJPA(EntityManager entityManager) {
        return new NamesServiceJPA(entityManager);
    }

    @Bean
    CombineService combineService(
            @Qualifier("customerServiceJPA")
            NamesService a,
            @Qualifier("customerServiceKaumei")
            NamesService b) {
        System.out.println("combineService ...");
        return new CombineServiceImpl(a, b);
    }

}