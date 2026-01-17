/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.DatasourceExtension;
import io.kaumei.jdbc.spec.ClassHierarchy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Optional;

import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class NativeCodeParameterTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeParameter service;

    @BeforeEach
    void beforeEach() {
        db.executeSqls("DELETE FROM db_types");
        service = new NativeCodeParameterJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void exactParam() {
        assertThat(service.exactParam(1, "foobar")).isEqualTo("1foobar");
    }

    @Test
    void lesserParam() {
        kaumeiThrows(() -> service.lesserParam(1, "foobar"))
                .annotationProcessError("have different parameter");
    }

    @Test
    void firstParamInvalid1() {
        kaumeiThrows(() -> service.firstParamInvalid1("foobar"))
                .annotationProcessError("first param must have type 'java.sql.Connection'");
    }

    @Test
    void firstParamInvalid2() {
        kaumeiThrows(() -> service.firstParamInvalid2("foobar"))
                .annotationProcessError("first param must have type 'java.sql.Connection'");
    }

    @Test
    void firstParamInvalid3() {
        kaumeiThrows(() -> service.firstParamInvalid3("foobar"))
                .annotationProcessError("first param must have type 'java.sql.Connection'");
    }

    @Test
    void compatible() {
        // TODO
        var value = new ClassHierarchy.Level02Record("foobar");
        //assertThat(service.compatible(value)).isEqualTo(value);
        kaumeiThrows(() -> service.compatible(value))
                .annotationProcessError("Param mismatch type at pos 0: 'io.kaumei.jdbc.spec.ClassHierarchy.Level02' is not same type 'io.kaumei.jdbc.spec.ClassHierarchy.Level01'");
    }

    @Test
    void compatibleInvalid() {
        var value = new ClassHierarchy.Level02Record("foobar");
        kaumeiThrows(() -> service.compatibleInvalid(value))
                .annotationProcessError("Param mismatch type at pos 0: 'io.kaumei.jdbc.spec.ClassHierarchy.Level01' is not same type 'io.kaumei.jdbc.spec.ClassHierarchy.Level02'");
    }

    // ------------------------------------------------------------------------


    @Test
    void unspecified_unspecified() {
        assertThat(service.unspecified_unspecified("foobar")).isEqualTo("foobar");
        assertThat(service.unspecified_unspecified(null)).isEqualTo(null);
    }

    @Test
    void unspecified_mandatory() {
        kaumeiThrows(() -> service.unspecified_mandatory("foobar"))
                .annotationProcessError("Param mismatch optional at pos 0: 'unspecific' is not compatible with 'non-null'");
    }

    @Test
    void unspecified_nullable() {
        assertThat(service.unspecified_nullable("foobar")).isEqualTo("foobar");
        assertThat(service.unspecified_nullable(null)).isEqualTo(null);
    }

    @Test
    void unspecified_optional() {
        kaumeiThrows(() -> service.unspecified_optional("foobar"))
                .paramOptionalTypeIsInvalid();
    }

    // ------------------------------------------------------------------------

    @Test
    void mandatory_unspecified() {
        assertThat(service.mandatory_unspecified("foobar")).isEqualTo("foobar");
    }

    @Test
    void mandatory_mandatory() {
        assertThat(service.mandatory_mandatory("foobar")).isEqualTo("foobar");
    }

    @Test
    void mandatory_nullable() {
        assertThat(service.mandatory_nullable("foobar")).isEqualTo("foobar");
    }

    @Test
    void mandatory_optional() {
        kaumeiThrows(() -> service.mandatory_optional("foobar"))
                .paramOptionalTypeIsInvalid();
    }

    // ------------------------------------------------------------------------

    @Test
    void nullable_unspecified() {
        assertThat(service.nullable_unspecified("foobar")).isEqualTo("foobar");
        assertThat(service.nullable_unspecified(null)).isEqualTo(null);
    }

    @Test
    void nullable_mandatory() {
        kaumeiThrows(() -> service.nullable_mandatory("foobar"))
                .annotationProcessError("Param mismatch optional at pos 0: 'nullable' is not compatible with 'non-null'");
    }

    @Test
    void nullable_nullable() {
        assertThat(service.nullable_nullable("foobar")).isEqualTo("foobar");
        assertThat(service.nullable_nullable(null)).isEqualTo(null);
    }

    @Test
    void nullable_optional() {
        kaumeiThrows(() -> service.nullable_optional("foobar"))
                .paramOptionalTypeIsInvalid();
    }

    // ------------------------------------------------------------------------

    @Test
    void optional_unspecified() {
        kaumeiThrows(() -> service.optional_unspecified(Optional.of("foobar")))
                .paramOptionalTypeIsInvalid();
    }

    @Test
    void optional_mandatory() {
        kaumeiThrows(() -> service.optional_mandatory(Optional.empty()))
                .paramOptionalTypeIsInvalid();
    }

    @Test
    void optional_nullable() {
        kaumeiThrows(() -> service.optional_nullable(Optional.empty()))
                .paramOptionalTypeIsInvalid();
    }

    @Test
    void optional_optional() {
        kaumeiThrows(() -> service.optional_optional(Optional.empty()))
                .paramOptionalTypeIsInvalid();
    }

    // @part:spec -------------------------------------------------------------

}
