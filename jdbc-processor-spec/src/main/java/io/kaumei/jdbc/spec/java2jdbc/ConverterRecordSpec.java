/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.annotation.JdbcSelect;
import io.kaumei.jdbc.spec.NoJdbcType;
import io.kaumei.jdbc.spec.common.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ConverterRecordSpec {

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT :value")
    String recordStringUnspecific(RecordString value);

    @JdbcSelect("SELECT :value")
    String recordStringNullable(@Nullable RecordString value);

    @JdbcSelect("SELECT :value")
    String recordStringNonNull(@NonNull RecordString value);

    // ------------------------------------------------------------------------

    @JdbcSelect("select :value")
    Boolean recordBooleanUnspecific(RecordBoolean value);

    @JdbcSelect("select :value")
    Boolean recordBooleanNullable(@Nullable RecordBoolean value);

    @JdbcSelect("select :value")
    Boolean recordBooleanNonNull(@NonNull RecordBoolean value);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT :value")
    Character recordChar(RecordChar value);

    @JdbcSelect("SELECT :value")
    Short recordShort(RecordShort value);

    @JdbcSelect("SELECT :value")
    Integer recordInt(RecordInt value);

    @JdbcSelect("SELECT :value")
    Long recordLong(RecordLong value);

    @JdbcSelect("SELECT :value")
    Float recordFloat(RecordFloat value);

    @JdbcSelect("SELECT :value")
    Double recordDouble(RecordDouble value);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT :value")
    String recordComponentNullable(RecordStringNullable value);

    // ------------------------------------------------------------------------

    @JdbcSelect("SELECT :value")
    String recordComponentNonNull(RecordStringNonNull value);

    // ------------------------------------------------------------------------
    record RecordTwoComponents(String value, String value01) {
    }

    @JdbcSelect("SELECT :value")
    String recordTwoComponents(RecordTwoComponents value);

    // ------------------------------------------------------------------------
    record RecordNoJdbcType(NoJdbcType value) {
    }

    @JdbcSelect("SELECT :value")
    String recordNoJdbcType(RecordNoJdbcType value);


    // ------------------------------------------------------------------------

    record RecordUnspecified(String value) {
    }

    record RecordNullable(@Nullable String value) {
    }

    record RecordNonnull(@NonNull String value) {
    }

    @JdbcSelect("SELECT :value")
    String unspecified_unspecified(RecordUnspecified value);

    @JdbcSelect("SELECT :value")
    String unspecified_nullable(RecordNullable value);

    @JdbcSelect("SELECT :value")
    String unspecified_nonnull(RecordNonnull value);

    @JdbcSelect("SELECT :value")
    String nullable_unspecified(@Nullable RecordUnspecified value);

    @JdbcSelect("SELECT :value")
    String nullable_nullable(@Nullable RecordNullable value);

    @JdbcSelect("SELECT :value")
    String nullable_nonnull(@Nullable RecordNonnull value);

    @JdbcSelect("SELECT :value")
    String nonnull_unspecified(@NonNull RecordUnspecified value);

    @JdbcSelect("SELECT :value")
    String nonnull_nullable(@NonNull RecordNullable value);

    @JdbcSelect("SELECT :value")
    String nonnull_nonnull(@NonNull RecordNonnull value);

    // ------------------------------------------------------------------------

}
