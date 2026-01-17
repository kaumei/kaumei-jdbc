/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.hibernate;

import io.kaumei.jdbc.JdbcConnectionProvider;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

import static java.util.Objects.requireNonNull;

// @part:hibernate
public class HibernateConnectionProvider implements JdbcConnectionProvider {
    private final SessionFactory sessionFactory;
    private final DataSource dataSource;

    public HibernateConnectionProvider(SessionFactory sessionFactory, DataSource dataSource) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.dataSource = requireNonNull(dataSource);
    }

    @Override
    public Connection getConnection() {
        sessionFactory.getCurrentSession().flush();
        return DataSourceUtils.getConnection(dataSource);
    }
}
// @part:hibernate
