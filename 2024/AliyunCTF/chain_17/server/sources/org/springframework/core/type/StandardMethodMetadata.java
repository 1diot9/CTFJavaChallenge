package org.springframework.core.type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/StandardMethodMetadata.class */
public class StandardMethodMetadata implements MethodMetadata {
    private final Method introspectedMethod;
    private final boolean nestedAnnotationsAsMap;
    private final MergedAnnotations mergedAnnotations;

    @Deprecated
    public StandardMethodMetadata(Method introspectedMethod) {
        this(introspectedMethod, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
        Assert.notNull(introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
        this.mergedAnnotations = MergedAnnotations.from(introspectedMethod, MergedAnnotations.SearchStrategy.DIRECT, RepeatableContainers.none());
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isOverridable() {
        return (isStatic() || isFinal() || isPrivate()) ? false : true;
    }

    private boolean isPrivate() {
        return Modifier.isPrivate(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.nestedAnnotationsAsMap) {
            return super.getAnnotationAttributes(annotationName, classValuesAsString);
        }
        return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.nestedAnnotationsAsMap) {
            return super.getAllAnnotationAttributes(annotationName, classValuesAsString);
        }
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, false);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof StandardMethodMetadata) {
                StandardMethodMetadata that = (StandardMethodMetadata) other;
                if (this.introspectedMethod.equals(that.introspectedMethod)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.introspectedMethod.hashCode();
    }

    public String toString() {
        return this.introspectedMethod.toString();
    }
}
