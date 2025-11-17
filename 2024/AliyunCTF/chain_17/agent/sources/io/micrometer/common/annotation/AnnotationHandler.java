package io.micrometer.common.annotation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.aspectj.lang.ProceedingJoinPoint;

/* loaded from: agent.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/annotation/AnnotationHandler.class */
public class AnnotationHandler<T> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance((Class<?>) AnnotationHandler.class);
    private final BiConsumer<KeyValue, T> keyValueConsumer;
    private final Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider;
    private final Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider;
    private final Class<? extends Annotation> annotationClass;
    private final BiFunction<Annotation, Object, KeyValue> toKeyValue;

    public AnnotationHandler(BiConsumer<KeyValue, T> keyValueConsumer, Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider, Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider, Class<? extends Annotation> annotation, BiFunction<Annotation, Object, KeyValue> toKeyValue) {
        this.keyValueConsumer = keyValueConsumer;
        this.resolverProvider = resolverProvider;
        this.expressionResolverProvider = expressionResolverProvider;
        this.annotationClass = annotation;
        this.toKeyValue = toKeyValue;
    }

    public void addAnnotatedParameters(T objectToModify, ProceedingJoinPoint pjp) {
        try {
            Method method = pjp.getSignature().getMethod();
            Method method2 = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            List<AnnotatedParameter> annotatedParameters = AnnotationUtils.findAnnotatedParameters(this.annotationClass, method2, pjp.getArgs());
            getAnnotationsFromInterfaces(pjp, method2, annotatedParameters);
            addAnnotatedArguments(objectToModify, annotatedParameters);
        } catch (Exception ex) {
            log.error("Exception occurred while trying to add annotated parameters", (Throwable) ex);
        }
    }

    private void getAnnotationsFromInterfaces(ProceedingJoinPoint pjp, Method mostSpecificMethod, List<AnnotatedParameter> annotatedParameters) {
        Class<?>[] implementedInterfaces = pjp.getThis().getClass().getInterfaces();
        for (Class<?> implementedInterface : implementedInterfaces) {
            for (Method methodFromInterface : implementedInterface.getMethods()) {
                if (methodsAreTheSame(mostSpecificMethod, methodFromInterface)) {
                    List<AnnotatedParameter> annotatedParametersForActualMethod = AnnotationUtils.findAnnotatedParameters(this.annotationClass, methodFromInterface, pjp.getArgs());
                    mergeAnnotatedParameters(annotatedParameters, annotatedParametersForActualMethod);
                }
            }
        }
    }

    private boolean methodsAreTheSame(Method mostSpecificMethod, Method method) {
        return method.getName().equals(mostSpecificMethod.getName()) && Arrays.equals(method.getParameterTypes(), mostSpecificMethod.getParameterTypes());
    }

    private void mergeAnnotatedParameters(List<AnnotatedParameter> annotatedParameters, List<AnnotatedParameter> annotatedParametersForActualMethod) {
        for (AnnotatedParameter container : annotatedParametersForActualMethod) {
            int index = container.parameterIndex;
            boolean parameterContained = false;
            Iterator<AnnotatedParameter> it = annotatedParameters.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AnnotatedParameter parameterContainer = it.next();
                if (parameterContainer.parameterIndex == index) {
                    parameterContained = true;
                    break;
                }
            }
            if (!parameterContained) {
                annotatedParameters.add(container);
            }
        }
    }

    private void addAnnotatedArguments(T objectToModify, List<AnnotatedParameter> toBeAdded) {
        for (AnnotatedParameter container : toBeAdded) {
            KeyValue keyValue = this.toKeyValue.apply(container.annotation, container.argument);
            this.keyValueConsumer.accept(keyValue, objectToModify);
        }
    }

    public Function<Class<? extends ValueResolver>, ? extends ValueResolver> getResolverProvider() {
        return this.resolverProvider;
    }

    public Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> getExpressionResolverProvider() {
        return this.expressionResolverProvider;
    }
}
