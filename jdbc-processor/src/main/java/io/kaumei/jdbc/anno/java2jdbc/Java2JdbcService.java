/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.java2jdbc;

import io.kaumei.jdbc.JavaToJdbcConverter;
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

import static javax.lang.model.element.ElementKind.RECORD;

public class Java2JdbcService implements ProcessorSteps, TryToGenerate<Java2JdbcConverter> {
    // ----- services
    private final JavaAnnoMessenger logger;
    private final JavaAnnoTypes types;
    // ----- state
    private final Java2JdbcFactory converterFactory;
    private final StoreJava2Jdbc basic;
    private final StoreJava2Jdbc global;
    private final CompositeStore<Java2JdbcConverter> compositeStore;

    public Java2JdbcService(JavaAnnoMessenger logger, JavaAnnoTypes types, JavaAnnoElements elements) {
        this.logger = logger;
        this.types = types;
        this.converterFactory = new Java2JdbcFactory(this.logger, this.types, elements);
        this.basic = new StoreJava2Jdbc(this.logger, this.types, "java2jdbc.basic");
        this.global = this.basic.createChildStore("java2jdbc.global");
        this.compositeStore = new CompositeStore<>(logger, this.types, "java2jdbc", this.basic, this.global, this);
    }

    // ------------------------------------------------------------------------

    @Override
    public void process(ProcessorEnvironment roundEnv) {
        this.logger.info("Process Kaumei JDBC processor JavaToJdbc converter.");
        this.processBasicConverter();
        for (var elem0 : roundEnv.javaToJdbc()) {
            this.logger.acceptWithDebugFlag(elem0, (elem) -> this.process(this.global, elem));
        }
        this.compositeStore.process(roundEnv);
        for (var elem0 : roundEnv.jdbcInterfaces()) {
            var store = this.compositeStore.getStoreForElement(elem0);
            this.logger.acceptWithDebugFlag(elem0, (elem) -> this.processType(store, elem));
        }
    }

    // ------------------------------------------------------------------------

    private void processBasicConverter() {
        basic.putValid(jdbcStatement("setBoolean", this.types.typeMirror(TypeKind.BOOLEAN)));
        basic.putValid(jdbcStatement("setByte", this.types.typeMirror(TypeKind.BYTE)));
        basic.putValid(jdbcStatementStatic("setChar", this.types.typeMirror(TypeKind.CHAR)));
        basic.putValid(jdbcStatement("setShort", this.types.typeMirror(TypeKind.SHORT)));
        basic.putValid(jdbcStatement("setInt", this.types.typeMirror(TypeKind.INT)));
        basic.putValid(jdbcStatement("setLong", this.types.typeMirror(TypeKind.LONG)));
        basic.putValid(jdbcStatement("setFloat", this.types.typeMirror(TypeKind.FLOAT)));
        basic.putValid(jdbcStatement("setDouble", this.types.typeMirror(TypeKind.DOUBLE)));
        // ----
        basic.putValid(jdbcStatement("setBigDecimal", this.types.typeMirror(java.math.BigDecimal.class)));
        basic.putValid(jdbcStatement("setString", this.types.typeMirror(java.lang.String.class)));
        basic.putValid(jdbcStatement("setDate", this.types.typeMirror(java.sql.Date.class)));
        basic.putValid(jdbcStatement("setTime", this.types.typeMirror(java.sql.Time.class)));
        basic.putValid(jdbcStatement("setTimestamp", this.types.typeMirror(java.sql.Timestamp.class)));
        basic.putValid(jdbcStatement("setObject", this.types.typeMirror(java.sql.Struct.class)));
        basic.putValid(jdbcStatement("setRef", this.types.typeMirror(java.sql.Ref.class)));
        basic.putValid(jdbcStatement("setBlob", this.types.typeMirror(java.sql.Blob.class)));
        basic.putValid(jdbcStatement("setClob", this.types.typeMirror(java.sql.Clob.class)));
        basic.putValid(jdbcStatement("setArray", this.types.typeMirror(java.sql.Array.class)));
        basic.putValid(jdbcStatement("setURL", this.types.typeMirror(java.net.URL.class)));
        basic.putValid(jdbcStatement("setRowId", this.types.typeMirror(java.sql.RowId.class)));
        basic.putValid(jdbcStatement("setNClob", this.types.typeMirror(java.sql.NClob.class)));
        basic.putValid(jdbcStatement("setSQLXML", this.types.typeMirror(java.sql.SQLXML.class)));
        basic.putValid(jdbcStatement("setBytes", this.types.getArrayType(this.types.typeMirror(TypeKind.BYTE))));
        // ----
        basic.putValid(jdbcStatementStatic("setBoolean", this.types.typeMirror(Boolean.class)));
        basic.putValid(jdbcStatementStatic("setByte", this.types.typeMirror(Byte.class)));
        basic.putValid(jdbcStatementStatic("setCharacter", this.types.typeMirror(Character.class)));
        basic.putValid(jdbcStatementStatic("setDouble", this.types.typeMirror(Double.class)));
        basic.putValid(jdbcStatementStatic("setFloat", this.types.typeMirror(Float.class)));
        basic.putValid(jdbcStatementStatic("setInteger", this.types.typeMirror(Integer.class)));
        basic.putValid(jdbcStatementStatic("setLong", this.types.typeMirror(Long.class)));
        basic.putValid(jdbcStatementStatic("setShort", this.types.typeMirror(Short.class)));
    }

