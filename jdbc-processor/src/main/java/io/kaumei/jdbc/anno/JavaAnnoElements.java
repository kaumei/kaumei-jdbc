/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import org.jspecify.annotations.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class JavaAnnoElements {
    public static final TypeElement[] EMPTY = new TypeElement[0];

    // ---- service
    private final JavaAnnoTypes types;
    private final Elements elements;
    private final @Nullable Trees trees;

    public JavaAnnoElements(JavaAnnoTypes types, ProcessingEnvironment env) {
        this.types = types;
        this.elements = requireNonNull(env.getElementUtils());
        this.trees = treesInstance(env);
    }

    private static @Nullable Trees treesInstance(ProcessingEnvironment env) {
        try {
            return Trees.instance(env);
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------------

    public @Nullable TypeElement getTypeElement(CharSequence name) {
        return this.elements.getTypeElement(name);
    }

    public boolean hasKaumeiPkg(Element e) {
        var elemPkg = this.elements.getPackageOf(e);
        return elemPkg.getQualifiedName().contentEquals("io.kaumei.jdbc.annotation");
    }

    public PackageElement getPackageOf(Element e) {
        return this.elements.getPackageOf(e);
    }

    public String getQualifiedName(Element element, boolean lineNumbers) {
        var sb = new StringBuilder();
        addQualifiedName(sb, element, lineNumbers);
        return sb.toString();
    }

    public void addQualifiedName(StringBuilder sb, Element element, boolean lineNumbers) {
        switch (element) {
            case PackageElement p -> sb.append(p.getQualifiedName());
            case TypeElement t -> sb.append(t.getQualifiedName());
            case ExecutableElement e -> {
                sb.append(e.getEnclosingElement());
                sb.append(".");
                sb.append(e.getSimpleName());
            }
            default -> sb.append(element.getSimpleName());
        }

        if(lineNumbers && trees != null) {
            try {
                var path = trees.getPath(element);
                var sp = trees.getSourcePositions();
                if(path != null) {
                    CompilationUnitTree cu = path.getCompilationUnit();
                    long pos = sp.getStartPosition(cu, path.getLeaf());
                    var filename = Paths.get(cu.getSourceFile().toUri().getPath()).getFileName();
                    sb.append("(").append(filename).append(":").append(cu.getLineMap().getLineNumber(pos)).append(")");
                }
            } catch (Exception e) {
                // fallback if something went wrong
                sb.append("(unknown)");
            }
        }
    }

    // ------------------------------------------------------------------------

    public boolean hasValidSqlExceptions(ExecutableElement method) {
        for (TypeMirror methodThrows : method.getThrownTypes()) {
            if(!this.types.isSubtype(methodThrows, this.types.JAVA_SQL_SQLException)
                    && !this.types.isSubtype(methodThrows, this.types.JAVA_RuntimeException)) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------

    public record CheckExceptionResult(boolean containsSqlException, Set<TypeMirror> notCovered) {
    }

    public CheckExceptionResult checkExceptions(ExecutableElement source, ExecutableElement target) {
        var containsSqlException = false;
        Set<TypeMirror> notCovered = new HashSet<>();
        for (var targetExp : target.getThrownTypes()) {
            if(this.types.isSubtype(targetExp, this.types.JAVA_SQL_SQLException)) {
                containsSqlException = true;
            } else if(this.types.isSubtype(targetExp, this.types.JAVA_RuntimeException)) {
                continue;
            } else if(!this.types.isSubtype(targetExp, source.getThrownTypes())) {
                notCovered.add(targetExp);
            }
        }
        return new CheckExceptionResult(containsSqlException, Collections.unmodifiableSet(notCovered));
    }

    // ------------------------------------------------------------------------

    /**
     * Can we call target with in source.
     */
    public MsgSet isCallable(ExecutableElement source, ExecutableElement target) {
        var result = new MsgSetBuilder();

        if(source.getReturnType().getKind() == TypeKind.VOID) {
            // ok
        } else if(!this.types.isAssignable(target.getReturnType(), source.getReturnType())) {
            result.add(Msg.of("Return type '" + target.getReturnType() + "' is not assignable to '" + source.getReturnType() + "'"));
        } else {
            var sourceFlag = this.types.optionalFlag(source, source.getReturnType());
            var targetFlag = this.types.optionalFlag(target, target.getReturnType());
            if(!targetFlag.isAssignableTo(sourceFlag)) {
                result.add(Msg.of("Return type mismatch. target: '" + targetFlag + "' is not compatible with source: '" + sourceFlag + "'"));
            }
        }

        var parameters1 = source.getParameters();
        var parameters2 = target.getParameters();
        var size1 = parameters1.size();
        var size2 = parameters2.size();
        if(size1 + 1 != size2) {
            result.add(Msg.of("have different parameter"));
        } else {
            if(!this.types.isSameType(this.types.JAVA_SQL_Connection, parameters2.getFirst().asType())) {
                result.add(Msg.of("first param must have type 'java.sql.Connection'"));
            }
            for (int i = 0; i < size1; i++) {
                var p1 = parameters1.get(i).asType();
                var p2 = parameters2.get(i + 1).asType();
                var sourceFlag = this.types.optionalFlag(source, p1);
                var targetFlag = this.types.optionalFlag(target, p2);

                if(sourceFlag.isOptionalType()) {
                    result.add(Msg.of("Source param at pos " + i + ": " + Msg.INVALID_PARAM_OPTIONAL_TYPE));
                } else if(targetFlag.isOptionalType()) {
                    result.add(Msg.of("Target param at pos " + (i + 1) + ": " + Msg.INVALID_PARAM_OPTIONAL_TYPE));
                } else if(!sourceFlag.isAssignableTo(targetFlag)) {
                    result.add(Msg.of("Param mismatch optional at pos " + i + ": '" + sourceFlag + "' is not compatible with '" + targetFlag + "'"));
                } else if(!this.types.isSameType(p1, p2)) {
                    result.add(Msg.of("Param mismatch type at pos " + i + ": '" + p1 + "' is not same type '" + p2 + "'"));
                }
            }
        }
        var checkResult = checkExceptions(source, target);
        for (var exp : checkResult.notCovered()) {
            result.add(Msg.of("Exception not compatible: " + exp));
        }
        return result.build();
    }

    // ------------------------------------------------------------------------
    // static stuff

    public static ExecutableElement getStaticMethod(Element elem, CharSequence simpleMethodName) {
        ExecutableElement found = null;
        for (var child : elem.getEnclosedElements()) {
            if(child instanceof ExecutableElement method
                    && isStatic(method)
                    && isVisible(method)
                    && method.getSimpleName().contentEquals(simpleMethodName)) {
                if(found == null) {
                    found = method;
                } else {
                    throw new ProcessorException("To many methods found: " + simpleMethodName, elem); // sanity-check
                }
            }
        }
        // sanity-check:on
        if(found == null) {
            throw new ProcessorException("No method found: " + simpleMethodName, elem);
        }
        // sanity-check:off
        return found;
    }

    public static Name getQualifiedClassName(ExecutableElement element) {
        if(element.getEnclosingElement() instanceof TypeElement typeElement) {
            return typeElement.getQualifiedName();
        }
        throw new ProcessorException("Unknown element type: " + element.getClass().getName()); // sanity-check
    }

    public static boolean isVisible(Element element) {
        var modifiers = element.getModifiers();
        return !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.PROTECTED);
    }

    public static boolean isStatic(Element element) {
        var modifiers = element.getModifiers();
        return modifiers.contains(Modifier.STATIC);
    }

}