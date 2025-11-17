package org.springframework.scheduling.concurrent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.class */
public class ThreadPoolTaskExecutor extends ExecutorConfigurationSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

    @Nullable
    private TaskDecorator taskDecorator;

    @Nullable
    private ThreadPoolExecutor threadPoolExecutor;
    private final Object poolSizeMonitor = new Object();
    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean allowCoreThreadTimeOut = false;
    private boolean prestartAllCoreThreads = false;
    private final Map<Runnable, Object> decoratedTaskMap = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public void setCorePoolSize(int corePoolSize) {
        synchronized (this.poolSizeMonitor) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setCorePoolSize(corePoolSize);
            }
            this.corePoolSize = corePoolSize;
        }
    }

    public int getCorePoolSize() {
        int i;
        synchronized (this.poolSizeMonitor) {
            i = this.corePoolSize;
        }
        return i;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        synchronized (this.poolSizeMonitor) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            }
            this.maxPoolSize = maxPoolSize;
        }
    }

    public int getMaxPoolSize() {
        int i;
        synchronized (this.poolSizeMonitor) {
            i = this.maxPoolSize;
        }
        return i;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        synchronized (this.poolSizeMonitor) {
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
            this.keepAliveSeconds = keepAliveSeconds;
        }
    }

    public int getKeepAliveSeconds() {
        int i;
        synchronized (this.poolSizeMonitor) {
            i = this.keepAliveSeconds;
        }
        return i;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public void setPrestartAllCoreThreads(boolean prestartAllCoreThreads) {
        this.prestartAllCoreThreads = prestartAllCoreThreads;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler) { // from class: org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor.1
            @Override // java.util.concurrent.ThreadPoolExecutor, java.util.concurrent.Executor
            public void execute(Runnable command) {
                Runnable decorated = command;
                if (ThreadPoolTaskExecutor.this.taskDecorator != null) {
                    decorated = ThreadPoolTaskExecutor.this.taskDecorator.decorate(command);
                    if (decorated != command) {
                        ThreadPoolTaskExecutor.this.decoratedTaskMap.put(decorated, command);
                    }
                }
                super.execute(decorated);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable task) {
                ThreadPoolTaskExecutor.this.beforeExecute(thread, task);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void afterExecute(Runnable task, Throwable ex) {
                ThreadPoolTaskExecutor.this.afterExecute(task, ex);
            }
        };
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }
        if (this.prestartAllCoreThreads) {
            executor.prestartAllCoreThreads();
        }
        this.threadPoolExecutor = executor;
        return executor;
    }

    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue(queueCapacity);
        }
        return new SynchronousQueue();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
        return this.threadPoolExecutor;
    }

    public int getPoolSize() {
        if (this.threadPoolExecutor == null) {
            return this.corePoolSize;
        }
        return this.threadPoolExecutor.getPoolSize();
    }

    public int getQueueSize() {
        if (this.threadPoolExecutor == null) {
            return 0;
        }
        return this.threadPoolExecutor.getQueue().size();
    }

    public int getActiveCount() {
        if (this.threadPoolExecutor == null) {
            return 0;
        }
        return this.threadPoolExecutor.getActiveCount();
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        Executor executor = getThreadPoolExecutor();
        try {
            executor.execute(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            ListenableFutureTask<Object> future = new ListenableFutureTask<>(task, null);
            executor.execute(future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
            executor.execute(future);
            return future;
        } catch (RejectedExecutionException ex) {
            throw new TaskRejectedException(executor, task, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    public void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(task);
        Object original = this.decoratedTaskMap.get(task);
        if (original instanceof Future) {
            Future<?> future = (Future) original;
            future.cancel(true);
        }
    }
}
