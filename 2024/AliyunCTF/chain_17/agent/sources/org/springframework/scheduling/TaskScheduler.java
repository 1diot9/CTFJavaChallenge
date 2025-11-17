package org.springframework.scheduling;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/TaskScheduler.class */
public interface TaskScheduler {
    @Nullable
    ScheduledFuture<?> schedule(Runnable task, Trigger trigger);

    ScheduledFuture<?> schedule(Runnable task, Instant startTime);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay);

    default Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Deprecated(since = "6.0")
    default ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return schedule(task, startTime.toInstant());
    }

    @Deprecated(since = "6.0")
    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return scheduleAtFixedRate(task, startTime.toInstant(), Duration.ofMillis(period));
    }

    @Deprecated(since = "6.0")
    default ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return scheduleAtFixedRate(task, Duration.ofMillis(period));
    }

    @Deprecated(since = "6.0")
    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return scheduleWithFixedDelay(task, startTime.toInstant(), Duration.ofMillis(delay));
    }

    @Deprecated(since = "6.0")
    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return scheduleWithFixedDelay(task, Duration.ofMillis(delay));
    }
}
