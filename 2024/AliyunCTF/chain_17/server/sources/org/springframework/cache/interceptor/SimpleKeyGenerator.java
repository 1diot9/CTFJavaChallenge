package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.core.KotlinDetector;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/SimpleKeyGenerator.class */
public class SimpleKeyGenerator implements KeyGenerator {
    @Override // org.springframework.cache.interceptor.KeyGenerator
    public Object generate(Object target, Method method, Object... params) {
        return generateKey(KotlinDetector.isSuspendingFunction(method) ? Arrays.copyOf(params, params.length - 1) : params);
    }

    public static Object generateKey(Object... params) {
        Object param;
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1 && (param = params[0]) != null && !param.getClass().isArray()) {
            return param;
        }
        return new SimpleKey(params);
    }
}
