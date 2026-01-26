/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.examples.spring;


import jakarta.persistence.*;

@Entity
@IdClass(ValueBudgeId.class)
@Table(name = "value_budge")
public class ValueBudgeEntity {
    @Id
    @Column(name = "value_name", nullable = false)
    private String value;

    @Id
    private Integer budge;

    public ValueBudgeId getId() {
        return new ValueBudgeId(value, budge);
    }

    public void setId(ValueBudgeId id) {
        this.value = id.getValue();
        this.budge = id.getBudge();
    }

    public NamesService.ValueBudge toCustomer() {
        return new NamesService.ValueBudge(value, budge);
    }

}
