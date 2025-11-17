package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.BasicOperation;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/CacheOperationInvocationContext.class */
public interface CacheOperationInvocationContext<O extends BasicOperation> {
    O getOperation();

    Object getTarget();

    Method getMethod();

    Object[] getArgs();
}
