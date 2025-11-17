package org.springframework.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/ProxyMethodInvocation.class */
public interface ProxyMethodInvocation extends MethodInvocation {
    Object getProxy();

    MethodInvocation invocableClone();

    MethodInvocation invocableClone(Object... arguments);

    void setArguments(Object... arguments);

    void setUserAttribute(String key, @Nullable Object value);

    @Nullable
    Object getUserAttribute(String key);
}
