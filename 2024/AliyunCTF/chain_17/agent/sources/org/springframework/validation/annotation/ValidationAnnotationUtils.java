package org.springframework.validation.annotation;

import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/annotation/ValidationAnnotationUtils.class */
public abstract class ValidationAnnotationUtils {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    @Nullable
    public static Object[] determineValidationHints(Annotation ann) {
        if (ann instanceof Validated) {
            Validated validated = (Validated) ann;
            return validated.value();
        }
        Class<? extends Annotation> annotationType = ann.annotationType();
        if ("jakarta.validation.Valid".equals(annotationType.getName())) {
            return EMPTY_OBJECT_ARRAY;
        }
        Validated validatedAnn = (Validated) AnnotationUtils.getAnnotation(ann, Validated.class);
        if (validatedAnn != null) {
            return validatedAnn.value();
        }
        if (annotationType.getSimpleName().startsWith("Valid")) {
            return convertValidationHints(AnnotationUtils.getValue(ann));
        }
        return null;
    }

    private static Object[] convertValidationHints(@Nullable Object hints) {
        if (hints == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        if (!(hints instanceof Object[])) {
            return new Object[]{hints};
        }
        Object[] objectHints = (Object[]) hints;
        return objectHints;
    }
}
