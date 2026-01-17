/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.integration.jpa.service;

/**
 * Immutable view of a customer persisted by JPA.
 */
public record CustomerDto(Long id, String name) {
}
