/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JdbcTypeKind;
import io.kaumei.jdbc.anno.ProcessorException;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;

import static java.util.Objects.requireNonNull;

public class StoreResolve<T extends Converter> {

    private final JdbcTypeKind kind;
    private final SearchKey searchKey;
    private final @Nullable T converter;
    private final @Nullable TypeMirror component;

    public StoreResolve(JdbcTypeKind kind,
                        SearchKey searchKey,
                        @Nullable T converter,
                        @Nullable TypeMirror component) {
        // sanity-check:on
        if(converter != null && component != null) {
            throw new ProcessorException(converter + "," + component);
        }
        // sanity-check:off
        this.kind = kind;
        this.searchKey = searchKey;
        this.converter = converter;
        this.component = component;
    }

    @Override
    public String toString() {
        if(kind == JdbcTypeKind.VOID) {
            return "Result{" + kind + "}";
        }
        return "Result{" + kind
                + ", " + searchKey.type()
                + ", " + converter + ", " + component + "}";
    }

    // ------------------------------------------------------------------------

    public JdbcTypeKind kind() {
        return this.kind;
    }

    // ------------------------------------------------------------------------

    public TypeMirror type() {
        return this.searchKey.type();
    }

    public boolean hasValidConverter() {
        return converter != null && !converter.hasMessages();
    }

    public boolean hasConverter() {
        return converter != null;
    }

    public @Nullable T converterOpt() {
        return this.converter;
    }

    public T converter() {
        return requireNonNull(this.converter);
    }

    public boolean hasMessages() {
        return this.converter == null || this.converter.hasMessages();
    }

    /*
        public MsgSet messages() {
            return this.converter == null ? Msg.of("No converter found") : this.converter.messages();
        }
        public boolean hasComponent() {
            return this.component != null;
        }
    */
    public TypeMirror component() {
        return requireNonNull(this.component);
    }
}
