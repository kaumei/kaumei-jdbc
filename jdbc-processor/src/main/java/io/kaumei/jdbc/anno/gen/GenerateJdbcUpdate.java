/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.MethodSpec;
import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.jdbc2java.ColumnIndex;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.utils.SqlParser;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateJdbcUpdate implements GenerateJdbc {
    // ----- services
    private final JavaAnnoMessenger logger;
    private final GenerateService genService;
    // ------ state
    private final KaumeiBuilder parent;
    private final ExecutableElement method;
    private final KaumeiAnno methodAnnotations;
    private final GenerateService.MethodParameters methodParameters;
    private final KaumeiMethodBuilder methodBuilder;

    GenerateJdbcUpdate(GenerateService genService, KaumeiBuilder parent, ExecutableElement method, KaumeiAnno methodAnnotations) {
        this.logger = genService.logger;
        this.genService = genService;
        this.parent = parent;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.methodParameters = genService.getParameters(method);
        this.methodBuilder = new KaumeiMethodBuilder(this.logger, genService.java2JdbcService.getStoreForElement(method),
                genService.jdbc2JavaService.getStoreForElement(method));
    }

    private MethodSpec build(String comment) {
        return this.methodBuilder.build(this.genService, this.method, comment);
    }

    @Override
    public MethodSpec generateMethod() {
        //this.logger.debug("---- JdbcUpdate ----", "iface", this.parent.type().getSimpleName(), "method", method.getSimpleName());
        var sqlUpdate = methodAnnotations.jdbcUpdate();
        if(sqlUpdate.isEmpty()) {
            methodBuilder.body().addError(Msg.of("@JdbcUpdate must provide a SQL string"));
            return this.build(sqlUpdate);
        }

        var sql = SqlParser.parse(sqlUpdate);

        var returnType = genService.returnType(method, methodAnnotations);
        var returnTypeKind = returnType.type().getKind();
        if(methodAnnotations.hasAnnotation(Anno.JDBC_RETURN_GENERATED_VALUES)) {
            updateReturning(returnType, sql);
        } else if(returnTypeKind == TypeKind.VOID
                || returnTypeKind == TypeKind.INT
                || returnTypeKind == TypeKind.BOOLEAN) {
            updateSimple(sql);
        } else {
            this.methodBuilder.body().addError(Msg.returnTypeNotSupported(method.getReturnType()));
        }
        methodBuilder.body().processUnused(methodAnnotations, methodParameters);
        return this.build(sqlUpdate);
    }

    private void updateSimple(SqlParser.Result sql) {
        var body = methodBuilder.body();
        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        body.beginControlFlow("try (var stmt = con.prepareStatement($S))", sql.nativeSql());
        var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
        body.processParameter(sql, this.methodParameters);
        body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
        // ----
        switch (method.getReturnType().getKind()) { // will never cover all branches in black box test: JaCoCo:no
            case VOID -> body.addStatement("stmt.executeUpdate()");
            case INT -> body.addStatement("return stmt.executeUpdate()");
            case BOOLEAN -> body.addStatement("return stmt.executeUpdate() != 0");
            default ->
                    throw new ProcessorException("Unexpected return type: " + method.getReturnType().getKind()); // sanity-check
        }
        // ----
        body.endControlFlow();
        body.nextControlFlow("catch ($T e)", SQLException.class);
        body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
        body.endControlFlow();
    }

    private void updateReturning(GenerateService.MethodReturn result, SqlParser.Result sql) {
        var body = methodBuilder.body();

        if(result.kind().isVoid()
                || genService.types.isKaumeiJdbcBatch(result.type())
                || !result.kind().isPrimitive() && !result.kind().isObject()
        ) {
            body.addError(Msg.returnTypeNotSupported(method.getReturnType()));
            //} else if(genService.types.isKaumeiJdbcBatch(result.type())) {
            //  body.addError(Msg.returnTypeNotSupported(JdbcReturnGeneratedValues.class,result.type()));
            //} else if(!result.kind().isPrimitive() && !result.kind().isObject()) {
            //  body.addError(Msg.returnTypeNotSupported(JdbcReturnGeneratedValues.class,result.type()));
        }
        var optReason = result.optional().checkNonNullOrUnspecific();
        if(optReason != null) {
            body.addError(Msg.returnTypeOptional(optReason));
        }
        if(body.hasErrors()) {
            return;
        }

        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        var jdbcReturnGeneratedValues = genService.jdbcConfigService.jdbcReturnGeneratedValues(methodAnnotations, method.getEnclosingElement());
        switch (jdbcReturnGeneratedValues) { // will never cover all branches in black box test: JaCoCo:no
            case GENERATED_KEYS -> {
                body.beginControlFlow("try (var stmt = con.prepareStatement($S, $T.RETURN_GENERATED_KEYS))", sql.nativeSql(), Statement.class);
                var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
                body.processParameter(sql, this.methodParameters);
                body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
                body.addStatement("stmt.executeUpdate()");
                body.beginControlFlow("try(var rs = stmt.getGeneratedKeys())");
            }
            case EXECUTE_QUERY -> {
                body.beginControlFlow("try (var stmt = con.prepareStatement($S))", sql.nativeSql());
                var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
                body.processParameter(sql, this.methodParameters);
                body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
                body.beginControlFlow("try(var rs = stmt.executeQuery())");
            }
            default ->
                    throw new ProcessorException("Unexpected return type: " + jdbcReturnGeneratedValues); // sanity-check
        }
        body.addOneResult(result.converter(),
                JdbcNoRows.Kind.THROW_EXCEPTION, JdbcNoMoreRows.Kind.THROW_EXCEPTION,
                OptionalFlag.NON_NULL, ColumnIndex.ofValue(1));
        body.endControlFlow();

        body.endControlFlow();
        body.nextControlFlow("catch ($T e)", SQLException.class);
        body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
        body.endControlFlow();
    }

    // ------------------------------------------------------------------------

    <A extends Annotation, T> GenerateService.@Nullable AnnoCode processAnno(Anno.WithConfigValue<A, T> anno) {
        return genService.searchAnno(anno, this.methodParameters, this.methodAnnotations, method.getEnclosingElement());
    }

}
