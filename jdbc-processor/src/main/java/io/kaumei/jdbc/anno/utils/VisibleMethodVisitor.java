/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.utils;

import javax.lang.model.element.*;
import javax.lang.model.util.AbstractElementVisitor14;

public abstract class VisibleMethodVisitor<T> extends AbstractElementVisitor14<Void, T> {

    @Override
    public Void visitType(TypeElement e, T repository) {
        for (var child : e.getEnclosedElements()) {
            if (child.getKind() == ElementKind.METHOD) {
                this.visit(child, repository);
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------

    @Override
    public Void visitPackage(PackageElement e, T repository) {
        throw new UnsupportedOperationException("visitPackage"); // sanity-check
    }

    @Override
    public Void visitVariable(VariableElement e, T repository) {
        throw new UnsupportedOperationException("visitVariable"); // sanity-check
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, T repository) {
        throw new UnsupportedOperationException("visitTypeParameter"); // sanity-check
    }

    @Override
    public Void visitRecordComponent(RecordComponentElement e, T repository) {
        throw new UnsupportedOperationException("visitRecordComponent"); // sanity-check
    }

    @Override
    public Void visitModule(ModuleElement e, T repository) {
        throw new UnsupportedOperationException("visitModule"); // sanity-check
    }

}