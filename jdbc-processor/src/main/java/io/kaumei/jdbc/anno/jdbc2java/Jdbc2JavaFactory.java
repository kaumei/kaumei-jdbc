/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import io.kaumei.jdbc.anno.JavaAnnoElements;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import io.kaumei.jdbc.anno.store.SearchKey;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

import static io.kaumei.jdbc.anno.OptionalFlag.OPTIONAL_TYPE;

class Jdbc2JavaFactory {
    private final Function<VariableElement, String> nameConverter = NameMapper::mapName;
    private final JavaAnnoTypes types;
    private final JavaAnnoElements elements;

    Jdbc2JavaFactory(JavaAnnoTypes types, JavaAnnoElements elements) {
        this.types = types;
        this.elements = elements;
    }

    // ------------------------------------------------------------------------

    Jdbc2JavaConverter converterStatic(ExecutableElement method) {
        var type = switch (method.getKind()) {
            case METHOD -> method.getReturnType();
            case CONSTRUCTOR -> method.getEnclosingElement().asType();
            default -> throw new ProcessorException("Wrong method kind: " + method.getKind());
        };

        var messages = new MsgSetBuilder();
        if(method.getKind() == ElementKind.METHOD && !JavaAnnoElements.isStatic(method)) {
            messages.add(Msg.of("@JdbcToJava method must be static."));
        }
        if(!JavaAnnoElements.isVisible(method)) {
            messages.add(Msg.of("@JdbcToJava method must be visible (public/package)."));
        }
        if(!this.elements.hasValidSqlExceptions(method)) {
            messages.add(Msg.of("@JdbcToJava method throws incompatible exceptions."));
        }
        if(type.getKind() == TypeKind.VOID) {
            messages.add(Msg.of("@JdbcToJava must not return void"));
        }

        var paramSize = method.getParameters().size();
        if(paramSize == 0) {
            messages.add(Msg.of("@JdbcToJava method must have at least one parameter."));
            return new Jdbc2JavaConverter(type, messages.build());
        }

        var firstParamType = method.getParameters().getFirst().asType();
        var isFirstResultSet = this.types.isJavaSqlResultSet(firstParamType);
        if(isFirstResultSet) {
            if(method.getKind() == ElementKind.CONSTRUCTOR && this.types.asElement(type).getKind() == ElementKind.RECORD) {
                messages.add(Msg.of("@JdbcToJava record constructor does not support ResultSet"));
            } else if(paramSize == 1) {
                return this.converterRowResultSet(messages, type, method);
            } else if(paramSize == 2) {
                if(method.getKind() == ElementKind.METHOD) {
                    return this.converterOrMessageSetInt(messages, type, method);
                } else {
                    messages.add(Msg.of("@JdbcToJava class constructor does not support ResultSet,int"));
                }
            } else {
                messages.add(Msg.of("@JdbcToJava with first ResultSet param has to many parameters."));
            }
            return new Jdbc2JavaConverter(type, messages.build());
        } else if(paramSize == 1) {
            return this.converterColumnObject(messages, type, method);
        }
        return this.converterRowObjects(messages, type, method);
    }

    // ------------------------------------------------------------------------

    private Jdbc2JavaConverter converterOrMessageSetInt(MsgSetBuilder messages,
                                                        TypeMirror type,
                                                        ExecutableElement method) {
        // sanity-check:on
        if(method.getParameters().size() != 2) {
            throw new ProcessorException("Invalid parameter count: " + method.getParameters().size());
        }
        // sanity-check:off

        // ----- check parameter statement
        var param0 = method.getParameters().get(0);
        if(!this.types.isSameType(param0.asType(), this.types.JAVA_SQL_ResultSet)) {
            messages.add(Msg.of("@JdbcToJava method first parameter must be a ResultSet."));
        } else if(!this.types.optionalFlag(method, param0.asType()).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava method first parameter must be @NonNull or unspecific."));
        }

        // ----- check parameter: index
        var paramIndex = method.getParameters().get(1);
        if(paramIndex.asType().getKind() != TypeKind.INT) {
            messages.add(Msg.of("@JdbcToJava method second parameter must be int."));
        }

        if(!this.types.optionalFlag(method, type).isNullableOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava method return type must be a @Nullable or unspecific."));
        }

        if(!messages.isEmpty()) {
            return new Jdbc2JavaConverter(type, messages.build());
        }
        return new ConverterColumnResultSet(type, method);
    }

