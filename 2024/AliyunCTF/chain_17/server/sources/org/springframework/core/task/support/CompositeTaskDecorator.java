package org.springframework.core.task.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.core.task.TaskDecorator;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/support/CompositeTaskDecorator.class */
public class CompositeTaskDecorator implements TaskDecorator {
    private final List<TaskDecorator> taskDecorators;

    public CompositeTaskDecorator(Collection<? extends TaskDecorator> taskDecorators) {
        Assert.notNull(taskDecorators, "TaskDecorators must not be null");
        this.taskDecorators = new ArrayList(taskDecorators);
    }

    @Override // org.springframework.core.task.TaskDecorator
    public Runnable decorate(Runnable runnable) {
        Assert.notNull(runnable, "Runnable must not be null");
        for (TaskDecorator taskDecorator : this.taskDecorators) {
            runnable = taskDecorator.decorate(runnable);
        }
        return runnable;
    }
}
