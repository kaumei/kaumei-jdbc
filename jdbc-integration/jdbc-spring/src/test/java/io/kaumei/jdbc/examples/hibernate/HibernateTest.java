/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.hibernate;

import io.kaumei.jdbc.DatasourceExtension;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The table to use has this structure
 * CREATE TABLE PERSON (
 * id          long primary key,
 * name        varchar(255) not null,
 * birth       date,
 * plz         number(5),
 * city        varchar(255),
 * uuid        uuid,
 * is_customer varchar(3)
 */
@NullUnmarked
@Disabled("long running test")
public class HibernateTest {

    private static final int runCount = 3;
    private static final long runInSeconds = 3;

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();
    static SessionFactory sessionFactory;

    @BeforeAll
    static void beforeAll() {
        DataSource ds = db.dataSource();
        // @claude setup a hibernate to use the db datasource
        Properties properties = new Properties();
        properties.put(Environment.JAKARTA_NON_JTA_DATASOURCE, ds);
        properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        properties.put(Environment.SHOW_SQL, "false");
        properties.put(Environment.FORMAT_SQL, "false");

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void test_count_with_tx() {
        try (var session = sessionFactory.openSession()) {
            for (var run = 0; run < runCount; run++) {
                count_with_tx(session);
            }
        }
    }

    void count_with_tx(Session session) {
        int i = 0;
        long start = System.nanoTime();
        long stop = start + NANOSECONDS.convert(runInSeconds, SECONDS);
        while (System.nanoTime() < stop) {
            session.beginTransaction();
            var query = session.createNativeQuery("SELECT 3", Long.class);
            assertThat(query.getSingleResult()).isEqualTo(3L);
            session.getTransaction().commit();
            i++;
        }
        long end = System.nanoTime();
        System.out.println("hibernate.count_with_tx: " + ((end - start) / i) + " ns/op");
    }

    // ------------------------------------------------------------------------

    @Test
    void test_count_in_tx() {
        try (var session = sessionFactory.openSession()) {
            for (var run = 0; run < runCount; run++) {
                count_in_tx(session);
            }
        }
    }

    void count_in_tx(Session session) {
        int i = 0;
        long start = System.nanoTime();
        long stop = start + NANOSECONDS.convert(runInSeconds, SECONDS);
        session.beginTransaction();
        while (System.nanoTime() < stop) {
            var query = session.createNativeQuery("SELECT 3", Long.class);
            assertThat(query.getSingleResult()).isEqualTo(3L);
            i++;
        }
        session.getTransaction().commit();
        long end = System.nanoTime();
        System.out.println("hibernate.count_in_tx: " + ((end - start) / i) + " ns/op");
    }

    // ------------------------------------------------------------------------

    @Test
    void test_person_in_txt() {
        try (var session = sessionFactory.openSession()) {
            for (var run = 0; run < runCount; run++) {
                person_in_tx(session);
            }
        }
    }

    void person_in_tx(Session session) {
        //var birth = LocalDate.of(2000, 1, 1);
        int i = 0;
        long start = System.nanoTime();
        long stop = start + NANOSECONDS.convert(runInSeconds, SECONDS);
        session.beginTransaction();
        while (System.nanoTime() < stop) {
            var query = session.createNativeQuery("select id, name, birth from PERSON where id = 2", Person.class);
            var person = query.getSingleResult();
            assertThat(person.id()).isEqualTo(2L);
            i++;
        }
        session.getTransaction().commit();
        long end = System.nanoTime();
        System.out.println("hibernate.person_in_tx: " + ((end - start) / i) + " ns/op");
    }

    public record Person(long id, String name, @Nullable LocalDate birth) {
    }


}
