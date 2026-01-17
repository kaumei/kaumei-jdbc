/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.annotool;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.annotation.*;
import io.kaumei.jdbc.annotation.config.*;
import org.jspecify.annotations.Nullable;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.function.Function;

public interface Anno<A extends Annotation> {

    Class<A> cls();

    default boolean hasAnno(AnnotatedConstruct element) {
        return element.getAnnotation(cls()) != null;
    }

    interface WithValue<A extends Annotation, T> extends Anno<A> {
        T valueOrUnset(AnnotatedConstruct element);

        T value(AnnotatedConstruct element, AnnotationMirror mirror);

        T unsetValue();

        default boolean isUnset(T value) {
            return unsetValue().equals(value);
        }

    }

    interface WithConfigValue<A extends Annotation, T> extends WithValue<A, T> {
        CodeBlock checkUnsetValue(String varName);

        CodeBlock codeBlock(T value);

        T convertValue(Object obj);

    }

    // ---------------------------------------------------------------------------------------------------
    // definitions
    // ---------------------------------------------------------------------------------------------------

    record NativeProps(@Nullable TypeElement cls, @Nullable String method) {
        static NativeProps of() {
            return new NativeProps(null, null);
        }

        static NativeProps of(AnnotationMirror annoMirror) {
            var map = new AnnoElementValues(annoMirror);
            var cls = map.getTypeElement("cls");
            var method = map.getString("method");
            return new NativeProps(cls, method == null || method.isBlank() ? null : method);
        }
    }

    record ConfigProps(@Nullable TypeElement parent, TypeElement[] converter) {
        static ConfigProps of() {
            return new ConfigProps(null, JavaAnnoTypes.EMPTY);
        }

        static ConfigProps of(AnnotationMirror annoMirror) {
            var map = new AnnoElementValues(annoMirror);
            var parent = map.getTypeElement("parent");
            var converter = map.getTypeElements("converter");
            return new ConfigProps(parent, converter);
        }
    }

    // @formatter:off
    Anno.WithValue<JavaToJdbc,String>        JAVA_TO_JDBC        = new WithValueByElem<>(JavaToJdbc.class,JavaToJdbc::value,"");
    Anno.WithValue<JdbcConverterName,String> JDBC_CONVERTER_NAME = new WithValueByElem<>(JdbcConverterName.class,JdbcConverterName::value,"");
    Anno.NoValue<JdbcDebug>                  JDBC_DEBUG          = new NoValue<>(JdbcDebug.class);
    Anno.WithValue<JdbcName,String>          JDBC_NAME           = new WithValueByElem<>(JdbcName.class,JdbcName::value,"");
    Anno.WithValue<JdbcNative,NativeProps>   JDBC_NATIVE         = new WithValueByMirror<>(JdbcNative.class, NativeProps::of, NativeProps.of());
    Anno.WithValue<JdbcSelect,String>        JDBC_SELECT         = new WithValueByElem<>(JdbcSelect.class,JdbcSelect::value,"");
    Anno.WithValue<JdbcToJava,String>        JDBC_TO_JAVA        = new WithValueByElem<>(JdbcToJava.class,JdbcToJava::value,"");
    Anno.WithValue<JdbcUpdate,String>        JDBC_UPDATE         = new WithValueByElem<>(JdbcUpdate.class,JdbcUpdate::value,"");
    Anno<JdbcUpdateBatch>                    JDBC_UPDATE_BATCH   = new NoValue<>(JdbcUpdateBatch.class);

    Anno.WithConfigValue<JdbcBatchSize,Integer>                                    JDBC_BATCH_SIZE              = new ConfigInteger<>(JdbcBatchSize.class,JdbcBatchSize::value,-1);
    Anno.WithValue<JdbcConfig,ConfigProps>                                         JDBC_CONFIG_PROPS            = new WithValueByMirror<>(JdbcConfig.class,ConfigProps::of,ConfigProps.of());
    Anno.WithConfigValue<JdbcFetchDirection,JdbcFetchDirection.Kind>               JDBC_FETCH_DIRECTION         = new ConfigEnum<>(JdbcFetchDirection.class,JdbcFetchDirection::value,JdbcFetchDirection.Kind.UNSPECIFIED);
    Anno.WithConfigValue<JdbcFetchSize,Integer>                                    JDBC_FETCH_SIZE              = new ConfigInteger<>(JdbcFetchSize.class,JdbcFetchSize::value,-1);
    Anno.WithConfigValue<JdbcMaxRows,Integer>                                      JDBC_MAX_ROWS                = new ConfigInteger<>(JdbcMaxRows.class,JdbcMaxRows::value,-1);
    Anno.WithConfigValue<JdbcNoMoreRows,JdbcNoMoreRows.Kind>                       JDBC_NO_MORE_ROWS            = new ConfigEnum<>(JdbcNoMoreRows.class,JdbcNoMoreRows::value,JdbcNoMoreRows.Kind.UNSPECIFIED);
    Anno.WithConfigValue<JdbcNoRows,JdbcNoRows.Kind>                               JDBC_NO_ROWS                 = new ConfigEnum<>(JdbcNoRows.class,JdbcNoRows::value,JdbcNoRows.Kind.UNSPECIFIED);
    Anno.WithConfigValue<JdbcQueryTimeout,Integer>                                 JDBC_QUERY_TIMEOUT           = new ConfigInteger<>(JdbcQueryTimeout.class,JdbcQueryTimeout::value,-1);
    Anno.WithConfigValue<JdbcResultSetConcurrency,JdbcResultSetConcurrency.Kind>   JDBC_RESULT_SET_CONCURRENCY  = new ConfigEnum<>(JdbcResultSetConcurrency.class,JdbcResultSetConcurrency::value,JdbcResultSetConcurrency.Kind.UNSPECIFIED);
    Anno.WithConfigValue<JdbcResultSetType,JdbcResultSetType.Kind>                 JDBC_RESULT_SET_TYPE         = new ConfigEnum<>(JdbcResultSetType.class,JdbcResultSetType::value,JdbcResultSetType.Kind.UNSPECIFIED);
    Anno.WithConfigValue<JdbcReturnGeneratedValues,JdbcReturnGeneratedValues.Kind> JDBC_RETURN_GENERATED_VALUES = new ConfigEnum<>(JdbcReturnGeneratedValues.class,JdbcReturnGeneratedValues::value,JdbcReturnGeneratedValues.Kind.UNSPECIFIED);
    // @formatter:on

