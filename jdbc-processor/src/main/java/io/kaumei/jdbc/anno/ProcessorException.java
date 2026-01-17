/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;

public class ProcessorException extends RuntimeException {
    private final @Nullable Element element;

    public ProcessorException() {
        this.element = null;
    }

    public ProcessorException(Exception e) {
        super(e.getMessage(), e);
        this.element = null;
    }

    public ProcessorException(String msg) {
        super(msg);
        this.element = null;
    }

    public ProcessorException(String msg, Element element) {
        super(msg);
        this.element = element;
    }

    public @Nullable Element element() {
        return this.element;
    }
}
