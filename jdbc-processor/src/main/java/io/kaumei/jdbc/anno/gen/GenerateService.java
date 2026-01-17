/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.*;
import io.kaumei.jdbc.anno.*;
import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.anno.annotool.KaumeiAnno;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcConverter;
import io.kaumei.jdbc.anno.java2jdbc.Java2JdbcService;
import io.kaumei.jdbc.anno.jdbc2java.Jdbc2JavaConverter;
import io.kaumei.jdbc.anno.jdbc2java.Jdbc2JavaService;
import io.kaumei.jdbc.anno.msg.Msg;
import io.kaumei.jdbc.anno.msg.MsgSet;
import io.kaumei.jdbc.anno.msg.MsgSetBuilder;
import io.kaumei.jdbc.anno.store.SearchKey;
import io.kaumei.jdbc.anno.store.StoreResolve;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.annotation.Annotation;
import java.util.*;

public class GenerateService implements ProcessorSteps {

    // ------ services
    final JavaAnnoMessenger logger;
    final JavaAnnoTypes types;
    final JavaAnnoElements elements;
    final JavaAnnoFiler filer;

    final ConfigService jdbcConfigService;
    final Jdbc2JavaService jdbc2JavaService;
    final Java2JdbcService java2JdbcService;

    public GenerateService(JavaAnnoMessenger logger, JavaAnnoTypes types, JavaAnnoElements elements, JavaAnnoFiler filer,
                           ConfigService jdbcConfigService, Jdbc2JavaService jdbc2JavaService, Java2JdbcService java2JdbcService) {
        this.logger = logger;
        this.types = types;
        this.elements = elements;
        this.filer = filer;
        this.jdbcConfigService = jdbcConfigService;
        this.jdbc2JavaService = jdbc2JavaService;
        this.java2JdbcService = java2JdbcService;
    }

    // ------------------------------------------------------------------------

    @Override
    public void process(ProcessorEnvironment roundEnv) {
        for (var entry : roundEnv.jdbcInterfaces()) {
            this.logger.acceptWithDebugFlag(entry, this::generateImplementation);
        }
    }

    // ------------------------------------------------------------------------

    private void generateImplementation(TypeElement iface) {
        if(types.isKaumeiJdbcBatch(iface.asType())) {
            // skip JdbcBatches, they will be processed during @JdbcUpdateBatch processing
            return;
        }
        this.logger.info("Process interface", iface);
        var implBuilder = this.create(iface);
        // ----- process methods
        for (var child : iface.getEnclosedElements()) {
            if(!child.getModifiers().contains(Modifier.STATIC)
                    && !child.getModifiers().contains(Modifier.DEFAULT)
                    && child.getKind() == ElementKind.METHOD
                    && child instanceof ExecutableElement method) {
                generateMethod(implBuilder, method);
            }
        }
        // ----- write to disk
        this.filer.writeJava(iface, implBuilder.packageName(), implBuilder.build());
    }

    void generateMethod(KaumeiClassBuilder implBuilder, ExecutableElement method0) {
        this.logger.acceptWithDebugFlag(method0, (method) -> {
            this.logger.debug("generateMethod", method);
            var methodAnno = new KaumeiAnno(method);
            GenerateJdbc generateJdbc;
            if(methodAnno.hasJdbcSelect()) {
                generateJdbc = new GenerateJdbcSelect(this, implBuilder, method, methodAnno);
            } else if(methodAnno.hasJdbcUpdate()) {
                generateJdbc = new GenerateJdbcUpdate(this, implBuilder, method, methodAnno);
            } else if(methodAnno.hasJdbcNative()) {
                generateJdbc = new GenerateJdbcNative(this, implBuilder, method, methodAnno);
            } else if(methodAnno.hasJdbcUpdateBatch()) {
                generateJdbc = new GenerateJdbcUpdateBatch(this, implBuilder, method, methodAnno);
            } else {
                generateJdbc = new GenerateJdbcUnknown(this, implBuilder, method, methodAnno);
            }
            var methodSpec = generateJdbc.generateMethod();
            implBuilder.addMethod(methodSpec);
        });
    }

    // ------------------------------------------------------------------------

    public KaumeiClassBuilder create(TypeElement iface) {
        var packageName = elements.getPackageOf(iface).getQualifiedName().toString();
        return new KaumeiClassBuilder(this, packageName, iface);
    }

