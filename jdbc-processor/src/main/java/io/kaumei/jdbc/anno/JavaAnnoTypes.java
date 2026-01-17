/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import io.kaumei.jdbc.JdbcBatch;
import io.kaumei.jdbc.JdbcIterable;
import io.kaumei.jdbc.JdbcResultSet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class JavaAnnoTypes {
    public static final TypeElement[] EMPTY = new TypeElement[0];

    // ---- service
    private final Types types;
    private final Elements elements;

    // ----- state
    public final TypeMirror JAVA_List;
    final TypeMirror JAVA_Optional;
    final TypeMirror JAVA_RuntimeException;
    final TypeMirror JAVA_SQL_Connection;
    public final TypeMirror JAVA_SQL_PreparedStatement;
    public final TypeMirror JAVA_SQL_ResultSet;
    final TypeMirror JAVA_SQL_SQLException;
    final TypeMirror JAVA_Stream;
    public final TypeMirror JAVA_String;
    final TypeMirror JSPECIFY_NonNull;
    final TypeMirror JSPECIFY_NullMarked;
    final TypeMirror JSPECIFY_Nullable;
    final TypeMirror KAUMEI_JDBC_JdbcBatch;
    final TypeMirror KAUMEI_JDBC_JdbcIterable;
    final TypeMirror KAUMEI_JDBC_JdbcResultSet;

    public JavaAnnoTypes(Types types, Elements elements) {
        this.types = types;
        this.elements = elements;

        this.JAVA_List = requireNonNull(this.erasure(typeMirror(List.class)));
        this.JAVA_Optional = requireNonNull(this.erasure(this.typeMirror(Optional.class)));
        this.JAVA_RuntimeException = requireNonNull(this.typeMirror(RuntimeException.class));
        this.JAVA_SQL_Connection = requireNonNull(this.typeMirror(Connection.class));
        this.JAVA_SQL_PreparedStatement = requireNonNull(this.typeMirror(PreparedStatement.class));
        this.JAVA_SQL_ResultSet = requireNonNull(this.typeMirror(java.sql.ResultSet.class));
        this.JAVA_SQL_SQLException = requireNonNull(this.typeMirror(SQLException.class));
        this.JAVA_Stream = requireNonNull(this.erasure(this.typeMirror(Stream.class)));
        this.JAVA_String = requireNonNull(this.typeMirror(String.class));
        this.JSPECIFY_NonNull = requireNonNull(this.typeMirror(NonNull.class));
        this.JSPECIFY_NullMarked = requireNonNull(this.typeMirror(NullMarked.class));
        this.JSPECIFY_Nullable = requireNonNull(this.typeMirror(Nullable.class));
        this.KAUMEI_JDBC_JdbcBatch = requireNonNull(this.typeMirror(JdbcBatch.class));
        this.KAUMEI_JDBC_JdbcIterable = requireNonNull(this.erasure(this.typeMirror(JdbcIterable.class)));
        this.KAUMEI_JDBC_JdbcResultSet = requireNonNull(this.erasure(this.typeMirror(JdbcResultSet.class)));
    }

    // ------------------------------------------------------------------------

    public TypeMirror typeMirror(TypeKind kind) {
        return this.types.getPrimitiveType(kind);
    }

    public TypeMirror typeMirror(Class<?> cls) {
        return this.elements.getTypeElement(cls.getCanonicalName()).asType();
    }

    public TypeMirror erasure(TypeMirror type) {
        return this.types.erasure(type);
    }

    public boolean isSameType(TypeMirror t1, TypeMirror t2) {
        return this.types.isSameType(t1, t2);
    }

    public ArrayType getArrayType(TypeMirror componentType) {
        return this.types.getArrayType(componentType);
    }

    public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        return this.types.isAssignable(t1, t2);
    }

    /**
     * Tests whether t1 is a subtype of one in list.
     * Any type is considered to be a subtype of itself.
     */
    public boolean isSubtype(TypeMirror type, List<? extends TypeMirror> list) {
        for (TypeMirror methodThrows : list) {
            if(this.types.isSubtype(type, methodThrows)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether t1 is a subtype of t2. Any type is considered to be a subtype of itself.
     */
    public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        return this.types.isSubtype(t1, t2);
    }

    public boolean isJavaSqlResultSet(TypeMirror type) {
        return this.types.isSubtype(type, JAVA_SQL_ResultSet);
    }

    public boolean isKaumeiJdbcBatch(TypeMirror type) {
        return this.types.isSubtype(type, KAUMEI_JDBC_JdbcBatch);
    }

    public boolean isOptionalType(TypeMirror type) {
        return this.types.isSameType(this.types.erasure(type), JAVA_Optional);
    }

    /**
     * Tests whether t1 is a subtype of one in list.
     * Any type is considered to be a subtype of itself.
     */
    public boolean isSubtype(TypeMirror type, TypeMirror... list) {
        for (TypeMirror methodThrows : list) {
            if(this.types.isSubtype(type, methodThrows)) {
                return true;
            }
        }
        return false;
    }

    public String getFqn(TypeMirror mirror) {
        TypeMirror erased = this.types.erasure(mirror);
        if(erased.getKind() == TypeKind.DECLARED) {
            TypeElement te = (TypeElement) ((DeclaredType) erased).asElement();
            return te.getQualifiedName().toString();
        }
        return erased.toString(); // primitives, arrays, type vars fallback
    }
    // ------------------------------------------------------------------------

    public @Nullable Element asElementOpt(TypeMirror tm) {
        return this.types.asElement(tm);
    }

    public Element asElement(TypeMirror tm) {
        return requireNonNull(this.types.asElement(tm));
    }

    // ------------------------------------------------------------------------

    public record AnalyseResult(JdbcTypeKind kind, TypeMirror type,
                                @Nullable TypeMirror component) {
    }

    public AnalyseResult analyseTypeMirror(TypeMirror type) {
        if(type.getKind() == TypeKind.VOID) {
            return new AnalyseResult(JdbcTypeKind.VOID, type, null);
        } else if(type.getKind().isPrimitive()) {
            return new AnalyseResult(JdbcTypeKind.PRIMITIVE, type, null);
        } else if(this.isKaumeiJdbcBatch(type)) {
            return new AnalyseResult(JdbcTypeKind.KAUMEI_JDBC_BATCH, type, null);
        } else if(this.isOptionalType(type) && type instanceof DeclaredType declared) {
            var type0 = this.erasure(type);
            var component = declared.getTypeArguments().getFirst();
            return new AnalyseResult(JdbcTypeKind.OPTIONAL_TYPE, type0, component);
        } else if(type instanceof DeclaredType declared) {
            var type0 = this.erasure(type);
            if(this.isSameType(type0, this.JAVA_List)) {
                var component = declared.getTypeArguments().getFirst();
                return new AnalyseResult(JdbcTypeKind.LIST, type0, component);
            } else if(this.isSameType(type0, this.JAVA_Stream)) {
                var component = declared.getTypeArguments().getFirst();
                return new AnalyseResult(JdbcTypeKind.STREAM, type0, component);
            } else if(this.isSameType(type0, this.KAUMEI_JDBC_JdbcIterable)) {
                var component = declared.getTypeArguments().getFirst();
                return new AnalyseResult(JdbcTypeKind.KAUMEI_JDBC_ITERABLE, type0, component);
            } else if(this.isSameType(type0, this.KAUMEI_JDBC_JdbcResultSet)) {
                var component = declared.getTypeArguments().getFirst();
                return new AnalyseResult(JdbcTypeKind.KAUMEI_JDBC_RESULT_SET, type0, component);
            }
        } else if(type instanceof ArrayType arrayType && arrayType.getComponentType().getKind() != TypeKind.ARRAY) {
            var type0 = this.erasure(type);
            var component = arrayType.getComponentType();
            return new AnalyseResult(JdbcTypeKind.ARRAY, type0, component);
        }
        return new AnalyseResult(JdbcTypeKind.OBJECT, type, null);
    }

    // ------------------------------------------------------------------------

    public <T> @Nullable T visitTypeHierarchy(TypeMirror type, Function<TypeMirror, @Nullable T> search) {
        return new VisitTypeSearch<>(this.types, search).visit(type);
    }

    private static class VisitTypeSearch<T> {
        private final Types types;
        private final List<TypeMirror> clsList = new ArrayList<>();
        private final Set<TypeMirror> visited = new HashSet<>();
        private final Function<TypeMirror, @Nullable T> search;

        VisitTypeSearch(Types types, Function<TypeMirror, @Nullable T> search) {
            this.types = types;
            this.search = search;
        }

        public @Nullable T visit(TypeMirror start) {
            // 1. process all super classes
            TypeMirror current = start;
            while (current != null && current.getKind() != TypeKind.NONE) {
                T result = search.apply(current);
                if(result != null) {
                    return result;
                }
                clsList.add(current);
                visited.add(current);
                if(this.types.asElement(current) instanceof TypeElement typeElement) {
                    current = typeElement.getSuperclass();
                } else {
                    throw new ProcessorException("Unexpected type: " + current); // sanity-check
                }
            }

            // 2. process all interfaces
            for (var cls : clsList) {
                T result = visitInterfaces(cls);
                if(result != null) {
                    return result;
                }
            }
            return null;
        }

        private @Nullable T visitInterfaces(TypeMirror type) {
            if(this.types.asElement(type) instanceof TypeElement element) {
                for (var iface : element.getInterfaces()) {
                    if(visited.add(iface)) {
                        T result = search.apply(iface);
                        if(result != null) {
                            return result;
                        }
                        result = visitInterfaces(iface);
                        if(result != null) {
                            return result;
                        }
                    }
                }
            } else {
                throw new ProcessorException("Unexpected type: " + type); // sanity-check
            }
            return null;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Only Jspecify is supported and must be used
     * @param context the element the type belongs to
     * @param type the type to check
     * @return result of the check, not null
     */
    public OptionalFlag optionalFlag(Element context, TypeMirror type) {
        if(type.getKind().isPrimitive()) {
            return OptionalFlag.NON_NULL;
        } else if(this.types.isSameType(this.types.erasure(type), this.JAVA_Optional)) {
            return OptionalFlag.OPTIONAL_TYPE;
        }
        return isOptionalJspecify(context, type);
    }

    /**
     * Primitives → MANDATORY
     * - Optional<T> → OPTIONAL_TYPE
     * - @Nullable on element → OPTIONAL
     * - @NonNull on element → MANDATORY
     * - In @NullMarked scope → unannotated types are MANDATORY
     * - In @NullUnmarked scope → unannotated types are UNSPECIFIED
     * - No markers found → UNSPECIFIED
     */
    private OptionalFlag isOptionalJspecify(Element context, TypeMirror type) {
        boolean nullable = false;
        boolean nonnull = false;

        // Check for @Nullable or @Nonnull annotation on the element (e.g., method, parameter, field)
        for (var annotation : type.getAnnotationMirrors()) {
            var annoType = annotation.getAnnotationType();
            if(!nullable && this.types.isSameType(annoType, JSPECIFY_Nullable)) {
                nullable = true;
            } else if(!nonnull && this.types.isSameType(annoType, JSPECIFY_NonNull)) {
                nonnull = true;
            }
        }

        Element current = context;
        while (current != null && !nullable && !nonnull) {
            // Check for @Nullable or @Nonnull annotation on the enclosing elements (bottom up)
            // stop if we found nullable or nonnull
            for (var annotation : current.getAnnotationMirrors()) {
                var annoType = annotation.getAnnotationType();
                if(!nullable && this.types.isSameType(annoType, JSPECIFY_NullMarked)) {
                    nonnull = true;
                } else if(!nonnull && this.types.isSameType(annoType, JSPECIFY_NullMarked)) {
                    nullable = true;
                }
            }
            current = current.getEnclosingElement();
        }

        if(nonnull && !nullable) {
            return OptionalFlag.NON_NULL;
        } else if(!nonnull && nullable) {
            return OptionalFlag.NULLABLE;
        }
        return OptionalFlag.UNSPECIFIED;
    }

}