/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO describing a restaurant in the Spring JPA demo.
 */
public record RestaurantDto(Long id, String name, String address, Long customerId) {
}