    // ------------------------------------------------------------------------

    /**
     * @return the AnnotationSpec if the mirror, return null if it was a Kaumei internal annotation
     */
    @Nullable
    AnnotationSpec annotationSpec(AnnotationMirror mirror) {
        return this.elements.hasKaumeiPkg(mirror.getAnnotationType().asElement())
                ? null
                : AnnotationSpec.get(mirror);
    }

    private TypeName typeNameWithAnnotations(TypeMirror typeMirror) {
        var annotationMirrors = typeMirror.getAnnotationMirrors();
        if(annotationMirrors.isEmpty()) {
            return TypeName.get(typeMirror);
        }
        List<AnnotationSpec> specs = new ArrayList<>();
        for (AnnotationMirror annoMirror : annotationMirrors) {
            var spec = this.annotationSpec(annoMirror);
            if(spec != null) {
                specs.add(AnnotationSpec.get(annoMirror));
            }
        }
        return specs.isEmpty() ? TypeName.get(typeMirror) : TypeName.get(typeMirror).annotated(specs);
    }

    // ------------------------------------------------------------------------

    MethodSpec.Builder createMethodBuilder(ExecutableElement method) {
        var methodName = method.getSimpleName().toString();
        var methodBuilder = MethodSpec.methodBuilder(methodName);

        // ----- method annotations
        methodBuilder.addAnnotation(Override.class);
        for (var mirror : method.getAnnotationMirrors()) {
            if(!this.elements.hasKaumeiPkg(mirror.getAnnotationType().asElement())) {
                methodBuilder.addAnnotation(AnnotationSpec.get(mirror));
            }
        }

        // ----- modifiers
        var modifiers = new LinkedHashSet<>(method.getModifiers());
        modifiers.remove(Modifier.ABSTRACT);
        modifiers.remove(Modifier.DEFAULT);
        methodBuilder.addModifiers(modifiers);

        // ----- type parameter
        for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
            TypeVariable var = (TypeVariable) typeParameterElement.asType();
            methodBuilder.addTypeVariable(TypeVariableName.get(var));
        }

        // ----- return type with annotations
        methodBuilder.returns(this.typeNameWithAnnotations(method.getReturnType()));

        // ----- parameter
        for (VariableElement param : method.getParameters()) {
            var type = this.typeNameWithAnnotations(param.asType());
            var name = param.getSimpleName().toString();
            var paramBuilder = ParameterSpec.builder(type, name);
            paramBuilder.addModifiers(param.getModifiers());
            for (AnnotationMirror mirror : param.getAnnotationMirrors()) {
                var spec = this.annotationSpec(mirror);
                if(spec != null) {
                    paramBuilder.addAnnotation(AnnotationSpec.get(mirror));
                }
            }
            methodBuilder.addParameter(paramBuilder.build());
        }
        //methodBuilder.varargs(method.isVarArgs());

