package org.springframework.aop.framework;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AopProxy.class */
public interface AopProxy {
    Object getProxy();

    Object getProxy(@Nullable ClassLoader classLoader);

    Class<?> getProxyClass(@Nullable ClassLoader classLoader);
}
