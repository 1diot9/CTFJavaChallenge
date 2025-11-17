package org.springframework.cglib.core;

import org.springframework.asm.Label;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ProcessSwitchCallback.class */
public interface ProcessSwitchCallback {
    void processCase(int key, Label end) throws Exception;

    void processDefault() throws Exception;
}
