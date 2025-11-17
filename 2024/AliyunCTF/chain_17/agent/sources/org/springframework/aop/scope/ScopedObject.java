package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/scope/ScopedObject.class */
public interface ScopedObject extends RawTargetAccess {
    Object getTargetObject();

    void removeFromScope();
}
