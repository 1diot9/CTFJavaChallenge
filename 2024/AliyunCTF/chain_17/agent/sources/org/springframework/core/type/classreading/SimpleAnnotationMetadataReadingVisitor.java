package org.springframework.core.type.classreading;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.Constants;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadataReadingVisitor.class */
public final class SimpleAnnotationMetadataReadingVisitor extends ClassVisitor {

    @Nullable
    private final ClassLoader classLoader;
    private String className;
    private int access;

    @Nullable
    private String superClassName;

    @Nullable
    private String enclosingClassName;
    private boolean independentInnerClass;
    private final Set<String> interfaceNames;
    private final Set<String> memberClassNames;
    private final Set<MergedAnnotation<?>> annotations;
    private final Set<MethodMetadata> declaredMethods;

    @Nullable
    private SimpleAnnotationMetadata metadata;

    @Nullable
    private Source source;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleAnnotationMetadataReadingVisitor(@Nullable ClassLoader classLoader) {
        super(17432576);
        this.className = "";
        this.interfaceNames = new LinkedHashSet(4);
        this.memberClassNames = new LinkedHashSet(4);
        this.annotations = new LinkedHashSet(4);
        this.declaredMethods = new LinkedHashSet(4);
        this.classLoader = classLoader;
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, @Nullable String supername, String[] interfaces) {
        this.className = toClassName(name);
        this.access = access;
        if (supername != null && !isInterface(access)) {
            this.superClassName = toClassName(supername);
        }
        for (String element : interfaces) {
            this.interfaceNames.add(toClassName(element));
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitOuterClass(String owner, String name, String desc) {
        this.enclosingClassName = toClassName(owner);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitInnerClass(String name, @Nullable String outerName, String innerName, int access) {
        if (outerName != null) {
            String className = toClassName(name);
            String outerClassName = toClassName(outerName);
            if (this.className.equals(className)) {
                this.enclosingClassName = outerClassName;
                this.independentInnerClass = (access & 8) != 0;
            } else if (this.className.equals(outerClassName)) {
                this.memberClassNames.add(className);
            }
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    @Nullable
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        ClassLoader classLoader = this.classLoader;
        Source source = getSource();
        Set<MergedAnnotation<?>> set = this.annotations;
        Objects.requireNonNull(set);
        return MergedAnnotationReadingVisitor.get(classLoader, source, descriptor, visible, (v1) -> {
            r4.add(v1);
        });
    }

    @Override // org.springframework.asm.ClassVisitor
    @Nullable
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (isBridge(access) || name.equals(Constants.CONSTRUCTOR_NAME)) {
            return null;
        }
        ClassLoader classLoader = this.classLoader;
        String str = this.className;
        Set<MethodMetadata> set = this.declaredMethods;
        Objects.requireNonNull(set);
        return new SimpleMethodMetadataReadingVisitor(classLoader, str, access, name, descriptor, (v1) -> {
            r7.add(v1);
        });
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
        MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
        this.metadata = new SimpleAnnotationMetadata(this.className, this.access, this.enclosingClassName, this.superClassName, this.independentInnerClass, this.interfaceNames, this.memberClassNames, this.declaredMethods, annotations);
    }

    public SimpleAnnotationMetadata getMetadata() {
        Assert.state(this.metadata != null, "AnnotationMetadata not initialized");
        return this.metadata;
    }

    private Source getSource() {
        Source source = this.source;
        if (source == null) {
            source = new Source(this.className);
            this.source = source;
        }
        return source;
    }

    private String toClassName(String name) {
        return ClassUtils.convertResourcePathToClassName(name);
    }

    private boolean isBridge(int access) {
        return (access & 64) != 0;
    }

    private boolean isInterface(int access) {
        return (access & 512) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadataReadingVisitor$Source.class */
    public static final class Source {
        private final String className;

        Source(String className) {
            this.className = className;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof Source) {
                    Source that = (Source) other;
                    if (this.className.equals(that.className)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.className.hashCode();
        }

        public String toString() {
            return this.className;
        }
    }
}
