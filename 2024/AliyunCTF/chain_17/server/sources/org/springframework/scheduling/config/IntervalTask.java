package org.springframework.scheduling.config;

import java.time.Duration;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/IntervalTask.class */
public class IntervalTask extends DelayedTask {
    private final Duration interval;

    @Deprecated(since = "6.0")
    public IntervalTask(Runnable runnable, long interval, long initialDelay) {
        this(runnable, Duration.ofMillis(interval), Duration.ofMillis(initialDelay));
    }

    @Deprecated(since = "6.0")
    public IntervalTask(Runnable runnable, long interval) {
        this(runnable, Duration.ofMillis(interval), Duration.ZERO);
    }

    public IntervalTask(Runnable runnable, Duration interval) {
        this(runnable, interval, Duration.ZERO);
    }

    public IntervalTask(Runnable runnable, Duration interval, Duration initialDelay) {
        super(runnable, initialDelay);
        Assert.notNull(interval, "Interval must not be null");
        this.interval = interval;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IntervalTask(IntervalTask task) {
        super(task);
        this.interval = task.getIntervalDuration();
    }

    @Deprecated(since = "6.0")
    public long getInterval() {
        return this.interval.toMillis();
    }

    public Duration getIntervalDuration() {
        return this.interval;
    }

    @Deprecated(since = "6.0")
    public long getInitialDelay() {
        return getInitialDelayDuration().toMillis();
    }

    @Override // org.springframework.scheduling.config.DelayedTask
    public Duration getInitialDelayDuration() {
        return super.getInitialDelayDuration();
    }
}
