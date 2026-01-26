/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.docs;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.JdbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.kaumei.jdbc.docs.SimpleExample.*;
import static io.kaumei.jdbc.docs.SimpleExample.PricingPlan.ENTERPRISE;
import static io.kaumei.jdbc.docs.SimpleExample.PricingPlan.FREE;
import static java.util.Objects.requireNonNull;
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
            var alphaGen = dba.insertCustomer("Alpha", null, FREE);
            var bravoGen = dba.insertCustomer("Bravo", 100_000, ENTERPRISE);

            var alphaRecord = new CustomerAsRecord(alphaGen.id(), "Alpha", null, FREE, alphaGen.createdDateTime());
            var bravoRecord = new CustomerAsRecord(bravoGen.id(), "Bravo", 100_000, ENTERPRISE, bravoGen.createdDateTime());
            var alphaClass = new CustomerAsClass(alphaGen.id(), "Alpha", null, FREE, alphaGen.createdDateTime());
            var bravoClass = new CustomerAsClass(bravoGen.id(), "Bravo", 100_000, ENTERPRISE, bravoGen.createdDateTime());

            // count the customers
            assertThat(service.countCustomers()).isEqualTo(2);

            // list all customers and check values
            assertThat(service.listCustomers())
                    .containsExactly(alphaRecord, bravoRecord);

            // stream all customers and check values
            try (var stream = service.streamCustomers()) {
                assertThat(stream.toList())
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(alphaClass, bravoClass);
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
        db.executeSqls("INSERT INTO db_customers (name, budge, pricing_plan) VALUES ('name', 100, 'FREE')");

        // when ... then
        assertThat(service.deleteCustomers()).isEqualTo(1);
        assertThat(service.countCustomers()).isEqualTo(0);
    }

    @Test
    void test_countCustomers() {
        // given
        db.executeSqls("INSERT INTO db_customers (name, budge, pricing_plan) VALUES ('name', 100, 'FREE')");

        // when ... then
        assertThat(service.countCustomers()).isEqualTo(1);
    }

    @Test
    void test_insertCustomer() {
        // when ... then
        var alphaGen = service.insertCustomer("Alpha", null, FREE);
        var bravoGen = service.insertCustomer("Bravo", 100_000, ENTERPRISE);

        var alphaRecord = new CustomerAsRecord(alphaGen.id(), "Alpha", null, FREE, alphaGen.createdDateTime());
        var bravoRecord = new CustomerAsRecord(bravoGen.id(), "Bravo", 100_000, ENTERPRISE, bravoGen.createdDateTime());
        var alphaClass = new CustomerAsClass(alphaGen.id(), "Alpha", null, FREE, alphaGen.createdDateTime());
        var bravoClass = new CustomerAsClass(bravoGen.id(), "Bravo", 100_000, ENTERPRISE, bravoGen.createdDateTime());

        assertThat(service.listCustomers())
                .containsExactly(alphaRecord, bravoRecord);
        assertThat(listCustomers())
                .containsExactly(alphaRecord, bravoRecord);
        try (var stream = service.streamCustomers()) {
            assertThat(stream.toList())
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(alphaClass, bravoClass);
        }
    }

    public List<CustomerAsRecord> listCustomers() {
        try {
            var con = db.getConnection();
            var sql = "SELECT * FROM db_customers ORDER BY name";
            try (var stmt = con.prepareStatement(sql)) {
                try (var rs = stmt.executeQuery()) {
                    var list = new ArrayList<CustomerAsRecord>();
                    while (rs.next()) {
                        var id = rs.getLong("id");
                        if (rs.wasNull()) {
                            throw new java.lang.NullPointerException("JDBC column was null on column id");
                        }
                        var name = requireNonNull(rs.getString("name"));
                        var budgeInt = rs.getInt("budge");
                        var budge = rs.wasNull() ? null : budgeInt;
                        var pricePlan = rs.getString("pricing_plan");
                        if (pricePlan == null) {
                            throw new java.lang.NullPointerException("JDBC column was null on column pricing_plan");
                        }
                        var plan = PricingPlan.valueOf(pricePlan);
                        var created = rs.getTimestamp("created_at").toLocalDateTime();
                        var row = new CustomerAsRecord(id, name, budge, plan, created);
                        list.add(row);
                    }
                    return Collections.unmodifiableList(list);
                }
            }
        } catch (SQLException e) {
            throw new JdbcException(e.getMessage(), e);
        }
    }

}
