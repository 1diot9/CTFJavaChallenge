package org.springframework.javapoet;

import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/AnnotationSpec.class */
public final class AnnotationSpec {
    public final TypeName type;
    public final Map<String, List<CodeBlock>> members;

    private AnnotationSpec(Builder builder) {
        this.type = builder.type;
        this.members = Util.immutableMultimap(builder.members);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void emit(CodeWriter codeWriter, boolean inline) throws IOException {
        String whitespace = inline ? "" : StrPool.LF;
        String memberSeparator = inline ? ", " : ",\n";
        if (this.members.isEmpty()) {
            codeWriter.emit("@$T", this.type);
            return;
        }
        if (this.members.size() == 1 && this.members.containsKey("value")) {
            codeWriter.emit("@$T(", this.type);
            emitAnnotationValues(codeWriter, whitespace, memberSeparator, this.members.get("value"));
            codeWriter.emit(")");
            return;
        }
        codeWriter.emit("@$T(" + whitespace, this.type);
        codeWriter.indent(2);
        Iterator<Map.Entry<String, List<CodeBlock>>> i = this.members.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, List<CodeBlock>> entry = i.next();
            codeWriter.emit("$L = ", entry.getKey());
            emitAnnotationValues(codeWriter, whitespace, memberSeparator, entry.getValue());
            if (i.hasNext()) {
                codeWriter.emit(memberSeparator);
            }
        }
        codeWriter.unindent(2);
        codeWriter.emit(whitespace + ")");
    }

    private void emitAnnotationValues(CodeWriter codeWriter, String whitespace, String memberSeparator, List<CodeBlock> values) throws IOException {
        if (values.size() == 1) {
            codeWriter.indent(2);
            codeWriter.emit(values.get(0));
            codeWriter.unindent(2);
            return;
        }
        codeWriter.emit(StrPool.DELIM_START + whitespace);
        codeWriter.indent(2);
        boolean first = true;
        for (CodeBlock codeBlock : values) {
            if (!first) {
                codeWriter.emit(memberSeparator);
            }
            codeWriter.emit(codeBlock);
            first = false;
        }
        codeWriter.unindent(2);
        codeWriter.emit(whitespace + "}");
    }

    public static AnnotationSpec get(Annotation annotation) {
        return get(annotation, false);
    }

    public static AnnotationSpec get(Annotation annotation, boolean includeDefaultValues) {
        Builder builder = builder(annotation.annotationType());
        try {
            Method[] methods = annotation.annotationType().getDeclaredMethods();
            Arrays.sort(methods, Comparator.comparing((v0) -> {
                return v0.getName();
            }));
            for (Method method : methods) {
                Object value = method.invoke(annotation, new Object[0]);
                if (includeDefaultValues || !Objects.deepEquals(value, method.getDefaultValue())) {
                    if (value.getClass().isArray()) {
                        for (int i = 0; i < Array.getLength(value); i++) {
                            builder.addMemberForValue(method.getName(), Array.get(value, i));
                        }
                    } else if (value instanceof Annotation) {
                        builder.addMember(method.getName(), "$L", get((Annotation) value));
                    } else {
                        builder.addMemberForValue(method.getName(), value);
                    }
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Reflecting " + annotation + " failed!", e);
        }
    }

    public static AnnotationSpec get(AnnotationMirror annotation) {
        TypeElement element = annotation.getAnnotationType().asElement();
        Builder builder = builder(ClassName.get(element));
        Visitor visitor = new Visitor(builder);
        for (ExecutableElement executableElement : annotation.getElementValues().keySet()) {
            String name = executableElement.getSimpleName().toString();
            AnnotationValue value = (AnnotationValue) annotation.getElementValues().get(executableElement);
            value.accept(visitor, name);
        }
        return builder.build();
    }

    public static Builder builder(ClassName type) {
        Util.checkNotNull(type, "type == null", new Object[0]);
        return new Builder(type);
    }

    public static Builder builder(Class<?> type) {
        return builder(ClassName.get(type));
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.type);
        for (Map.Entry<String, List<CodeBlock>> entry : this.members.entrySet()) {
            builder.members.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        return builder;
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
            codeWriter.emit("$L", this);
            return out.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/AnnotationSpec$Builder.class */
    public static final class Builder {
        private final TypeName type;
        public final Map<String, List<CodeBlock>> members;

        private Builder(TypeName type) {
            this.members = new LinkedHashMap();
            this.type = type;
        }

        public Builder addMember(String name, String format, Object... args) {
            return addMember(name, CodeBlock.of(format, args));
        }

        public Builder addMember(String name, CodeBlock codeBlock) {
            List<CodeBlock> values = this.members.computeIfAbsent(name, k -> {
                return new ArrayList();
            });
            values.add(codeBlock);
            return this;
        }

        Builder addMemberForValue(String memberName, Object value) {
            Util.checkNotNull(memberName, "memberName == null", new Object[0]);
            Util.checkNotNull(value, "value == null, constant non-null value expected for %s", memberName);
            Util.checkArgument(SourceVersion.isName(memberName), "not a valid name: %s", memberName);
            if (value instanceof Class) {
                return addMember(memberName, "$T.class", value);
            }
            if (value instanceof Enum) {
                return addMember(memberName, "$T.$L", value.getClass(), ((Enum) value).name());
            }
            if (value instanceof String) {
                return addMember(memberName, "$S", value);
            }
            if (value instanceof Float) {
                return addMember(memberName, "$Lf", value);
            }
            if (value instanceof Character) {
                return addMember(memberName, "'$L'", Util.characterLiteralWithoutSingleQuotes(((Character) value).charValue()));
            }
            return addMember(memberName, "$L", value);
        }

        public AnnotationSpec build() {
            for (String name : this.members.keySet()) {
                Util.checkNotNull(name, "name == null", new Object[0]);
                Util.checkArgument(SourceVersion.isName(name), "not a valid name: %s", name);
            }
            return new AnnotationSpec(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/AnnotationSpec$Visitor.class */
    public static class Visitor extends SimpleAnnotationValueVisitor8<Builder, String> {
        final Builder builder;

        public /* bridge */ /* synthetic */ Object visitArray(List list, Object obj) {
            return visitArray((List<? extends AnnotationValue>) list, (String) obj);
        }

        Visitor(Builder builder) {
            super(builder);
            this.builder = builder;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Builder defaultAction(Object o, String name) {
            return this.builder.addMemberForValue(name, o);
        }

        public Builder visitAnnotation(AnnotationMirror a, String name) {
            return this.builder.addMember(name, "$L", AnnotationSpec.get(a));
        }

        public Builder visitEnumConstant(VariableElement c, String name) {
            return this.builder.addMember(name, "$T.$L", c.asType(), c.getSimpleName());
        }

        public Builder visitType(TypeMirror t, String name) {
            return this.builder.addMember(name, "$T.class", t);
        }

        public Builder visitArray(List<? extends AnnotationValue> values, String name) {
            for (AnnotationValue value : values) {
                value.accept(this, name);
            }
            return this.builder;
        }
    }
}
