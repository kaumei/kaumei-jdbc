/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring;

import java.io.Serializable;
import java.util.Objects;

public class ValueBudgeId implements Serializable {
    private String value;
    private Integer budge;


    public ValueBudgeId(String value, Integer budge) {
        this.value = value;
        this.budge = budge;
    }

    private ValueBudgeId() {
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getBudge() {
        return budge;
    }

    public void setBudge(Integer budge) {
        this.budge = budge;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueBudgeId that = (ValueBudgeId) o;
        return Objects.equals(value, that.value) && Objects.equals(budge, that.budge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, budge);
    }
}