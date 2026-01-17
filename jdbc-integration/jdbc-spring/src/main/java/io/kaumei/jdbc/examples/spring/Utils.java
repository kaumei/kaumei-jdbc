/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.AopProxyUtils;

import javax.sql.DataSource;

public class Utils {

    private static final NamesService.ValueBudge ALPHA = new NamesService.ValueBudge("alpha", 0);
    private static final NamesService.ValueBudge BRAVO_1 = new NamesService.ValueBudge("bravo", 100_000);
    private static final NamesService.ValueBudge BRAVO_2 = new NamesService.ValueBudge("bravo", 111_000);
    private static @Nullable DataSource dataSource;

    public static synchronized DataSource dataSource() {
        if (dataSource == null) {
            var config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:test;" +
                    "MODE=PostgreSQL;" +
                    "DB_CLOSE_DELAY=-1;" +
                    "INIT=RUNSCRIPT FROM 'classpath:schema.sql'");
            config.setUsername("sa");
            config.setPassword("sa");
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static void test(NamesService service) {
        System.out.println("service: " + AopProxyUtils.ultimateTargetClass(service));
        // when ... then
        service.deleteAll();
        service.insert("alpha", 0);
        Assertions.assertThat(service.selectAll()).containsExactly(ALPHA);
        service.insert("bravo", 100_000);
        Assertions.assertThat(service.selectAll()).containsExactly(ALPHA, BRAVO_1);
        service.insert("bravo", 111_000);
        Assertions.assertThat(service.selectAll()).containsExactly(ALPHA, BRAVO_1, BRAVO_2);
        Assertions.assertThat(service.selectAll()).containsExactly(ALPHA, BRAVO_1, BRAVO_2);
        Assertions.assertThat(service.delete("bravo")).isEqualTo(2);
        Assertions.assertThat(service.selectAll()).containsExactly(ALPHA);
        Assertions.assertThat(service.deleteAll()).isEqualTo(1);
    }


    public static void testCombine(NamesService a, NamesService b) {
        System.out.println("a: " + AopProxyUtils.ultimateTargetClass(a));
        System.out.println("b: " + AopProxyUtils.ultimateTargetClass(b));

        // delete
        a.deleteAll();
        Assertions.assertThat(b.deleteAll()).isEqualTo(0);

        a.insert("alpha", 0);
        Assertions.assertThat(a.selectAll()).containsExactly(ALPHA);
        Assertions.assertThat(b.selectAll()).containsExactly(ALPHA);

        b.insert("bravo", 100_000);
        Assertions.assertThat(a.selectAll()).containsExactly(ALPHA, BRAVO_1);
        Assertions.assertThat(b.selectAll()).containsExactly(ALPHA, BRAVO_1);

        a.insert("bravo", 111_000);
        Assertions.assertThat(a.selectAll()).containsExactly(ALPHA, BRAVO_1, BRAVO_2);
        Assertions.assertThat(b.selectAll()).containsExactly(ALPHA, BRAVO_1, BRAVO_2);

        Assertions.assertThat(a.delete("bravo")).isEqualTo(2);
        Assertions.assertThat(a.selectAll()).containsExactly(ALPHA);
        Assertions.assertThat(b.selectAll()).containsExactly(ALPHA);

        Assertions.assertThat(b.delete("bravo")).isEqualTo(0);
        Assertions.assertThat(a.selectAll()).containsExactly(ALPHA);
        Assertions.assertThat(b.selectAll()).containsExactly(ALPHA);

        Assertions.assertThat(a.deleteAll()).isEqualTo(1);
        Assertions.assertThat(b.deleteAll()).isEqualTo(0);

        Assertions.assertThat(a.selectAll()).isEmpty();
        Assertions.assertThat(b.selectAll()).isEmpty();
    }

}
