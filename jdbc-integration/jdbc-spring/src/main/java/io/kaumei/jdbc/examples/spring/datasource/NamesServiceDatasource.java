/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.datasource;

import io.kaumei.jdbc.examples.spring.NamesService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Service
@Transactional
public class NamesServiceDatasource implements NamesService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NamesServiceDatasource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM value_budge");
    }

    @Override
    public int delete(String value) {
        return jdbcTemplate.update("DELETE FROM value_budge where value_name = ?", value);
    }

    @Override
    public void insert(String value, @Nullable Integer budged) {
        jdbcTemplate.update("INSERT INTO value_budge (value_name, budge) VALUES (?,?)", value, budged);
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM value_budge", Integer.class);
    }

    @Override
    public List<ValueBudge> selectAll() {
        return jdbcTemplate.query(
                "SELECT * from value_budge ORDER BY value_name, budge",
                (rs, rowNum) -> new ValueBudge(
                        rs.getString("value_name"),
                        rs.getObject("budge", Integer.class))
        );
    }
}
