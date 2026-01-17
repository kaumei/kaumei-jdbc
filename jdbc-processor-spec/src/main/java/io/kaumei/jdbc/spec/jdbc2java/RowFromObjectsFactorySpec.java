/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.jdbc2java;


import io.kaumei.jdbc.annotation.JdbcSelect;

/**
 * Order of methods:
 * * static factory methods
 * * record constructors
 * * class constructors
 * * util methods
 */
public interface RowFromObjectsFactorySpec {
    @JdbcSelect("SELECT 'RowObjectsGeneratedSpec'")
    String select();
    // static factory methods #################################################
    // record constructors ####################################################
    // class constructors #####################################################
    // util methods ###########################################################
}
