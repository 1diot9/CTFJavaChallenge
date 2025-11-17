package org.springframework.scheduling.config;

import java.time.Duration;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/FixedRateTask.class */
public class FixedRateTask extends IntervalTask {
    @Deprecated(since = "6.0")
    public FixedRateTask(Runnable runnable, long interval, long initialDelay) {
        super(runnable, interval, initialDelay);
    }

    public FixedRateTask(Runnable runnable, Duration interval, Duration initialDelay) {
        super(runnable, interval, initialDelay);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FixedRateTask(IntervalTask task) {
        super(task);
    }
}
