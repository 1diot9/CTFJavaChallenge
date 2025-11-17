package org.springframework.http.client.reactive;

import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyResourceFactory.class */
public class JettyResourceFactory implements InitializingBean, DisposableBean {

    @Nullable
    private Executor executor;

    @Nullable
    private ByteBufferPool byteBufferPool;

    @Nullable
    private Scheduler scheduler;
    private String threadPrefix = "jetty-http";

    public void setExecutor(@Nullable Executor executor) {
        this.executor = executor;
    }

    public void setByteBufferPool(@Nullable ByteBufferPool byteBufferPool) {
        this.byteBufferPool = byteBufferPool;
    }

    public void setScheduler(@Nullable Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setThreadPrefix(String threadPrefix) {
        Assert.notNull(threadPrefix, "Thread prefix is required");
        this.threadPrefix = threadPrefix;
    }

    @Nullable
    public Executor getExecutor() {
        return this.executor;
    }

    @Nullable
    public ByteBufferPool getByteBufferPool() {
        return this.byteBufferPool;
    }

    @Nullable
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        int availableProcessors;
        String name = this.threadPrefix + "@" + Integer.toHexString(hashCode());
        if (this.executor == null) {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            threadPool.setName(name);
            this.executor = threadPool;
        }
        if (this.byteBufferPool == null) {
            ThreadPool.SizedThreadPool sizedThreadPool = this.executor;
            if (sizedThreadPool instanceof ThreadPool.SizedThreadPool) {
                ThreadPool.SizedThreadPool sizedThreadPool2 = sizedThreadPool;
                availableProcessors = sizedThreadPool2.getMaxThreads() / 2;
            } else {
                availableProcessors = ProcessorUtils.availableProcessors() * 2;
            }
            this.byteBufferPool = new ArrayByteBufferPool(0, 2048, 65536, availableProcessors);
        }
        if (this.scheduler == null) {
            this.scheduler = new ScheduledExecutorScheduler(name + "-scheduler", false);
        }
        LifeCycle lifeCycle = this.executor;
        if (lifeCycle instanceof LifeCycle) {
            LifeCycle lifeCycle2 = lifeCycle;
            lifeCycle2.start();
        }
        this.scheduler.start();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        try {
            LifeCycle lifeCycle = this.executor;
            if (lifeCycle instanceof LifeCycle) {
                LifeCycle lifeCycle2 = lifeCycle;
                lifeCycle2.stop();
            }
        } catch (Throwable th) {
        }
        try {
            if (this.scheduler != null) {
                this.scheduler.stop();
            }
        } catch (Throwable th2) {
        }
    }
}
