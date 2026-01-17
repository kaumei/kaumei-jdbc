/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.msg;

import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcConverter;
import io.kaumei.jdbc.anno.store.SearchKey;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.Set;

public record Msg(String text) implements MsgSet {

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isNotEmpty() {
        return true;
    }

    @Override
    public Iterator<Msg> iterator() {
        return Set.of(this).iterator();
    }

    @Override
    public String toString() {
        return text;
    }

    //  -----------------------------------------------------------------------
    public static Msg MUST_BE_STATIC = Msg.of("must be static.");
    public static Msg MUST_BE_VISIBLE = Msg.of("Must be visible (public/package)");


    public static Msg DUPLICATE_KEY = new Msg("duplicate key");
    public static Msg DUPLICATE_METHOD = new Msg("duplicate method");
    public static Msg INCOMPATIBLE_TYPE = new Msg("incompatible type");
    public static Msg NOT_FOUND = new Msg("not found");

    public static Msg notFound(SearchKey searchKey) {
        return searchKey.hasName()
                ? new Msg("name '" + searchKey.name() + "' not found")
                : new Msg("type '" + searchKey.type() + "' not found");
    }

    public static Msg INVALID_PARAM_OPTIONAL_TYPE = new Msg("""
            Optional<T> parameter type not supported.
            See JavaDoc of Optional:
            Optional is primarily intended for use as a method return ...
            """);
    public static Msg INVALID_RETURN_TYPE = new Msg("Invalid return type.");
    public static Msg INVALID_PARAM_TYPE = new Msg("Invalid parameter type.");
    public static Msg RECORD_COMPONENT_MUST_BE_VALID = new Msg("Record component must be a valid JDBC type.");
    public static Msg INVALID_ENUM_TYPE = new Msg("Invalid enum type.");

    public static Msg of(String text) {
        return new Msg(text);
    }

    public static Msg cycle(SearchKey searchKey, Set<SearchKey> cycle) {
        return new Msg("type " + searchKey + " generate cycle: " + cycle);
    }

    public static Msg cycle() {
        return new Msg("generate cycle");
    }

    public static Msg invalidConverter(SearchKey searchKey) {
        return new Msg("Found invalid converter for " + searchKey);
    }

    public static Msg invalidConverter(TypeMirror type) {
        return new Msg("Found invalid converter for " + type);
    }

    public static Msg invalidConverter(TypeMirror type, MsgSet messages) {
        return new Msg("Found invalid converter for " + type + ": " + messages);
    }

    // ------------------------------------------------------------------------

    public static Msg invalidParam(String name, TypeMirror type, MsgSet messages) {
        return new Msg("Converter invalid. [param: " + name + ", type='" + type + "', msg=" + messages + "]");
    }

    public static Msg invalidParam(String name, TypeMirror type, Java2JdbcConverter converter) {
        return invalidParam(name, type, converter.messages());
    }

    public static Msg invalidParam(String name, TypeMirror type, Java2JdbcConverter converter, MsgSet messages) {
        var msgSet = MsgSet.merge(converter.messages(), messages);
        return invalidParam(name, type, msgSet);
    }

    // ------------------------------------------------------------------------

    public static Msg returnTypeUnknown(@Nullable Element element) {
        return new Msg("return type not supported. [type=" + element + "]");
    }

    public static Msg returnTypeNotSupported(TypeMirror type) {
        return new Msg("return type not supported. [type=" + type + "]");
    }

    public static Msg returnTypeNotSupported(TypeMirror type, String reason) {
        return new Msg("return type not supported. [type=" + type + ", reason=" + reason + "]");
    }

    public static Msg returnTypeOptional(String reason) {
        return Msg.of("return type "+reason);
    }

}
