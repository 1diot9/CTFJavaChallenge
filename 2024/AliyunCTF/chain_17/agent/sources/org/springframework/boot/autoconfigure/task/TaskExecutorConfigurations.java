package org.springframework.boot.autoconfigure.task;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.SimpleAsyncTaskExecutorCustomizer;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutorConfigurations.class */
class TaskExecutorConfigurations {
    TaskExecutorConfigurations() {
    }

    @ConditionalOnMissingBean({Executor.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutorConfigurations$TaskExecutorConfiguration.class */
    static class TaskExecutorConfiguration {
        TaskExecutorConfiguration() {
        }

        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean(name = {TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME, "taskExecutor"})
        SimpleAsyncTaskExecutor applicationTaskExecutorVirtualThreads(SimpleAsyncTaskExecutorBuilder builder) {
            return builder.build();
        }

        @ConditionalOnThreading(Threading.PLATFORM)
        @Lazy
        @Bean(name = {TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME, "taskExecutor"})
        ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder taskExecutorBuilder, ObjectProvider<ThreadPoolTaskExecutorBuilder> threadPoolTaskExecutorBuilderProvider) {
            ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder = threadPoolTaskExecutorBuilderProvider.getIfUnique();
            if (threadPoolTaskExecutorBuilder != null) {
                return threadPoolTaskExecutorBuilder.build();
            }
            return taskExecutorBuilder.build();
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutorConfigurations$TaskExecutorBuilderConfiguration.class */
    static class TaskExecutorBuilderConfiguration {
        TaskExecutorBuilderConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @Deprecated(since = "3.2.0", forRemoval = true)
        TaskExecutorBuilder taskExecutorBuilder(TaskExecutionProperties properties, ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator) {
            TaskExecutionProperties.Pool pool = properties.getPool();
            TaskExecutorBuilder builder = new TaskExecutorBuilder();
            TaskExecutorBuilder builder2 = builder.queueCapacity(pool.getQueueCapacity()).corePoolSize(pool.getCoreSize()).maxPoolSize(pool.getMaxSize()).allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout()).keepAlive(pool.getKeepAlive());
            TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
            TaskExecutorBuilder builder3 = builder2.awaitTermination(shutdown.isAwaitTermination()).awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod()).threadNamePrefix(properties.getThreadNamePrefix());
            Stream<TaskExecutorCustomizer> orderedStream = taskExecutorCustomizers.orderedStream();
            Objects.requireNonNull(orderedStream);
            return builder3.customizers(orderedStream::iterator).taskDecorator(taskDecorator.getIfUnique());
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutorConfigurations$ThreadPoolTaskExecutorBuilderConfiguration.class */
    static class ThreadPoolTaskExecutorBuilderConfiguration {
        ThreadPoolTaskExecutorBuilderConfiguration() {
        }

        @ConditionalOnMissingBean({TaskExecutorBuilder.class, ThreadPoolTaskExecutorBuilder.class})
        @Bean
        ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder(TaskExecutionProperties properties, ObjectProvider<ThreadPoolTaskExecutorCustomizer> threadPoolTaskExecutorCustomizers, ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator) {
            TaskExecutionProperties.Pool pool = properties.getPool();
            ThreadPoolTaskExecutorBuilder builder = new ThreadPoolTaskExecutorBuilder();
            ThreadPoolTaskExecutorBuilder builder2 = builder.queueCapacity(pool.getQueueCapacity()).corePoolSize(pool.getCoreSize()).maxPoolSize(pool.getMaxSize()).allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout()).keepAlive(pool.getKeepAlive());
            TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
            ThreadPoolTaskExecutorBuilder builder3 = builder2.awaitTermination(shutdown.isAwaitTermination()).awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod()).threadNamePrefix(properties.getThreadNamePrefix());
            Stream<ThreadPoolTaskExecutorCustomizer> orderedStream = threadPoolTaskExecutorCustomizers.orderedStream();
            Objects.requireNonNull(orderedStream);
            return builder3.customizers(orderedStream::iterator).taskDecorator(taskDecorator.getIfUnique()).additionalCustomizers(taskExecutorCustomizers.orderedStream().map(this::adapt).toList());
        }

        private ThreadPoolTaskExecutorCustomizer adapt(TaskExecutorCustomizer customizer) {
            Objects.requireNonNull(customizer);
            return customizer::customize;
        }
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutorConfigurations$SimpleAsyncTaskExecutorBuilderConfiguration.class */
    static class SimpleAsyncTaskExecutorBuilderConfiguration {
        private final TaskExecutionProperties properties;
        private final ObjectProvider<SimpleAsyncTaskExecutorCustomizer> taskExecutorCustomizers;
        private final ObjectProvider<TaskDecorator> taskDecorator;

        SimpleAsyncTaskExecutorBuilderConfiguration(TaskExecutionProperties properties, ObjectProvider<SimpleAsyncTaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator) {
            this.properties = properties;
            this.taskExecutorCustomizers = taskExecutorCustomizers;
            this.taskDecorator = taskDecorator;
        }

        @ConditionalOnMissingBean
        @ConditionalOnThreading(Threading.PLATFORM)
        @Bean
        SimpleAsyncTaskExecutorBuilder simpleAsyncTaskExecutorBuilder() {
            return builder();
        }

        @ConditionalOnMissingBean
        @ConditionalOnThreading(Threading.VIRTUAL)
        @Bean(name = {"simpleAsyncTaskExecutorBuilder"})
        SimpleAsyncTaskExecutorBuilder simpleAsyncTaskExecutorBuilderVirtualThreads() {
            SimpleAsyncTaskExecutorBuilder builder = builder();
            return builder.virtualThreads(true);
        }

        private SimpleAsyncTaskExecutorBuilder builder() {
            SimpleAsyncTaskExecutorBuilder builder = new SimpleAsyncTaskExecutorBuilder();
            SimpleAsyncTaskExecutorBuilder builder2 = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
            Stream<SimpleAsyncTaskExecutorCustomizer> orderedStream = this.taskExecutorCustomizers.orderedStream();
            Objects.requireNonNull(orderedStream);
            SimpleAsyncTaskExecutorBuilder builder3 = builder2.customizers(orderedStream::iterator).taskDecorator(this.taskDecorator.getIfUnique());
            TaskExecutionProperties.Simple simple = this.properties.getSimple();
            SimpleAsyncTaskExecutorBuilder builder4 = builder3.concurrencyLimit(simple.getConcurrencyLimit());
            TaskExecutionProperties.Shutdown shutdown = this.properties.getShutdown();
            if (shutdown.isAwaitTermination()) {
                builder4 = builder4.taskTerminationTimeout(shutdown.getAwaitTerminationPeriod());
            }
            return builder4;
        }
    }
}
