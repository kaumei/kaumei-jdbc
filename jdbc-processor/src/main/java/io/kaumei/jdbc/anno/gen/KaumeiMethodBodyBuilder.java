/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.CodeGenerationException;
import io.kaumei.jdbc.JdbcEmptyResultSetException;
import io.kaumei.jdbc.JdbcUnexpectedRowException;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcConverter;
import io.kaumei.jdbc.anno.jdbc2java.ColumnIndex;
import io.kaumei.jdbc.anno.jdbc2java.Jdbc2JavaConverter;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import io.kaumei.jdbc.anno.store.ConverterSearch;
import io.kaumei.jdbc.anno.store.SearchKey;
import io.kaumei.jdbc.anno.utils.SqlParser;
import io.kaumei.jdbc.annotation.config.JdbcNoMoreRows;
import io.kaumei.jdbc.annotation.config.JdbcNoRows;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import java.util.Optional;
import java.util.TreeSet;

public class KaumeiMethodBodyBuilder {

    // ----- services
    private final JavaAnnoMessenger logger;
    private final ConverterSearch<Java2JdbcConverter> searchJava;
    private final ConverterSearch<Jdbc2JavaConverter> searchJdbc;

    // ------ state
    private final MsgSetBuilder errors = new MsgSetBuilder();
    private final CodeBlock.Builder code = CodeBlock.builder();

    KaumeiMethodBodyBuilder(JavaAnnoMessenger logger, GenerateService service, Element element) {
        this.logger = logger;
        this.searchJava = service.java2JdbcService.getStoreForElement(element);
        this.searchJdbc = service.jdbc2JavaService.getStoreForElement(element);
    }

    KaumeiMethodBodyBuilder(JavaAnnoMessenger logger, ConverterSearch<Java2JdbcConverter> searchJava, ConverterSearch<Jdbc2JavaConverter> searchJdbc) {
        this.logger = logger;
        this.searchJava = searchJava;
        this.searchJdbc = searchJdbc;
    }

    // ------------------------------------------------------------------------

    public Java2JdbcConverter searchJava(SearchKey key) {
        return searchJava.search(key);
    }

    public @Nullable Jdbc2JavaConverter searchJdbc(SearchKey key) {
        var result = searchJdbc.search(key);
        if (!result.hasMessages()) {
            return result;
        }
        this.addError(Msg.invalidConverter(key));
        this.addError(result.messages());
        return null;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return this.code.build().toString();
    }

    // ------------------------------------------------------------------------

    public CodeBlock build(String comment) {
        if (this.hasErrors()) {
            var errorCode = CodeBlock.builder();
            if (!comment.isBlank()) {
                for (var line : comment.split("\n")) {
                    errorCode.add("// " + line + "\n");
                }
            }
            errorCode.addStatement("throw new $T($S)", CodeGenerationException.class, errors().withLinefeed());
            return errorCode.build();
        }
        return code.build();
    }

    // ------------------------------------------------------------------------

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addError(Msg error) {
        this.errors.add(error);
    }

    public void addError(MsgSet error) {
        this.errors.add(error);
    }

    public MsgSet errors() {
        return this.errors.build();
    }

    // ------------------------------------------------------------------------

    public void addComment(String message, Object... args) {
        var msg = this.logger.format(message, args);
        this.logger.debug("Add comment to code:", msg);
        this.code.add("// " + msg + "\n");
    }

    public void indent() {
        this.code.indent();
    }

    public void unindent() {
        this.code.unindent();
    }

    public void beginControlFlow(String controlFlow, Object... args) {
        this.code.beginControlFlow(controlFlow, args);
    }

    public void nextControlFlow(String controlFlow, Object... args) {
        this.code.nextControlFlow(controlFlow, args);
    }

    public void endControlFlow() {
        this.code.endControlFlow();
    }

    public void addStatement(String format, Object... args) {
        this.code.addStatement(format, args);
    }

    public void add(String format, Object... args) {
        this.code.add(format, args);
    }


    public void addCodeBlock(CodeBlock codeBlock) {
        this.code.add(codeBlock);
    }

    public void addStatement(CodeBlock codeBlock) {
        this.code.addStatement(codeBlock);
    }

    // ------------------------------------------------------------------------

    void processParameter(SqlParser.Result sql, GenerateService.MethodParameters methodParameters) {
        this.logger.debug("processParameter", methodParameters);
        var hasCollections = methodParameters.hasCollections();
        if (hasCollections) {
            this.addStatement("var index = 1");
        }
        var notUseNames = new TreeSet<>(methodParameters.parameterMap().keySet());

        for (var e : sql.index2name()) {
            var name = e.name();
            notUseNames.remove(name);

            var param = methodParameters.parameterMap().get(name);
            this.logger.debug("processParameter", "name", name, "param", param);

            var indexCode = hasCollections
                    ? CodeBlock.of("index++")
                    : CodeBlock.of("$L", e.index());
            if (param == null) {
                this.addError(Msg.of("No method parameter with '" + name + "' found."));
            } else if (param.kind().isArray() || param.kind().isList()) {
                this.addStatement("// list");
                this.beginControlFlow("for (var $L : $L)", name + "Item", name);
                param.converter().setParameter(this, name + "Item", indexCode, param.optional());
                this.endControlFlow();
            } else {
                param.converter().setParameter(this, name, indexCode, param.optional());
            }
        }
        for (var name : notUseNames) {
            this.addError(Msg.of("No sql named parameter marker with '" + name + "' found."));
        }
    }

