package io.micrometer.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/annotation/AnnotationUtils.class */
final class AnnotationUtils {
    private AnnotationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<AnnotatedParameter> findAnnotatedParameters(Class<? extends Annotation> annotationClazz, Method method, Object[] args) {
        Annotation[][] parameters = method.getParameterAnnotations();
        List<AnnotatedParameter> result = new ArrayList<>();
        int i = 0;
        for (Annotation[] parameter : parameters) {
            for (Annotation parameter2 : parameter) {
                if (annotationClazz.isAssignableFrom(parameter2.annotationType())) {
                    result.add(new AnnotatedParameter(i, parameter2, args[i]));
                }
            }
            i++;
        }
        return result;
    }
}
