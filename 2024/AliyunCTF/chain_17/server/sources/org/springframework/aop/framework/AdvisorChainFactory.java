package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AdvisorChainFactory.class */
public interface AdvisorChainFactory {
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass);
}
