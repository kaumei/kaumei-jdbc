/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ServiceOnlineBooking onlineBooking;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ServiceAccounting accounting;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ServiceDeliveryIntegration deliveryIntegration;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ServiceFeedback feedback;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ServiceStaffScheduling staffScheduling;

    public Restaurant() {
    }

    public Restaurant(String name, String address, Customer customer) {
        this.name = name;
        this.address = address;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ServiceOnlineBooking getOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(ServiceOnlineBooking onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public ServiceAccounting getAccounting() {
        return accounting;
    }

    public void setAccounting(ServiceAccounting accounting) {
        this.accounting = accounting;
    }

    public ServiceDeliveryIntegration getDeliveryIntegration() {
        return deliveryIntegration;
    }

    public void setDeliveryIntegration(ServiceDeliveryIntegration deliveryIntegration) {
        this.deliveryIntegration = deliveryIntegration;
    }

    public ServiceFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(ServiceFeedback feedback) {
        this.feedback = feedback;
    }

    public ServiceStaffScheduling getStaffScheduling() {
        return staffScheduling;
    }

    public void setStaffScheduling(ServiceStaffScheduling staffScheduling) {
        this.staffScheduling = staffScheduling;
    }
}
