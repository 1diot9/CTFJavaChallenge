package org.springframework.scheduling.concurrent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ErrorHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ThreadPoolTaskScheduler.class */
public class ThreadPoolTaskScheduler extends ExecutorConfigurationSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor, TaskScheduler {
    private static final TimeUnit NANO = TimeUnit.NANOSECONDS;
    private volatile boolean removeOnCancelPolicy;
    private volatile boolean continueExistingPeriodicTasksAfterShutdownPolicy;

    @Nullable
    private volatile ErrorHandler errorHandler;

    @Nullable
    private ScheduledExecutorService scheduledExecutor;
    private volatile int poolSize = 1;
    private volatile boolean executeExistingDelayedTasksAfterShutdownPolicy = true;
    private Clock clock = Clock.systemDefaultZone();
    private final Map<Object, ListenableFuture<?>> listenableFutureMap = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public void setPoolSize(int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) scheduledExecutorService;
            threadPoolExecutor.setCorePoolSize(poolSize);
        }
        this.poolSize = poolSize;
    }

    public void setRemoveOnCancelPolicy(boolean flag) {
        ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) scheduledExecutorService;
            threadPoolExecutor.setRemoveOnCancelPolicy(flag);
        }
        this.removeOnCancelPolicy = flag;
    }

    public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean flag) {
        ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) scheduledExecutorService;
            threadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(flag);
        }
        this.continueExistingPeriodicTasksAfterShutdownPolicy = flag;
    }

    public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean flag) {
        ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) scheduledExecutorService;
            threadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(flag);
        }
        this.executeExistingDelayedTasksAfterShutdownPolicy = flag;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public Clock getClock() {
        return this.clock;
    }

    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        this.scheduledExecutor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        ScheduledExecutorService scheduledExecutorService = this.scheduledExecutor;
        if (scheduledExecutorService instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) scheduledExecutorService;
            if (this.removeOnCancelPolicy) {
                threadPoolExecutor.setRemoveOnCancelPolicy(true);
            }
            if (this.continueExistingPeriodicTasksAfterShutdownPolicy) {
                threadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
            }
            if (!this.executeExistingDelayedTasksAfterShutdownPolicy) {
                threadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            }
        }
        return this.scheduledExecutor;
    }

    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler) { // from class: org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler.1
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable task) {
                ThreadPoolTaskScheduler.this.beforeExecute(thread, task);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void afterExecute(Runnable task, Throwable ex) {
                ThreadPoolTaskScheduler.this.afterExecute(task, ex);
            }
        };
    }

    public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor != null, "ThreadPoolTaskScheduler not initialized");
        return this.scheduledExecutor;
    }

    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor instanceof ScheduledThreadPoolExecutor, "No ScheduledThreadPoolExecutor available");
        return (ScheduledThreadPoolExecutor) this.scheduledExecutor;
    }

    public int getPoolSize() {
        if (this.scheduledExecutor == null) {
            return this.poolSize;
        }
        return getScheduledThreadPoolExecutor().getPoolSize();
    }

    public int getActiveCount() {
        if (this.scheduledExecutor == null) {
            return 0;
        }
        return getScheduledThreadPoolExecutor().getActiveCount();
    }

    @Deprecated
    public boolean isRemoveOnCancelPolicy() {
        if (this.scheduledExecutor == null) {
            return this.removeOnCancelPolicy;
        }
        return getScheduledThreadPoolExecutor().getRemoveOnCancelPolicy();
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        Executor executor = getScheduledExecutor();
        try {
            executor.execute(errorHandlingTask(task, false));
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            return executor.submit(errorHandlingTask(task, false));
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            Callable<T> taskToUse = task;
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler != null) {
                taskToUse = new DelegatingErrorHandlingCallable(task, errorHandler);
            }
            return executor.submit(taskToUse);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            ListenableFutureTask<Object> listenableFuture = new ListenableFutureTask<>(task, null);
            executeAndTrack(executor, listenableFuture);
            return listenableFuture;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ExecutorService executor = getScheduledExecutor();
        try {
            ListenableFutureTask<?> listenableFutureTask = new ListenableFutureTask<>(task);
            executeAndTrack(executor, listenableFutureTask);
            return listenableFutureTask;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    private void executeAndTrack(ExecutorService executor, ListenableFutureTask<?> listenableFuture) {
        Future<?> scheduledFuture = executor.submit(errorHandlingTask(listenableFuture, false));
        this.listenableFutureMap.put(scheduledFuture, listenableFuture);
        listenableFuture.addCallback(result -> {
            this.listenableFutureMap.remove(scheduledFuture);
        }, ex -> {
            this.listenableFutureMap.remove(scheduledFuture);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    public void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
        ListenableFuture<?> listenableFuture = this.listenableFutureMap.get(task);
        if (listenableFuture != null) {
            listenableFuture.cancel(true);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            ErrorHandler errorHandler = this.errorHandler;
            if (errorHandler == null) {
                errorHandler = TaskUtils.getDefaultErrorHandler(true);
            }
            return new ReschedulingRunnable(task, trigger, this.clock, executor, errorHandler).schedule();
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> schedule(Runnable task, Instant startTime) {
        ScheduledExecutorService executor = getScheduledExecutor();
        Duration delay = Duration.between(this.clock.instant(), startTime);
        try {
            return executor.schedule(errorHandlingTask(task, false), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Instant startTime, Duration period) {
        ScheduledExecutorService executor = getScheduledExecutor();
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return executor.scheduleAtFixedRate(errorHandlingTask(task, true), NANO.convert(initialDelay), NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            return executor.scheduleAtFixedRate(errorHandlingTask(task, true), 0L, NANO.convert(period), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Instant startTime, Duration delay) {
        ScheduledExecutorService executor = getScheduledExecutor();
        Duration initialDelay = Duration.between(this.clock.instant(), startTime);
        try {
            return executor.scheduleWithFixedDelay(errorHandlingTask(task, true), NANO.convert(initialDelay), NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.scheduling.TaskScheduler
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        ScheduledExecutorService executor = getScheduledExecutor();
        try {
            return executor.scheduleWithFixedDelay(errorHandlingTask(task, true), 0L, NANO.convert(delay), NANO);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    private Runnable errorHandlingTask(Runnable task, boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ThreadPoolTaskScheduler$DelegatingErrorHandlingCallable.class */
    private static class DelegatingErrorHandlingCallable<V> implements Callable<V> {
        private final Callable<V> delegate;
        private final ErrorHandler errorHandler;

        public DelegatingErrorHandlingCallable(Callable<V> delegate, ErrorHandler errorHandler) {
            this.delegate = delegate;
            this.errorHandler = errorHandler;
        }

        @Override // java.util.concurrent.Callable
        @Nullable
        public V call() throws Exception {
            try {
                return this.delegate.call();
            } catch (Throwable ex) {
                this.errorHandler.handleError(ex);
                return null;
            }
        }
    }
}
