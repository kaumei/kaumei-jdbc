/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

public final class JavaAnnoFiler {
    // ---- service
    private final JavaAnnoMessenger logger;
    private final Filer filer;

    public JavaAnnoFiler(JavaAnnoMessenger logger, Filer filer) {
        this.logger = logger;
        this.filer = filer;
    }

    // ------------------------------------------------------------------------

    public void writeJava(TypeElement elem, String packageName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .addFileComment(Processor.FILE_COMMENT)
                .indent("    ")
                .build();
        try {
            javaFile.writeTo(this.filer);
        } catch (IOException e) { // sanity-check
            this.logger.error(elem, "Failed to write generated file: " + javaFile.toJavaFileObject().getName()); // sanity-check
        }
    }
}