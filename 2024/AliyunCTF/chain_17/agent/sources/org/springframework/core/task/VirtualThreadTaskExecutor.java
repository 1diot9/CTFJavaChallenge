package org.springframework.core.task;

import java.util.concurrent.ThreadFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/VirtualThreadTaskExecutor.class */
public class VirtualThreadTaskExecutor implements AsyncTaskExecutor {
    private final ThreadFactory virtualThreadFactory;

    public VirtualThreadTaskExecutor() {
        this.virtualThreadFactory = new VirtualThreadDelegate().virtualThreadFactory();
    }

    public VirtualThreadTaskExecutor(String threadNamePrefix) {
        this.virtualThreadFactory = new VirtualThreadDelegate().virtualThreadFactory(threadNamePrefix);
    }

    public final ThreadFactory getVirtualThreadFactory() {
        return this.virtualThreadFactory;
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        this.virtualThreadFactory.newThread(task).start();
    }
}
