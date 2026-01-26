/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import org.jspecify.annotations.Nullable;

import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;

/**
 * Store converter by name or type. Handles duplicate detection.
 * @param <T>
 */
public abstract class Store<T extends Converter> {

    // ----- services
    protected final JavaAnnoMessenger logger;
    protected final JavaAnnoTypes javaModelUtil;
    private final String name;
    protected final @Nullable Store<T> parent;
    // ----- state
    protected final Map<String, T> map = new HashMap<>();

    // ------------------------------------------------------------------------

    protected Store(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name) {
        this.logger = logger;
        this.javaModelUtil = javaModelUtil;
        this.name = name;
        this.parent = null;
    }

    protected Store(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name, Store<T> parent) {
        this.logger = logger;
        this.javaModelUtil = javaModelUtil;
        this.name = name;
        this.parent = requireNonNull(parent);
    }

    public abstract Store<T> createChildStore(String name);

    protected abstract T createInvalidConverter(MsgSet messages);

    // ------------------------------------------------------------------------

    protected String toKey(String name, TypeMirror type) {
        return name.isEmpty() ? javaModelUtil.getFqn(type) : name;
    }

    /**
     * Register a converter and fails if it's type is already registered
     */
    public void putValid(T value) {
        var key = toKey("", value.type());
        T old = this.map.put(key, value);
        if (old != null) {
            throw new ProcessorException(key + ": found duplicate");
        }
    }

    public void put(String name, T value) {
        put0(name, value.type(), value);
    }

    public T put(SearchKey searchKey, T value) {
        return put0(searchKey.name(), searchKey.type(), value);
    }

    private T put0(String name, TypeMirror type, T value) {
        var key = toKey(name, type);
        T old = this.map.put(key, value);
        if (old == null) {
            return value;
        } else if (!old.hasMessages() && old.isSame(value)) {
            return value;
        }
        // duplicate detected
        var newEntry = createInvalidConverter(MsgSet.merge(old.messages(), value.messages(), Msg.DUPLICATE_KEY));
        if (this.map.put(key, newEntry) == null) {
            throw new ProcessorException("Invalid state for key: " + key);
        }
        this.logger.debug(this.name, "key", key, "messages", newEntry.messages());
        return newEntry;
    }

    // ------------------------------------------------------------------------

    public @Nullable T search(SearchKey searchKey) {
        return searchKey.hasName()
                ? this.searchByName(searchKey.name())
                : this.searchByType(searchKey.type());
    }

    public @Nullable T searchByName(String name) {
        T entry = this.map.get(name);
        if (entry != null) {
            return entry;
        }
        // ----- search parent
        return parent == null ? null : parent.searchByName(name);
    }

    public @Nullable T searchByType(TypeMirror typeMirror) {
        T entry = map.get(toKey("", typeMirror));
        if (entry != null) {
            return entry;
        }
        // ----- search parent
        return parent == null ? null : parent.searchByType(typeMirror);
    }

    // ------------------------------------------------------------------------

    void dump(StringBuilder out) {
        out.append("----- ").append(this.name).append("\n");
        var sorted = new TreeMap<>(map);
        for (var entry : sorted.entrySet()) {
            out.append(entry.getKey());
            var value = entry.getValue();
            if (value.hasMessages()) {
                out.append(": INVALID : ").append(value.messages());
            } else {
                out.append(": VALID");
            }
            out.append("\n");
        }
    }

    int size() {
        return map.size();
    }

}