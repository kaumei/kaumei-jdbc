/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.jdbc2java;

import io.kaumei.jdbc.JdbcToJavaConverter;
import io.kaumei.jdbc.anno.*;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import io.kaumei.jdbc.anno.store.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.sql.ResultSet;

import static io.kaumei.jdbc.anno.JavaAnnoElements.getStaticMethod;

public class Jdbc2JavaService implements ProcessorSteps, TryToGenerate<Jdbc2JavaConverter> {
    // ----- services
    private final JavaAnnoMessenger logger;
    private final JavaAnnoTypes types;
    // ----- state
    private final Jdbc2JavaFactory converterFactory;
    private final StoreJdbc2Java basic;
    private final StoreJdbc2Java global;
    private final CompositeStore<Jdbc2JavaConverter> compositeStore;

    public Jdbc2JavaService(JavaAnnoMessenger logger, JavaAnnoTypes types, JavaAnnoElements elements) {
        this.logger = logger;
        this.types = types;
        this.converterFactory = new Jdbc2JavaFactory(this.types, elements);
        this.basic = new StoreJdbc2Java(this.logger, this.types, "jdbc2ava.basic");
        this.global = this.basic.createChildStore("jdbc2java.global");
        this.compositeStore = new CompositeStore<>(logger, this.types, "java2jdbc", this.basic, this.global, this);
    }

    // ------------------------------------------------------------------------

    @Override
    public void process(ProcessorEnvironment roundEnv) {
        this.logger.info("Process Kaumei JDBC processor JdbcToJava converter.");
        this.processBasicConverter0();
        for (var elem0 : roundEnv.jdbcToJava()) {
            this.logger.acceptWithDebugFlag(elem0, (elem) -> this.process(this.global, elem));
        }
        this.compositeStore.process(roundEnv);
        for (var elem0 : roundEnv.jdbcInterfaces()) {
            var store = this.compositeStore.getStoreForElement(elem0);
            this.logger.acceptWithDebugFlag(elem0, (elem) -> this.processType(store, elem));
        }
    }

    // ------------------------------------------------------------------------

