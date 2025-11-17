package org.springframework.boot;

import org.springframework.context.ApplicationEvent;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ExitCodeEvent.class */
public class ExitCodeEvent extends ApplicationEvent {
    private final int exitCode;

    public ExitCodeEvent(Object source, int exitCode) {
        super(source);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return this.exitCode;
    }
}
