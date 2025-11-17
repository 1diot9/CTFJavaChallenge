package org.springframework.core.task;

import java.util.concurrent.Executor;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/TaskExecutor.class */
public interface TaskExecutor extends Executor {
    @Override // java.util.concurrent.Executor
    void execute(Runnable task);
}
