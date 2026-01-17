/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * DTO for provisioning the feedback service.
 */
public record CreateServiceFeedbackRequest(Long restaurantId, boolean enabled) {
}
