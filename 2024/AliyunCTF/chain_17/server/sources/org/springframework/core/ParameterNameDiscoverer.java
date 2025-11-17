package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/ParameterNameDiscoverer.class */
public interface ParameterNameDiscoverer {
    @Nullable
    String[] getParameterNames(Method method);

    @Nullable
    String[] getParameterNames(Constructor<?> ctor);
}
