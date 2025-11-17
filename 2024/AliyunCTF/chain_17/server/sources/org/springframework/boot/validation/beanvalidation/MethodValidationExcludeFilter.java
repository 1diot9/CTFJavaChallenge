package org.springframework.boot.validation.beanvalidation;

import java.lang.annotation.Annotation;
import org.springframework.core.annotation.MergedAnnotations;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/validation/beanvalidation/MethodValidationExcludeFilter.class */
public interface MethodValidationExcludeFilter {
    boolean isExcluded(Class<?> type);

    static MethodValidationExcludeFilter byAnnotation(Class<? extends Annotation> annotationType) {
        return byAnnotation(annotationType, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS);
    }

    static MethodValidationExcludeFilter byAnnotation(Class<? extends Annotation> annotationType, MergedAnnotations.SearchStrategy searchStrategy) {
        return type -> {
            return MergedAnnotations.from(type, searchStrategy).isPresent(annotationType);
        };
    }
}
