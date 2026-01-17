/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO for provisioning the staff scheduling service.
 */
public record CreateServiceStaffSchedulingRequest(Long restaurantId, boolean enabled) {
}
