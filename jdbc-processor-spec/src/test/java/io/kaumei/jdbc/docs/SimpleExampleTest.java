/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.docs;

import io.kaumei.jdbc.DatasourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleExampleTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private DataSource dataSource() {
        return db.dataSource();
    }

    @Test
    void test_example() throws SQLException {
        // @part:example
        final AtomicReference<Connection> connectionRef = new AtomicReference<>();

        // create the CustomerDBA with a connection provider
        final SimpleExample dba = new SimpleExampleJdbc(connectionRef::get);
        try (Connection con = dataSource().getConnection()) {
            connectionRef.set(con); // simulate a currently open connection

            // insert two customers and return the generated values
            var alpha = dba.insertCustomer("Alpha", null);
            var bravo = dba.insertCustomer("Bravo", 100_000);

            // count the customers
            assertThat(service.countCustomers()).isEqualTo(2);

            // list all customers and check values
            assertThat(service.listCustomers())
                    .containsExactly(
                            new SimpleExample.CustomerAsRecord(alpha.id(), "Alpha", null, alpha.createdDateTime()),
                            new SimpleExample.CustomerAsRecord(bravo.id(), "Bravo", 100_000, bravo.createdDateTime())
                    );

            // stream all customers and check values
            try (var stream = service.streamCustomers()) {
                assertThat(stream.toList())
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                                new SimpleExample.CustomerAsClass(alpha.id(), "Alpha", null, alpha.createdDateTime()),
                                new SimpleExample.CustomerAsClass(bravo.id(), "Bravo", 100_000, bravo.createdDateTime())
                        );
            }
        }
        // @part:example
    }

    // ------------------------------------------------------------------------

    private SimpleExample service;

    @BeforeEach
    void beforeEach() {
        service = new SimpleExampleJdbc(db::getConnection);
    }

    @Test
    void test_deleteCustomers() {
        // given
        db.executeSqls("INSERT INTO db_customers (name, budge) VALUES ('name', 100)");

        // when ... then
        assertThat(service.deleteCustomers()).isEqualTo(1);
        assertThat(service.countCustomers()).isEqualTo(0);
    }

    @Test
    void test_countCustomers() {
        // given
        db.executeSqls("INSERT INTO db_customers (name, budge) VALUES ('name', 100)");

        // when ... then
        assertThat(service.countCustomers()).isEqualTo(1);
    }

    @Test
    void test_insertCustomer() {
        // when ... then
        var alpha = service.insertCustomer("Alpha", null);
        var bravo = service.insertCustomer("Bravo", 100_000);
        assertThat(service.listCustomers())
                .containsExactly(
                        new SimpleExample.CustomerAsRecord(alpha.id(), "Alpha", null, alpha.createdDateTime()),
                        new SimpleExample.CustomerAsRecord(bravo.id(), "Bravo", 100_000, bravo.createdDateTime())
                );
        try (var stream = service.streamCustomers()) {
            assertThat(stream.toList())
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(
                            new SimpleExample.CustomerAsClass(alpha.id(), "Alpha", null, alpha.createdDateTime()),
                            new SimpleExample.CustomerAsClass(bravo.id(), "Bravo", 100_000, bravo.createdDateTime())
                    );
        }
    }

}
