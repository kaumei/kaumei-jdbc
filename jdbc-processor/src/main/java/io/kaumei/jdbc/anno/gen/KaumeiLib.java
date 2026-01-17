/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.CodeBlock;
import io.kaumei.jdbc.anno.OptionalFlag;
import io.kaumei.jdbc.anno.ProcessorException;
import io.kaumei.jdbc.impl.ResultSetUtils;

import javax.lang.model.type.TypeMirror;
import java.sql.Types;
import java.util.Objects;

public final class KaumeiLib {

    private KaumeiLib() {
        // prevent instantiation
    }

    // ------------------------------------------------------------------------

    public static CodeBlock requireNonNull(String javaName) {
        return CodeBlock.of("$T.requireNonNull($L,$S)", Objects.class, javaName, javaName);
    }

    public static CodeBlock nullCheck(OptionalFlag isMandatory, TypeMirror type, String javaName) {
        return isMandatory.isNonNull() && !type.getKind().isPrimitive()
                ? CodeBlock.of("$T.requireNonNull($L,$S)", Objects.class, javaName, javaName)
                : CodeBlock.of("$L", javaName);
    }

    public static CodeBlock setNull(TypeMirror type, CodeBlock columnIndex) {
        // @formatter:off
        return switch (type.getKind()) { // will never cover all branches in black box test: JaCoCo:no
            case BOOLEAN -> CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "BOOLEAN");
            case BYTE ->    CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "TINYINT");
            case SHORT ->   CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "SMALLINT");
            case INT ->     CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "INTEGER");
            case LONG ->    CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "BIGINT");
            case CHAR ->    CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "CHAR");
            case FLOAT ->   CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "REAL");
            case DOUBLE ->  CodeBlock.of("stmt.setNull($L, $T.$L)", columnIndex, Types.class, "DOUBLE");
            default -> throw new ProcessorException("type must be primitive"); // sanity-check
        };
        // @formatter:on
    }

    public static CodeBlock throwNullPointerException(String message) {
        return CodeBlock.of("throw new $T($S)", NullPointerException.class, message);
    }

    public static CodeBlock marks(CodeBlock count) {
        return CodeBlock.of("$T.marks($L)", ResultSetUtils.class, count);
    }

}
