/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * View of the delivery integration service row.
 */
public record ServiceDeliveryIntegrationDto(
        Long restaurantId,
        boolean enabled,
        String uberId,
        String lieferandoId,
        String custom) {
}
