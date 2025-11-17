package org.springframework.boot.autoconfigure.task;

import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.task.TaskSchedulingConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

@EnableConfigurationProperties({TaskSchedulingProperties.class})
@ConditionalOnClass({ThreadPoolTaskScheduler.class})
@AutoConfiguration(after = {TaskExecutionAutoConfiguration.class})
@Import({TaskSchedulingConfigurations.ThreadPoolTaskSchedulerBuilderConfiguration.class, TaskSchedulingConfigurations.TaskSchedulerBuilderConfiguration.class, TaskSchedulingConfigurations.SimpleAsyncTaskSchedulerBuilderConfiguration.class, TaskSchedulingConfigurations.TaskSchedulerConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/task/TaskSchedulingAutoConfiguration.class */
public class TaskSchedulingAutoConfiguration {
    @ConditionalOnBean(name = {TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME})
    @Bean
    public static LazyInitializationExcludeFilter scheduledBeanLazyInitializationExcludeFilter() {
        return new ScheduledBeanLazyInitializationExcludeFilter();
    }
}
