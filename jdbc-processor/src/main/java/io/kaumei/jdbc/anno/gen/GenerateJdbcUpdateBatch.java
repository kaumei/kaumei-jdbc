/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import io.kaumei.jdbc.JdbcException;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.utils.SqlParser;
import io.kaumei.jdbc.impl.JdbcBatchImpl;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenerateJdbcUpdateBatch implements GenerateJdbc {
    // ----- services
    private final JavaAnnoMessenger logger;
    private final GenerateService genService;
    // ------ state
    private final KaumeiClassBuilder parent;
    private final ExecutableElement method;

    private final KaumeiAnno methodAnnotations;
    private final GenerateService.MethodParameters methodParameters;

    private final KaumeiMethodBuilder methodBuilder;

    GenerateJdbcUpdateBatch(GenerateService genService, KaumeiClassBuilder parent, ExecutableElement method, KaumeiAnno methodAnnotations) {
        this.genService = genService;
        this.logger = genService.logger;
        this.parent = parent;
        this.method = method;
        this.methodAnnotations = methodAnnotations;
        this.methodParameters = genService.getParameters(method);
        this.methodBuilder = new KaumeiMethodBuilder(this.logger,
                genService.java2JdbcService.getStoreForElement(method),
                genService.jdbc2JavaService.getStoreForElement(method));
    }

    private MethodSpec build(String comment) {
        return this.methodBuilder.build(this.genService, this.method, comment);
    }

    @Override
    public MethodSpec generateMethod() {
        this.logger.debug("---- @JdbcUpdateBatch ----");

        var body = methodBuilder.body();

        // sanity-check:on
        if(!methodAnnotations.useAnnotation(Anno.JDBC_UPDATE_BATCH)) {
            body.addError(Msg.of("@JdbcUpdateBatch: not found"));
            return this.build("");
        }
        // sanity-check:off

        var returnType = genService.returnType(method, methodAnnotations);
        this.logger.debug("returnType", returnType);

        var optReason = returnType.optional().checkNonNullOrUnspecific();
        if(optReason != null) {
            body.addError(Msg.returnTypeOptional(optReason));
        } else if(!returnType.kind().isKaumeiJdbcBatch()) {
            body.addError(Msg.returnTypeNotSupported(method.getReturnType()));
        }

        var batchSize = processAnno(Anno.JDBC_BATCH_SIZE);
        var batchType = this.genService.types.asElementOpt(returnType.type());
        UpdateMethod updateMethod = batchType == null ? null : getUpdateMethod(batchType);
        if(updateMethod == null || body.hasErrors()) {
            return this.build("");
        }

        // ------------

        var batchClassName = updateMethod.parent.getSimpleName() + "Jdbc";
        var sqlUpdate = updateMethod.anno.useAnnotationOrUnset(Anno.JDBC_UPDATE);
        var sql = SqlParser.parse(sqlUpdate);
        if(!this.parent.containsClass(batchClassName)) {
            var batchClass = TypeSpec.classBuilder(batchClassName)
                    .addModifiers(Modifier.STATIC)
                    .superclass(ClassName.get(JdbcBatchImpl.class))
                    .addSuperinterface(ClassName.get(updateMethod.parent))
                    .addMethod(MethodSpec.constructorBuilder()
                            .addParameter(PreparedStatement.class, "stmt")
                            .addParameter(int.class, "batchSize")
                            .addStatement("super(stmt, batchSize)")
                            .build());
            var batchMethod = genService.createMethodBuilder(updateMethod.method);

            var batchBody = new KaumeiMethodBodyBuilder(this.logger, this.genService, this.method);

            if(sqlUpdate.isEmpty()) {
                batchBody.addError(Msg.of("@JdbcUpdate must provide a SQL string"));
            }
            if(updateMethod.method.getReturnType().getKind() != TypeKind.VOID) {
                batchBody.addError(Msg.returnTypeNotSupported(updateMethod.method.getReturnType(), "must be void"));
            }

            batchBody.beginControlFlow("try");
            batchBody.processParameter(sql, updateMethod.parameters);
            //if (batchRetrunType.getKind() == TypeKind.VOID) {
            batchBody.addStatement("super.addBatch()");
            //} else if (Processor.typeUtils.isIntArray(returnType)) {
            //    body.addStatement("return super.addBatch()");
            //} else if (Processor.typeUtils.isSameType(returnType, Processor.typeUtils.JAVA_Boolean())) {
            //    body.addStatement("return $T.mapExecuteBatchResultToBoolean(super.addBatch())", JdbcBatch.class);
            //} else if (Processor.typeUtils.isSameType(returnType, Processor.typeUtils.JAVA_Integer())) {
            //    body.addStatement("return $T.mapExecuteBatchResultToToSum(super.addBatch())", JdbcBatch.class);
            //} else {
            //    body.addError("Return must be one of: void, int[], Integer or Boolean");
            //}

            batchBody.nextControlFlow("catch ($T e)", SQLException.class);
            batchBody.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
            batchBody.endControlFlow();
            batchMethod.addCode(batchBody.build(""));

            batchClass.addMethod(batchMethod.build());
            this.parent.addClass(batchClassName, batchClass.build());
        }
        // ------------

        // check return type

        body.beginControlFlow("try");
        body.addStatement("var con = supplier.getConnection()");
        // ----
        var queryTimeout = this.processAnno(Anno.JDBC_QUERY_TIMEOUT);
        body.addStatement("var stmt = con.prepareStatement($S)", sql.nativeSql());
        body.addIfAnnotationIsPresent("stmt.setQueryTimeout($L)", queryTimeout);
        if(batchSize != null) {
            body.addStatement("return new $N(stmt, $L)", batchClassName, batchSize.nameOrValue());
        } else {
            throw new ProcessorException();
        }
        // ----
        body.nextControlFlow("catch ($T e)", SQLException.class);
        body.addStatement("throw new $T(e.getMessage(), e)", JdbcException.class);
        body.endControlFlow();

        methodBuilder.body().processUnused(methodAnnotations, methodParameters);
        methodBuilder.body().processUnused(updateMethod.anno, updateMethod.parameters);
        return this.build("");
    }


    record UpdateMethod(TypeElement parent, ExecutableElement method, KaumeiAnno anno,
                        GenerateService.MethodParameters parameters) {
    }

    @Nullable UpdateMethod getUpdateMethod(@Nullable Element batchType) {
        var body = methodBuilder.body();
        if(!(batchType instanceof TypeElement)) {
            body.addError(Msg.returnTypeUnknown(batchType));
            return null;
        }
        if(!batchType.getKind().isInterface()) {
            body.addError(Msg.returnTypeNotSupported(batchType.asType(), "must be an interface"));
        }
        if(!batchType.getEnclosingElement().equals(this.parent.type())) {
            body.addError(Msg.returnTypeNotSupported(batchType.asType(), "must be declared in the interface of the method"));
        }
        var methods = batchType.getEnclosedElements();
        if(methods.size() == 1) {
            var method0 = methods.getFirst();
            if(!method0.getModifiers().contains(Modifier.STATIC)
                    && !method0.getModifiers().contains(Modifier.DEFAULT)
                    && method0.getKind() == ElementKind.METHOD
                    && method0 instanceof ExecutableElement updateMethod) {
                var anno = new KaumeiAnno(updateMethod);
                if(anno.hasJdbcUpdate()) {
                    var parameters = genService.getParameters(updateMethod);
                    return new UpdateMethod((TypeElement) batchType, updateMethod, anno, parameters);
                }
            }
        }
        body.addError(Msg.of("@JdbcUpdateBatch: Batch interface must have exactly one update method"));
        return null;
    }

    // ------------------------------------------------------------------------

    <A extends Annotation, T> GenerateService.@Nullable AnnoCode processAnno(Anno.WithConfigValue<A, T> anno) {
        return genService.searchAnno(anno, this.methodParameters, this.methodAnnotations, method.getEnclosingElement());
    }

}
