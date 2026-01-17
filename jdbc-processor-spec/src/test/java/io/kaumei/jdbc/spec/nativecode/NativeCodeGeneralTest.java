/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.spec.nativecode;

import io.kaumei.jdbc.DatasourceExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.kaumei.jdbc.KaumeiAssert.assertSource;
import static org.assertj.core.api.Assertions.assertThat;

class NativeCodeGeneralTest {

    @RegisterExtension
    final static DatasourceExtension db = new DatasourceExtension();

    private NativeCodeGeneral service;

    @BeforeEach
    void beforeEach() {
        db.executeSqls("DELETE FROM db_types");
        service = new NativeCodeGeneralJdbc(db::getConnection);
    }

    @Test
    void nativeCode() {
        assertThat(service.nativeCode(1, "data")).isTrue();
        assertThat(service.otherNativeCode(1)).isEqualTo("data");
    }

    // @part:spec -------------------------------------------------------------

    // empty paramter
    // first connection

    @Test
    void nativeCode_source() {
        assertSource(NativeCodeGeneralJdbc.class)
                .hasClass("NativeCodeGeneralJdbc")
                .hasMethod("nativeCode")
                .hasCall("NativeCodeGeneral.nativeCode(con,id,someDate)");
    }

    @Test
    void otherNativeCode_source() {
        assertSource(NativeCodeGeneralJdbc.class)
                .hasClass("NativeCodeGeneralJdbc")
                .hasMethod("otherNativeCode")
                .hasCall("NativeCodeLib.specialAgentLoadLob(con,id)");
    }

    // @part:spec -------------------------------------------------------------

}
