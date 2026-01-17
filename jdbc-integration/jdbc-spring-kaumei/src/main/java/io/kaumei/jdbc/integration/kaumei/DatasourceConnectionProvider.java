/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.kaumei;

import io.kaumei.jdbc.JdbcConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

import static java.util.Objects.requireNonNull;

@Service
public class DatasourceConnectionProvider implements JdbcConnectionProvider {
    private final DataSource dataSource;

    public DatasourceConnectionProvider(DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    @Override
    public Connection getConnection() {
        return DataSourceUtils.getConnection(this.dataSource);
    }
}