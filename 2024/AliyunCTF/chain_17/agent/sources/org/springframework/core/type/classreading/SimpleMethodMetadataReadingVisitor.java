package org.springframework.core.type.classreading;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleMethodMetadataReadingVisitor.class */
final class SimpleMethodMetadataReadingVisitor extends MethodVisitor {

    @Nullable
    private final ClassLoader classLoader;
    private final String declaringClassName;
    private final int access;
    private final String methodName;
    private final String descriptor;
    private final List<MergedAnnotation<?>> annotations;
    private final Consumer<SimpleMethodMetadata> consumer;

    @Nullable
    private Source source;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleMethodMetadataReadingVisitor(@Nullable ClassLoader classLoader, String declaringClassName, int access, String methodName, String descriptor, Consumer<SimpleMethodMetadata> consumer) {
        super(17432576);
        this.annotations = new ArrayList(4);
        this.classLoader = classLoader;
        this.declaringClassName = declaringClassName;
        this.access = access;
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.consumer = consumer;
    }

    @Override // org.springframework.asm.MethodVisitor
    @Nullable
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        ClassLoader classLoader = this.classLoader;
        Object source = getSource();
        List<MergedAnnotation<?>> list = this.annotations;
        Objects.requireNonNull(list);
        return MergedAnnotationReadingVisitor.get(classLoader, source, descriptor, visible, (v1) -> {
            r4.add(v1);
        });
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitEnd() {
        String returnTypeName = Type.getReturnType(this.descriptor).getClassName();
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        SimpleMethodMetadata metadata = new SimpleMethodMetadata(this.methodName, this.access, this.declaringClassName, returnTypeName, getSource(), annotations);
        this.consumer.accept(metadata);
    }

    private Object getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.declaringClassName, this.methodName, this.descriptor);
            this.source = source;
        }
        return source;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleMethodMetadataReadingVisitor$Source.class */
    public static final class Source {
        private final String declaringClassName;
        private final String methodName;
        private final String descriptor;

        @Nullable
        private String toStringValue;

        Source(String declaringClassName, String methodName, String descriptor) {
            this.declaringClassName = declaringClassName;
            this.methodName = methodName;
            this.descriptor = descriptor;
        }

        public int hashCode() {
            int result = (31 * 1) + this.declaringClassName.hashCode();
            return (31 * ((31 * result) + this.methodName.hashCode())) + this.descriptor.hashCode();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Source otherSource = (Source) other;
            return this.declaringClassName.equals(otherSource.declaringClassName) && this.methodName.equals(otherSource.methodName) && this.descriptor.equals(otherSource.descriptor);
        }

        public String toString() {
            String value = this.toStringValue;
            if (value == null) {
                StringBuilder builder = new StringBuilder();
                builder.append(this.declaringClassName);
                builder.append('.');
                builder.append(this.methodName);
                Type[] argumentTypes = Type.getArgumentTypes(this.descriptor);
                builder.append('(');
                for (int i = 0; i < argumentTypes.length; i++) {
                    if (i != 0) {
                        builder.append(',');
                    }
                    builder.append(argumentTypes[i].getClassName());
                }
                builder.append(')');
                value = builder.toString();
                this.toStringValue = value;
            }
            return value;
        }
    }
}
