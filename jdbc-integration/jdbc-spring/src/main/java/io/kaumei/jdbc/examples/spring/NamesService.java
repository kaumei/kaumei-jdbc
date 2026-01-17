/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring;

import io.kaumei.jdbc.annotation.JdbcName;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface NamesService {

    record ValueBudge(@JdbcName("value_name") String value, @Nullable Integer budge) {
    }

    int deleteAll();

    int delete(String value);

    void insert(String value, @Nullable Integer budge);

    int count();

    List<ValueBudge> selectAll();

}