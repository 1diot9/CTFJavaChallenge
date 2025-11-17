package org.springframework.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/TaskRejectedException.class */
public class TaskRejectedException extends RejectedExecutionException {
    public TaskRejectedException(String msg) {
        super(msg);
    }

    public TaskRejectedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TaskRejectedException(Executor executor, Object task, RejectedExecutionException cause) {
        super(executorDescription(executor) + " did not accept task: " + task, cause);
    }

    private static String executorDescription(Executor executor) {
        if (!(executor instanceof ExecutorService)) {
            return executor.toString();
        }
        ExecutorService executorService = (ExecutorService) executor;
        return "ExecutorService in " + (executorService.isShutdown() ? "shutdown" : "active") + " state";
    }
}
