package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/TaskExecutorBuilder.class */
public class TaskExecutorBuilder {
    private final Integer queueCapacity;
    private final Integer corePoolSize;
    private final Integer maxPoolSize;
    private final Boolean allowCoreThreadTimeOut;
    private final Duration keepAlive;
    private final Boolean awaitTermination;
    private final Duration awaitTerminationPeriod;
    private final String threadNamePrefix;
    private final TaskDecorator taskDecorator;
    private final Set<TaskExecutorCustomizer> customizers;

    public TaskExecutorBuilder() {
        this.queueCapacity = null;
        this.corePoolSize = null;
        this.maxPoolSize = null;
        this.allowCoreThreadTimeOut = null;
        this.keepAlive = null;
        this.awaitTermination = null;
        this.awaitTerminationPeriod = null;
        this.threadNamePrefix = null;
        this.taskDecorator = null;
        this.customizers = null;
    }

    private TaskExecutorBuilder(Integer queueCapacity, Integer corePoolSize, Integer maxPoolSize, Boolean allowCoreThreadTimeOut, Duration keepAlive, Boolean awaitTermination, Duration awaitTerminationPeriod, String threadNamePrefix, TaskDecorator taskDecorator, Set<TaskExecutorCustomizer> customizers) {
        this.queueCapacity = queueCapacity;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.keepAlive = keepAlive;
        this.awaitTermination = awaitTermination;
        this.awaitTerminationPeriod = awaitTerminationPeriod;
        this.threadNamePrefix = threadNamePrefix;
        this.taskDecorator = taskDecorator;
        this.customizers = customizers;
    }

    public TaskExecutorBuilder queueCapacity(int queueCapacity) {
        return new TaskExecutorBuilder(Integer.valueOf(queueCapacity), this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder corePoolSize(int corePoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, Integer.valueOf(corePoolSize), this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder maxPoolSize(int maxPoolSize) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, Integer.valueOf(maxPoolSize), this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, Boolean.valueOf(allowCoreThreadTimeOut), this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder keepAlive(Duration keepAlive) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder awaitTermination(boolean awaitTermination) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, Boolean.valueOf(awaitTermination), this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder awaitTerminationPeriod(Duration awaitTerminationPeriod) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder threadNamePrefix(String threadNamePrefix) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, threadNamePrefix, this.taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, taskDecorator, this.customizers);
    }

    public TaskExecutorBuilder customizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder customizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, append(null, customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(TaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public TaskExecutorBuilder additionalCustomizers(Iterable<TaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new TaskExecutorBuilder(this.queueCapacity, this.corePoolSize, this.maxPoolSize, this.allowCoreThreadTimeOut, this.keepAlive, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.taskDecorator, append(this.customizers, customizers));
    }

    public ThreadPoolTaskExecutor build() {
        return configure(new ThreadPoolTaskExecutor());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ThreadPoolTaskExecutor> T build(Class<T> cls) {
        return (T) configure((ThreadPoolTaskExecutor) BeanUtils.instantiateClass(cls));
    }

    public <T extends ThreadPoolTaskExecutor> T configure(T taskExecutor) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.queueCapacity);
        Objects.requireNonNull(taskExecutor);
        from.to((v1) -> {
            r1.setQueueCapacity(v1);
        });
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.corePoolSize);
        Objects.requireNonNull(taskExecutor);
        from2.to((v1) -> {
            r1.setCorePoolSize(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.maxPoolSize);
        Objects.requireNonNull(taskExecutor);
        from3.to((v1) -> {
            r1.setMaxPoolSize(v1);
        });
        PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) this.keepAlive).asInt((v0) -> {
            return v0.getSeconds();
        });
        Objects.requireNonNull(taskExecutor);
        asInt.to((v1) -> {
            r1.setKeepAliveSeconds(v1);
        });
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.allowCoreThreadTimeOut);
        Objects.requireNonNull(taskExecutor);
        from4.to((v1) -> {
            r1.setAllowCoreThreadTimeOut(v1);
        });
        PropertyMapper.Source from5 = map.from((PropertyMapper) this.awaitTermination);
        Objects.requireNonNull(taskExecutor);
        from5.to((v1) -> {
            r1.setWaitForTasksToCompleteOnShutdown(v1);
        });
        PropertyMapper.Source as = map.from((PropertyMapper) this.awaitTerminationPeriod).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(taskExecutor);
        as.to((v1) -> {
            r1.setAwaitTerminationMillis(v1);
        });
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) this.threadNamePrefix).whenHasText();
        Objects.requireNonNull(taskExecutor);
        whenHasText.to(taskExecutor::setThreadNamePrefix);
        PropertyMapper.Source from6 = map.from((PropertyMapper) this.taskDecorator);
        Objects.requireNonNull(taskExecutor);
        from6.to(taskExecutor::setTaskDecorator);
        if (!CollectionUtils.isEmpty(this.customizers)) {
            this.customizers.forEach(customizer -> {
                customizer.customize(taskExecutor);
            });
        }
        return taskExecutor;
    }

    private <T> Set<T> append(Set<T> set, Iterable<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        Objects.requireNonNull(result);
        additions.forEach(result::add);
        return Collections.unmodifiableSet(result);
    }
}
