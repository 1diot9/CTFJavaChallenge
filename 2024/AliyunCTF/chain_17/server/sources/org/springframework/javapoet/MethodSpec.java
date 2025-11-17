package org.springframework.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import org.springframework.javapoet.CodeBlock;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/MethodSpec.class */
public final class MethodSpec {
    static final String CONSTRUCTOR = "<init>";
    public final String name;
    public final CodeBlock javadoc;
    public final List<AnnotationSpec> annotations;
    public final Set<Modifier> modifiers;
    public final List<TypeVariableName> typeVariables;
    public final TypeName returnType;
    public final List<ParameterSpec> parameters;
    public final boolean varargs;
    public final List<TypeName> exceptions;
    public final CodeBlock code;
    public final CodeBlock defaultValue;

    private MethodSpec(Builder builder) {
        CodeBlock code = builder.code.build();
        Util.checkArgument(code.isEmpty() || !builder.modifiers.contains(Modifier.ABSTRACT), "abstract method %s cannot have code", builder.name);
        Util.checkArgument(!builder.varargs || lastParameterIsArray(builder.parameters), "last parameter of varargs method %s must be an array", builder.name);
        this.name = (String) Util.checkNotNull(builder.name, "name == null", new Object[0]);
        this.javadoc = builder.javadoc.build();
        this.annotations = Util.immutableList(builder.annotations);
        this.modifiers = Util.immutableSet(builder.modifiers);
        this.typeVariables = Util.immutableList(builder.typeVariables);
        this.returnType = builder.returnType;
        this.parameters = Util.immutableList(builder.parameters);
        this.varargs = builder.varargs;
        this.exceptions = Util.immutableList(builder.exceptions);
        this.defaultValue = builder.defaultValue;
        this.code = code;
    }

