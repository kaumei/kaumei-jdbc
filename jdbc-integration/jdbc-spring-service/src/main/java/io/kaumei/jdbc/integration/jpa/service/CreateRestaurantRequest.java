/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO used to request creation of a new restaurant bound to a customer.
 */
public record CreateRestaurantRequest(String name, String address, Long customerId) {
}
