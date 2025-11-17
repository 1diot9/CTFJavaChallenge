package org.springframework.scheduling.config;

import java.time.Duration;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/DelayedTask.class */
public class DelayedTask extends Task {
    private final Duration initialDelay;

    public DelayedTask(Runnable runnable, Duration initialDelay) {
        super(runnable);
        Assert.notNull(initialDelay, "InitialDelay must not be null");
        this.initialDelay = initialDelay;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DelayedTask(DelayedTask task) {
        super(task.getRunnable());
        Assert.notNull(task, "DelayedTask must not be null");
        this.initialDelay = task.getInitialDelayDuration();
    }

    public Duration getInitialDelayDuration() {
        return this.initialDelay;
    }
}
