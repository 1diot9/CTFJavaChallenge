package org.springframework.core.type;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/StandardAnnotationMetadata.class */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {
    private final MergedAnnotations mergedAnnotations;
    private final boolean nestedAnnotationsAsMap;

    @Nullable
    private Set<String> annotationTypes;

    @Deprecated
    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        this(introspectedClass, false);
    }

    @Deprecated
    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.mergedAnnotations = MergedAnnotations.from(introspectedClass, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none());
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
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

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.nestedAnnotationsAsMap) {
            return super.getAnnotationAttributes(annotationName, classValuesAsString);
        }
        return AnnotatedElementUtils.getMergedAnnotationAttributes(getIntrospectedClass(), annotationName, classValuesAsString, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.nestedAnnotationsAsMap) {
            return super.getAllAnnotationAttributes(annotationName, classValuesAsString);
        }
        return AnnotatedElementUtils.getAllAnnotationAttributes(getIntrospectedClass(), annotationName, classValuesAsString, false);
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasAnnotatedMethods(String annotationName) {
        if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
            try {
                Method[] methods = ReflectionUtils.getDeclaredMethods(getIntrospectedClass());
                for (Method method : methods) {
                    if (isAnnotatedMethod(method, annotationName)) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
            }
        }
        return false;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> result = new LinkedHashSet<>(4);
        if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
            ReflectionUtils.doWithLocalMethods(getIntrospectedClass(), method -> {
                if (isAnnotatedMethod(method, annotationName)) {
                    result.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
                }
            });
        }
        return result;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getDeclaredMethods() {
        Set<MethodMetadata> result = new LinkedHashSet<>(16);
        ReflectionUtils.doWithLocalMethods(getIntrospectedClass(), method -> {
            result.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
        });
        return result;
    }

    private static boolean isAnnotatedMethod(Method method, String annotationName) {
        return !method.isBridge() && method.getAnnotations().length > 0 && AnnotatedElementUtils.isAnnotated(method, annotationName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotationMetadata from(Class<?> introspectedClass) {
        return new StandardAnnotationMetadata(introspectedClass, true);
    }
}