    private boolean lastParameterIsArray(List<ParameterSpec> parameters) {
        return (parameters.isEmpty() || TypeName.asArray(parameters.get(parameters.size() - 1).type) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void emit(CodeWriter codeWriter, String enclosingName, Set<Modifier> implicitModifiers) throws IOException {
        codeWriter.emitJavadoc(javadocWithParameters());
        codeWriter.emitAnnotations(this.annotations, false);
        codeWriter.emitModifiers(this.modifiers, implicitModifiers);
        if (!this.typeVariables.isEmpty()) {
            codeWriter.emitTypeVariables(this.typeVariables);
            codeWriter.emit(" ");
        }
        if (isConstructor()) {
            codeWriter.emit("$L($Z", enclosingName);
        } else {
            codeWriter.emit("$T $L($Z", this.returnType, this.name);
        }
        boolean firstParameter = true;
        Iterator<ParameterSpec> i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterSpec parameter = i.next();
            if (!firstParameter) {
                codeWriter.emit(",").emitWrappingSpace();
            }
            parameter.emit(codeWriter, !i.hasNext() && this.varargs);
            firstParameter = false;
        }
        codeWriter.emit(")");
        if (this.defaultValue != null && !this.defaultValue.isEmpty()) {
            codeWriter.emit(" default ");
            codeWriter.emit(this.defaultValue);
        }
        if (!this.exceptions.isEmpty()) {
            codeWriter.emitWrappingSpace().emit("throws");
            boolean firstException = true;
            for (TypeName exception : this.exceptions) {
                if (!firstException) {
                    codeWriter.emit(",");
                }
                codeWriter.emitWrappingSpace().emit("$T", exception);
                firstException = false;
            }
        }
        if (hasModifier(Modifier.ABSTRACT)) {
            codeWriter.emit(";\n");
        } else if (hasModifier(Modifier.NATIVE)) {
            codeWriter.emit(this.code);
            codeWriter.emit(";\n");
        } else {
            codeWriter.emit(" {\n");
            codeWriter.indent();
            codeWriter.emit(this.code, true);
            codeWriter.unindent();
            codeWriter.emit("}\n");
        }
        codeWriter.popTypeVariables(this.typeVariables);
    }

    private CodeBlock javadocWithParameters() {
        CodeBlock.Builder builder = this.javadoc.toBuilder();
        boolean emitTagNewline = true;
        for (ParameterSpec parameterSpec : this.parameters) {
            if (!parameterSpec.javadoc.isEmpty()) {
                if (emitTagNewline && !this.javadoc.isEmpty()) {
                    builder.add("\n", new Object[0]);
                }
                emitTagNewline = false;
                builder.add("@param $L $L", parameterSpec.name, parameterSpec.javadoc);
            }
        }
        return builder.build();
    }

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    public boolean isConstructor() {
        return this.name.equals("<init>");
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
            emit(codeWriter, "Constructor", Collections.emptySet());
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public static Builder methodBuilder(String name) {
        return new Builder(name);
    }

    public static Builder constructorBuilder() {
        return new Builder("<init>");
    }

    public static Builder overriding(ExecutableElement method) {
        Util.checkNotNull(method, "method == null", new Object[0]);
        Element enclosingClass = method.getEnclosingElement();
        if (enclosingClass.getModifiers().contains(Modifier.FINAL)) {
            throw new IllegalArgumentException("Cannot override method on final class " + enclosingClass);
        }
        Set<Modifier> modifiers = method.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.STATIC)) {
            throw new IllegalArgumentException("cannot override method with modifiers: " + modifiers);
        }
        String methodName = method.getSimpleName().toString();
        Builder methodBuilder = methodBuilder(methodName);
        methodBuilder.addAnnotation(Override.class);
        Set<Modifier> modifiers2 = new LinkedHashSet<>(modifiers);
        modifiers2.remove(Modifier.ABSTRACT);
        modifiers2.remove(Modifier.DEFAULT);
        methodBuilder.addModifiers(modifiers2);
        for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
            TypeVariable var = typeParameterElement.asType();
            methodBuilder.addTypeVariable(TypeVariableName.get(var));
        }
        methodBuilder.returns(TypeName.get(method.getReturnType()));
        methodBuilder.addParameters(ParameterSpec.parametersOf(method));
        methodBuilder.varargs(method.isVarArgs());
        for (TypeMirror thrownType : method.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(thrownType));
        }
        return methodBuilder;
    }

    public static Builder overriding(ExecutableElement method, DeclaredType enclosing, Types types) {
        ExecutableType executableType = types.asMemberOf(enclosing, method);
        List<? extends TypeMirror> resolvedParameterTypes = executableType.getParameterTypes();
        List<? extends TypeMirror> resolvedThrownTypes = executableType.getThrownTypes();
        TypeMirror resolvedReturnType = executableType.getReturnType();
        Builder builder = overriding(method);
        builder.returns(TypeName.get(resolvedReturnType));
        int size = builder.parameters.size();
        for (int i = 0; i < size; i++) {
            ParameterSpec parameter = builder.parameters.get(i);
            TypeName type = TypeName.get((TypeMirror) resolvedParameterTypes.get(i));
            builder.parameters.set(i, parameter.toBuilder(type, parameter.name).build());
        }
        builder.exceptions.clear();
        int size2 = resolvedThrownTypes.size();
        for (int i2 = 0; i2 < size2; i2++) {
            builder.addException(TypeName.get((TypeMirror) resolvedThrownTypes.get(i2)));
        }
        return builder;
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.name);
        builder.javadoc.add(this.javadoc);
        builder.annotations.addAll(this.annotations);
        builder.modifiers.addAll(this.modifiers);
        builder.typeVariables.addAll(this.typeVariables);
        builder.returnType = this.returnType;
        builder.parameters.addAll(this.parameters);
        builder.exceptions.addAll(this.exceptions);
        builder.code.add(this.code);
        builder.varargs = this.varargs;
        builder.defaultValue = this.defaultValue;
        return builder;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/MethodSpec$Builder.class */
    public static final class Builder {
        private String name;
        private final CodeBlock.Builder javadoc;
        private TypeName returnType;
        private final Set<TypeName> exceptions;
        private final CodeBlock.Builder code;
        private boolean varargs;
        private CodeBlock defaultValue;
        public final List<TypeVariableName> typeVariables;
        public final List<AnnotationSpec> annotations;
        public final List<Modifier> modifiers;
        public final List<ParameterSpec> parameters;

        private Builder(String name) {
            this.javadoc = CodeBlock.builder();
            this.exceptions = new LinkedHashSet();
            this.code = CodeBlock.builder();
            this.typeVariables = new ArrayList();
            this.annotations = new ArrayList();
            this.modifiers = new ArrayList();
            this.parameters = new ArrayList();
            setName(name);
        }

        public Builder setName(String name) {
            Util.checkNotNull(name, "name == null", new Object[0]);
            Util.checkArgument(name.equals("<init>") || SourceVersion.isName(name), "not a valid name: %s", name);
            this.name = name;
            this.returnType = name.equals("<init>") ? null : TypeName.VOID;
            return this;
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
            this.annotations.add(annotationSpec);
            return this;
        }

        public Builder addAnnotation(ClassName annotation) {
            this.annotations.add(AnnotationSpec.builder(annotation).build());
            return this;
        }

        public Builder addAnnotation(Class<?> annotation) {
            return addAnnotation(ClassName.get(annotation));
        }

        public Builder addModifiers(Modifier... modifiers) {
            Util.checkNotNull(modifiers, "modifiers == null", new Object[0]);
            Collections.addAll(this.modifiers, modifiers);
            return this;
        }

        public Builder addModifiers(Iterable<Modifier> modifiers) {
            Util.checkNotNull(modifiers, "modifiers == null", new Object[0]);
            for (Modifier modifier : modifiers) {
                this.modifiers.add(modifier);
            }
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

        public Builder returns(TypeName returnType) {
            Util.checkState(!this.name.equals("<init>"), "constructor cannot have return type.", new Object[0]);
            this.returnType = returnType;
            return this;
        }

        public Builder returns(Type returnType) {
            return returns(TypeName.get(returnType));
        }

        public Builder addParameters(Iterable<ParameterSpec> parameterSpecs) {
            Util.checkArgument(parameterSpecs != null, "parameterSpecs == null", new Object[0]);
            for (ParameterSpec parameterSpec : parameterSpecs) {
                this.parameters.add(parameterSpec);
            }
            return this;
        }

        public Builder addParameter(ParameterSpec parameterSpec) {
            this.parameters.add(parameterSpec);
            return this;
        }

        public Builder addParameter(TypeName type, String name, Modifier... modifiers) {
            return addParameter(ParameterSpec.builder(type, name, modifiers).build());
        }

        public Builder addParameter(Type type, String name, Modifier... modifiers) {
            return addParameter(TypeName.get(type), name, modifiers);
        }

        public Builder varargs() {
            return varargs(true);
        }

        public Builder varargs(boolean varargs) {
            this.varargs = varargs;
            return this;
        }

        public Builder addExceptions(Iterable<? extends TypeName> exceptions) {
            Util.checkArgument(exceptions != null, "exceptions == null", new Object[0]);
            for (TypeName exception : exceptions) {
                this.exceptions.add(exception);
            }
            return this;
        }

        public Builder addException(TypeName exception) {
            this.exceptions.add(exception);
            return this;
        }

        public Builder addException(Type exception) {
            return addException(TypeName.get(exception));
        }

        public Builder addCode(String format, Object... args) {
            this.code.add(format, args);
            return this;
        }

        public Builder addNamedCode(String format, Map<String, ?> args) {
            this.code.addNamed(format, args);
            return this;
        }

        public Builder addCode(CodeBlock codeBlock) {
            this.code.add(codeBlock);
            return this;
        }

        public Builder addComment(String format, Object... args) {
            this.code.add("// " + format + "\n", args);
            return this;
        }

        public Builder defaultValue(String format, Object... args) {
            return defaultValue(CodeBlock.of(format, args));
        }

        public Builder defaultValue(CodeBlock codeBlock) {
            Util.checkState(this.defaultValue == null, "defaultValue was already set", new Object[0]);
            this.defaultValue = (CodeBlock) Util.checkNotNull(codeBlock, "codeBlock == null", new Object[0]);
            return this;
        }

        public Builder beginControlFlow(String controlFlow, Object... args) {
            this.code.beginControlFlow(controlFlow, args);
            return this;
        }

        public Builder beginControlFlow(CodeBlock codeBlock) {
            return beginControlFlow("$L", codeBlock);
        }

        public Builder nextControlFlow(String controlFlow, Object... args) {
            this.code.nextControlFlow(controlFlow, args);
            return this;
        }

        public Builder nextControlFlow(CodeBlock codeBlock) {
            return nextControlFlow("$L", codeBlock);
        }

        public Builder endControlFlow() {
            this.code.endControlFlow();
            return this;
        }

        public Builder endControlFlow(String controlFlow, Object... args) {
            this.code.endControlFlow(controlFlow, args);
            return this;
        }

        public Builder endControlFlow(CodeBlock codeBlock) {
            return endControlFlow("$L", codeBlock);
        }

        public Builder addStatement(String format, Object... args) {
            this.code.addStatement(format, args);
            return this;
        }

        public Builder addStatement(CodeBlock codeBlock) {
            this.code.addStatement(codeBlock);
            return this;
        }

        public MethodSpec build() {
            return new MethodSpec(this);
        }
    }
}
