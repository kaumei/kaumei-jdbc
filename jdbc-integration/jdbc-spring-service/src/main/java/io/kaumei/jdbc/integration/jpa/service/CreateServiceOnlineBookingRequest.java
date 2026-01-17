/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO for provisioning the online booking service.
 */
public record CreateServiceOnlineBookingRequest(
        Long restaurantId,
        boolean enabled,
        int maxDaysBefore,
        Integer minPersons,
        boolean needPhoneNumber) {
}
