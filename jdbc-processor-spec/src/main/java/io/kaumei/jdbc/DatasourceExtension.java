/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.util.Objects.requireNonNull;

public class DatasourceExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    private final static ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(DatasourceExtension.class);

    public enum DBType {
        H2, Postgres
    }

    private final DBType dbType;
    private @Nullable DataSource ds;
    private @Nullable Connection con;

    public DatasourceExtension() {
        this(DBType.H2);
    }

    public DatasourceExtension(DBType dbType) {
        this.dbType = dbType;
    }

    // ------------------------------------------------------------------------

    @Override
    public void beforeAll(ExtensionContext context) {
        this.ds = context
                .getStore(ExtensionContext.StoreScope.LAUNCHER_SESSION, NS)
                .computeIfAbsent(dbType, DatasourceExtension::createOrGetDataSource, DataSource.class);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (dbType == DBType.Postgres) {
            this.executeSqls(
                    "DELETE FROM db_address",
                    "DELETE FROM db_customers",
                    "DELETE FROM db_name_mapping",
                    "DELETE FROM db_types",
                    "ALTER SEQUENCE db_address_id_seq      RESTART WITH 1",
                    "ALTER SEQUENCE db_customers_id_seq    RESTART WITH 1",
                    "ALTER SEQUENCE db_name_mapping_id_seq RESTART WITH 1",
                    "ALTER SEQUENCE db_types_id_seq        RESTART WITH 1"
            );
        } else {
            this.executeSqls(
                    "DELETE FROM db_address",
                    "DELETE FROM db_customers",
                    "DELETE FROM db_name_mapping",
                    "DELETE FROM db_types",
                    "ALTER TABLE db_address      ALTER COLUMN id RESTART WITH 1",
                    "ALTER TABLE db_customers    ALTER COLUMN id RESTART WITH 1",
                    "ALTER TABLE db_name_mapping ALTER COLUMN id RESTART WITH 1",
                    "ALTER TABLE db_types        ALTER COLUMN id RESTART WITH 1"
            );
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws SQLException {
        if (this.con != null && !this.con.isClosed()) {
            this.con.close();
        }

    }

    DBType dbType() {
        return dbType;
    }

    public DataSource dataSource() {
        return requireNonNull(ds);
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = dataSource().getConnection();
            }
            return con;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void executeSqls(String... sql) {
        try (Connection con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                for (String s : sql) {
                    stmt.execute(s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void select(String sql) {
        try (Connection con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                try (var rs = stmt.executeQuery(sql)) {
                    var columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(i + ":" +
                                rs.getMetaData().getColumnName(i) + ", " +
                                rs.getMetaData().getColumnLabel(i) + ", " +
                                rs.getMetaData().getColumnClassName(i) + ", " +
                                rs.getMetaData().getColumnTypeName(i));
                    }
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getObject(i));
                            System.out.print(", ");
                        }
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------------

    record StoreEntry(DataSource ds) implements AutoCloseable {
        @Override
        public void close() throws Exception {
            if (this.ds instanceof JdbcConnectionPool h2) {
                h2.dispose();
            } else if (this.ds instanceof PGSimpleDataSource c) {
                // nothing to do
            }
        }
    }

    static DataSource createOrGetDataSource(DBType dbType) {
        return switch (dbType) {
            case Postgres -> {
                PGSimpleDataSource source = new PGSimpleDataSource();
                source.setURL("jdbc:postgresql://localhost:5432/postgres?currentSchema=test");
                source.setUser("postgres");
                source.setPassword("postgres");
                yield source;
            }
            case H2 -> {
                var source = JdbcConnectionPool.create("jdbc:h2:mem:test;MODE=PostgreSQL", "sa", "sa");
                try (Connection conn = source.getConnection()) {
                    try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream("h2_create_db.sql")) {
                        if (in == null) {
                            throw new RuntimeException("Could not find resource from classpath: h2_create_db.sql");
                        }
                        try (var reader = new InputStreamReader(in)) {
                            RunScript.execute(conn, reader);
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                yield source;
            }
        };
    }

}
