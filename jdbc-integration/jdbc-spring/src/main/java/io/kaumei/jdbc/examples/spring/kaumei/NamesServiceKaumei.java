/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring.kaumei;

import io.kaumei.jdbc.annotation.JdbcConstructorAnnotations;
import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.annotation.JdbcUpdate;
import io.kaumei.jdbc.examples.spring.NamesService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @part:NamesServiceKaumei
@JdbcConstructorAnnotations(Autowired.class)
@Transactional
@Service
public interface NamesServiceKaumei extends NamesService {
    @JdbcUpdate("DELETE FROM value_budge")
    int deleteAll();

    @JdbcUpdate("DELETE FROM value_budge where value_name = :value")
    int delete(String value);

    @JdbcUpdate("INSERT INTO value_budge (value_name, budge) VALUES (:value, :budge)")
    void insert(String value, @Nullable Integer budge);

    @JdbcSelect("SELECT count(*) FROM value_budge")
    int count();

    @JdbcSelect("SELECT * FROM value_budge order by value_name, budge")
    List<ValueBudge> selectAll();
}
// @part:NamesServiceKaumei

