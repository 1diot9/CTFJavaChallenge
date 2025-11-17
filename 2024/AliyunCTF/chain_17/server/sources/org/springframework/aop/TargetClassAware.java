package org.springframework.aop;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/TargetClassAware.class */
public interface TargetClassAware {
    @Nullable
    Class<?> getTargetClass();
}
