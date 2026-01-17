/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * Captures whether a service is active for a restaurant.
 */
public enum ServiceAvailability {
    ENABLED,
    DISABLED,
    ABSENT
}
