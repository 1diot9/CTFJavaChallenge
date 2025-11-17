package org.springframework.boot.task;

import org.springframework.core.task.SimpleAsyncTaskExecutor;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/task/SimpleAsyncTaskExecutorCustomizer.class */
public interface SimpleAsyncTaskExecutorCustomizer {
    void customize(SimpleAsyncTaskExecutor taskExecutor);
}