        // ----- throws
        for (TypeMirror thrownType : method.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(thrownType));
        }

        return methodBuilder;
    }

    // ------------------------------------------------------------------------

    record MethodParameters(boolean hasCollections, Map<String, Item> parameterMap,
                            MsgSet messages) {
        public boolean hasMessages() {
            return !messages.isEmpty();
        }

        record Item(JdbcTypeKind kind, VariableElement elem, OptionalFlag optional, KaumeiAnno anno,
                    Java2JdbcConverter converter) {
        }
    }

    MethodParameters getParameters(ExecutableElement method) {
        var hasCollections = false;
        var messages = new MsgSetBuilder();
        var parameterMap = new TreeMap<String, MethodParameters.Item>();
        var store = java2JdbcService.getStoreForElement(method);

        for (var param : method.getParameters()) {
            var paramSimpleName = param.getSimpleName().toString();

            var paramAnno = new KaumeiAnno(param); // TODO check at the end for unused annotations
            var converterName = paramAnno.jdbcConverterName();

            var type = store.resolve(new SearchKey(converterName, param.asType()));
            var typeOptional = this.types.optionalFlag(method, type.type());
            if(type.kind().isOptionalType()) {
                messages.add(Msg.INVALID_PARAM_OPTIONAL_TYPE);
                continue;
            }

            if(type.hasConverter()) {
                if(type.hasValidConverter()) {
                    var item = new MethodParameters.Item(type.kind(), param, typeOptional, paramAnno, type.converter());
                    parameterMap.put(paramSimpleName, item);
                } else {
                    messages.add(Msg.invalidParam(paramSimpleName, param.asType(), type.converter()));
                }
            } else if(type.kind().isArray() || type.kind().isList()) {
                var componentType = store.resolve(new SearchKey(converterName, type.component()));
                // check for List or an Array
                var compOptional = this.types.optionalFlag(method, componentType.type());
                if(compOptional.isOptionalType()) {
                    messages.add(Msg.INVALID_PARAM_OPTIONAL_TYPE);
                } else if(componentType.hasMessages()) {
                    messages.add(Msg.invalidParam(paramSimpleName, param.asType(), componentType.converter()));
                } else if(typeOptional.isNonNullOrUnspecific()
                        && compOptional.isNonNullOrUnspecific()) {
                    hasCollections = true;
                    var item = new MethodParameters.Item(type.kind(), param, compOptional,
                            paramAnno, componentType.converter());
                    parameterMap.put(paramSimpleName, item);
                } else {
                    messages.add(Msg.of("Collection must not be optional. [param: " + paramSimpleName + ", type='" + param.asType() + "']"));
                }
            } else {
                messages.add(Msg.invalidParam(paramSimpleName, param.asType(), Msg.INVALID_PARAM_TYPE));
            }
        }
        // sanity-check:on
        if(method.getParameters().size() != parameterMap.size() && messages.isEmpty()) {
            throw new ProcessorException(method + ": param.size=" + method.getParameters().size() + ", map.size=" + parameterMap.size());
        }
        // sanity-check:off
        return new MethodParameters(hasCollections, parameterMap, messages.build());
    }

    // ------------------------------------------------------------------------

    record MethodReturn(SearchKey searchKey, JdbcTypeKind kind, OptionalFlag optional,
                        TypeMirror type, MsgSet messages, @Nullable Jdbc2JavaConverter converter) {

        static MethodReturn of(SearchKey searchKey, JdbcTypeKind kind, OptionalFlag optionalFlag, TypeMirror type, MsgSet messages) {
            return new MethodReturn(searchKey, kind, optionalFlag, type, messages, null);
        }

        static MethodReturn of(SearchKey searchKey, JdbcTypeKind kind, OptionalFlag optionalFlag, TypeMirror type, @Nullable Jdbc2JavaConverter result) {
            if(result == null) {
                return new MethodReturn(searchKey, kind, optionalFlag, type, Msg.returnTypeNotSupported(type), null);
            } else if(result.hasMessages()) {
                var messages = new MsgSetBuilder();
                messages.add(Msg.invalidConverter(searchKey));
                messages.add(result.messages());
                return new MethodReturn(searchKey, kind, optionalFlag, type, messages.build(), null);
            }
            return new MethodReturn(searchKey, kind, optionalFlag, type, MsgSet.EMPTY, result);
        }

        MethodReturn {
            // sanity-check:on
            if(converter != null && messages.isNotEmpty()) {
                throw new ProcessorException(converter + ", " + messages);
            }
            // sanity-check:off
        }

        public boolean hasMessages() {
            return (this.converter != null && this.converter.hasMessages()) || this.messages.isNotEmpty();
        }

        public MsgSet messages() {
            return this.converter != null ? this.converter.messages() : this.messages;
        }

        public JdbcTypeKind kind() {
            return kind;
        }

        public OptionalFlag optional() {
            return optional;
        }

        public TypeMirror type() {
            return type;
        }

        public Jdbc2JavaConverter converter() {
            // sanity-check:on
            if(converter == null) {
                throw new ProcessorException(searchKey + ", " + messages);
            }
            // sanity-check:off
            return converter;
        }
    }

    MethodReturn returnType(ExecutableElement method, KaumeiAnno methodAnnotations) {

        var converterName = methodAnnotations.jdbcConverterName();
        var searchKey = new SearchKey(converterName, method.getReturnType());

        // check for void early in the process
        if(method.getReturnType().getKind() == TypeKind.VOID) {
            return MethodReturn.of(searchKey, JdbcTypeKind.VOID, OptionalFlag.UNSPECIFIED, method.getReturnType(), MsgSet.EMPTY);
        }
        // get the correct store to search
        var store = this.jdbc2JavaService.getStoreForElement(method);

        // resolve the return type
        var resolved = store.resolve(searchKey);
        JdbcTypeKind kind = resolved.kind();
        OptionalFlag optional;
        StoreResolve<Jdbc2JavaConverter> compResolved;

        switch (resolved.kind()) {
            case OPTIONAL_TYPE:
                optional = this.types.optionalFlag(method, resolved.type());
                if(optional.isNullable()) {
                    return MethodReturn.of(searchKey, kind, optional, resolved.type(),
                            Msg.of("Return type: Optional<> must be 'non-null' or 'unspecific'"));
                }
                searchKey = new SearchKey(converterName, resolved.component());
                compResolved = store.resolve(searchKey);
                if(!this.types.optionalFlag(method, compResolved.type()).isNonNullOrUnspecific()) {
                    return MethodReturn.of(searchKey, kind, optional, method.getReturnType(),
                            Msg.of("Return type: Component of Optional must be 'non-null' or 'unspecific'"));
                }
                return MethodReturn.of(searchKey, kind, optional, method.getReturnType(),
                        compResolved.converterOpt());

            case LIST, KAUMEI_JDBC_ITERABLE, KAUMEI_JDBC_RESULT_SET, STREAM:
                optional = this.types.optionalFlag(method, resolved.type());
                var optReason = optional.checkNonNullOrUnspecific();
                if(optReason != null) {
                    return MethodReturn.of(searchKey, kind, optional, resolved.type(), Msg.returnTypeOptional(optReason));
                }
                searchKey = new SearchKey(converterName, resolved.component());
                optional = this.types.optionalFlag(method, resolved.component());
                compResolved = store.resolve(searchKey);
                if(compResolved.kind().isOptionalType()) {
                    searchKey = new SearchKey(converterName, compResolved.component());
                    compResolved = store.resolve(searchKey);
                }

                if(!compResolved.hasMessages() && isRowConverter(compResolved)
                        && !this.types.optionalFlag(method, resolved.component()).isNonNullOrUnspecific()) {
                    return MethodReturn.of(searchKey, kind, optional, resolved.component(),
                            Msg.of("Return type: Return row component must be mandatory or unspecific"));
                }
                return MethodReturn.of(searchKey, kind, optional, resolved.component(), compResolved.converterOpt());

            case VOID:
                throw new ProcessorException("Void not supported", method); // sanity-check

            case PRIMITIVE, OBJECT:
                optional = this.types.optionalFlag(method, resolved.type());
                return MethodReturn.of(searchKey, kind, optional, resolved.type(), resolved.converterOpt());

            default:
                optional = this.types.optionalFlag(method, resolved.type());
                return MethodReturn.of(searchKey, kind, optional, resolved.type(), Msg.of("unknown"));
        }
    }

    private boolean isRowConverter(StoreResolve<Jdbc2JavaConverter> resolved) {
        return resolved.hasConverter() && !resolved.converter().isColumn();
    }

    // ------------------------------------------------------------------------

    record AnnoCode(CodeBlock nameOrValue, @Nullable CodeBlock check) {
    }

    <A extends Annotation, T> @Nullable AnnoCode searchAnno(Anno.WithConfigValue<A, T> anno,
                                                            MethodParameters parameters,
                                                            KaumeiAnno methodAnno,
                                                            Element parent) {
        for (var item : parameters.parameterMap().entrySet()) {
            var param = item.getValue();
            if(param.anno().useAnnotationWithoutValue(anno)) {
                var key = item.getKey();
                var codeBlock = CodeBlock.builder()
                        .beginControlFlow("if($L)", anno.checkUnsetValue(key))
                        .addStatement("throw new $T($S + $L)", IllegalArgumentException.class, "Invalid value for " + key + ": ", key)
                        .endControlFlow()
                        .build();
                // remove used parameter
                parameters.parameterMap().remove(key);
                return new AnnoCode(CodeBlock.of("$N", key), codeBlock);
            }
        }
        T value = this.jdbcConfigService.searchAnno(anno, methodAnno, parent);
        if(!anno.isUnset(value)) {
            return new AnnoCode(anno.codeBlock(value), null);
        }
        return null;
    }

    // ------------------------------------------------------------------------

}
