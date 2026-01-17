/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_online_booking")
public class ServiceOnlineBooking {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "max_days_before", nullable = false)
    private int maxDaysBefore;

    @Column(name = "min_persons")
    private Integer minPersons;

    @Column(name = "need_phone_number", nullable = false)
    private boolean needPhoneNumber;

    public ServiceOnlineBooking() {
    }

    public ServiceOnlineBooking(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxDaysBefore() {
        return maxDaysBefore;
    }

    public void setMaxDaysBefore(int maxDaysBefore) {
        this.maxDaysBefore = maxDaysBefore;
    }

    public Integer getMinPersons() {
        return minPersons;
    }

    public void setMinPersons(Integer minPersons) {
        this.minPersons = minPersons;
    }

    public boolean isNeedPhoneNumber() {
        return needPhoneNumber;
    }

    public void setNeedPhoneNumber(boolean needPhoneNumber) {
        this.needPhoneNumber = needPhoneNumber;
    }
}
