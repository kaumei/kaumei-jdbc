/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO capturing all service flags for a restaurant.
 */
public record RestaurantServicesDto(
        String restaurantName,
        ServiceAvailability onlineBooking,
        ServiceAvailability accounting,
        ServiceAvailability delivery,
        ServiceAvailability feedback,
        ServiceAvailability staffScheduling) {
}