    // ------------------------------------------------------------------------

    private Jdbc2JavaConverter converterColumnObject(MsgSetBuilder messages,
                                                     TypeMirror type,
                                                     ExecutableElement method) {
        // sanity-check:on
        if(method.getParameters().size() != 1) {
            throw new ProcessorException("wrong number of parameters: " + method.getParameters().size());
        }
        // sanity-check:off

        // ----- parameter type
        var param = method.getParameters().getFirst();
        if(Anno.JDBC_NAME.hasAnno(param)) {
            messages.add(Msg.of("@JdbcToJava name mapping not supported for one param"));
        }
        var jdbcType = this.types.erasure(param.asType());
        if(!this.types.optionalFlag(method, param.asType()).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava nullable param not supported"));
        }

        // ----- return type
        if(!this.types.optionalFlag(method, type).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava method return type must be @NonNull or unspecific."));
        }

        var jdbcToJava = new SearchKey(jdbcType);
        if(!messages.isEmpty()) {
            return new Jdbc2JavaConverter(type, messages.build());
        }
        return new ConverterColumnObject(type, method, jdbcToJava);
    }

    // ------------------------------------------------------------------------

    private Jdbc2JavaConverter converterRowObjects(MsgSetBuilder messages,
                                                   TypeMirror type,
                                                   ExecutableElement method) {
        // sanity-check:on
        if(method.getParameters().size() <= 1) {
            throw new ProcessorException("parameter.size must be greater than 1.");
        }
        // sanity-check:off

        // ----- parameter type
        var paramSize = method.getParameters().size();
        var isNonnull = new OptionalFlag[paramSize];
        var jdbcNames = new String[paramSize];
        var paramNameTypes = new SearchKey[paramSize];
        for (int i = 0; i < paramSize; i++) {
            var paramVar = method.getParameters().get(i);
            jdbcNames[i] = nameConverter.apply(paramVar);
            isNonnull[i] = this.types.optionalFlag(method, paramVar.asType());
            if(isNonnull[i] == OPTIONAL_TYPE) {
                messages.add(Msg.of("@JdbcToJava parameter " + paramVar + " invalid Optional"));
            }
            var localName = Anno.JDBC_CONVERTER_NAME.valueOrUnset(paramVar);
            paramNameTypes[i] = new SearchKey(localName, paramVar.asType());
        }

        // ----- return type
        if(!this.types.optionalFlag(method, type).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava method return type must be @NonNull or unspecific."));
        }
        if(!messages.isEmpty()) {
            return new Jdbc2JavaConverter(type, messages.build());
        }

        return new ConverterRowObjects(type, method, isNonnull, jdbcNames, paramNameTypes);
    }

    // ------------------------------------------------------------------------
    private Jdbc2JavaConverter converterRowResultSet(MsgSetBuilder messages,
                                                     TypeMirror type,
                                                     ExecutableElement method) {
        // sanity-check:on
        if(method.getParameters().size() != 1) {
            throw new ProcessorException("Invalid parameter count: " + method.getParameters().size());
        }
        // sanity-check:off

        // ----- parameter type
        var param = method.getParameters().getFirst();
        if(Anno.JDBC_NAME.hasAnno(param)) {
            messages.add(Msg.of("@JdbcToJava name mapping not supported for ResultSet"));
        }
        if(!this.types.isSameType(param.asType(), this.types.JAVA_SQL_ResultSet)) {
            messages.add(Msg.of("@JdbcToJava parameter must be ResultSet"));
        }
        if(!this.types.optionalFlag(method, param.asType()).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava nullable param not supported"));
        }

        // ----- return type
        if(!this.types.optionalFlag(method, type).isNonNullOrUnspecific()) {
            messages.add(Msg.of("@JdbcToJava method return type must be @NonNull or unspecific."));
        }

        if(!messages.isEmpty()) {
            return new Jdbc2JavaConverter(type, messages.build());
        }
        return new ConverterRowResultSet(type, method);
    }

}