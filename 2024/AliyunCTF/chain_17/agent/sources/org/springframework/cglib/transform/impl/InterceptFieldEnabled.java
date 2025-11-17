package org.springframework.cglib.transform.impl;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/InterceptFieldEnabled.class */
public interface InterceptFieldEnabled {
    void setInterceptFieldCallback(InterceptFieldCallback callback);

    InterceptFieldCallback getInterceptFieldCallback();
}
