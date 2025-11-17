package cn.hutool.core.thread;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.util.ObjectUtil;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/thread/ExecutorBuilder.class */
public class ExecutorBuilder implements Builder<ThreadPoolExecutor> {
    private static final long serialVersionUID = 1;
    public static final int DEFAULT_QUEUE_CAPACITY = 1024;
    private int corePoolSize;
    private int maxPoolSize = Integer.MAX_VALUE;
    private long keepAliveTime = TimeUnit.SECONDS.toNanos(60);
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler handler;
    private Boolean allowCoreThreadTimeOut;

    public ExecutorBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ExecutorBuilder setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public ExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
        return setKeepAliveTime(unit.toNanos(keepAliveTime));
    }

    public ExecutorBuilder setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ExecutorBuilder useArrayBlockingQueue(int capacity) {
        return setWorkQueue(new ArrayBlockingQueue(capacity));
    }

    public ExecutorBuilder useSynchronousQueue() {
        return useSynchronousQueue(false);
    }

    public ExecutorBuilder useSynchronousQueue(boolean fair) {
        return setWorkQueue(new SynchronousQueue(fair));
    }

    public ExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ExecutorBuilder setHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public ExecutorBuilder setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = Boolean.valueOf(allowCoreThreadTimeOut);
        return this;
    }

    public static ExecutorBuilder create() {
        return new ExecutorBuilder();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public ThreadPoolExecutor build() {
        return build(this);
    }

    public ExecutorService buildFinalizable() {
        return new FinalizableDelegatedExecutorService(build());
    }

    private static ThreadPoolExecutor build(ExecutorBuilder builder) {
        BlockingQueue<Runnable> workQueue;
        int corePoolSize = builder.corePoolSize;
        int maxPoolSize = builder.maxPoolSize;
        long keepAliveTime = builder.keepAliveTime;
        if (null != builder.workQueue) {
            workQueue = builder.workQueue;
        } else {
            workQueue = corePoolSize <= 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(1024);
        }
        ThreadFactory threadFactory = null != builder.threadFactory ? builder.threadFactory : Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = (RejectedExecutionHandler) ObjectUtil.defaultIfNull(builder.handler, RejectPolicy.ABORT.getValue());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.NANOSECONDS, workQueue, threadFactory, handler);
        if (null != builder.allowCoreThreadTimeOut) {
            threadPoolExecutor.allowCoreThreadTimeOut(builder.allowCoreThreadTimeOut.booleanValue());
        }
        return threadPoolExecutor;
    }
}
