package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/SimpleAsyncTaskExecutorBuilder.class */
public class SimpleAsyncTaskExecutorBuilder {
    private final Boolean virtualThreads;
    private final String threadNamePrefix;
    private final Integer concurrencyLimit;
    private final TaskDecorator taskDecorator;
    private final Set<SimpleAsyncTaskExecutorCustomizer> customizers;
    private final Duration taskTerminationTimeout;

    public SimpleAsyncTaskExecutorBuilder() {
        this(null, null, null, null, null, null);
    }

    private SimpleAsyncTaskExecutorBuilder(Boolean virtualThreads, String threadNamePrefix, Integer concurrencyLimit, TaskDecorator taskDecorator, Set<SimpleAsyncTaskExecutorCustomizer> customizers, Duration taskTerminationTimeout) {
        this.virtualThreads = virtualThreads;
        this.threadNamePrefix = threadNamePrefix;
        this.concurrencyLimit = concurrencyLimit;
        this.taskDecorator = taskDecorator;
        this.customizers = customizers;
        this.taskTerminationTimeout = taskTerminationTimeout;
    }

    public SimpleAsyncTaskExecutorBuilder threadNamePrefix(String threadNamePrefix) {
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, threadNamePrefix, this.concurrencyLimit, this.taskDecorator, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder virtualThreads(Boolean virtualThreads) {
        return new SimpleAsyncTaskExecutorBuilder(virtualThreads, this.threadNamePrefix, this.concurrencyLimit, this.taskDecorator, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder concurrencyLimit(Integer concurrencyLimit) {
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, this.threadNamePrefix, concurrencyLimit, this.taskDecorator, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder taskDecorator(TaskDecorator taskDecorator) {
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, this.threadNamePrefix, this.concurrencyLimit, taskDecorator, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder taskTerminationTimeout(Duration taskTerminationTimeout) {
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, this.threadNamePrefix, this.concurrencyLimit, this.taskDecorator, this.customizers, taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder customizers(SimpleAsyncTaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public SimpleAsyncTaskExecutorBuilder customizers(Iterable<? extends SimpleAsyncTaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, this.threadNamePrefix, this.concurrencyLimit, this.taskDecorator, append(null, customizers), this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutorBuilder additionalCustomizers(SimpleAsyncTaskExecutorCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public SimpleAsyncTaskExecutorBuilder additionalCustomizers(Iterable<? extends SimpleAsyncTaskExecutorCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new SimpleAsyncTaskExecutorBuilder(this.virtualThreads, this.threadNamePrefix, this.concurrencyLimit, this.taskDecorator, append(this.customizers, customizers), this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskExecutor build() {
        return configure(new SimpleAsyncTaskExecutor());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends SimpleAsyncTaskExecutor> T build(Class<T> cls) {
        return (T) configure((SimpleAsyncTaskExecutor) BeanUtils.instantiateClass(cls));
    }

    public <T extends SimpleAsyncTaskExecutor> T configure(T taskExecutor) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.virtualThreads);
        Objects.requireNonNull(taskExecutor);
        from.to((v1) -> {
            r1.setVirtualThreads(v1);
        });
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) this.threadNamePrefix).whenHasText();
        Objects.requireNonNull(taskExecutor);
        whenHasText.to(taskExecutor::setThreadNamePrefix);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.concurrencyLimit);
        Objects.requireNonNull(taskExecutor);
        from2.to((v1) -> {
            r1.setConcurrencyLimit(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.taskDecorator);
        Objects.requireNonNull(taskExecutor);
        from3.to(taskExecutor::setTaskDecorator);
        PropertyMapper.Source as = map.from((PropertyMapper) this.taskTerminationTimeout).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(taskExecutor);
        as.to((v1) -> {
            r1.setTaskTerminationTimeout(v1);
        });
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
