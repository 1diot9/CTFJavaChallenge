package org.springframework.boot.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@FunctionalInterface
@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/TaskExecutorCustomizer.class */
public interface TaskExecutorCustomizer {
    void customize(ThreadPoolTaskExecutor taskExecutor);
}
