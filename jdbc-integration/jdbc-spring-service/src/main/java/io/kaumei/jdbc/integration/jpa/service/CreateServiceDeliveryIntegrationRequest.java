/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO for provisioning the delivery integration service.
 */
public record CreateServiceDeliveryIntegrationRequest(
        Long restaurantId,
        boolean enabled,
        String uberId,
        String lieferandoId,
        String custom) {
}
