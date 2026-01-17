/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.types;

import io.kaumei.jdbc.annotation.JdbcUpdate;

public interface JdbcTypesAdditionalParamSpec {

    // @formatter:off
    @JdbcUpdate("UPDATE :value") void typeBoolean(Boolean value);
    @JdbcUpdate("UPDATE :value") void typeByte(Byte value);
    @JdbcUpdate("UPDATE :value") void typeShort(Short value);
    @JdbcUpdate("UPDATE :value") void typeInteger(Integer value);
    @JdbcUpdate("UPDATE :value") void typeLong(Long value);
    @JdbcUpdate("UPDATE :value") void typeFloat(Float value);
    @JdbcUpdate("UPDATE :value") void typeDouble(Double value);
    @JdbcUpdate("UPDATE :value") void typeChar(char value);
    @JdbcUpdate("UPDATE :value") void typeCharacter(Character value);
    // @formatter:on

}
