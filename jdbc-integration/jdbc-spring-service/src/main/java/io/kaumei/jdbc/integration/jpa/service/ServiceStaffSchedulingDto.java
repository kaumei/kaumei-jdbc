/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * View of the staff scheduling service row.
 */
public record ServiceStaffSchedulingDto(Long restaurantId, boolean enabled) {
}
