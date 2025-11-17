package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/SimpleAsyncTaskSchedulerCustomizer.class */
public interface SimpleAsyncTaskSchedulerCustomizer {
    void customize(SimpleAsyncTaskScheduler taskScheduler);
}
