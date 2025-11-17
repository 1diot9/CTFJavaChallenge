package org.springframework.cglib.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/MethodInfoTransformer.class */
public class MethodInfoTransformer implements Transformer {
    private static final MethodInfoTransformer INSTANCE = new MethodInfoTransformer();

    public static MethodInfoTransformer getInstance() {
        return INSTANCE;
    }

    @Override // org.springframework.cglib.core.Transformer
    public Object transform(Object value) {
        if (value instanceof Method) {
            Method method = (Method) value;
            return ReflectUtils.getMethodInfo(method);
        }
        if (value instanceof Constructor) {
            Constructor<?> constructor = (Constructor) value;
            return ReflectUtils.getMethodInfo(constructor);
        }
        throw new IllegalArgumentException("cannot get method info for " + value);
    }
}
