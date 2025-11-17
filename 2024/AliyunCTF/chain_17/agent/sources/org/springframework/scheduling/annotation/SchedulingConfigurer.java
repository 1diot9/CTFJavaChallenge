package org.springframework.scheduling.annotation;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/SchedulingConfigurer.class */
public interface SchedulingConfigurer {
    void configureTasks(ScheduledTaskRegistrar taskRegistrar);
}
