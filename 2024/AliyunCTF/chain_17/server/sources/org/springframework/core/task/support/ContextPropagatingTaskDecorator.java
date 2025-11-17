package org.springframework.core.task.support;

import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.core.task.TaskDecorator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/support/ContextPropagatingTaskDecorator.class */
public class ContextPropagatingTaskDecorator implements TaskDecorator {
    private final ContextSnapshotFactory factory;

    public ContextPropagatingTaskDecorator() {
        this(ContextSnapshotFactory.builder().build());
    }

    public ContextPropagatingTaskDecorator(ContextSnapshotFactory factory) {
        this.factory = factory;
    }

    @Override // org.springframework.core.task.TaskDecorator
    public Runnable decorate(Runnable runnable) {
        return this.factory.captureAll(new Object[0]).wrap(runnable);
    }
}