    private Java2JdbcConverter jdbcStatement(String methodName, TypeMirror type) {
        return new ConverterJdbcStatement(type, methodName);
    }

    private Java2JdbcConverter jdbcStatementStatic(String methodName, TypeMirror type) {
        return new ConverterStaticStatement(type, JavaToJdbcConverter.class.getCanonicalName(), methodName);
    }

    private void process(Store<Java2JdbcConverter> store, Element element) {
        if(element instanceof TypeElement type) {
            processType(store, type);
        } else if(element instanceof ExecutableElement executable) {
            processExecutable(store, false, executable);
        } else {
            this.logger.error(element, "Unknown annotated element. Element is ignored.");
        }
    }

    private void processType(Store<Java2JdbcConverter> store, TypeElement type) {
        boolean clsFlag = Anno.JAVA_TO_JDBC.hasAnno(type);
        for (Element child : type.getEnclosedElements()) {
            if(child instanceof ExecutableElement executable0) {
                this.logger.acceptWithDebugFlag(executable0, (executable) ->
                        this.processExecutable(store, clsFlag, executable));
            }
        }
    }

    private void processExecutable(Store<Java2JdbcConverter> store, boolean addIfValid, ExecutableElement executable) {
        var converter = this.converterFactory.converterMethod(executable);
        if(Anno.JAVA_TO_JDBC.hasAnno(executable)) {
            var name = Anno.JAVA_TO_JDBC.valueOrUnset(executable);
            if(Anno.JAVA_TO_JDBC.isUnset(name)) {
                if(converter.hasType()) {
                    store.put(name, converter);
                } else {
                    // we could ne get a type, this is logged in the converterMethod method
                }
            } else {
                store.put(name, converter);
            }
        } else if(addIfValid && !converter.hasMessages()) {
            store.put("", converter);
        }
    }

    // ------------------------------------------------------------------------

    public ConverterSearch<Java2JdbcConverter> getStoreForElement(Element context) {
        return this.compositeStore.getSearchForElement(context);
    }

    // ------------------------------------------------------------------------
    @Override
    public Java2JdbcConverter tryToCreate(SearchKey searchKey) {
        var typeMirror = searchKey.type();
        this.logger.debug("@JavaToJdbc.createConverterFor", typeMirror);
        var elem = this.types.asElementOpt(typeMirror);
        if(elem == null) {
            return new Java2JdbcConverter(typeMirror, Msg.of("@JavaToJdbc element type not found for " + typeMirror));
        }
        var converter = this.logger.applyWithDebugFlag(elem, (elem0) -> this.tryToCreate(typeMirror, elem0));
        return this.compositeStore.getStoreForElement(elem).put(searchKey, converter);
    }

    private Java2JdbcConverter tryToCreate(TypeMirror typeMirror, Element elem) {
        var annotatedMessages = new MsgSetBuilder();
        Java2JdbcConverter annotatedConverter = null;

        for (var child : elem.getEnclosedElements()) {
            if(!(child instanceof ExecutableElement executable)) {
                continue;
            } else if(!Anno.JAVA_TO_JDBC.hasAnno(executable)) {
                continue;
            }
            var name = Anno.JAVA_TO_JDBC.valueOrUnset(executable);
            if(Anno.JAVA_TO_JDBC.isUnset(name)) {
                var converter = converterFactory.converterMethod(executable);
                if(converter.hasMessages()) {
                    annotatedMessages.add(converter.messages());
                } else if(!checkType(typeMirror, converter)) {
                    annotatedMessages.add(Msg.of("Annotation has wrong type: " + converter.type()));
                } else if(annotatedConverter == null) {
                    annotatedConverter = converter;
                } else {
                    annotatedMessages.add(Msg.of("To many annotations."));
                }
            } else {
                annotatedMessages.add(Msg.of("Annotation must not have a name"));
            }
        }

        if(!annotatedMessages.isEmpty()) {
            return new Java2JdbcConverter(typeMirror, annotatedMessages.build());
        } else if(annotatedConverter != null) {
            return annotatedConverter;
        } else if(elem instanceof TypeElement type) {
            if(type.getKind() == RECORD) {
                return this.converterFactory.converterRecord(type);
            } else if(type.getKind() == ElementKind.ENUM) {
                return this.converterFactory.converterEnum(type);
            }
        }
        return new Java2JdbcConverter(typeMirror, Msg.of("@JavaToJdbc Element type '" + elem.getKind() + "' not supported for " + typeMirror));
    }

    @Override
    public boolean checkType(TypeMirror expectedType, Java2JdbcConverter entry) {
        return this.types.isSubtype(expectedType, entry.type());
    }

    // ------------------------------------------------------------------------

    public void dump(StringBuilder out) {
        this.compositeStore.dump(out);
    }

    public String csvStats() {
        return compositeStore.csvStats();
    }

}
