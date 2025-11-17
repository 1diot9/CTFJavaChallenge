package org.springframework.scheduling;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/SchedulingAwareRunnable.class */
public interface SchedulingAwareRunnable extends Runnable {
    default boolean isLongLived() {
        return false;
    }

    @Nullable
    default String getQualifier() {
        return null;
    }
}