    // ---------------------------------------------------------------------------------------------------
    // implementations
    // ---------------------------------------------------------------------------------------------------

    class NoValue<A extends Annotation> implements Anno<A> {
        private final Class<A> cls;

        NoValue(Class<A> cls) {
            this.cls = cls;
        }

        @Override
        public Class<A> cls() {
            return cls;
        }

        @Override
        public String toString() {
            return cls.getCanonicalName();
        }
    }

    class WithValueByMirror<A extends Annotation, T> implements WithValue<A, T> {
        private final Class<A> cls;
        private final Function<AnnotationMirror, T> func;
        private final T unsetValue;

        WithValueByMirror(Class<A> cls, Function<AnnotationMirror, T> func, T unsetValue) {
            this.cls = cls;
            this.func = func;
            this.unsetValue = unsetValue;
        }

        @Override
        public Class<A> cls() {
            return cls;
        }

        @Override
        public T unsetValue() {
            return unsetValue;
        }

        @Override
        public T valueOrUnset(AnnotatedConstruct element) {
            throw new ProcessorException("not supported"); // sanity-check
        }

        @Override
        public T value(AnnotatedConstruct element, AnnotationMirror mirror) {
            return func.apply(mirror);
        }

        @Override
        public String toString() {
            return cls.getCanonicalName();
        }
    }

    class WithValueByElem<A extends Annotation, T> implements WithValue<A, T> {
        protected final Class<A> cls;
        protected final Function<A, T> func;
        protected final T unsetValue;

        WithValueByElem(Class<A> cls, Function<A, T> func, T unsetValue) {
            this.cls = cls;
            this.func = func;
            this.unsetValue = unsetValue;
        }

        @Override
        public Class<A> cls() {
            return cls;
        }

        @Override
        public T unsetValue() {
            return unsetValue;
        }

        @Override
        public T valueOrUnset(AnnotatedConstruct element) {
            A anno = element.getAnnotation(cls);
            return anno == null ? unsetValue : func.apply(anno);
        }

        @Override
        public T value(AnnotatedConstruct element, AnnotationMirror mirror) {
            A anno = element.getAnnotation(cls);
            return anno == null ? unsetValue : func.apply(anno);
        }

        @Override
        public String toString() {
            return cls.getCanonicalName();
        }
    }

    class ConfigEnum<A extends Annotation, T extends Enum<T>> extends WithValueByElem<A, T> implements WithConfigValue<A, T> {

        private final Class<T> valueType;

        ConfigEnum(Class<A> cls, Function<A, T> func, T unsetValue) {
            super(cls, func, unsetValue);
            valueType = (Class<T>) unsetValue.getClass();
        }

        @Override
        public CodeBlock checkUnsetValue(String varName) {
            return CodeBlock.of("$N == $T.$L", varName, unsetValue.getClass(), unsetValue);
        }

        @Override
        public CodeBlock codeBlock(T value) {
            return CodeBlock.of("$T.$L", value.getClass(), value);
        }

        @Override
        public T convertValue(Object value) {
            if (value instanceof String str) {
                return Enum.valueOf(valueType, str);
            }
            return valueType.cast(value);
        }

    }

    class ConfigInteger<A extends Annotation> extends WithValueByElem<A, Integer> implements WithConfigValue<A, Integer> {

        ConfigInteger(Class<A> cls, Function<A, Integer> func, Integer unsetValue) {
            super(cls, func, unsetValue);
        }

        @Override
        public CodeBlock checkUnsetValue(String varName) {
            return CodeBlock.of("$N == $L", varName, unsetValue);
        }

        @Override
        public CodeBlock codeBlock(java.lang.Integer value) {
            return CodeBlock.of("$L", value);
        }

        @Override
        public Integer convertValue(Object value) {
            if (value instanceof java.lang.Integer i) {
                return i;
            } else if (value instanceof String s) {
                return Integer.parseInt(s);
            }
            throw new ProcessorException("Incompatible input: " + value);
        }

    }

}
