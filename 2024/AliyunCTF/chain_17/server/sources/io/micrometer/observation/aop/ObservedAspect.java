package io.micrometer.observation.aop;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.function.Predicate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@NonNullApi
/* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/aop/ObservedAspect.class */
public class ObservedAspect {
    private static final Predicate<ProceedingJoinPoint> DONT_SKIP_ANYTHING = pjp -> {
        return false;
    };
    private final ObservationRegistry registry;

    @Nullable
    private final ObservationConvention<ObservedAspectContext> observationConvention;
    private final Predicate<ProceedingJoinPoint> shouldSkip;

    public ObservedAspect(ObservationRegistry registry) {
        this(registry, null, DONT_SKIP_ANYTHING);
    }

    public ObservedAspect(ObservationRegistry registry, ObservationConvention<ObservedAspectContext> observationConvention) {
        this(registry, observationConvention, DONT_SKIP_ANYTHING);
    }

    public ObservedAspect(ObservationRegistry registry, Predicate<ProceedingJoinPoint> shouldSkip) {
        this(registry, null, shouldSkip);
    }

    public ObservedAspect(ObservationRegistry registry, @Nullable ObservationConvention<ObservedAspectContext> observationConvention, Predicate<ProceedingJoinPoint> shouldSkip) {
        this.registry = registry;
        this.observationConvention = observationConvention;
        this.shouldSkip = shouldSkip;
    }

    @Around("@within(io.micrometer.observation.annotation.Observed)")
    @Nullable
    public Object observeClass(ProceedingJoinPoint pjp) throws Throwable {
        if (this.shouldSkip.test(pjp)) {
            return pjp.proceed();
        }
        Method method = pjp.getSignature().getMethod();
        Observed observed = (Observed) getDeclaringClass(pjp).getAnnotation(Observed.class);
        return observe(pjp, method, observed);
    }

    @Around("execution (@io.micrometer.observation.annotation.Observed * *.*(..))")
    @Nullable
    public Object observeMethod(ProceedingJoinPoint pjp) throws Throwable {
        if (this.shouldSkip.test(pjp)) {
            return pjp.proceed();
        }
        Method method = getMethod(pjp);
        Observed observed = (Observed) method.getAnnotation(Observed.class);
        return observe(pjp, method, observed);
    }

    private Object observe(ProceedingJoinPoint pjp, Method method, Observed observed) throws Throwable {
        Observation observation = ObservedAspectObservationDocumentation.of(pjp, observed, this.registry, this.observationConvention);
        if (CompletionStage.class.isAssignableFrom(method.getReturnType())) {
            observation.start();
            Observation.Scope scope = observation.openScope();
            try {
                try {
                    CompletionStage whenComplete = ((CompletionStage) pjp.proceed()).whenComplete((result, error) -> {
                        stopObservation(observation, scope, error);
                    });
                    scope.close();
                    return whenComplete;
                } finally {
                }
            } catch (Throwable th) {
                scope.close();
                throw th;
            }
        }
        return observation.observeChecked(() -> {
            return pjp.proceed();
        });
    }

    private Class<?> getDeclaringClass(ProceedingJoinPoint pjp) {
        Method method = pjp.getSignature().getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        if (!declaringClass.isAnnotationPresent(Observed.class)) {
            return pjp.getTarget().getClass();
        }
        return declaringClass;
    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Method method = pjp.getSignature().getMethod();
        if (method.getAnnotation(Observed.class) == null) {
            return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        }
        return method;
    }

    private void stopObservation(Observation observation, Observation.Scope scope, @Nullable Throwable error) {
        if (error != null) {
            observation.error(error);
        }
        scope.close();
        observation.stop();
    }

    /* loaded from: server.jar:BOOT-INF/lib/micrometer-observation-1.12.2.jar:io/micrometer/observation/aop/ObservedAspect$ObservedAspectContext.class */
    public static class ObservedAspectContext extends Observation.Context {
        private final ProceedingJoinPoint proceedingJoinPoint;

        public ObservedAspectContext(ProceedingJoinPoint proceedingJoinPoint) {
            this.proceedingJoinPoint = proceedingJoinPoint;
        }

        public ProceedingJoinPoint getProceedingJoinPoint() {
            return this.proceedingJoinPoint;
        }
    }
}
