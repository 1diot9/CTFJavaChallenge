package org.springframework.scheduling.config;

import java.time.Duration;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/FixedDelayTask.class */
public class FixedDelayTask extends IntervalTask {
    @Deprecated(since = "6.0")
    public FixedDelayTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable, interval, initialDelay);
    }

    public FixedDelayTask(Runnable runnable, Duration interval, Duration initialDelay) {
        super(runnable, interval, initialDelay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FixedDelayTask(IntervalTask task) {
        super(task);
    }
}
