package org.springframework.aop;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/TargetSource.class */
public interface TargetSource extends TargetClassAware {
    @Override // org.springframework.aop.TargetClassAware
    @Nullable
    Class<?> getTargetClass();

    @Nullable
    Object getTarget() throws Exception;

    default boolean isStatic() {
        return false;
    }

    default void releaseTarget(Object target) throws Exception {
    }
}
