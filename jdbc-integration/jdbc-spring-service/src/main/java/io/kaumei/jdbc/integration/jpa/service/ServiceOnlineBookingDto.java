/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * View of the online booking service row.
 */
public record ServiceOnlineBookingDto(
        Long restaurantId,
        boolean enabled,
        int maxDaysBefore,
        Integer minPersons,
        boolean needPhoneNumber) {
}
