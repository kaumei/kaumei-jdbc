/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.java2jdbc;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.common.WithString01;
import io.kaumei.jdbc.spec.common.WithString02;
import io.kaumei.jdbc.spec.db.DbAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConverterStatementSpecTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private ConverterStatementSpec service;

    @BeforeEach
    void beforeEach() {
        DbAddress.init(db.dataSource());
        service = new ConverterStatementSpecJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void paramUnspecific() {
        var value = new WithString01("1");
        assertThat(service.paramUnspecific(value)).isEqualTo("1");
        assertThat(service.paramUnspecific(null)).isNull();
        var valueRuntime = new WithString01("IllegalArgumentException");
        assertThatThrownBy(() -> service.paramUnspecific(valueRuntime))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validNullable() {
        var value = new WithString02("1");
        assertThat(service.validNullable(value)).isEqualTo("1");
        assertThat(service.validNullable(null)).isNull();
        var valueRuntime = new WithString02("IllegalArgumentException");
        assertThatThrownBy(() -> service.validNullable(valueRuntime))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validNonNull() {
        kaumeiThrows(() -> service.validNonNull(null))
                .paramInvalidConverter("value", "WithString03", "third parameter must be @Nullable or unspecific");
    }

    @Test
    void integerParam() {
        kaumeiThrows(() -> service.integerParam(null))
                .paramInvalidConverter("value", "WithString04", "second parameter must be int");
    }

    @Test
    void callableStatement() {
        kaumeiThrows(() -> service.callableStatement(null))
                .paramInvalidConverter("value", "Class01", "first parameter must be a PreparedStatement");
    }

    @Test
    void optional() {
        kaumeiThrows(() -> service.optional(null))
                .paramNoConverterFound("value", "Class02");
    }

    @Test
    void duplicate() {
        kaumeiThrows(() -> service.duplicate(null))
                .paramInvalidConverter("value", "Class03", "duplicate key");
    }

    @Test
    void ioException() {
        kaumeiThrows(() -> service.ioException(null))
                .paramNoConverterFound("value", "Class04");
    }

    @Test
    void toManyParameters() {
        kaumeiThrows(() -> service.toManyParameters(null))
                .paramNoConverterFound("value", "Class05");
    }

    @Test
    void invalidException() {
        kaumeiThrows(() -> service.invalidException(null))
                .paramInvalidConverter("value", "Class07", "has incompatible exceptions");
    }

    @Test
    void firstNullable() {
        kaumeiThrows(() -> service.firstNullable(null))
                .paramInvalidConverter("value", "Class08", "first parameter must be must be @NonNull or unspecific");
    }
    // ------------------------------------------------------------------------

    @Test
    void staticInObject() {
        var value = new ConverterStatementSpec.ObjectRecordStatic("1");
        assertThat(service.staticInObject(value)).isEqualTo("1");
        assertThat(service.staticInObject(null)).isNull();
    }

    @Test
    void objectRecordToManyAnnotations() {
        kaumeiThrows(() -> service.objectRecordToManyAnnotations(null))
                .paramInvalidConverter("value", "ObjectRecordToManyAnnotations", "To many annotations");
    }

    @Test
    void objectRecordWrongType() {
        kaumeiThrows(() -> service.objectRecordWrongType(null))
                .paramInvalidConverter("value", "ObjectRecordWrongType", "Annotation has wrong type: java.lang.String");
    }

    @Test
    void objectRecordWithName() {
        kaumeiThrows(() -> service.objectRecordWithName(null))
                .paramInvalidConverter("value", "ObjectRecordWithName", "Annotation must not have a name");
    }

    @Test
    void objectRecordWrongParameterCount() {
        kaumeiThrows(() -> service.objectRecordWrongParameterCount(null))
                .paramInvalidConverter("value", "ObjectRecordWrongParameterCount", "Must have one or three parameters");
    }

    // @part:spec -------------------------------------------------------------

}
