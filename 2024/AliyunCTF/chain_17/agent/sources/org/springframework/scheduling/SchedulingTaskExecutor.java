package org.springframework.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/SchedulingTaskExecutor.class */
public interface SchedulingTaskExecutor extends AsyncTaskExecutor {
    default boolean prefersShortLivedTasks() {
        return true;
    }
}
