package org.springframework.cglib.core;

import org.springframework.asm.Label;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ObjectSwitchCallback.class */
public interface ObjectSwitchCallback {
    void processCase(Object key, Label end) throws Exception;

    void processDefault() throws Exception;
}
