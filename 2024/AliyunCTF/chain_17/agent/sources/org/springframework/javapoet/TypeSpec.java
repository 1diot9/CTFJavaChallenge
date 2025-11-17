package org.springframework.javapoet;

import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeSpec.class */
public final class TypeSpec {
    public final Kind kind;
    public final String name;
    public final CodeBlock anonymousTypeArguments;
    public final CodeBlock javadoc;
    public final List<AnnotationSpec> annotations;
    public final Set<Modifier> modifiers;
    public final List<TypeVariableName> typeVariables;
    public final TypeName superclass;
    public final List<TypeName> superinterfaces;
    public final Map<String, TypeSpec> enumConstants;
    public final List<FieldSpec> fieldSpecs;
    public final CodeBlock staticBlock;
    public final CodeBlock initializerBlock;
    public final List<MethodSpec> methodSpecs;
    public final List<TypeSpec> typeSpecs;
    final Set<String> nestedTypesSimpleNames;
    public final List<Element> originatingElements;
    public final Set<String> alwaysQualifiedNames;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !TypeSpec.class.desiredAssertionStatus();
    }

    private TypeSpec(Builder builder) {
        this.kind = builder.kind;
        this.name = builder.name;
        this.anonymousTypeArguments = builder.anonymousTypeArguments;
        this.javadoc = builder.javadoc.build();
        this.annotations = Util.immutableList(builder.annotations);
        this.modifiers = Util.immutableSet(builder.modifiers);
        this.typeVariables = Util.immutableList(builder.typeVariables);
        this.superclass = builder.superclass;
        this.superinterfaces = Util.immutableList(builder.superinterfaces);
        this.enumConstants = Util.immutableMap(builder.enumConstants);
        this.fieldSpecs = Util.immutableList(builder.fieldSpecs);
        this.staticBlock = builder.staticBlock.build();
        this.initializerBlock = builder.initializerBlock.build();
        this.methodSpecs = Util.immutableList(builder.methodSpecs);
        this.typeSpecs = Util.immutableList(builder.typeSpecs);
        this.alwaysQualifiedNames = Util.immutableSet(builder.alwaysQualifiedNames);
        this.nestedTypesSimpleNames = new HashSet(builder.typeSpecs.size());
        List<Element> originatingElementsMutable = new ArrayList<>();
        originatingElementsMutable.addAll(builder.originatingElements);
        for (TypeSpec typeSpec : builder.typeSpecs) {
            this.nestedTypesSimpleNames.add(typeSpec.name);
            originatingElementsMutable.addAll(typeSpec.originatingElements);
        }
        this.originatingElements = Util.immutableList(originatingElementsMutable);
    }

    private TypeSpec(TypeSpec type) {
        if (!$assertionsDisabled && type.anonymousTypeArguments != null) {
            throw new AssertionError();
        }
        this.kind = type.kind;
        this.name = type.name;
        this.anonymousTypeArguments = null;
        this.javadoc = type.javadoc;
        this.annotations = Collections.emptyList();
        this.modifiers = Collections.emptySet();
        this.typeVariables = Collections.emptyList();
        this.superclass = null;
        this.superinterfaces = Collections.emptyList();
        this.enumConstants = Collections.emptyMap();
        this.fieldSpecs = Collections.emptyList();
        this.staticBlock = type.staticBlock;
        this.initializerBlock = type.initializerBlock;
        this.methodSpecs = Collections.emptyList();
        this.typeSpecs = Collections.emptyList();
        this.originatingElements = Collections.emptyList();
        this.nestedTypesSimpleNames = Collections.emptySet();
        this.alwaysQualifiedNames = Collections.emptySet();
    }

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    public static Builder classBuilder(String name) {
        return new Builder(Kind.CLASS, (String) Util.checkNotNull(name, "name == null", new Object[0]), null);
    }

    public static Builder classBuilder(ClassName className) {
        return classBuilder(((ClassName) Util.checkNotNull(className, "className == null", new Object[0])).simpleName());
    }

    public static Builder interfaceBuilder(String name) {
        return new Builder(Kind.INTERFACE, (String) Util.checkNotNull(name, "name == null", new Object[0]), null);
    }

    public static Builder interfaceBuilder(ClassName className) {
        return interfaceBuilder(((ClassName) Util.checkNotNull(className, "className == null", new Object[0])).simpleName());
    }

    public static Builder enumBuilder(String name) {
        return new Builder(Kind.ENUM, (String) Util.checkNotNull(name, "name == null", new Object[0]), null);
    }

    public static Builder enumBuilder(ClassName className) {
        return enumBuilder(((ClassName) Util.checkNotNull(className, "className == null", new Object[0])).simpleName());
    }

    public static Builder anonymousClassBuilder(String typeArgumentsFormat, Object... args) {
        return anonymousClassBuilder(CodeBlock.of(typeArgumentsFormat, args));
    }

    public static Builder anonymousClassBuilder(CodeBlock typeArguments) {
        return new Builder(Kind.CLASS, null, typeArguments);
    }

    public static Builder annotationBuilder(String name) {
        return new Builder(Kind.ANNOTATION, (String) Util.checkNotNull(name, "name == null", new Object[0]), null);
    }

    public static Builder annotationBuilder(ClassName className) {
        return annotationBuilder(((ClassName) Util.checkNotNull(className, "className == null", new Object[0])).simpleName());
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.kind, this.name, this.anonymousTypeArguments);
        builder.javadoc.add(this.javadoc);
        builder.annotations.addAll(this.annotations);
        builder.modifiers.addAll(this.modifiers);
        builder.typeVariables.addAll(this.typeVariables);
        builder.superclass = this.superclass;
        builder.superinterfaces.addAll(this.superinterfaces);
        builder.enumConstants.putAll(this.enumConstants);
        builder.fieldSpecs.addAll(this.fieldSpecs);
        builder.methodSpecs.addAll(this.methodSpecs);
        builder.typeSpecs.addAll(this.typeSpecs);
        builder.initializerBlock.add(this.initializerBlock);
        builder.staticBlock.add(this.staticBlock);
        builder.originatingElements.addAll(this.originatingElements);
        builder.alwaysQualifiedNames.addAll(this.alwaysQualifiedNames);
        return builder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void emit(CodeWriter codeWriter, String enumName, Set<Modifier> implicitModifiers) throws IOException {
        List<TypeName> singletonList;
        List<TypeName> extendsTypes;
        List<TypeName> implementsTypes;
        int previousStatementLine = codeWriter.statementLine;
        codeWriter.statementLine = -1;
        try {
            if (enumName != null) {
                codeWriter.emitJavadoc(this.javadoc);
                codeWriter.emitAnnotations(this.annotations, false);
                codeWriter.emit("$L", enumName);
                if (!this.anonymousTypeArguments.formatParts.isEmpty()) {
                    codeWriter.emit("(");
                    codeWriter.emit(this.anonymousTypeArguments);
                    codeWriter.emit(")");
                }
                if (this.fieldSpecs.isEmpty() && this.methodSpecs.isEmpty() && this.typeSpecs.isEmpty()) {
                    return;
                } else {
                    codeWriter.emit(" {\n");
                }
            } else if (this.anonymousTypeArguments != null) {
                TypeName supertype = !this.superinterfaces.isEmpty() ? this.superinterfaces.get(0) : this.superclass;
                codeWriter.emit("new $T(", supertype);
                codeWriter.emit(this.anonymousTypeArguments);
                codeWriter.emit(") {\n");
            } else {
                codeWriter.pushType(new TypeSpec(this));
                codeWriter.emitJavadoc(this.javadoc);
                codeWriter.emitAnnotations(this.annotations, false);
                codeWriter.emitModifiers(this.modifiers, Util.union(implicitModifiers, this.kind.asMemberModifiers));
                if (this.kind == Kind.ANNOTATION) {
                    codeWriter.emit("$L $L", "@interface", this.name);
                } else {
                    codeWriter.emit("$L $L", this.kind.name().toLowerCase(Locale.US), this.name);
                }
                codeWriter.emitTypeVariables(this.typeVariables);
                if (this.kind == Kind.INTERFACE) {
                    extendsTypes = this.superinterfaces;
                    implementsTypes = Collections.emptyList();
                } else {
                    if (this.superclass.equals(ClassName.OBJECT)) {
                        singletonList = Collections.emptyList();
                    } else {
                        singletonList = Collections.singletonList(this.superclass);
                    }
                    extendsTypes = singletonList;
                    implementsTypes = this.superinterfaces;
                }
                if (!extendsTypes.isEmpty()) {
                    codeWriter.emit(" extends");
                    boolean firstType = true;
                    for (TypeName type : extendsTypes) {
                        if (!firstType) {
                            codeWriter.emit(",");
                        }
                        codeWriter.emit(" $T", type);
                        firstType = false;
                    }
                }
                if (!implementsTypes.isEmpty()) {
                    codeWriter.emit(" implements");
                    boolean firstType2 = true;
                    for (TypeName type2 : implementsTypes) {
                        if (!firstType2) {
                            codeWriter.emit(",");
                        }
                        codeWriter.emit(" $T", type2);
                        firstType2 = false;
                    }
                }
                codeWriter.popType();
                codeWriter.emit(" {\n");
            }
            codeWriter.pushType(this);
            codeWriter.indent();
            boolean firstMember = true;
            Iterator<Map.Entry<String, TypeSpec>> i = this.enumConstants.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, TypeSpec> enumConstant = i.next();
                if (!firstMember) {
                    codeWriter.emit(StrPool.LF);
                }
                enumConstant.getValue().emit(codeWriter, enumConstant.getKey(), Collections.emptySet());
                firstMember = false;
                if (i.hasNext()) {
                    codeWriter.emit(",\n");
                } else if (!this.fieldSpecs.isEmpty() || !this.methodSpecs.isEmpty() || !this.typeSpecs.isEmpty()) {
                    codeWriter.emit(";\n");
                } else {
                    codeWriter.emit(StrPool.LF);
                }
            }
            for (FieldSpec fieldSpec : this.fieldSpecs) {
                if (fieldSpec.hasModifier(Modifier.STATIC)) {
                    if (!firstMember) {
                        codeWriter.emit(StrPool.LF);
                    }
                    fieldSpec.emit(codeWriter, this.kind.implicitFieldModifiers);
                    firstMember = false;
                }
            }
            if (!this.staticBlock.isEmpty()) {
                if (!firstMember) {
                    codeWriter.emit(StrPool.LF);
                }
                codeWriter.emit(this.staticBlock);
                firstMember = false;
            }
            for (FieldSpec fieldSpec2 : this.fieldSpecs) {
                if (!fieldSpec2.hasModifier(Modifier.STATIC)) {
                    if (!firstMember) {
                        codeWriter.emit(StrPool.LF);
                    }
                    fieldSpec2.emit(codeWriter, this.kind.implicitFieldModifiers);
                    firstMember = false;
                }
            }
            if (!this.initializerBlock.isEmpty()) {
                if (!firstMember) {
                    codeWriter.emit(StrPool.LF);
                }
                codeWriter.emit(this.initializerBlock);
                firstMember = false;
            }
            for (MethodSpec methodSpec : this.methodSpecs) {
                if (methodSpec.isConstructor()) {
                    if (!firstMember) {
                        codeWriter.emit(StrPool.LF);
                    }
                    methodSpec.emit(codeWriter, this.name, this.kind.implicitMethodModifiers);
                    firstMember = false;
                }
            }
            for (MethodSpec methodSpec2 : this.methodSpecs) {
                if (!methodSpec2.isConstructor()) {
                    if (!firstMember) {
                        codeWriter.emit(StrPool.LF);
                    }
                    methodSpec2.emit(codeWriter, this.name, this.kind.implicitMethodModifiers);
                    firstMember = false;
                }
            }
            for (TypeSpec typeSpec : this.typeSpecs) {
                if (!firstMember) {
                    codeWriter.emit(StrPool.LF);
                }
                typeSpec.emit(codeWriter, null, this.kind.implicitTypeModifiers);
                firstMember = false;
            }
            codeWriter.unindent();
            codeWriter.popType();
            codeWriter.popTypeVariables(this.typeVariables);
            codeWriter.emit("}");
            if (enumName == null && this.anonymousTypeArguments == null) {
                codeWriter.emit(StrPool.LF);
            }
            codeWriter.statementLine = previousStatementLine;
        } finally {
            codeWriter.statementLine = previousStatementLine;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass()) {
            return toString().equals(o.toString());
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        try {
            CodeWriter codeWriter = new CodeWriter(out);
            emit(codeWriter, null, Collections.emptySet());
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeSpec$Kind.class */
    public enum Kind {
        CLASS(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet()),
        INTERFACE(Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)), Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT)), Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)), Util.immutableSet(Collections.singletonList(Modifier.STATIC))),
        ENUM(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.singleton(Modifier.STATIC)),
        ANNOTATION(Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)), Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.ABSTRACT)), Util.immutableSet(Arrays.asList(Modifier.PUBLIC, Modifier.STATIC)), Util.immutableSet(Collections.singletonList(Modifier.STATIC)));

        private final Set<Modifier> implicitFieldModifiers;
        private final Set<Modifier> implicitMethodModifiers;
        private final Set<Modifier> implicitTypeModifiers;
        private final Set<Modifier> asMemberModifiers;

        Kind(Set set, Set set2, Set set3, Set set4) {
            this.implicitFieldModifiers = set;
            this.implicitMethodModifiers = set2;
            this.implicitTypeModifiers = set3;
            this.asMemberModifiers = set4;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeSpec$Builder.class */
    public static final class Builder {
        private final Kind kind;
        private final String name;
        private final CodeBlock anonymousTypeArguments;
        private final CodeBlock.Builder javadoc;
        private TypeName superclass;
        private final CodeBlock.Builder staticBlock;
        private final CodeBlock.Builder initializerBlock;
        public final Map<String, TypeSpec> enumConstants;
        public final List<AnnotationSpec> annotations;
        public final List<Modifier> modifiers;
        public final List<TypeVariableName> typeVariables;
        public final List<TypeName> superinterfaces;
        public final List<FieldSpec> fieldSpecs;
        public final List<MethodSpec> methodSpecs;
        public final List<TypeSpec> typeSpecs;
        public final List<Element> originatingElements;
        public final Set<String> alwaysQualifiedNames;

        private Builder(Kind kind, String name, CodeBlock anonymousTypeArguments) {
            this.javadoc = CodeBlock.builder();
            this.superclass = ClassName.OBJECT;
            this.staticBlock = CodeBlock.builder();
            this.initializerBlock = CodeBlock.builder();
            this.enumConstants = new LinkedHashMap();
            this.annotations = new ArrayList();
            this.modifiers = new ArrayList();
            this.typeVariables = new ArrayList();
            this.superinterfaces = new ArrayList();
            this.fieldSpecs = new ArrayList();
            this.methodSpecs = new ArrayList();
            this.typeSpecs = new ArrayList();
            this.originatingElements = new ArrayList();
            this.alwaysQualifiedNames = new LinkedHashSet();
            Util.checkArgument(name == null || SourceVersion.isName(name), "not a valid name: %s", name);
            this.kind = kind;
            this.name = name;
            this.anonymousTypeArguments = anonymousTypeArguments;
        }

        public Builder addJavadoc(String format, Object... args) {
            this.javadoc.add(format, args);
            return this;
        }

        public Builder addJavadoc(CodeBlock block) {
            this.javadoc.add(block);
            return this;
        }

        public Builder addAnnotations(Iterable<AnnotationSpec> annotationSpecs) {
            Util.checkArgument(annotationSpecs != null, "annotationSpecs == null", new Object[0]);
            for (AnnotationSpec annotationSpec : annotationSpecs) {
                this.annotations.add(annotationSpec);
            }
            return this;
        }

        public Builder addAnnotation(AnnotationSpec annotationSpec) {
            Util.checkNotNull(annotationSpec, "annotationSpec == null", new Object[0]);
            this.annotations.add(annotationSpec);
            return this;
        }

        public Builder addAnnotation(ClassName annotation) {
            return addAnnotation(AnnotationSpec.builder(annotation).build());
        }

        public Builder addAnnotation(Class<?> annotation) {
            return addAnnotation(ClassName.get(annotation));
        }

        public Builder addModifiers(Modifier... modifiers) {
            Collections.addAll(this.modifiers, modifiers);
            return this;
        }

        public Builder addTypeVariables(Iterable<TypeVariableName> typeVariables) {
            Util.checkArgument(typeVariables != null, "typeVariables == null", new Object[0]);
            for (TypeVariableName typeVariable : typeVariables) {
                this.typeVariables.add(typeVariable);
            }
            return this;
        }

        public Builder addTypeVariable(TypeVariableName typeVariable) {
            this.typeVariables.add(typeVariable);
            return this;
        }

        public Builder superclass(TypeName superclass) {
            Util.checkState(this.kind == Kind.CLASS, "only classes have super classes, not " + this.kind, new Object[0]);
            Util.checkState(this.superclass == ClassName.OBJECT, "superclass already set to " + this.superclass, new Object[0]);
            Util.checkArgument(!superclass.isPrimitive(), "superclass may not be a primitive", new Object[0]);
            this.superclass = superclass;
            return this;
        }

        public Builder superclass(Type superclass) {
            return superclass(superclass, true);
        }

        public Builder superclass(Type superclass, boolean avoidNestedTypeNameClashes) {
            Class<?> clazz;
            superclass(TypeName.get(superclass));
            if (avoidNestedTypeNameClashes && (clazz = getRawType(superclass)) != null) {
                avoidClashesWithNestedClasses(clazz);
            }
            return this;
        }

        public Builder superclass(TypeMirror superclass) {
            return superclass(superclass, true);
        }

        public Builder superclass(TypeMirror superclass, boolean avoidNestedTypeNameClashes) {
            superclass(TypeName.get(superclass));
            if (avoidNestedTypeNameClashes && (superclass instanceof DeclaredType)) {
                TypeElement superInterfaceElement = (TypeElement) ((DeclaredType) superclass).asElement();
                avoidClashesWithNestedClasses(superInterfaceElement);
            }
            return this;
        }

        public Builder addSuperinterfaces(Iterable<? extends TypeName> superinterfaces) {
            Util.checkArgument(superinterfaces != null, "superinterfaces == null", new Object[0]);
            for (TypeName superinterface : superinterfaces) {
                addSuperinterface(superinterface);
            }
            return this;
        }

        public Builder addSuperinterface(TypeName superinterface) {
            Util.checkArgument(superinterface != null, "superinterface == null", new Object[0]);
            this.superinterfaces.add(superinterface);
            return this;
        }

        public Builder addSuperinterface(Type superinterface) {
            return addSuperinterface(superinterface, true);
        }

        public Builder addSuperinterface(Type superinterface, boolean avoidNestedTypeNameClashes) {
            Class<?> clazz;
            addSuperinterface(TypeName.get(superinterface));
            if (avoidNestedTypeNameClashes && (clazz = getRawType(superinterface)) != null) {
                avoidClashesWithNestedClasses(clazz);
            }
            return this;
        }

        private Class<?> getRawType(Type type) {
            if (type instanceof Class) {
                return (Class) type;
            }
            if (type instanceof ParameterizedType) {
                return getRawType(((ParameterizedType) type).getRawType());
            }
            return null;
        }

        public Builder addSuperinterface(TypeMirror superinterface) {
            return addSuperinterface(superinterface, true);
        }

        public Builder addSuperinterface(TypeMirror superinterface, boolean avoidNestedTypeNameClashes) {
            addSuperinterface(TypeName.get(superinterface));
            if (avoidNestedTypeNameClashes && (superinterface instanceof DeclaredType)) {
                TypeElement superInterfaceElement = (TypeElement) ((DeclaredType) superinterface).asElement();
                avoidClashesWithNestedClasses(superInterfaceElement);
            }
            return this;
        }

        public Builder addEnumConstant(String name) {
            return addEnumConstant(name, TypeSpec.anonymousClassBuilder("", new Object[0]).build());
        }

        public Builder addEnumConstant(String name, TypeSpec typeSpec) {
            this.enumConstants.put(name, typeSpec);
            return this;
        }

        public Builder addFields(Iterable<FieldSpec> fieldSpecs) {
            Util.checkArgument(fieldSpecs != null, "fieldSpecs == null", new Object[0]);
            for (FieldSpec fieldSpec : fieldSpecs) {
                addField(fieldSpec);
            }
            return this;
        }

        public Builder addField(FieldSpec fieldSpec) {
            this.fieldSpecs.add(fieldSpec);
            return this;
        }

        public Builder addField(TypeName type, String name, Modifier... modifiers) {
            return addField(FieldSpec.builder(type, name, modifiers).build());
        }

        public Builder addField(Type type, String name, Modifier... modifiers) {
            return addField(TypeName.get(type), name, modifiers);
        }

        public Builder addStaticBlock(CodeBlock block) {
            this.staticBlock.beginControlFlow("static", new Object[0]).add(block).endControlFlow();
            return this;
        }

        public Builder addInitializerBlock(CodeBlock block) {
            if (this.kind != Kind.CLASS && this.kind != Kind.ENUM) {
                throw new UnsupportedOperationException(this.kind + " can't have initializer blocks");
            }
            this.initializerBlock.add("{\n", new Object[0]).indent().add(block).unindent().add("}\n", new Object[0]);
            return this;
        }

        public Builder addMethods(Iterable<MethodSpec> methodSpecs) {
            Util.checkArgument(methodSpecs != null, "methodSpecs == null", new Object[0]);
            for (MethodSpec methodSpec : methodSpecs) {
                addMethod(methodSpec);
            }
            return this;
        }

        public Builder addMethod(MethodSpec methodSpec) {
            this.methodSpecs.add(methodSpec);
            return this;
        }

        public Builder addTypes(Iterable<TypeSpec> typeSpecs) {
            Util.checkArgument(typeSpecs != null, "typeSpecs == null", new Object[0]);
            for (TypeSpec typeSpec : typeSpecs) {
                addType(typeSpec);
            }
            return this;
        }

        public Builder addType(TypeSpec typeSpec) {
            this.typeSpecs.add(typeSpec);
            return this;
        }

        public Builder addOriginatingElement(Element originatingElement) {
            this.originatingElements.add(originatingElement);
            return this;
        }

        public Builder alwaysQualify(String... simpleNames) {
            Util.checkArgument(simpleNames != null, "simpleNames == null", new Object[0]);
            int length = simpleNames.length;
            for (int i = 0; i < length; i++) {
                String name = simpleNames[i];
                Util.checkArgument(name != null, "null entry in simpleNames array: %s", Arrays.toString(simpleNames));
                this.alwaysQualifiedNames.add(name);
            }
            return this;
        }

        public Builder avoidClashesWithNestedClasses(TypeElement typeElement) {
            Util.checkArgument(typeElement != null, "typeElement == null", new Object[0]);
            for (TypeElement nestedType : ElementFilter.typesIn(typeElement.getEnclosedElements())) {
                alwaysQualify(nestedType.getSimpleName().toString());
            }
            DeclaredType superclass = typeElement.getSuperclass();
            if (!(superclass instanceof NoType) && (superclass instanceof DeclaredType)) {
                TypeElement superclassElement = (TypeElement) superclass.asElement();
                avoidClashesWithNestedClasses(superclassElement);
            }
            for (DeclaredType declaredType : typeElement.getInterfaces()) {
                if (declaredType instanceof DeclaredType) {
                    TypeElement superinterfaceElement = (TypeElement) declaredType.asElement();
                    avoidClashesWithNestedClasses(superinterfaceElement);
                }
            }
            return this;
        }

        public Builder avoidClashesWithNestedClasses(Class<?> clazz) {
            Util.checkArgument(clazz != null, "clazz == null", new Object[0]);
            for (Class<?> nestedType : clazz.getDeclaredClasses()) {
                alwaysQualify(nestedType.getSimpleName());
            }
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && !Object.class.equals(superclass)) {
                avoidClashesWithNestedClasses(superclass);
            }
            for (Class<?> superinterface : clazz.getInterfaces()) {
                avoidClashesWithNestedClasses(superinterface);
            }
            return this;
        }

        public TypeSpec build() {
            for (AnnotationSpec annotationSpec : this.annotations) {
                Util.checkNotNull(annotationSpec, "annotationSpec == null", new Object[0]);
            }
            if (!this.modifiers.isEmpty()) {
                Util.checkState(this.anonymousTypeArguments == null, "forbidden on anonymous types.", new Object[0]);
                Iterator<Modifier> it = this.modifiers.iterator();
                while (it.hasNext()) {
                    Modifier modifier = it.next();
                    Util.checkArgument(modifier != null, "modifiers contain null", new Object[0]);
                }
            }
            Util.checkArgument((this.kind == Kind.ENUM && this.enumConstants.isEmpty()) ? false : true, "at least one enum constant is required for %s", this.name);
            Iterator<TypeName> it2 = this.superinterfaces.iterator();
            while (it2.hasNext()) {
                TypeName superinterface = it2.next();
                Util.checkArgument(superinterface != null, "superinterfaces contains null", new Object[0]);
            }
            if (!this.typeVariables.isEmpty()) {
                Util.checkState(this.anonymousTypeArguments == null, "typevariables are forbidden on anonymous types.", new Object[0]);
                Iterator<TypeVariableName> it3 = this.typeVariables.iterator();
                while (it3.hasNext()) {
                    TypeVariableName typeVariableName = it3.next();
                    Util.checkArgument(typeVariableName != null, "typeVariables contain null", new Object[0]);
                }
            }
            for (Map.Entry<String, TypeSpec> enumConstant : this.enumConstants.entrySet()) {
                Util.checkState(this.kind == Kind.ENUM, "%s is not enum", this.name);
                Util.checkArgument(enumConstant.getValue().anonymousTypeArguments != null, "enum constants must have anonymous type arguments", new Object[0]);
                Util.checkArgument(SourceVersion.isName(this.name), "not a valid enum constant: %s", this.name);
            }
            for (FieldSpec fieldSpec : this.fieldSpecs) {
                if (this.kind == Kind.INTERFACE || this.kind == Kind.ANNOTATION) {
                    Util.requireExactlyOneOf(fieldSpec.modifiers, Modifier.PUBLIC, Modifier.PRIVATE);
                    Set<Modifier> check = EnumSet.of(Modifier.STATIC, Modifier.FINAL);
                    Util.checkState(fieldSpec.modifiers.containsAll(check), "%s %s.%s requires modifiers %s", this.kind, this.name, fieldSpec.name, check);
                }
            }
            for (MethodSpec methodSpec : this.methodSpecs) {
                if (this.kind == Kind.INTERFACE) {
                    Util.requireExactlyOneOf(methodSpec.modifiers, Modifier.ABSTRACT, Modifier.STATIC, Modifier.DEFAULT);
                    Util.requireExactlyOneOf(methodSpec.modifiers, Modifier.PUBLIC, Modifier.PRIVATE);
                } else if (this.kind == Kind.ANNOTATION) {
                    Util.checkState(methodSpec.modifiers.equals(this.kind.implicitMethodModifiers), "%s %s.%s requires modifiers %s", this.kind, this.name, methodSpec.name, this.kind.implicitMethodModifiers);
                }
                if (this.kind != Kind.ANNOTATION) {
                    Util.checkState(methodSpec.defaultValue == null, "%s %s.%s cannot have a default value", this.kind, this.name, methodSpec.name);
                }
                if (this.kind != Kind.INTERFACE) {
                    Util.checkState(!methodSpec.hasModifier(Modifier.DEFAULT), "%s %s.%s cannot be default", this.kind, this.name, methodSpec.name);
                }
            }
            for (TypeSpec typeSpec : this.typeSpecs) {
                Util.checkArgument(typeSpec.modifiers.containsAll(this.kind.implicitTypeModifiers), "%s %s.%s requires modifiers %s", this.kind, this.name, typeSpec.name, this.kind.implicitTypeModifiers);
            }
            boolean isAbstract = this.modifiers.contains(Modifier.ABSTRACT) || this.kind != Kind.CLASS;
            for (MethodSpec methodSpec2 : this.methodSpecs) {
                Util.checkArgument(isAbstract || !methodSpec2.hasModifier(Modifier.ABSTRACT), "non-abstract type %s cannot declare abstract method %s", this.name, methodSpec2.name);
            }
            boolean superclassIsObject = this.superclass.equals(ClassName.OBJECT);
            int interestingSupertypeCount = (superclassIsObject ? 0 : 1) + this.superinterfaces.size();
            Util.checkArgument(this.anonymousTypeArguments == null || interestingSupertypeCount <= 1, "anonymous type has too many supertypes", new Object[0]);
            return new TypeSpec(this);
        }
    }
}
