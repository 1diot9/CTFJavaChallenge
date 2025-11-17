package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/SimpleAsyncTaskSchedulerBuilder.class */
public class SimpleAsyncTaskSchedulerBuilder {
    private final String threadNamePrefix;
    private final Integer concurrencyLimit;
    private final Boolean virtualThreads;
    private final Set<SimpleAsyncTaskSchedulerCustomizer> customizers;
    private final Duration taskTerminationTimeout;

    public SimpleAsyncTaskSchedulerBuilder() {
        this(null, null, null, null, null);
    }

    private SimpleAsyncTaskSchedulerBuilder(String threadNamePrefix, Integer concurrencyLimit, Boolean virtualThreads, Set<SimpleAsyncTaskSchedulerCustomizer> taskSchedulerCustomizers, Duration taskTerminationTimeout) {
        this.threadNamePrefix = threadNamePrefix;
        this.concurrencyLimit = concurrencyLimit;
        this.virtualThreads = virtualThreads;
        this.customizers = taskSchedulerCustomizers;
        this.taskTerminationTimeout = taskTerminationTimeout;
    }

    public SimpleAsyncTaskSchedulerBuilder threadNamePrefix(String threadNamePrefix) {
        return new SimpleAsyncTaskSchedulerBuilder(threadNamePrefix, this.concurrencyLimit, this.virtualThreads, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskSchedulerBuilder concurrencyLimit(Integer concurrencyLimit) {
        return new SimpleAsyncTaskSchedulerBuilder(this.threadNamePrefix, concurrencyLimit, this.virtualThreads, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskSchedulerBuilder virtualThreads(Boolean virtualThreads) {
        return new SimpleAsyncTaskSchedulerBuilder(this.threadNamePrefix, this.concurrencyLimit, virtualThreads, this.customizers, this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskSchedulerBuilder taskTerminationTimeout(Duration taskTerminationTimeout) {
        return new SimpleAsyncTaskSchedulerBuilder(this.threadNamePrefix, this.concurrencyLimit, this.virtualThreads, this.customizers, taskTerminationTimeout);
    }

    public SimpleAsyncTaskSchedulerBuilder customizers(SimpleAsyncTaskSchedulerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public SimpleAsyncTaskSchedulerBuilder customizers(Iterable<? extends SimpleAsyncTaskSchedulerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new SimpleAsyncTaskSchedulerBuilder(this.threadNamePrefix, this.concurrencyLimit, this.virtualThreads, append(null, customizers), this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskSchedulerBuilder additionalCustomizers(SimpleAsyncTaskSchedulerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public SimpleAsyncTaskSchedulerBuilder additionalCustomizers(Iterable<? extends SimpleAsyncTaskSchedulerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new SimpleAsyncTaskSchedulerBuilder(this.threadNamePrefix, this.concurrencyLimit, this.virtualThreads, append(this.customizers, customizers), this.taskTerminationTimeout);
    }

    public SimpleAsyncTaskScheduler build() {
        return configure(new SimpleAsyncTaskScheduler());
    }

    public <T extends SimpleAsyncTaskScheduler> T configure(T taskScheduler) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.threadNamePrefix);
        Objects.requireNonNull(taskScheduler);
        from.to(taskScheduler::setThreadNamePrefix);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.concurrencyLimit);
        Objects.requireNonNull(taskScheduler);
        from2.to((v1) -> {
            r1.setConcurrencyLimit(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.virtualThreads);
        Objects.requireNonNull(taskScheduler);
        from3.to((v1) -> {
            r1.setVirtualThreads(v1);
        });
        PropertyMapper.Source as = map.from((PropertyMapper) this.taskTerminationTimeout).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(taskScheduler);
        as.to((v1) -> {
            r1.setTaskTerminationTimeout(v1);
        });
        if (!CollectionUtils.isEmpty(this.customizers)) {
            this.customizers.forEach(customizer -> {
                customizer.customize(taskScheduler);
            });
        }
        return taskScheduler;
    }

    private <T> Set<T> append(Set<T> set, Iterable<? extends T> additions) {
        Set<T> result = new LinkedHashSet<>(set != null ? set : Collections.emptySet());
        Objects.requireNonNull(result);
        additions.forEach(result::add);
        return Collections.unmodifiableSet(result);
    }
}