    private void processBasicConverter0() {
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.BOOLEAN), "getBoolean"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.BYTE), "getByte"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(TypeKind.CHAR), "getChar"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.DOUBLE), "getDouble"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.FLOAT), "getFloat"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.INT), "getInt"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.LONG), "getLong"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(TypeKind.SHORT), "getShort"));
        // ----
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.math.BigDecimal.class), "getBigDecimal"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.lang.String.class), "getString"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Date.class), "getDate"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Time.class), "getTime"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Timestamp.class), "getTimestamp"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(java.sql.Struct.class), "getSqlStruct"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Ref.class), "getRef"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Blob.class), "getBlob"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Clob.class), "getClob"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.Array.class), "getArray"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.net.URL.class), "getURL"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.RowId.class), "getRowId"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.NClob.class), "getNClob"));
        basic.putValid(jdbcResultSet(this.types.typeMirror(java.sql.SQLXML.class), "getSQLXML"));
        basic.putValid(jdbcResultSet(this.types.getArrayType(this.types.typeMirror(TypeKind.BYTE)), "getBytes"));
        // ----
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Boolean.class), "columnToBoolean"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Byte.class), "columnToByte"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Character.class), "columnToCharacter"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Double.class), "columnToDouble"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Float.class), "columnToFloat"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Integer.class), "columnToInteger"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Long.class), "columnToLong"));
        basic.putValid(jdbcToJavaConverter(this.types.typeMirror(Short.class), "columnToShort"));
    }

    private Jdbc2JavaConverter jdbcToJavaConverter(TypeMirror type, String methodName) {
        return new ConverterColumnNative(type, JdbcToJavaConverter.class, methodName);
    }

    private Jdbc2JavaConverter jdbcResultSet(TypeMirror type, String methodName) {
        return new ConverterColumnNative(type, ResultSet.class, methodName);
    }

    private void process(Store<Jdbc2JavaConverter> store, Element element) {
        if(element instanceof TypeElement type) {
            processType(store, type);
        } else if(element instanceof ExecutableElement executable) {
            processExecutable(store, false, executable);
        } else {
            this.logger.error(element, "Unknown annotated element. Element is ignored.");
        }
    }

    private void processType(Store<Jdbc2JavaConverter> store, TypeElement type) {
        boolean clsFlag = Anno.JDBC_TO_JAVA.hasAnno(type);
        for (Element child : type.getEnclosedElements()) {
            if(child instanceof ExecutableElement executable0) {
                this.logger.acceptWithDebugFlag(executable0, (executable) ->
                        this.processExecutable(store, clsFlag, executable));
            }
        }
    }

    private void processExecutable(Store<Jdbc2JavaConverter> store, boolean addIfValid, ExecutableElement executable) {
        var converter = this.converterFactory.converterStatic(executable);
        if(Anno.JDBC_TO_JAVA.hasAnno(executable)) {
            var name = Anno.JDBC_TO_JAVA.valueOrUnset(executable);
            store.put(name, converter);
        } else if(addIfValid && !converter.hasMessages()) {
            store.put("", converter);
        }
    }

    // ------------------------------------------------------------------------

    public ConverterSearch<Jdbc2JavaConverter> getStoreForElement(Element context) {
        return this.compositeStore.getSearchForElement(context);
    }

    // ------------------------------------------------------------------------

    @Override
    public Jdbc2JavaConverter tryToCreate(SearchKey searchKey) {
        this.logger.debug("@JdbcToJava.createConverterFor", searchKey);
        var typeMirror = searchKey.type();
        var elem0 = this.types.asElementOpt(typeMirror);
        if(elem0 == null) {
            return new Jdbc2JavaConverter(typeMirror, Msg.of("@JdbcToJava element type not found for " + typeMirror));
        }
        var converter = this.logger.applyWithDebugFlag(elem0, (elem) -> this.tryToCreate(typeMirror, elem));
        return this.compositeStore.getStoreForElement(elem0).put(searchKey, converter);
    }

    private Jdbc2JavaConverter tryToCreate(TypeMirror typeMirror, Element elem) {
        this.logger.debug("@JdbcToJava.createConverterFor", elem);
        var annotatedMessages = new MsgSetBuilder();
        Jdbc2JavaConverter annotatedConverter = null;
        var skipConstructorCount = 0;
        Jdbc2JavaConverter constructorConverter = null;

        for (var child : elem.getEnclosedElements()) {
            if(!(child instanceof ExecutableElement executable)) {
                continue;
            }
            if(Anno.JDBC_TO_JAVA.hasAnno(executable)) {
                var name = Anno.JDBC_TO_JAVA.valueOrUnset(executable);
                if(Anno.JDBC_TO_JAVA.isUnset(name)) {
                    var converter = converterFactory.converterStatic(executable);
                    this.logger.debug("converter", converter);
                    if(!checkType(typeMirror, converter)) {
                        annotatedMessages.add(Msg.of("Annotation has wrong type: " + converter.type()));
                    } else if(converter.hasMessages()) {
                        annotatedMessages.add(converter.messages());
                    } else if(annotatedConverter == null) {
                        annotatedConverter = converter;
                    } else {
                        annotatedMessages.add(Msg.of("To many annotations."));
                    }
                } else {
                    annotatedMessages.add(Msg.of("Annotation must not have a name"));
                }
            } else if(executable.getKind() == ElementKind.CONSTRUCTOR && !executable.getParameters().isEmpty()) {
                var converter = converterFactory.converterStatic(executable);
                if(constructorConverter == null || constructorConverter.hasMessages()) {
                    constructorConverter = converter;
                } else if(!converter.hasMessages()) {
                    skipConstructorCount++;
                }
            }
        }
        // ----- first check any annotated methods
        if(!annotatedMessages.isEmpty()) {
            return new Jdbc2JavaConverter(typeMirror, annotatedMessages.build());
        } else if(annotatedConverter != null) {
            return annotatedConverter;
        }
        // ----- next check enum
        if(elem.getKind() == ElementKind.ENUM) {
            var factoryMethod = getStaticMethod(elem, "valueOf");
            return converterFactory.converterStatic(factoryMethod);
        }
        // ----- last check constructors
        if(constructorConverter == null) {
            return new Jdbc2JavaConverter(typeMirror, Msg.of("@JdbcToJava no constructor found."));
        } else if(skipConstructorCount > 0) {
            return new Jdbc2JavaConverter(typeMirror, Msg.of("@JdbcToJava to many constructors."));
        }
        return constructorConverter;

    }

    @Override
    public boolean checkType(TypeMirror expectedType, Jdbc2JavaConverter entry) {
        return this.types.isAssignable(entry.type(), expectedType);
    }

    // ------------------------------------------------------------------------

    public void dump(StringBuilder out) {
        this.compositeStore.dump(out);
    }

    public String csvStats() {
        return this.compositeStore.csvStats();
    }

}
