package org.springframework.core.task;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/task/TaskDecorator.class */
public interface TaskDecorator {
    Runnable decorate(Runnable runnable);
}
