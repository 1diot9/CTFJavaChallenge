package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ExecutorConfigurationSupport.class */
public abstract class ExecutorConfigurationSupport extends CustomizableThreadFactory implements BeanNameAware, ApplicationContextAware, InitializingBean, DisposableBean, SmartLifecycle, ApplicationListener<ContextClosedEvent> {
    protected final Log logger = LogFactory.getLog(getClass());
    private ThreadFactory threadFactory = this;
    private boolean threadNamePrefixSet = false;
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    private boolean acceptTasksAfterContextClose = false;
    private boolean waitForTasksToCompleteOnShutdown = false;
    private long awaitTerminationMillis = 0;
    private int phase = Integer.MAX_VALUE;

    @Nullable
    private String beanName;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private ExecutorService executor;

    @Nullable
    private ExecutorLifecycleDelegate lifecycleDelegate;
    private volatile boolean lateShutdown;

    protected abstract ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler);

    public void setThreadFactory(@Nullable ThreadFactory threadFactory) {
        this.threadFactory = threadFactory != null ? threadFactory : this;
    }

    @Override // org.springframework.util.CustomizableThreadCreator
    public void setThreadNamePrefix(@Nullable String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix);
        this.threadNamePrefixSet = true;
    }

    public void setRejectedExecutionHandler(@Nullable RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler != null ? rejectedExecutionHandler : new ThreadPoolExecutor.AbortPolicy();
    }

    public void setAcceptTasksAfterContextClose(boolean acceptTasksAfterContextClose) {
        this.acceptTasksAfterContextClose = acceptTasksAfterContextClose;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationMillis = awaitTerminationSeconds * 1000;
    }

    public void setAwaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Override // org.springframework.context.SmartLifecycle, org.springframework.context.Phased
    public int getPhase() {
        return this.phase;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        initialize();
    }

    public void initialize() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Initializing ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : ""));
        }
        if (!this.threadNamePrefixSet && this.beanName != null) {
            setThreadNamePrefix(this.beanName + "-");
        }
        this.executor = initializeExecutor(this.threadFactory, this.rejectedExecutionHandler);
        this.lifecycleDelegate = new ExecutorLifecycleDelegate(this.executor);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        shutdown();
    }

    public void initiateShutdown() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    public void shutdown() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Shutting down ExecutorService" + (this.beanName != null ? " '" + this.beanName + "'" : ""));
        }
        if (this.executor != null) {
            if (this.waitForTasksToCompleteOnShutdown) {
                this.executor.shutdown();
            } else {
                for (Runnable remainingTask : this.executor.shutdownNow()) {
                    cancelRemainingTask(remainingTask);
                }
            }
            awaitTerminationIfNecessary(this.executor);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancelRemainingTask(Runnable task) {
        if (task instanceof Future) {
            Future<?> future = (Future) task;
            future.cancel(true);
        }
    }

    private void awaitTerminationIfNecessary(ExecutorService executor) {
        if (this.awaitTerminationMillis > 0) {
            try {
                if (!executor.awaitTermination(this.awaitTerminationMillis, TimeUnit.MILLISECONDS) && this.logger.isWarnEnabled()) {
                    this.logger.warn("Timed out while waiting for executor" + (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
                }
            } catch (InterruptedException e) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Interrupted while waiting for executor" + (this.beanName != null ? " '" + this.beanName + "'" : "") + " to terminate");
                }
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        if (this.lifecycleDelegate != null) {
            this.lifecycleDelegate.start();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        if (this.lifecycleDelegate != null && !this.lateShutdown) {
            this.lifecycleDelegate.stop();
        }
    }

    @Override // org.springframework.context.SmartLifecycle
    public void stop(Runnable callback) {
        if (this.lifecycleDelegate != null && !this.lateShutdown) {
            this.lifecycleDelegate.stop(callback);
        } else {
            callback.run();
        }
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.lifecycleDelegate != null && this.lifecycleDelegate.isRunning();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void beforeExecute(Thread thread, Runnable task) {
        if (this.lifecycleDelegate != null) {
            this.lifecycleDelegate.beforeExecute(thread);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void afterExecute(Runnable task, @Nullable Throwable ex) {
        if (this.lifecycleDelegate != null) {
            this.lifecycleDelegate.afterExecute();
        }
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            if (this.acceptTasksAfterContextClose || this.waitForTasksToCompleteOnShutdown) {
                this.lateShutdown = true;
            } else {
                initiateShutdown();
            }
        }
    }
}
