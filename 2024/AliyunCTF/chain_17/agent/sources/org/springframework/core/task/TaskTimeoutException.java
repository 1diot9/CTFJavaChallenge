package org.springframework.core.task;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/TaskTimeoutException.class */
public class TaskTimeoutException extends TaskRejectedException {
    public TaskTimeoutException(String msg) {
        super(msg);
    }

    public TaskTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
