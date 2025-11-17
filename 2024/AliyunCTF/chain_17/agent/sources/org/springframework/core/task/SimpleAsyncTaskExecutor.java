package org.springframework.core.task;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrencyThrottleSupport;
import org.springframework.util.CustomizableThreadCreator;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/SimpleAsyncTaskExecutor.class */
public class SimpleAsyncTaskExecutor extends CustomizableThreadCreator implements AsyncListenableTaskExecutor, Serializable, AutoCloseable {
    public static final int UNBOUNDED_CONCURRENCY = -1;
    public static final int NO_CONCURRENCY = 0;
    private final ConcurrencyThrottleAdapter concurrencyThrottle;

    @Nullable
    private VirtualThreadDelegate virtualThreadDelegate;

    @Nullable
    private ThreadFactory threadFactory;

    @Nullable
    private TaskDecorator taskDecorator;
    private long taskTerminationTimeout;

    @Nullable
    private Set<Thread> activeThreads;
    private volatile boolean active;

    public SimpleAsyncTaskExecutor() {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
        this.active = true;
    }

    public SimpleAsyncTaskExecutor(String threadNamePrefix) {
        super(threadNamePrefix);
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
        this.active = true;
    }

    public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
        this.active = true;
        this.threadFactory = threadFactory;
    }

    public void setVirtualThreads(boolean virtual) {
        this.virtualThreadDelegate = virtual ? new VirtualThreadDelegate() : null;
    }

    public void setThreadFactory(@Nullable ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Nullable
    public final ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    public void setTaskTerminationTimeout(long timeout) {
        Assert.isTrue(timeout >= 0, "Timeout value must be >=0");
        this.taskTerminationTimeout = timeout;
        this.activeThreads = timeout > 0 ? Collections.newSetFromMap(new ConcurrentHashMap()) : null;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
    }

    public final int getConcurrencyLimit() {
        return this.concurrencyThrottle.getConcurrencyLimit();
    }

    public final boolean isThrottleActive() {
        return this.concurrencyThrottle.isThrottleActive();
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        execute(task, Long.MAX_VALUE);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        Assert.notNull(task, "Runnable must not be null");
        if (!isActive()) {
            throw new TaskRejectedException(getClass().getSimpleName() + " has been closed already");
        }
        Runnable taskToUse = this.taskDecorator != null ? this.taskDecorator.decorate(task) : task;
        if (isThrottleActive() && startTimeout > 0) {
            this.concurrencyThrottle.beforeAccess();
            doExecute(new TaskTrackingRunnable(taskToUse));
        } else if (this.activeThreads != null) {
            doExecute(new TaskTrackingRunnable(taskToUse));
        } else {
            doExecute(taskToUse);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<>(task, null);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask<Object> future = new ListenableFutureTask<>(task, null);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
        execute(future, Long.MAX_VALUE);
        return future;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doExecute(Runnable task) {
        newThread(task).start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Thread newThread(Runnable task) {
        if (this.virtualThreadDelegate != null) {
            return this.virtualThreadDelegate.newVirtualThread(nextThreadName(), task);
        }
        return this.threadFactory != null ? this.threadFactory.newThread(task) : createThread(task);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.active) {
            this.active = false;
            Set<Thread> threads = this.activeThreads;
            if (threads != null) {
                threads.forEach((v0) -> {
                    v0.interrupt();
                });
                synchronized (threads) {
                    try {
                        if (!threads.isEmpty()) {
                            threads.wait(this.taskTerminationTimeout);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/SimpleAsyncTaskExecutor$ConcurrencyThrottleAdapter.class */
    public static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport {
        private ConcurrencyThrottleAdapter() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.ConcurrencyThrottleSupport
        public void beforeAccess() {
            super.beforeAccess();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.ConcurrencyThrottleSupport
        public void afterAccess() {
            super.afterAccess();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/SimpleAsyncTaskExecutor$TaskTrackingRunnable.class */
    public class TaskTrackingRunnable implements Runnable {
        private final Runnable task;

        public TaskTrackingRunnable(Runnable task) {
            Assert.notNull(task, "Task must not be null");
            this.task = task;
        }

        @Override // java.lang.Runnable
        public void run() {
            Set<Thread> threads = SimpleAsyncTaskExecutor.this.activeThreads;
            Thread thread = null;
            if (threads != null) {
                thread = Thread.currentThread();
                threads.add(thread);
            }
            try {
                this.task.run();
                if (threads != null) {
                    threads.remove(thread);
                    if (!SimpleAsyncTaskExecutor.this.isActive()) {
                        synchronized (threads) {
                            if (threads.isEmpty()) {
                                threads.notify();
                            }
                        }
                    }
                }
                SimpleAsyncTaskExecutor.this.concurrencyThrottle.afterAccess();
            } catch (Throwable th) {
                if (threads != null) {
                    threads.remove(thread);
                    if (!SimpleAsyncTaskExecutor.this.isActive()) {
                        synchronized (threads) {
                            if (threads.isEmpty()) {
                                threads.notify();
                            }
                        }
                    }
                }
                SimpleAsyncTaskExecutor.this.concurrencyThrottle.afterAccess();
                throw th;
            }
        }
    }
}
