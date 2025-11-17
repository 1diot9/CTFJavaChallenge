package org.springframework.scheduling.concurrent;

import java.lang.Thread;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ForkJoinPoolFactoryBean.class */
public class ForkJoinPoolFactoryBean implements FactoryBean<ForkJoinPool>, InitializingBean, DisposableBean {

    @Nullable
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Nullable
    private ForkJoinPool forkJoinPool;
    private boolean commonPool = false;
    private int parallelism = Runtime.getRuntime().availableProcessors();
    private ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
    private boolean asyncMode = false;
    private int awaitTerminationSeconds = 0;

    public void setCommonPool(boolean commonPool) {
        this.commonPool = commonPool;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public void setThreadFactory(ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public void setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.forkJoinPool = this.commonPool ? ForkJoinPool.commonPool() : new ForkJoinPool(this.parallelism, this.threadFactory, this.uncaughtExceptionHandler, this.asyncMode);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ForkJoinPool getObject() {
        return this.forkJoinPool;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return ForkJoinPool.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.forkJoinPool != null) {
            this.forkJoinPool.shutdown();
            if (this.awaitTerminationSeconds > 0) {
                try {
                    this.forkJoinPool.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
