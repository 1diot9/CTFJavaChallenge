package org.springframework.scheduling.config;

import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/Task.class */
public class Task {
    private final Runnable runnable;

    public Task(Runnable runnable) {
        Assert.notNull(runnable, "Runnable must not be null");
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public String toString() {
        return this.runnable.toString();
    }
}
