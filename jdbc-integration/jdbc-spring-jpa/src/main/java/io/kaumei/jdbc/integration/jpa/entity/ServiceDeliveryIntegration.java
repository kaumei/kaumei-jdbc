/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_delivery_integration")
public class ServiceDeliveryIntegration {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "uber_id", length = 100)
    private String uberId;

    @Column(name = "lieferando_id", length = 100)
    private String lieferandoId;

    @Column(length = 100)
    private String custom;

    public ServiceDeliveryIntegration() {
    }

    public ServiceDeliveryIntegration(Restaurant restaurant) {
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

    public String getUberId() {
        return uberId;
    }

    public void setUberId(String uberId) {
        this.uberId = uberId;
    }

    public String getLieferandoId() {
        return lieferandoId;
    }

    public void setLieferandoId(String lieferandoId) {
        this.lieferandoId = lieferandoId;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }
}
