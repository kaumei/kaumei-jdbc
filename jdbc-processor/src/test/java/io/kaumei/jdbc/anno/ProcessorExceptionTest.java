/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProcessorExceptionTest {

    private final static Exception e = new Exception();

    @Mock
    private Element element;

    @Test
    void withEmpty() {
        // when
        var pe = new ProcessorException();
        // then
        assertThat(pe.getMessage()).isNull();
        assertThat(pe.getCause()).isNull();
        assertThat(pe.element()).isNull();
    }

    @Test
    void withException() {
        // when
        var pe = new ProcessorException(e);
        // then
        assertThat(pe.getMessage()).isNull();
        assertThat(pe.getCause()).isEqualTo(e);
        assertThat(pe.element()).isNull();
    }

    @Test
    void withString() {
        // when
        var pe = new ProcessorException("foobar");
        // then
        assertThat(pe.getMessage()).isEqualTo("foobar");
        assertThat(pe.getCause()).isNull();
        assertThat(pe.element()).isNull();
    }

    @Test
    void withMessageAndElement() {
        // when
        var pe = new ProcessorException("foobar", element);
        // then
        assertThat(pe.getMessage()).isEqualTo("foobar");
        assertThat(pe.getCause()).isNull();
        assertThat(pe.element()).isEqualTo(element);
    }
}
