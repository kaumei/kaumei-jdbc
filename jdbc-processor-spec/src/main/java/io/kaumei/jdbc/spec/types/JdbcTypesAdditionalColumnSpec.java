/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.types;

import io.kaumei.jdbc.annotation.JdbcSelect;

public interface JdbcTypesAdditionalColumnSpec {

    // @formatter:off
    @JdbcSelect("SELECT 1") Boolean typeBoolean();
    @JdbcSelect("SELECT 1") Byte typeByte();
    @JdbcSelect("SELECT 1") Short typeShort();
    @JdbcSelect("SELECT 1") Integer typeInteger();
    @JdbcSelect("SELECT 1") Long typeLong();
    @JdbcSelect("SELECT 1") Float typeFloat();
    @JdbcSelect("SELECT 1") Double typeDouble();
    @JdbcSelect("SELECT 1") char typeChar();
    @JdbcSelect("SELECT 1") Character typeCharacter();
    // @formatter:on

}
