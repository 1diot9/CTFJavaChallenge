package org.springframework.scheduling.concurrent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.ErrorHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/SimpleAsyncTaskScheduler.class */
public class SimpleAsyncTaskScheduler extends SimpleAsyncTaskExecutor implements TaskScheduler, ApplicationContextAware, SmartLifecycle, ApplicationListener<ContextClosedEvent> {
    private static final TimeUnit NANO = TimeUnit.NANOSECONDS;
    private final ScheduledExecutorService scheduledExecutor = createScheduledExecutor();
    private final ExecutorLifecycleDelegate lifecycleDelegate = new ExecutorLifecycleDelegate(this.scheduledExecutor);
    private Clock clock = Clock.systemDefaultZone();
    private int phase = Integer.MAX_VALUE;

    @Nullable
    private Executor targetTaskExecutor;

    @Nullable
    private ApplicationContext applicationContext;

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public Clock getClock() {
        return this.clock;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Override // org.springframework.context.SmartLifecycle, org.springframework.context.Phased
    public int getPhase() {
        return this.phase;
    }

    public void setTargetTaskExecutor(Executor targetTaskExecutor) {
        this.targetTaskExecutor = targetTaskExecutor == this ? null : targetTaskExecutor;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private ScheduledExecutorService createScheduledExecutor() {
        return new ScheduledThreadPoolExecutor(1, x$0 -> {
            return this.newThread(x$0);
        }) { // from class: org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler.1
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable task) {
                SimpleAsyncTaskScheduler.this.lifecycleDelegate.beforeExecute(thread);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void afterExecute(Runnable task, Throwable ex) {
                SimpleAsyncTaskScheduler.this.lifecycleDelegate.afterExecute();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.task.SimpleAsyncTaskExecutor
    public void doExecute(Runnable task) {
        if (this.targetTaskExecutor != null) {
            this.targetTaskExecutor.execute(task);
        } else {
            super.doExecute(task);
        }
    }

    private Runnable scheduledTask(Runnable task) {
        return () -> {
            execute(task);
        };
    }

    private Runnable taskOnSchedulerThread(Runnable task) {
        return new DelegatingErrorHandlingRunnable(task, TaskUtils.getDefaultErrorHandler(true));
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        try {
            Runnable delegate = scheduledTask(task);
            ErrorHandler errorHandler = TaskUtils.getDefaultErrorHandler(true);
            return new ReschedulingRunnable(delegate, trigger, this.clock, this.scheduledExecutor, errorHandler).schedule();
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        Duration delay = Duration.between(this.clock.instant(), startTime);
        try {
            return this.scheduledExecutor.schedule(scheduledTask(task), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(scheduledTask(task), NANO.convert(initialDelay), NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(scheduledTask(task), 0L, NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(taskOnSchedulerThread(task), NANO.convert(initialDelay), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(taskOnSchedulerThread(task), 0L, NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(this.scheduledExecutor, task, ex);
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        this.lifecycleDelegate.start();
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        this.lifecycleDelegate.stop();
    }

    @Override // org.springframework.context.SmartLifecycle
    public void stop(Runnable callback) {
        this.lifecycleDelegate.stop(callback);
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.lifecycleDelegate.isRunning();
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            this.scheduledExecutor.shutdown();
        }
    }

    @Override // org.springframework.core.task.SimpleAsyncTaskExecutor, java.lang.AutoCloseable
    public void close() {
        for (Runnable remainingTask : this.scheduledExecutor.shutdownNow()) {
            if (remainingTask instanceof Future) {
                Future<?> future = (Future) remainingTask;
                future.cancel(true);
            }
        }
        super.close();
    }
}
