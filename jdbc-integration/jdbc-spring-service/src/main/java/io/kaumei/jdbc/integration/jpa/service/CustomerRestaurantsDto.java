/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

import java.util.List;

/**
 * DTO representing a customer and their restaurants.
 */
public record CustomerRestaurantsDto(CustomerDto customer, List<RestaurantDto> restaurants) {
}
