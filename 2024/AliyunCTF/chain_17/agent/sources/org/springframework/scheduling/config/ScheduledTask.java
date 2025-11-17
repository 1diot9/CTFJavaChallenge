package org.springframework.scheduling.config;

import java.util.concurrent.ScheduledFuture;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/ScheduledTask.class */
public final class ScheduledTask {
    private final Task task;

    @Nullable
    volatile ScheduledFuture<?> future;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScheduledTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    public String toString() {
        return this.task.toString();
    }
}
