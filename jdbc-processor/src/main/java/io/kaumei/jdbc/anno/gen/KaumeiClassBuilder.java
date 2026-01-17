/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.gen;

import com.palantir.javapoet.*;
import io.kaumei.jdbc.JdbcConnectionProvider;
import io.kaumei.jdbc.anno.ProcessorException;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.kaumei.jdbc.anno.Processor.GENERATED;

public class KaumeiClassBuilder implements KaumeiBuilder {
    // ----- services
    private final GenerateService genService;
    // ------ state
    private final TypeElement iface;
    private final String packageName;
    private final String simpleName;
    private final TypeSpec.Builder typeSpecBuilder;
    private final Set<String> classNames = new HashSet<>();

    KaumeiClassBuilder(GenerateService genService, String packageName, TypeElement iface) {
        this.genService = genService;
        this.iface = iface;
        this.packageName = packageName;
        this.simpleName = iface.getSimpleName() + "Jdbc";

        this.typeSpecBuilder = TypeSpec.classBuilder(this.simpleName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(iface));

        typeSpecBuilder.addAnnotation(GENERATED);

        // ----- copy all annotations
        for (var mirror : iface.getAnnotationMirrors()) {
            var spec = this.genService.annotationSpec(mirror);
            if(spec != null) {
                typeSpecBuilder.addAnnotation(AnnotationSpec.get(mirror));
            }
        }

        // ----- Add supplier field
        typeSpecBuilder.addField(
                FieldSpec.builder(JdbcConnectionProvider.class, "supplier", Modifier.PRIVATE, Modifier.FINAL).build());


        var constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(JdbcConnectionProvider.class, "supplier")
                .addStatement("this.supplier = $L", KaumeiLib.requireNonNull("supplier"));

        // FIXME: this is dirty by now: Loop over all annotation mirrors on the interface
        for (AnnotationMirror annotationMirror : iface.getAnnotationMirrors()) {
            // Check if this is JdbcConstructorAnnotations
            TypeElement annotationElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if(!annotationElement.getSimpleName().toString().equals("JdbcConstructorAnnotations")) {
                continue;
            }
            // Extract the "value" member
            annotationMirror.getElementValues().forEach((executable, annotationValue) -> {
                if(executable.getSimpleName().toString().equals("value")) {
                    @SuppressWarnings("unchecked")
                    List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) annotationValue.getValue();
                    for (AnnotationValue v : values) {
                        TypeElement te = (TypeElement) ((DeclaredType) v.getValue()).asElement();
                        var className = ClassName.get(te);
                        var t = AnnotationSpec.builder(className).build();
                        constructor.addAnnotation(t);
                    }
                }
            });
        }

        // ----- Add constructor
        this.typeSpecBuilder.addMethod(constructor.build());
    }

    @Override
    public TypeElement type() {
        return this.iface;
    }

    @Override
    public TypeSpec build() {
        return this.typeSpecBuilder.build();
    }

    public String packageName() {
        return packageName;
    }

    public String simpleName() {
        return simpleName;
    }

    boolean containsClass(String name) {
        return classNames.contains(name);
    }

    void addClass(String name, TypeSpec typeSpec) {
        if(classNames.add(name)) { // JaCoCo:no
            this.typeSpecBuilder.addType(typeSpec);
        } else {
            throw new ProcessorException("Name already known: " + name); // sanity-check
        }
    }

    public void addMethod(MethodSpec methodSpec) {
        this.typeSpecBuilder.addMethod(methodSpec);
    }

}
