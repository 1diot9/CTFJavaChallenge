package org.springframework.boot.autoconfigure.task;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableConfigurationProperties({TaskExecutionProperties.class})
@ConditionalOnClass({ThreadPoolTaskExecutor.class})
@AutoConfiguration
@Import({TaskExecutorConfigurations.ThreadPoolTaskExecutorBuilderConfiguration.class, TaskExecutorConfigurations.TaskExecutorBuilderConfiguration.class, TaskExecutorConfigurations.SimpleAsyncTaskExecutorBuilderConfiguration.class, TaskExecutorConfigurations.TaskExecutorConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskExecutionAutoConfiguration.class */
public class TaskExecutionAutoConfiguration {
    public static final String APPLICATION_TASK_EXECUTOR_BEAN_NAME = "applicationTaskExecutor";
}
