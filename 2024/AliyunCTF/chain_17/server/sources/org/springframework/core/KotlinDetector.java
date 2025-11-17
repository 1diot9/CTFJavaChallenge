package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/KotlinDetector.class */
public abstract class KotlinDetector {

    @Nullable
    private static final Class<? extends Annotation> kotlinMetadata;
    private static final boolean kotlinPresent;
    private static final boolean kotlinReflectPresent;

    /* JADX WARN: Multi-variable type inference failed */
    static {
        Class cls;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            cls = ClassUtils.forName("kotlin.Metadata", classLoader);
        } catch (ClassNotFoundException e) {
            cls = null;
        }
        kotlinMetadata = cls;
        kotlinPresent = kotlinMetadata != null;
        kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
    }

    public static boolean isKotlinPresent() {
        return kotlinPresent;
    }

    public static boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    public static boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata == null || clazz.getDeclaredAnnotation(kotlinMetadata) == null) ? false : true;
    }

    public static boolean isSuspendingFunction(Method method) {
        if (isKotlinType(method.getDeclaringClass())) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length > 0 && "kotlin.coroutines.Continuation".equals(types[types.length - 1].getName())) {
                return true;
            }
            return false;
        }
        return false;
    }
}
