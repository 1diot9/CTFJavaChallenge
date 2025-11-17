package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/ThreadPoolTaskExecutorCustomizer.class */
public interface ThreadPoolTaskExecutorCustomizer {
    void customize(ThreadPoolTaskExecutor taskExecutor);
}
