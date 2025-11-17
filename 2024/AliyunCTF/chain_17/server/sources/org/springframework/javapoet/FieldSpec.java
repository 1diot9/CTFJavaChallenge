package org.springframework.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import org.springframework.javapoet.CodeBlock;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/FieldSpec.class */
public final class FieldSpec {
    public final TypeName type;
    public final String name;
    public final CodeBlock javadoc;
    public final List<AnnotationSpec> annotations;
    public final Set<Modifier> modifiers;
    public final CodeBlock initializer;

    private FieldSpec(Builder builder) {
        CodeBlock codeBlock;
        this.type = (TypeName) Util.checkNotNull(builder.type, "type == null", new Object[0]);
        this.name = (String) Util.checkNotNull(builder.name, "name == null", new Object[0]);
        this.javadoc = builder.javadoc.build();
        this.annotations = Util.immutableList(builder.annotations);
        this.modifiers = Util.immutableSet(builder.modifiers);
        if (builder.initializer == null) {
            codeBlock = CodeBlock.builder().build();
        } else {
            codeBlock = builder.initializer;
        }
        this.initializer = codeBlock;
    }

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.contains(modifier);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void emit(CodeWriter codeWriter, Set<Modifier> implicitModifiers) throws IOException {
        codeWriter.emitJavadoc(this.javadoc);
        codeWriter.emitAnnotations(this.annotations, false);
        codeWriter.emitModifiers(this.modifiers, implicitModifiers);
        codeWriter.emit("$T $L", this.type, this.name);
        if (!this.initializer.isEmpty()) {
            codeWriter.emit(" = ");
            codeWriter.emit(this.initializer);
        }
        codeWriter.emit(";\n");
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
            emit(codeWriter, Collections.emptySet());
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public static Builder builder(TypeName type, String name, Modifier... modifiers) {
        Util.checkNotNull(type, "type == null", new Object[0]);
        Util.checkArgument(SourceVersion.isName(name), "not a valid name: %s", name);
        return new Builder(type, name).addModifiers(modifiers);
    }

    public static Builder builder(Type type, String name, Modifier... modifiers) {
        return builder(TypeName.get(type), name, modifiers);
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.type, this.name);
        builder.javadoc.add(this.javadoc);
        builder.annotations.addAll(this.annotations);
        builder.modifiers.addAll(this.modifiers);
        builder.initializer = this.initializer.isEmpty() ? null : this.initializer;
        return builder;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/FieldSpec$Builder.class */
    public static final class Builder {
        private final TypeName type;
        private final String name;
        private final CodeBlock.Builder javadoc;
        private CodeBlock initializer;
        public final List<AnnotationSpec> annotations;
        public final List<Modifier> modifiers;

        private Builder(TypeName type, String name) {
            this.javadoc = CodeBlock.builder();
            this.initializer = null;
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

        public Builder initializer(String format, Object... args) {
            return initializer(CodeBlock.of(format, args));
        }

        public Builder initializer(CodeBlock codeBlock) {
            Util.checkState(this.initializer == null, "initializer was already set", new Object[0]);
            this.initializer = (CodeBlock) Util.checkNotNull(codeBlock, "codeBlock == null", new Object[0]);
            return this;
        }

        public FieldSpec build() {
            return new FieldSpec(this);
        }
    }
}
