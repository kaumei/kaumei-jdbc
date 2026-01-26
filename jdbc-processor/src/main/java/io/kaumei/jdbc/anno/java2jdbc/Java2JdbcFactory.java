/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.java2jdbc;

import io.kaumei.jdbc.anno.JavaAnnoElements;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import io.kaumei.jdbc.anno.store.SearchKey;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.util.List;

class Java2JdbcFactory {
    private final JavaAnnoMessenger logger;
    private final JavaAnnoTypes types;
    private final JavaAnnoElements elements;

    Java2JdbcFactory(JavaAnnoMessenger logger, JavaAnnoTypes types, JavaAnnoElements elements) {
        this.logger = logger;
        this.types = types;
        this.elements = elements;
    }

    // ------------------------------------------------------------------------

    Java2JdbcConverter converterEnum(Element elem) {
        // sanity-check:on
        if (elem.getKind() != ElementKind.ENUM) {
            throw new ProcessorException("Element must be a record.");
        }
        // sanity-check:off
        return new ConverterEnum(elem.asType(), new SearchKey(this.types.JAVA_String));
    }

    // ------------------------------------------------------------------------

    Java2JdbcConverter converterObjectSimple(ExecutableElement method) {
        var type = method.getEnclosingElement().asType();
        var messages = new MsgSetBuilder();

        // sanity-check:on
        if (JavaAnnoElements.isStatic(method)) {
            messages.add(Msg.of("@JavaToJdbc must not be static"));
        }
        if (!JavaAnnoElements.isVisible(method)) {
            messages.add(Msg.of("@JavaToJdbc must be visible (public/package)"));
        }
        // sanity-check:off

        // ----- check return type
        var returnType = method.getReturnType();
        if (returnType.getKind() == TypeKind.VOID) {
            messages.add(Msg.INVALID_RETURN_TYPE);
        } else if (!this.types.optionalFlag(method, returnType).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava return type must be @NonNull or unspecific"));
        }
        // ----- check parameter
        if (!method.getParameters().isEmpty()) {
            messages.add(Msg.of("@JavaToJdbc method must have no parameters"));
        }
        // ----- check throws
        if (!this.elements.hasValidSqlExceptions(method)) {
            messages.add(Msg.of("has incompatible exceptions"));
        }

        if (!messages.isEmpty()) {
            return new Java2JdbcConverter(type, messages.build());
        }
        return new ConverterObjectSimple(type, new SearchKey(returnType), method.getSimpleName());
    }

    // ------------------------------------------------------------------------

    Java2JdbcConverter converterRecord(TypeElement elem) {
        // sanity-check:on
        if (elem.getKind() != ElementKind.RECORD) {
            throw new ProcessorException("Element must be a record.");
        }
        // sanity-check:off

        var type = elem.asType();

        List<? extends RecordComponentElement> components = elem.getRecordComponents();
        if (components.size() != 1) {
            return new Java2JdbcConverter(type, Msg.of("Record must have exact one component."));
        }

        var errors = new MsgSetBuilder();

        var component = components.getFirst();
        var cType = component.asType();
        if (!this.types.optionalFlag(elem, cType).isNonNullOrUnspecific()) {
            errors.add(Msg.of("Record component must be 'non-null' or 'unspecific'."));
        }

        if (!errors.isEmpty()) {
            return new Java2JdbcConverter(type, errors.build());
        }

        return new ConverterRecord(type, new SearchKey(cType), component.getSimpleName());
    }

    // ------------------------------------------------------------------------

    Java2JdbcConverter converterMethod(ExecutableElement elem) {
        if (JavaAnnoElements.isStatic(elem)) {
            return converterStatic(elem);
        } else {
            return converterObjectSimple(elem);
        }
    }

    // ------------------------------------------------------------------------

    Java2JdbcConverter converterStatic(ExecutableElement method) {
        var errors = new MsgSetBuilder();
        if (!JavaAnnoElements.isStatic(method)) {
            errors.add(Msg.of("@JavaToJdbc Must be static."));
        }
        if (!JavaAnnoElements.isVisible(method)) {
            errors.add(Msg.of("@JavaToJdbc Must be visible (public/package)."));
        }
        var paramSize = method.getParameters().size();
        if (paramSize == 1) {
            return converterStaticSimple(errors, method);
        } else if (paramSize == 3) {
            return converterStaticStatement(errors, method);
        }
        errors.add(Msg.of("Must have one or three parameters."));
        this.logger.warn(method, "@JavaToJdbc unsupported method.", "errors", errors);
        return new Java2JdbcConverter(null, errors.build());
    }

    // ------------------------------------------------------------------------

    private Java2JdbcConverter converterStaticSimple(MsgSetBuilder messages, ExecutableElement method) {
        // sanity-check:on
        if (method.getParameters().size() != 1) {
            throw new ProcessorException("Invalid parameter count: " + method.getParameters().size());
        }
        // sanity-check:off

        // ----- check return type
        var returnType = method.getReturnType();
        if (returnType.getKind() == TypeKind.VOID) {
            messages.add(Msg.INVALID_RETURN_TYPE);
        } else if (!this.types.optionalFlag(method, returnType).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava return type must be @NonNull or unspecific"));
        }

        // ----- check throws
        if (!this.elements.hasValidSqlExceptions(method)) {
            messages.add(Msg.of("has incompatible exceptions"));
        }

        var paramType = method.getParameters().getFirst().asType();
        if (!messages.isEmpty()) {
            return new Java2JdbcConverter(paramType, messages.build());
        }

        return new ConverterStaticSimple(paramType, new SearchKey(returnType),
                JavaAnnoElements.getQualifiedClassName(method),
                method.getSimpleName());
    }

    // ------------------------------------------------------------------------

    private Java2JdbcConverter converterStaticStatement(MsgSetBuilder messages, ExecutableElement method) {
        // sanity-check:on
        if (method.getParameters().size() != 3) {
            throw new ProcessorException("Invalid parameter count: " + method.getParameters().size());
        }
        // sanity-check:off

        // ----- check return type
        var returnType = method.getReturnType();
        if (returnType.getKind() != TypeKind.VOID) {
            messages.add(Msg.INVALID_RETURN_TYPE);
        }

        // ----- check throws
        if (!this.elements.hasValidSqlExceptions(method)) {
            messages.add(Msg.of("has incompatible exceptions"));
        }

        // ----- check parameter statement
        var paramStmt = method.getParameters().get(0);
        if (!this.types.isSameType(paramStmt.asType(), this.types.JAVA_SQL_PreparedStatement)) {
            messages.add(Msg.of("first parameter must be a PreparedStatement"));
        } else if (!this.types.optionalFlag(method, paramStmt.asType()).isNonNullOrUnspecific()) {
            messages.add(Msg.of("first parameter must be must be @NonNull or unspecific"));
        }

        // ----- check parameter: index
        var paramIndex = method.getParameters().get(1);
        if (paramIndex.asType().getKind() != TypeKind.INT) {
            messages.add(Msg.of("second parameter must be int"));
        }

        // ----- check parameter: value
        var paramValue = method.getParameters().get(2);
        var paramValueType = paramValue.asType();
        if (!paramValueType.getKind().isPrimitive()
                && !this.types.optionalFlag(method, paramValueType).isNullableOrUnspecific()) {
            messages.add(Msg.of("third parameter must be @Nullable or unspecific"));
        }

        if (!messages.isEmpty()) {
            return new Java2JdbcConverter(paramValueType, messages.build());
        }

        return new ConverterStaticStatement(paramValueType,
                JavaAnnoElements.getQualifiedClassName(method).toString(),
                method.getSimpleName().toString());
    }
}
