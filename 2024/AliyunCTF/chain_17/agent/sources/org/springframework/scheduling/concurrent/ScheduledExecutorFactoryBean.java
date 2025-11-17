package org.springframework.scheduling.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/concurrent/ScheduledExecutorFactoryBean.class */
public class ScheduledExecutorFactoryBean extends ExecutorConfigurationSupport implements FactoryBean<ScheduledExecutorService> {

    @Nullable
    private ScheduledExecutorTask[] scheduledExecutorTasks;

    @Nullable
    private ScheduledExecutorService exposedExecutor;
    private int poolSize = 1;
    private boolean removeOnCancelPolicy = false;
    private boolean continueScheduledExecutionAfterException = false;
    private boolean exposeUnconfigurableExecutor = false;

    public void setPoolSize(int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        this.poolSize = poolSize;
    }

    public void setScheduledExecutorTasks(ScheduledExecutorTask... scheduledExecutorTasks) {
        this.scheduledExecutorTasks = scheduledExecutorTasks;
    }

    public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
    }

    public void setContinueScheduledExecutionAfterException(boolean continueScheduledExecutionAfterException) {
        this.continueScheduledExecutionAfterException = continueScheduledExecutionAfterException;
    }

    public void setExposeUnconfigurableExecutor(boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }

    @Override // org.springframework.scheduling.concurrent.ExecutorConfigurationSupport
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        ScheduledExecutorService executor = createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (this.removeOnCancelPolicy) {
            if (executor instanceof ScheduledThreadPoolExecutor) {
                ScheduledThreadPoolExecutor threadPoolExecutor = (ScheduledThreadPoolExecutor) executor;
                threadPoolExecutor.setRemoveOnCancelPolicy(true);
            } else {
                this.logger.debug("Could not apply remove-on-cancel policy - not a ScheduledThreadPoolExecutor");
            }
        }
        if (!ObjectUtils.isEmpty((Object[]) this.scheduledExecutorTasks)) {
            registerTasks(this.scheduledExecutorTasks, executor);
        }
        this.exposedExecutor = this.exposeUnconfigurableExecutor ? Executors.unconfigurableScheduledExecutorService(executor) : executor;
        return executor;
    }

    protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler) { // from class: org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean.1
            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void beforeExecute(Thread thread, Runnable task) {
                ScheduledExecutorFactoryBean.this.beforeExecute(thread, task);
            }

            @Override // java.util.concurrent.ThreadPoolExecutor
            protected void afterExecute(Runnable task, Throwable ex) {
                ScheduledExecutorFactoryBean.this.afterExecute(task, ex);
            }
        };
    }

    protected void registerTasks(ScheduledExecutorTask[] tasks, ScheduledExecutorService executor) {
        for (ScheduledExecutorTask task : tasks) {
            Runnable runnable = getRunnableToSchedule(task);
            if (task.isOneTimeTask()) {
                executor.schedule(runnable, task.getDelay(), task.getTimeUnit());
            } else if (task.isFixedRate()) {
                executor.scheduleAtFixedRate(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
            } else {
                executor.scheduleWithFixedDelay(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
            }
        }
    }

    protected Runnable getRunnableToSchedule(ScheduledExecutorTask task) {
        if (this.continueScheduledExecutionAfterException) {
            return new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
        }
        return new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ScheduledExecutorService getObject() {
        return this.exposedExecutor;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends ScheduledExecutorService> getObjectType() {
        return this.exposedExecutor != null ? this.exposedExecutor.getClass() : ScheduledExecutorService.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
