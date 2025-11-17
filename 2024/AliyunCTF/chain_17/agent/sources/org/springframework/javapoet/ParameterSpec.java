package org.springframework.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import org.springframework.javapoet.CodeBlock;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/ParameterSpec.class */
public final class ParameterSpec {
    public final String name;
    public final List<AnnotationSpec> annotations;
    public final Set<Modifier> modifiers;
    public final TypeName type;
    public final CodeBlock javadoc;

    private ParameterSpec(Builder builder) {
        this.name = (String) Util.checkNotNull(builder.name, "name == null", new Object[0]);
        this.annotations = Util.immutableList(builder.annotations);
        this.modifiers = Util.immutableSet(builder.modifiers);
        this.type = (TypeName) Util.checkNotNull(builder.type, "type == null", new Object[0]);
        this.javadoc = builder.javadoc.build();
    }

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void emit(CodeWriter codeWriter, boolean varargs) throws IOException {
        codeWriter.emitAnnotations(this.annotations, true);
        codeWriter.emitModifiers(this.modifiers);
        if (varargs) {
            TypeName.asArray(this.type).emit(codeWriter, true);
        } else {
            this.type.emit(codeWriter);
        }
        codeWriter.emit(" $L", this.name);
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
            emit(codeWriter, false);
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public static ParameterSpec get(VariableElement element) {
        Util.checkArgument(element.getKind().equals(ElementKind.PARAMETER), "element is not a parameter", new Object[0]);
        TypeName type = TypeName.get(element.asType());
        String name = element.getSimpleName().toString();
        return builder(type, name, new Modifier[0]).addModifiers(element.getModifiers()).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<ParameterSpec> parametersOf(ExecutableElement method) {
        List<ParameterSpec> result = new ArrayList<>();
        for (VariableElement parameter : method.getParameters()) {
            result.add(get(parameter));
        }
        return result;
    }

    private static boolean isValidParameterName(String name) {
        if (name.endsWith(".this")) {
            return SourceVersion.isIdentifier(name.substring(0, name.length() - ".this".length()));
        }
        return name.equals("this") || SourceVersion.isName(name);
    }

    public static Builder builder(TypeName type, String name, Modifier... modifiers) {
        Util.checkNotNull(type, "type == null", new Object[0]);
        Util.checkArgument(isValidParameterName(name), "not a valid name: %s", name);
        return new Builder(type, name).addModifiers(modifiers);
    }

    public static Builder builder(Type type, String name, Modifier... modifiers) {
        return builder(TypeName.get(type), name, modifiers);
    }

    public Builder toBuilder() {
        return toBuilder(this.type, this.name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Builder toBuilder(TypeName type, String name) {
        Builder builder = new Builder(type, name);
        builder.annotations.addAll(this.annotations);
        builder.modifiers.addAll(this.modifiers);
        return builder;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/ParameterSpec$Builder.class */
    public static final class Builder {
        private final TypeName type;
        private final String name;
        private final CodeBlock.Builder javadoc;
        public final List<AnnotationSpec> annotations;
        public final List<Modifier> modifiers;

        private Builder(TypeName type, String name) {
            this.javadoc = CodeBlock.builder();
            this.annotations = new ArrayList();
            this.modifiers = new ArrayList();
            this.type = type;
            this.name = name;
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
            Collections.addAll(this.modifiers, modifiers);
            return this;
        }

        public Builder addModifiers(Iterable<Modifier> modifiers) {
            Util.checkNotNull(modifiers, "modifiers == null", new Object[0]);
            for (Modifier modifier : modifiers) {
                if (!modifier.equals(Modifier.FINAL)) {
                    throw new IllegalStateException("unexpected parameter modifier: " + modifier);
                }
                this.modifiers.add(modifier);
            }
            return this;
        }

        public ParameterSpec build() {
            return new ParameterSpec(this);
        }
    }
}
