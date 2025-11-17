package org.springframework.scheduling.support;

import io.micrometer.observation.Observation;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/ScheduledTaskObservationContext.class */
public class ScheduledTaskObservationContext extends Observation.Context {
    private final Class<?> targetClass;
    private final Method method;
    private boolean complete;

    public ScheduledTaskObservationContext(Object target, Method method) {
        this.targetClass = ClassUtils.getUserClass(target);
        this.method = method;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Method getMethod() {
        return this.method;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
