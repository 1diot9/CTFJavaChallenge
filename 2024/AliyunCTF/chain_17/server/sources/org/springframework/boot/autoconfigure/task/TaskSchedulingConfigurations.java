package org.springframework.boot.autoconfigure.task;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerBuilder;
import org.springframework.boot.task.SimpleAsyncTaskSchedulerCustomizer;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingConfigurations.class */
class TaskSchedulingConfigurations {
    TaskSchedulingConfigurations() {
    }

    @ConditionalOnMissingBean({TaskScheduler.class, ScheduledExecutorService.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(name = {TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingConfigurations$TaskSchedulerConfiguration.class */
    static class TaskSchedulerConfiguration {
        TaskSchedulerConfiguration() {
        }

        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean(name = {"taskScheduler"})
        SimpleAsyncTaskScheduler taskSchedulerVirtualThreads(SimpleAsyncTaskSchedulerBuilder builder) {
            return builder.build();
        }

        @ConditionalOnThreading(Threading.PLATFORM)
        @Bean
        ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder taskSchedulerBuilder, ObjectProvider<ThreadPoolTaskSchedulerBuilder> threadPoolTaskSchedulerBuilderProvider) {
            ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder = threadPoolTaskSchedulerBuilderProvider.getIfUnique();
            if (threadPoolTaskSchedulerBuilder != null) {
                return threadPoolTaskSchedulerBuilder.build();
            }
            return taskSchedulerBuilder.build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingConfigurations$TaskSchedulerBuilderConfiguration.class */
    static class TaskSchedulerBuilderConfiguration {
        TaskSchedulerBuilderConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        TaskSchedulerBuilder taskSchedulerBuilder(TaskSchedulingProperties properties, ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {
            TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
            TaskSchedulerBuilder builder2 = builder.poolSize(properties.getPool().getSize());
            TaskSchedulingProperties.Shutdown shutdown = properties.getShutdown();
            return builder2.awaitTermination(shutdown.isAwaitTermination()).awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod()).threadNamePrefix(properties.getThreadNamePrefix()).customizers(taskSchedulerCustomizers);
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingConfigurations$ThreadPoolTaskSchedulerBuilderConfiguration.class */
    static class ThreadPoolTaskSchedulerBuilderConfiguration {
        ThreadPoolTaskSchedulerBuilderConfiguration() {
        }

        @ConditionalOnMissingBean({TaskSchedulerBuilder.class, ThreadPoolTaskSchedulerBuilder.class})
        @Bean
        ThreadPoolTaskSchedulerBuilder threadPoolTaskSchedulerBuilder(TaskSchedulingProperties properties, ObjectProvider<ThreadPoolTaskSchedulerCustomizer> threadPoolTaskSchedulerCustomizers, ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {
            TaskSchedulingProperties.Shutdown shutdown = properties.getShutdown();
            ThreadPoolTaskSchedulerBuilder builder = new ThreadPoolTaskSchedulerBuilder();
            return builder.poolSize(properties.getPool().getSize()).awaitTermination(shutdown.isAwaitTermination()).awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod()).threadNamePrefix(properties.getThreadNamePrefix()).customizers(threadPoolTaskSchedulerCustomizers).additionalCustomizers(taskSchedulerCustomizers.orderedStream().map(this::adapt).toList());
        }

        private ThreadPoolTaskSchedulerCustomizer adapt(TaskSchedulerCustomizer customizer) {
            Objects.requireNonNull(customizer);
            return customizer::customize;
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingConfigurations$SimpleAsyncTaskSchedulerBuilderConfiguration.class */
    static class SimpleAsyncTaskSchedulerBuilderConfiguration {
        private final TaskSchedulingProperties properties;
        private final ObjectProvider<SimpleAsyncTaskSchedulerCustomizer> taskSchedulerCustomizers;

        SimpleAsyncTaskSchedulerBuilderConfiguration(TaskSchedulingProperties properties, ObjectProvider<SimpleAsyncTaskSchedulerCustomizer> taskSchedulerCustomizers) {
            this.properties = properties;
            this.taskSchedulerCustomizers = taskSchedulerCustomizers;
        }

        @ConditionalOnMissingBean
        @ConditionalOnThreading(Threading.PLATFORM)
        @Bean
        SimpleAsyncTaskSchedulerBuilder simpleAsyncTaskSchedulerBuilder() {
            return builder();
        }

        @ConditionalOnMissingBean
        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean(name = {"simpleAsyncTaskSchedulerBuilder"})
        SimpleAsyncTaskSchedulerBuilder simpleAsyncTaskSchedulerBuilderVirtualThreads() {
            SimpleAsyncTaskSchedulerBuilder builder = builder();
            return builder.virtualThreads(true);
        }

        private SimpleAsyncTaskSchedulerBuilder builder() {
            SimpleAsyncTaskSchedulerBuilder builder = new SimpleAsyncTaskSchedulerBuilder();
            SimpleAsyncTaskSchedulerBuilder builder2 = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
            Stream<SimpleAsyncTaskSchedulerCustomizer> orderedStream = this.taskSchedulerCustomizers.orderedStream();
            Objects.requireNonNull(orderedStream);
            SimpleAsyncTaskSchedulerBuilder builder3 = builder2.customizers(orderedStream::iterator);
            TaskSchedulingProperties.Simple simple = this.properties.getSimple();
            SimpleAsyncTaskSchedulerBuilder builder4 = builder3.concurrencyLimit(simple.getConcurrencyLimit());
            TaskSchedulingProperties.Shutdown shutdown = this.properties.getShutdown();
            if (shutdown.isAwaitTermination()) {
                builder4 = builder4.taskTerminationTimeout(shutdown.getAwaitTerminationPeriod());
            }
            return builder4;
        }
    }
}
