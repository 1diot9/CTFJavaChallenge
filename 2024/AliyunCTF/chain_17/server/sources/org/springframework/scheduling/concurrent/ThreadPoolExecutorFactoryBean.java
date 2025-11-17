package org.springframework.scheduling.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ThreadPoolExecutorFactoryBean.class */
public class ThreadPoolExecutorFactoryBean extends ExecutorConfigurationSupport implements FactoryBean<ExecutorService> {
    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private boolean allowCoreThreadTimeOut = false;
    private boolean prestartAllCoreThreads = false;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean exposeUnconfigurableExecutor = false;

    @Nullable
    private ExecutorService exposedExecutor;

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public void setPrestartAllCoreThreads(boolean prestartAllCoreThreads) {
        this.prestartAllCoreThreads = prestartAllCoreThreads;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }

    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
        ThreadPoolExecutor executor = createExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, queue, threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }
        if (this.prestartAllCoreThreads) {
            executor.prestartAllCoreThreads();
        }
        this.exposedExecutor = this.exposeUnconfigurableExecutor ? Executors.unconfigurableExecutorService(executor) : executor;
        return executor;
    }

    protected ThreadPoolExecutor createExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds, BlockingQueue<Runnable> queue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler) { // from class: org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean.1
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable task) {
                ThreadPoolExecutorFactoryBean.this.beforeExecute(thread, task);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void afterExecute(Runnable task, Throwable ex) {
                ThreadPoolExecutorFactoryBean.this.afterExecute(task, ex);
            }
        };
    }

    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue(queueCapacity);
        }
        return new SynchronousQueue();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ExecutorService getObject() {
        return this.exposedExecutor;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends ExecutorService> getObjectType() {
        return this.exposedExecutor != null ? this.exposedExecutor.getClass() : ExecutorService.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
