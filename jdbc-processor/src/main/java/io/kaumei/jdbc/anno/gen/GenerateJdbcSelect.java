/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.MethodSpec;
import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.utils.SqlParser;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import io.kaumei.jdbc.impl.JdbcUtils;
import io.kaumei.jdbc.impl.ResultSetUtils;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class GenerateJdbcSelect implements GenerateJdbc {
    // ----- services
    private final GenerateService genService;
    private final JavaAnnoMessenger logger;
    // ------ state
    private final KaumeiBuilder parent;
    private final ExecutableElement method;
    private final KaumeiAnno methodAnnotations;
    private final GenerateService.MethodParameters methodParameters;
    private final KaumeiMethodBuilder methodBuilder;

    GenerateJdbcSelect(GenerateService genService, KaumeiBuilder parent, ExecutableElement method, KaumeiAnno methodAnnotations) {
        this.genService = genService;
        this.logger = genService.logger;
        this.parent = parent;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.methodParameters = genService.getParameters(method);
        this.methodBuilder = new KaumeiMethodBuilder(this.logger,
                genService.java2JdbcService.getStoreForElement(parent.type()),
                genService.jdbc2JavaService.getStoreForElement(parent.type()));
    }

    private MethodSpec build(String comment) {
        return this.methodBuilder.build(this.genService, this.method, comment);
    }

    // ------------------------------------------------------------------------

    @Override
    public MethodSpec generateMethod() {
        this.logger.debug("---- JdbcSelect ---- ", "method", method, "returnType", method.getReturnType());

        var sqlSelect = methodAnnotations.jdbcSelect();
        // sanity-check:on
        if (sqlSelect.isEmpty()) {
            methodBuilder.body().addError(Msg.of("@JdbcSelect must provide a SQL string"));
            return this.build(sqlSelect);
        }
        // sanity-check:off

        var returnType = genService.returnType(method, methodAnnotations);
        this.logger.debug("returnType", returnType);
        if (returnType.hasMessages() || this.methodParameters.hasMessages()) {
            this.methodBuilder.body().addError(returnType.messages());
            this.methodBuilder.body().addError(methodParameters.messages());
            return this.build(sqlSelect);
        }

        if (returnType.kind().isVoid()) {
            this.methodBuilder.body().addError(Msg.returnTypeNotSupported(method.getReturnType()));
            return this.build(sqlSelect);
        }

        var sql = SqlParser.parse(sqlSelect);
        switch (returnType.kind()) { // will never cover all branches in black box test: JaCoCo:no
            case PRIMITIVE, OBJECT, ARRAY, OPTIONAL_TYPE:
                selectValue(sql, returnType);
                break;
            case LIST:
                selectJavaList(sql, returnType);
                break;
            case STREAM, KAUMEI_JDBC_ITERABLE, KAUMEI_JDBC_RESULT_SET:
                selectStreamIterableResultSet(sql, returnType);
                break;
            default:
                String methodStr = this.parent.type().getSimpleName() + "." + method.getSimpleName() + ":" + method.getReturnType();
                throw new ProcessorException(methodStr + ": illegal kind:" + returnType.kind()); // sanity-check
        }

        methodBuilder.body().processUnused(methodAnnotations, methodParameters);
        return this.build(sqlSelect);
    }

    // ------------------------------------------------------------------------

    private void selectValue(SqlParser.Result sql, GenerateService.MethodReturn methodReturn) {
        this.logger.debug("selectValue", "sql", sql, "methodReturn", methodReturn);
        var converter = methodReturn.converter();

        var body = methodBuilder.body();

        var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);

        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        body.addStatement("var sql = $L", sqlToCodeBlock(sql));
        body.beginControlFlow("try (var stmt = con.prepareStatement(sql))");
        body.processParameter(sql, this.methodParameters);
        body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);

        var noMoreRows = genService.jdbcConfigService.searchAnno(Anno.JDBC_NO_MORE_ROWS, this.methodAnnotations, method.getEnclosingElement());
        if (noMoreRows == JdbcNoMoreRows.Kind.THROW_EXCEPTION) {
            body.addStatement("stmt.setFetchSize(2)");
            body.addStatement("stmt.setMaxRows(2)");
        } else {
            body.addStatement("stmt.setFetchSize(1)");
            body.addStatement("stmt.setMaxRows(1)");
        }
        body.beginControlFlow("try (var rs = stmt.executeQuery())");

        var noRows = genService.jdbcConfigService.searchAnno(Anno.JDBC_NO_ROWS, this.methodAnnotations, this.method.getEnclosingElement());
        if (noRows == JdbcNoRows.Kind.RETURN_NULL && methodReturn.optional().isNonNull()) {
            body.addError(Msg.of("@JdbcSelect incompatible: " + noRows + " and '" + methodReturn.optional() + "'"));
        } else {
            body.addCheckHasRows(noRows, methodReturn.optional());
        }

        if (body.hasErrors()) {
            return;
        }
        body.converter(converter, "result", methodAnnotations, methodReturn.optional());
        if (body.hasErrors()) {
            body.addError(Msg.invalidConverter(methodReturn.searchKey()));
        }

        body.addCheckNoMoreRows(noMoreRows);
        body.addStatement("return result");

        body.endControlFlow();
        body.endControlFlow();
        body.nextControlFlow("catch ($T e)", SQLException.class);
        body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
        body.endControlFlow();
    }

    private void selectJavaList(SqlParser.Result sql, GenerateService.MethodReturn methodReturn) {
        var converter = methodReturn.converter();
        var resultType = methodReturn.type();

        var body = methodBuilder.body();
        this.logger.debug("selectJavaCollection", "converter", converter, "resultType", resultType);

        var fetchDirection = processAnno(Anno.JDBC_FETCH_DIRECTION);
        var fetchSize = processAnno(Anno.JDBC_FETCH_SIZE);
        var maxRows = processAnno(Anno.JDBC_MAX_ROWS);
        var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
        var resultSetConcurrency = processAnno(Anno.JDBC_RESULT_SET_CONCURRENCY);
        var resultSetType = processAnno(Anno.JDBC_RESULT_SET_TYPE);

        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        body.addStatement("var sql = $L", sqlToCodeBlock(sql));
        body.beginControlFlow("try (var stmt = $L)", prepareStatement(resultSetType, resultSetConcurrency));
        body.processParameter(sql, this.methodParameters);
        body.addIfAnnotationIsPresent("stmt.setFetchDirection($L.sqlMagicNumber())", fetchDirection);
        body.addIfAnnotationIsPresent("stmt.setFetchSize($L)", fetchSize);
        body.addIfAnnotationIsPresent("stmt.setMaxRows($L)", maxRows);
        body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
        body.beginControlFlow("try (var resultSet = stmt.executeQuery())");

        var jdbcName = converter.isColumn() ? methodAnnotations.jdbcName() : "";
        var lambda = body.lambda(jdbcName, methodReturn.optional(), converter);
        body.addStatement("return $T.toList(resultSet, $L)", ResultSetUtils.class, lambda.toString());

        body.endControlFlow();
        body.endControlFlow();
        body.nextControlFlow("catch ($T e)", SQLException.class);
        body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
        body.endControlFlow();
    }

    private void selectStreamIterableResultSet(SqlParser.Result sql, GenerateService.MethodReturn methodReturn) {
        var converter = methodReturn.converter();
        var body = methodBuilder.body();
        body.addComment("stream");

        var fetchDirection = processAnno(Anno.JDBC_FETCH_DIRECTION);
        var fetchSize = processAnno(Anno.JDBC_FETCH_SIZE);
        var maxRows = processAnno(Anno.JDBC_MAX_ROWS);
        var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
        var resultSetConcurrency = processAnno(Anno.JDBC_RESULT_SET_CONCURRENCY);
        var resultSetType = processAnno(Anno.JDBC_RESULT_SET_TYPE);

        body.addStatement("$T stmt = null", PreparedStatement.class);
        body.addStatement("$T resultSet = null", ResultSet.class);
        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        body.addStatement("var sql = $L", sqlToCodeBlock(sql));
        body.addStatement("stmt = $L", prepareStatement(resultSetType, resultSetConcurrency));
        body.processParameter(sql, this.methodParameters);
        body.addIfAnnotationIsPresent("stmt.setFetchDirection($L.sqlMagicNumber())", fetchDirection);
        body.addIfAnnotationIsPresent("stmt.setFetchSize($L)", fetchSize);
        body.addIfAnnotationIsPresent("stmt.setMaxRows($L)", maxRows);
        body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
        body.addStatement("resultSet = stmt.executeQuery()");

        var jdbcName = converter.isColumn() ? methodAnnotations.jdbcName() : "";
        var lambda = body.lambda(jdbcName, methodReturn.optional(), converter);

        switch (methodReturn.kind()) { // will never cover all branches in black box test: JaCoCo:no
            case STREAM ->
                    body.addStatement("return $T.toStream(stmt, resultSet, $L)", ResultSetUtils.class, lambda.toString());
            case KAUMEI_JDBC_RESULT_SET ->
                    body.addStatement("return $T.toJdbcResultSet(stmt, resultSet, $L)", ResultSetUtils.class, lambda.toString());
            case KAUMEI_JDBC_ITERABLE ->
                    body.addStatement("return $T.toJdbcIterable(stmt, resultSet, $L)", ResultSetUtils.class, lambda.toString());
            default ->
                    throw new ProcessorException("Invalid kind: " + methodReturn.kind()); // sanity-check
        }

        body.nextControlFlow("catch ($T e)", Exception.class);
        body.addStatement("$T.close(e, stmt, resultSet)", JdbcUtils.class);
        body.addStatement("throw e instanceof $T re ? re :new $T(e.getMessage(), e)", RuntimeException.class, JdbcException.class);
        body.endControlFlow();
    }

    // -----------------------------------------------------------------

    private CodeBlock prepareStatement(GenerateService.@Nullable AnnoCode resultSetType, GenerateService.@Nullable AnnoCode resultSetConcurrency) {
        if (resultSetType == null && resultSetConcurrency == null) {
            return CodeBlock.of("con.prepareStatement(sql)");
        } else if (resultSetType != null && resultSetConcurrency != null) {
            if (resultSetType.check() != null) {
                methodBuilder.body().addCodeBlock(resultSetType.check());
            }
            if (resultSetConcurrency.check() != null) {
                methodBuilder.body().addCodeBlock(resultSetConcurrency.check());
            }
            return CodeBlock.of("con.prepareStatement(sql, $L.sqlMagicNumber(), $L.sqlMagicNumber())",
                    resultSetType.nameOrValue(), resultSetConcurrency.nameOrValue());
        }
        this.methodBuilder.body().addError(Msg.of("You must define @JdbcResultSetType and @JdbcResultSetConcurrency."));
        return CodeBlock.of("null");
    }

    private CodeBlock sqlToCodeBlock(SqlParser.Result sql) {
        var code = CodeBlock.builder();
        if (!this.methodParameters.hasCollections()) {
            code.add("$S", sql.nativeSql());
        } else {
            var index = 0;
            for (var entry : sql.index2name()) {
                var name = entry.name();
                var param = this.methodParameters.parameterMap().get(name);
                if (!param.kind().isArray() && !param.kind().isList()) {
                    continue;
                }
                if (index == 0) {
                    code.add("$S", sql.nativeSql().substring(index, entry.pos()));
                } else {
                    code.add(" + $S", sql.nativeSql().substring(index, entry.pos()));
                }
                if (param.kind().isArray()) {
                    code.add(" + $L", KaumeiLib.marks(CodeBlock.of("$L.length", KaumeiLib.requireNonNull(name))));
                } else if (param.kind().isList()) {
                    code.add(" + $L", KaumeiLib.marks(CodeBlock.of("$L.size()", KaumeiLib.requireNonNull(name))));
                }
                index = entry.pos() + 1;
            } // for
            code.add(" + $S", sql.nativeSql().substring(index));
        }
        return code.build();
    }

    // ------------------------------------------------------------------------

    <A extends Annotation, T> GenerateService.@Nullable AnnoCode processAnno(Anno.WithConfigValue<A, T> anno) {
        return genService.searchAnno(anno, this.methodParameters, this.methodAnnotations, method.getEnclosingElement());
    }

}
