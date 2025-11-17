package org.springframework.scheduling.support;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/ScheduledMethodRunnable.class */
public class ScheduledMethodRunnable implements SchedulingAwareRunnable {
    private static final ScheduledTaskObservationConvention DEFAULT_CONVENTION = new DefaultScheduledTaskObservationConvention();
    private final Object target;
    private final Method method;

    @Nullable
    private final String qualifier;
    private final Supplier<ObservationRegistry> observationRegistrySupplier;

    public ScheduledMethodRunnable(Object target, Method method, @Nullable String qualifier, Supplier<ObservationRegistry> observationRegistrySupplier) {
        this.target = target;
        this.method = method;
        this.qualifier = qualifier;
        this.observationRegistrySupplier = observationRegistrySupplier;
    }

    public ScheduledMethodRunnable(Object target, Method method) {
        this(target, method, null, () -> {
            return ObservationRegistry.NOOP;
        });
    }

    public ScheduledMethodRunnable(Object target, String methodName) throws NoSuchMethodException {
        this(target, target.getClass().getMethod(methodName, new Class[0]));
    }

    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override // org.springframework.scheduling.SchedulingAwareRunnable
    @Nullable
    public String getQualifier() {
        return this.qualifier;
    }

    @Override // java.lang.Runnable
    public void run() {
        ScheduledTaskObservationContext context = new ScheduledTaskObservationContext(this.target, this.method);
        Observation observation = ScheduledTaskObservationDocumentation.TASKS_SCHEDULED_EXECUTION.observation(null, DEFAULT_CONVENTION, () -> {
            return context;
        }, this.observationRegistrySupplier.get());
        observation.observe(() -> {
            runInternal(context);
        });
    }

    private void runInternal(ScheduledTaskObservationContext context) {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target, new Object[0]);
            context.setComplete(true);
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        } catch (InvocationTargetException ex2) {
            ReflectionUtils.rethrowRuntimeException(ex2.getTargetException());
        }
    }

    public String toString() {
        return this.method.getDeclaringClass().getName() + "." + this.method.getName();
    }
}
