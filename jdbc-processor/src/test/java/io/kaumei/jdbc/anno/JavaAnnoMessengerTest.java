/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JavaAnnoMessengerTest {

    private final static Exception exp = new Exception("exp");
    @Mock
    private JavaAnnoElements elements;
    @Mock
    private ProcessingEnvironment env;

    private JavaAnnoMessenger logger;

    @BeforeEach
    void beforeEach() {
        logger = new JavaAnnoMessenger(env, elements);
    }

    @Test
    void test_last_arg_is_exception() {
        assertThat(logger.format("foo", exp))
                .containsPattern("""
                        foo : java.lang.Exception: exp
                        	at io.kaumei.jdbc.anno.JavaAnnoMessengerTest.<clinit>(.*)
                        	at java.base/jdk.internal.misc.Unsafe.ensureClassInitialized0(.+)
                        """);
    }

    @Test
    void test_last_arg_is_collection() {
        // given
        var given = List.of("a", "b");
        // when ... then
        assertThat(logger.format("foo", given).toString()).isEqualTo("foo [size:2, a, b]");
    }

    @Test
    void test_last_arg_is_iterable() {
        var given = new MsgSetBuilder();
        given.add(Msg.of("a"));
        given.add(Msg.of("b"));
        // when ... then
        assertThat(logger.format("foo", given.build()).toString()).isEqualTo("foo [a, b]");
    }

}