package org.springframework.scheduling.config;

import java.util.concurrent.RejectedExecutionHandler;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/TaskExecutorFactoryBean.class */
public class TaskExecutorFactoryBean implements FactoryBean<TaskExecutor>, BeanNameAware, InitializingBean, DisposableBean {

    @Nullable
    private String poolSize;

    @Nullable
    private Integer queueCapacity;

    @Nullable
    private RejectedExecutionHandler rejectedExecutionHandler;

    @Nullable
    private Integer keepAliveSeconds;

    @Nullable
    private String beanName;

    @Nullable
    private ThreadPoolTaskExecutor target;

    public void setPoolSize(String poolSize) {
        this.poolSize = poolSize;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = Integer.valueOf(queueCapacity);
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = Integer.valueOf(keepAliveSeconds);
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        determinePoolSizeRange(executor);
        if (this.queueCapacity != null) {
            executor.setQueueCapacity(this.queueCapacity.intValue());
        }
        if (this.keepAliveSeconds != null) {
            executor.setKeepAliveSeconds(this.keepAliveSeconds.intValue());
        }
        if (this.rejectedExecutionHandler != null) {
            executor.setRejectedExecutionHandler(this.rejectedExecutionHandler);
        }
        if (this.beanName != null) {
            executor.setThreadNamePrefix(this.beanName + "-");
        }
        executor.afterPropertiesSet();
        this.target = executor;
    }

    private void determinePoolSizeRange(ThreadPoolTaskExecutor executor) {
        int corePoolSize;
        int maxPoolSize;
        if (StringUtils.hasText(this.poolSize)) {
            try {
                int separatorIndex = this.poolSize.indexOf(45);
                if (separatorIndex != -1) {
                    corePoolSize = Integer.parseInt(this.poolSize, 0, separatorIndex, 10);
                    maxPoolSize = Integer.parseInt(this.poolSize, separatorIndex + 1, this.poolSize.length(), 10);
                    if (corePoolSize > maxPoolSize) {
                        throw new IllegalArgumentException("Lower bound of pool-size range must not exceed the upper bound");
                    }
                    if (this.queueCapacity == null) {
                        if (corePoolSize == 0) {
                            executor.setAllowCoreThreadTimeOut(true);
                            corePoolSize = maxPoolSize;
                        } else {
                            throw new IllegalArgumentException("A non-zero lower bound for the size range requires a queue-capacity value");
                        }
                    }
                } else {
                    int value = Integer.parseInt(this.poolSize);
                    corePoolSize = value;
                    maxPoolSize = value;
                }
                executor.setCorePoolSize(corePoolSize);
                executor.setMaxPoolSize(maxPoolSize);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid pool-size value [" + this.poolSize + "]: only single maximum integer (e.g. \"5\") and minimum-maximum range (e.g. \"3-5\") are supported", ex);
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public TaskExecutor getObject() {
        return this.target;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends TaskExecutor> getObjectType() {
        return this.target != null ? this.target.getClass() : ThreadPoolTaskExecutor.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.target != null) {
            this.target.destroy();
        }
    }
}
