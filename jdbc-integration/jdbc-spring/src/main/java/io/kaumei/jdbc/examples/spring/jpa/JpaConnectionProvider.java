/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.jpa;

import io.kaumei.jdbc.JdbcConnectionProvider;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;

import javax.sql.DataSource;
import java.sql.Connection;

import static java.util.Objects.requireNonNull;

// @part:jpa
public class JpaConnectionProvider implements JdbcConnectionProvider {
    private final EntityManager entityManager;
    private final DataSource dataSource;

    public JpaConnectionProvider(EntityManager entityManager, DataSource dataSource) {
        var jpaDialect = entityManager.getEntityManagerFactory() instanceof EntityManagerFactoryInfo info
                ? info.getJpaDialect()
                : null;
        if (jpaDialect == null) {
            throw new IllegalArgumentException("JPA Dialect not found");
        } else if (jpaDialect instanceof HibernateJpaDialect) {
            this.entityManager = entityManager;
            this.dataSource = requireNonNull(dataSource);
        } else {
            throw new IllegalArgumentException("Not supported JPA Dialect: " + jpaDialect.getClass().getCanonicalName());
        }
    }

    @Override
    public Connection getConnection() {
        entityManager.flush();
        return DataSourceUtils.getConnection(dataSource);
    }
}
// @part:jpa
