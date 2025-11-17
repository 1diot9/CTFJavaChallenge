package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/ThreadPoolTaskSchedulerCustomizer.class */
public interface ThreadPoolTaskSchedulerCustomizer {
    void customize(ThreadPoolTaskScheduler taskScheduler);
}