    // ------------------------------------------------------------------------

    void processUnused(KaumeiAnno methodAnno, GenerateService.MethodParameters parameters) {
        if (methodAnno.hasUnused()) {
            this.addError(Msg.of("Method has unused annotations: " + methodAnno.unused()));
        }
        for (var entry : parameters.parameterMap().entrySet()) {
            var anno = entry.getValue().anno();
            if (anno.hasUnused()) {
                var name = entry.getKey();
                this.addError(Msg.of("Parameter '" + name + "' has unused annotations: " + anno.unused()));
            }
        }
    }

    // ------------------------------------------------------------------------

    void addIfAnnotationIsPresent(String format, GenerateService.@Nullable AnnoCode code) {
        if (code != null) {
            if (code.check() != null) {
                this.code.add(code.check());
            }
            this.code.addStatement(format, code.nameOrValue());
        }
    }

    // ------------------------------------------------------------------------

    KaumeiMethodBodyBuilder lambda(String jdbcName, OptionalFlag optional, Jdbc2JavaConverter converter) {
        var lambda = new KaumeiMethodBodyBuilder(this.logger, this.searchJava, this.searchJdbc);
        if (converter.isColumn()) {
            ColumnIndex index;
            if (jdbcName.isEmpty()) {
                index = ColumnIndex.ofValue(1);
            } else {
                index = ColumnIndex.ofVariable("index", jdbcName);
                this.addStatement("var $N = resultSet.findColumn($S)", index.columnIndexVar(), index.columnName());
            }
            lambda.add("(rs) -> {\n");
            lambda.indent();
            converter.addColumnByIndex(lambda, "row", index, optional); // FIXME
            lambda.addStatement("return row");
            lambda.unindent();
            lambda.add("}");
        } else {
            lambda.add("(rs) -> {\n");
            lambda.indent();
            converter.addResultSetToRow(lambda, "row"); // FIXME
            lambda.addStatement("return row");
            lambda.unindent();
            lambda.add("}");
        }

        if (lambda.hasErrors()) {
            addError(lambda.errors());
        }
        return lambda;
    }

    // ------------------------------------------------------------------------

    void converter(Jdbc2JavaConverter converter, String localVarName, KaumeiAnno anno, OptionalFlag optional) {
        if (converter.isColumn()) {
            var jdbcName = anno.jdbcName();
            if (jdbcName.isEmpty()) {
                converter.addColumnByIndex(this, localVarName, ColumnIndex.ofValue(1), optional);
            } else {
                converter.addColumnByName(this, localVarName, jdbcName, optional);
            }
        } else {
            converter.addResultSetToRow(this, localVarName);
        }
    }

    // ------------------------------------------------------------------------

    void addOneResult(Jdbc2JavaConverter jdbcToJava,
                      JdbcNoRows.Kind noRows, JdbcNoMoreRows.Kind noMoreRows,
                      OptionalFlag isMandatory, ColumnIndex index) {
        this.addCheckHasRows(noRows, isMandatory);
        if (jdbcToJava.isColumn()) {
            jdbcToJava.addColumnByIndex(this, "result", index, isMandatory);
        } else {
            jdbcToJava.addResultSetToRow(this, "result");
        }
        this.addCheckNoMoreRows(noMoreRows);
        this.addStatement("return result");
    }

    // ------------------------------------------------------------------------

    public void addThrowColumnWasNull(ColumnIndex index) {
        if (index.hasColumnName()) {
            this.code.addStatement("throw new $T($S + $L)", NullPointerException.class, "JDBC column was null on name '" + index.columnName() + "' with index: ", index.columnIndexVar());
        } else {
            this.code.addStatement("throw new $T($S + $L)", NullPointerException.class, "JDBC column was null on index: ", index.columnIndexVar());
        }
    }

    // ------------------------------------------------------------------------

    void addCheckHasRows(JdbcNoRows.Kind noRows, OptionalFlag optional) {
        switch (noRows) {
            case THROW_EXCEPTION -> {
                this.beginControlFlow("if(!rs.next())");
                this.addStatement("throw new $T()", JdbcEmptyResultSetException.class);
                this.endControlFlow();
            }
            case RETURN_NULL -> {
                // sanity-check:on
                if (optional.isNonNull()) {
                    throw new ProcessorException("optional must not be NONNULL if noRows is RETURN_NULL");
                }
                // sanity-check:off
                this.beginControlFlow("if(!rs.next())");
                if (optional.isOptionalType()) {
                    this.addStatement("return $T.empty()", Optional.class);
                } else {
                    this.addStatement("return null");
                }
                this.endControlFlow();
            }
            default -> throw new ProcessorException("invalid converter: " + noRows); // sanity-check
        }
    }

    // ------------------------------------------------------------------------

    void addCheckNoMoreRows(JdbcNoMoreRows.Kind noMoreRows) {
        switch (noMoreRows) {  // will never cover all branches in black box test: JaCoCo:no
            case THROW_EXCEPTION -> {
                this.beginControlFlow("if(rs.next())");
                this.addStatement("throw new $T()", JdbcUnexpectedRowException.class);
                this.endControlFlow();
            }
            case IGNORE -> this.addStatement("// ignore more results ");
            default ->
                    throw new ProcessorException("invalid converter: " + noMoreRows); // sanity-check
        }
    }

}