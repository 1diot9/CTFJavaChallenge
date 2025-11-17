package org.springframework.boot.task;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/ThreadPoolTaskSchedulerBuilder.class */
public class ThreadPoolTaskSchedulerBuilder {
    private final Integer poolSize;
    private final Boolean awaitTermination;
    private final Duration awaitTerminationPeriod;
    private final String threadNamePrefix;
    private final Set<ThreadPoolTaskSchedulerCustomizer> customizers;

    public ThreadPoolTaskSchedulerBuilder() {
        this.poolSize = null;
        this.awaitTermination = null;
        this.awaitTerminationPeriod = null;
        this.threadNamePrefix = null;
        this.customizers = null;
    }

    public ThreadPoolTaskSchedulerBuilder(Integer poolSize, Boolean awaitTermination, Duration awaitTerminationPeriod, String threadNamePrefix, Set<ThreadPoolTaskSchedulerCustomizer> taskSchedulerCustomizers) {
        this.poolSize = poolSize;
        this.awaitTermination = awaitTermination;
        this.awaitTerminationPeriod = awaitTerminationPeriod;
        this.threadNamePrefix = threadNamePrefix;
        this.customizers = taskSchedulerCustomizers;
    }

    public ThreadPoolTaskSchedulerBuilder poolSize(int poolSize) {
        return new ThreadPoolTaskSchedulerBuilder(Integer.valueOf(poolSize), this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, this.customizers);
    }

    public ThreadPoolTaskSchedulerBuilder awaitTermination(boolean awaitTermination) {
        return new ThreadPoolTaskSchedulerBuilder(this.poolSize, Boolean.valueOf(awaitTermination), this.awaitTerminationPeriod, this.threadNamePrefix, this.customizers);
    }

    public ThreadPoolTaskSchedulerBuilder awaitTerminationPeriod(Duration awaitTerminationPeriod) {
        return new ThreadPoolTaskSchedulerBuilder(this.poolSize, this.awaitTermination, awaitTerminationPeriod, this.threadNamePrefix, this.customizers);
    }

    public ThreadPoolTaskSchedulerBuilder threadNamePrefix(String threadNamePrefix) {
        return new ThreadPoolTaskSchedulerBuilder(this.poolSize, this.awaitTermination, this.awaitTerminationPeriod, threadNamePrefix, this.customizers);
    }

    public ThreadPoolTaskSchedulerBuilder customizers(ThreadPoolTaskSchedulerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return customizers(Arrays.asList(customizers));
    }

    public ThreadPoolTaskSchedulerBuilder customizers(Iterable<? extends ThreadPoolTaskSchedulerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new ThreadPoolTaskSchedulerBuilder(this.poolSize, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, append(null, customizers));
    }

    public ThreadPoolTaskSchedulerBuilder additionalCustomizers(ThreadPoolTaskSchedulerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return additionalCustomizers(Arrays.asList(customizers));
    }

    public ThreadPoolTaskSchedulerBuilder additionalCustomizers(Iterable<? extends ThreadPoolTaskSchedulerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        return new ThreadPoolTaskSchedulerBuilder(this.poolSize, this.awaitTermination, this.awaitTerminationPeriod, this.threadNamePrefix, append(this.customizers, customizers));
    }

    public ThreadPoolTaskScheduler build() {
        return configure(new ThreadPoolTaskScheduler());
    }

    public <T extends ThreadPoolTaskScheduler> T configure(T taskScheduler) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.poolSize);
        Objects.requireNonNull(taskScheduler);
        from.to((v1) -> {
            r1.setPoolSize(v1);
        });
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.awaitTermination);
        Objects.requireNonNull(taskScheduler);
        from2.to((v1) -> {
            r1.setWaitForTasksToCompleteOnShutdown(v1);
        });
        PropertyMapper.Source<Integer> asInt = map.from((PropertyMapper) this.awaitTerminationPeriod).asInt((v0) -> {
            return v0.getSeconds();
        });
        Objects.requireNonNull(taskScheduler);
        asInt.to((v1) -> {
            r1.setAwaitTerminationSeconds(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.threadNamePrefix);
        Objects.requireNonNull(taskScheduler);
        from3.to(taskScheduler::setThreadNamePrefix);
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
