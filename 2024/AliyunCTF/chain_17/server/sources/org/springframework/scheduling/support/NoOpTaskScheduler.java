package org.springframework.scheduling.support;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/NoOpTaskScheduler.class */
public class NoOpTaskScheduler implements TaskScheduler {
    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        Instant nextExecution = trigger.nextExecution(new SimpleTriggerContext(getClock()));
        if (nextExecution != null) {
            return new NoOpScheduledFuture();
        }
        return null;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        return new NoOpScheduledFuture();
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        return new NoOpScheduledFuture();
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return new NoOpScheduledFuture();
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        return new NoOpScheduledFuture();
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return new NoOpScheduledFuture();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/NoOpTaskScheduler$NoOpScheduledFuture.class */
    private static class NoOpScheduledFuture<V> implements ScheduledFuture<V> {
        private NoOpScheduledFuture() {
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            return true;
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            return true;
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            return true;
        }

        @Override // java.util.concurrent.Future
        public V get() {
            throw new CancellationException("No-op");
        }

        @Override // java.util.concurrent.Future
        public V get(long timeout, TimeUnit unit) {
            throw new CancellationException("No-op");
        }

        @Override // java.util.concurrent.Delayed
        public long getDelay(TimeUnit unit) {
            return 0L;
        }

        @Override // java.lang.Comparable
        public int compareTo(Delayed other) {
            return 0;
        }
    }
}
