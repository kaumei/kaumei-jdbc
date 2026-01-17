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

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static io.kaumei.jdbc.KaumeiAssert.kaumeiThrows;
import static org.assertj.core.api.Assertions.assertThat;

class NativeCodeReturnTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeReturn service;

    @BeforeEach
    void beforeEach() {
        db.executeSqls("DELETE FROM db_types");
        service = new NativeCodeReturnJdbc(db::getConnection);
    }

    // @part:spec -------------------------------------------------------------

    @Test
    void returnVoidValid1() {
        service.returnVoidValid1("foobar");
        assertSource(NativeCodeGeneralJdbc.class)
                .hasClass("NativeCodeGeneralJdbc")
                .hasMethod("nativeCode")
                .hasCall("NativeCodeGeneral.nativeCode(con,id,someDate)");
    }

    @Test
    void returnVoidValid2() {
        service.returnVoidValid2("foobar");
        assertSource(NativeCodeGeneralJdbc.class)
                .hasClass("NativeCodeGeneralJdbc")
                .hasMethod("nativeCode")
                .hasCall("NativeCodeGeneral.nativeCode(con,id,someDate)");
    }

    @Test
    void returnVoidInvalid() {
        kaumeiThrows(() -> service.returnVoidInvalid("foobar"))
                .annotationProcessError("Return type 'void' is not assignable to 'int'");
    }

    // ------------------------------------------------------------------------

    @Test
    void compatible() {
        var value = new ClassHierarchy.Level02Record("foobar");
        assertThat(service.compatible(value)).isEqualTo(value);
    }

    @Test
    void compatibleInvalid() {
        var value = new ClassHierarchy.Level01Record("foobar");
        kaumeiThrows(() -> service.compatibleInvalid(value))
                .annotationProcessError("Return type 'io.kaumei.jdbc.spec.ClassHierarchy.Level01' is not assignable to 'io.kaumei.jdbc.spec.ClassHierarchy.Level02'");
    }

    // ------------------------------------------------------------------------

    @Test
    void unspecified_unspecified() {
        assertThat(service.unspecified_unspecified("foobar")).isEqualTo("foobar");
        assertThat(service.unspecified_unspecified(null)).isEqualTo(null);
    }

    @Test
    void unspecified_mandatory() {
        assertThat(service.unspecified_mandatory("foobar")).isEqualTo("foobar");
        assertThat(service.unspecified_mandatory(null)).isEqualTo(null);
    }

    @Test
    void unspecified_nullable() {
        assertThat(service.unspecified_nullable("foobar")).isEqualTo("foobar");
        assertThat(service.unspecified_nullable(null)).isEqualTo(null);
    }

    @Test
    void unspecified_optional() {
        kaumeiThrows(() -> service.unspecified_optional(Optional.of("foobar")))
                .annotationProcessError("Return type 'java.util.Optional<java.lang.String>' is not assignable to 'java.lang.String'");
    }

    // ------------------------------------------------------------------------

    @Test
    void mandatory_unspecified() {
        kaumeiThrows(() -> service.mandatory_unspecified("foobar"))
                .annotationProcessError("Return type mismatch. target: 'unspecific' is not compatible with source: 'non-null'");
    }

    @Test
    void mandatory_mandatory() {
        assertThat(service.mandatory_mandatory("foobar")).isEqualTo("foobar");
        assertThat(service.mandatory_mandatory(null)).isEqualTo(null);
    }

    @Test
    void mandatory_nullable() {
        kaumeiThrows(() -> service.mandatory_nullable("foobar"))
                .annotationProcessError("Return type mismatch. target: 'nullable' is not compatible with source: 'non-null'");
    }

    @Test
    void mandatory_optional() {
        kaumeiThrows(() -> service.mandatory_optional(Optional.of("foobar")))
                .annotationProcessError("Return type 'java.util.Optional<java.lang.String>' is not assignable to 'java.lang.@org.jspecify.annotations.NonNull String'");
    }

    // ------------------------------------------------------------------------

    @Test
    void nullable_unspecified() {
        assertThat(service.nullable_unspecified("foobar")).isEqualTo("foobar");
        assertThat(service.nullable_unspecified(null)).isEqualTo(null);
    }

    @Test
    void nullable_mandatory() {
        assertThat(service.nullable_mandatory("foobar")).isEqualTo("foobar");
    }

    @Test
    void nullable_nullable() {
        assertThat(service.nullable_nullable("foobar")).isEqualTo("foobar");
        assertThat(service.nullable_nullable(null)).isEqualTo(null);
    }

    @Test
    void nullable_optional() {
        kaumeiThrows(() -> service.nullable_optional(Optional.of("foobar")))
                .annotationProcessError("Return type 'java.util.Optional<java.lang.String>' is not assignable to 'java.lang.@org.jspecify.annotations.Nullable String'");
    }

    // ------------------------------------------------------------------------

    @Test
    void optional_unspecified() {
        kaumeiThrows(() -> service.optional_unspecified("foobar"))
                .annotationProcessError("Return type 'java.lang.String' is not assignable to 'java.util.Optional<java.lang.String>'");
    }

    @Test
    void optional_mandatory() {
        kaumeiThrows(() -> service.optional_mandatory("foobar"))
                .annotationProcessError("Return type 'java.lang.@org.jspecify.annotations.NonNull String' is not assignable to 'java.util.Optional<java.lang.String>'");
    }

    @Test
    void optional_nullable() {
        kaumeiThrows(() -> service.optional_nullable("foobar"))
                .annotationProcessError("Return type 'java.lang.@org.jspecify.annotations.Nullable String' is not assignable to 'java.util.Optional<java.lang.String>'");
    }

    @Test
    void optional_optional() {
        kaumeiThrows(() -> service.optional_optional(Optional.empty()))
                .paramOptionalTypeIsInvalid();
    }

    // @part:spec -------------------------------------------------------------

}
