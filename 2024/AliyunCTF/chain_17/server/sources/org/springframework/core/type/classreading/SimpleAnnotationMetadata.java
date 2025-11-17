package org.springframework.core.type.classreading;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/classreading/SimpleAnnotationMetadata.class */
public final class SimpleAnnotationMetadata implements AnnotationMetadata {
    private final String className;
    private final int access;

    @Nullable
    private final String enclosingClassName;

    @Nullable
    private final String superClassName;
    private final boolean independentInnerClass;
    private final Set<String> interfaceNames;
    private final Set<String> memberClassNames;
    private final Set<MethodMetadata> declaredMethods;
    private final MergedAnnotations mergedAnnotations;

    @Nullable
    private Set<String> annotationTypes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleAnnotationMetadata(String className, int access, @Nullable String enclosingClassName, @Nullable String superClassName, boolean independentInnerClass, Set<String> interfaceNames, Set<String> memberClassNames, Set<MethodMetadata> declaredMethods, MergedAnnotations mergedAnnotations) {
        this.className = className;
        this.access = access;
        this.enclosingClassName = enclosingClassName;
        this.superClassName = superClassName;
        this.independentInnerClass = independentInnerClass;
        this.interfaceNames = interfaceNames;
        this.memberClassNames = memberClassNames;
        this.declaredMethods = declaredMethods;
        this.mergedAnnotations = mergedAnnotations;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String getClassName() {
        return this.className;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isInterface() {
        return (this.access & 512) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAnnotation() {
        return (this.access & 8192) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAbstract() {
        return (this.access & 1024) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isFinal() {
        return (this.access & 16) != 0;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isIndependent() {
        return this.enclosingClassName == null || this.independentInnerClass;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getSuperClassName() {
        return this.superClassName;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getInterfaceNames() {
        return StringUtils.toStringArray(this.interfaceNames);
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getMemberClassNames() {
        return StringUtils.toStringArray(this.memberClassNames);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> result = new LinkedHashSet<>(4);
        for (MethodMetadata annotatedMethod : this.declaredMethods) {
            if (annotatedMethod.isAnnotated(annotationName)) {
                result.add(annotatedMethod);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getDeclaredMethods() {
        return Collections.unmodifiableSet(this.declaredMethods);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof SimpleAnnotationMetadata) {
                SimpleAnnotationMetadata that = (SimpleAnnotationMetadata) other;
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
