package org.springframework.scheduling.concurrent;

import jakarta.enterprise.concurrent.LastExecution;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ErrorHandler;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler.class */
public class ConcurrentTaskScheduler extends ConcurrentTaskExecutor implements TaskScheduler {
    private static final TimeUnit NANO = TimeUnit.NANOSECONDS;

    @Nullable
    private static Class<?> managedScheduledExecutorServiceClass;

    @Nullable
    private ScheduledExecutorService scheduledExecutor;
    private boolean enterpriseConcurrentScheduler;

    @Nullable
    private ErrorHandler errorHandler;
    private Clock clock;

    static {
        try {
            managedScheduledExecutorServiceClass = ClassUtils.forName("jakarta.enterprise.concurrent.ManagedScheduledExecutorService", ConcurrentTaskScheduler.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            managedScheduledExecutorServiceClass = null;
        }
    }

    @Deprecated(since = "6.1")
    public ConcurrentTaskScheduler() {
        this.enterpriseConcurrentScheduler = false;
        this.clock = Clock.systemDefaultZone();
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.enterpriseConcurrentScheduler = false;
    }

    public ConcurrentTaskScheduler(@Nullable ScheduledExecutorService scheduledExecutor) {
        super(scheduledExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.clock = Clock.systemDefaultZone();
        if (scheduledExecutor != null) {
            initScheduledExecutor(scheduledExecutor);
        }
    }

    public ConcurrentTaskScheduler(Executor concurrentExecutor, ScheduledExecutorService scheduledExecutor) {
        super(concurrentExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.clock = Clock.systemDefaultZone();
        initScheduledExecutor(scheduledExecutor);
    }

    private void initScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        this.enterpriseConcurrentScheduler = managedScheduledExecutorServiceClass != null && managedScheduledExecutorServiceClass.isInstance(scheduledExecutor);
    }

    public void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        initScheduledExecutor(scheduledExecutor);
    }

    private ScheduledExecutorService getScheduledExecutor() {
        if (this.scheduledExecutor == null) {
            throw new IllegalStateException("No ScheduledExecutor is configured");
        }
        return this.scheduledExecutor;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public Clock getClock() {
        return this.clock;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        try {
            if (this.enterpriseConcurrentScheduler) {
                return new EnterpriseConcurrentTriggerScheduler().schedule(decorateTask(task, true), trigger);
            }
            ErrorHandler errorHandler = this.errorHandler != null ? this.errorHandler : TaskUtils.getDefaultErrorHandler(true);
            return new ReschedulingRunnable(task, trigger, this.clock, scheduleExecutorToUse, errorHandler).schedule();
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        Duration delay = Duration.between(this.clock.instant(), startTime);
        try {
            return scheduleExecutorToUse.schedule(decorateTask(task, false), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return scheduleExecutorToUse.scheduleAtFixedRate(decorateTask(task, true), NANO.convert(initialDelay), NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        try {
            return scheduleExecutorToUse.scheduleAtFixedRate(decorateTask(task, true), 0L, NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return scheduleExecutorToUse.scheduleWithFixedDelay(decorateTask(task, true), NANO.convert(initialDelay), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        ScheduledExecutorService scheduleExecutorToUse = getScheduledExecutor();
        try {
            return scheduleExecutorToUse.scheduleWithFixedDelay(decorateTask(task, true), 0L, NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(scheduleExecutorToUse, task, ex);
        }
    }

    private Runnable decorateTask(Runnable task, boolean isRepeatingTask) {
        Runnable result = TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
        if (this.enterpriseConcurrentScheduler) {
            result = ConcurrentTaskExecutor.ManagedTaskBuilder.buildManagedTask(result, task.toString());
        }
        return result;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler$EnterpriseConcurrentTriggerScheduler.class */
    private class EnterpriseConcurrentTriggerScheduler {
        private EnterpriseConcurrentTriggerScheduler() {
        }

        public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
            ManagedScheduledExecutorService executor = ConcurrentTaskScheduler.this.getScheduledExecutor();
            return executor.schedule(task, new TriggerAdapter(trigger));
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler$EnterpriseConcurrentTriggerScheduler$TriggerAdapter.class */
        public static class TriggerAdapter implements jakarta.enterprise.concurrent.Trigger {
            private final Trigger adaptee;

            public TriggerAdapter(Trigger adaptee) {
                this.adaptee = adaptee;
            }

            @Nullable
            public Date getNextRunTime(@Nullable LastExecution le, Date taskScheduledTime) {
                Instant instant = this.adaptee.nextExecution(new LastExecutionAdapter(le));
                if (instant != null) {
                    return Date.from(instant);
                }
                return null;
            }

            public boolean skipRun(LastExecution lastExecutionInfo, Date scheduledRunTime) {
                return false;
            }

            /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ConcurrentTaskScheduler$EnterpriseConcurrentTriggerScheduler$TriggerAdapter$LastExecutionAdapter.class */
            private static class LastExecutionAdapter implements TriggerContext {

                @Nullable
                private final LastExecution le;

                public LastExecutionAdapter(@Nullable LastExecution le) {
                    this.le = le;
                }

                @Override // org.springframework.scheduling.TriggerContext
                public Instant lastScheduledExecution() {
                    if (this.le != null) {
                        return toInstant(this.le.getScheduledStart());
                    }
                    return null;
                }

                @Override // org.springframework.scheduling.TriggerContext
                public Instant lastActualExecution() {
                    if (this.le != null) {
                        return toInstant(this.le.getRunStart());
                    }
                    return null;
                }

                @Override // org.springframework.scheduling.TriggerContext
                public Instant lastCompletion() {
                    if (this.le != null) {
                        return toInstant(this.le.getRunEnd());
                    }
                    return null;
                }

                @Nullable
                private static Instant toInstant(@Nullable Date date) {
                    if (date != null) {
                        return date.toInstant();
                    }
                    return null;
                }
            }
        }
    }
}
