package org.springframework.boot.diagnostics;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/FailureAnalysis.class */
public class FailureAnalysis {
    private final String description;
    private final String action;
    private final Throwable cause;

    public FailureAnalysis(String description, String action, Throwable cause) {
        this.description = description;
        this.action = action;
        this.cause = cause;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAction() {
        return this.action;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
