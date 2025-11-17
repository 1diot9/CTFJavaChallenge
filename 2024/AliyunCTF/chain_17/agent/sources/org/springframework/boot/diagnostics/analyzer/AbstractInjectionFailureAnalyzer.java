package org.springframework.boot.diagnostics.analyzer;

import java.lang.Throwable;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/AbstractInjectionFailureAnalyzer.class */
public abstract class AbstractInjectionFailureAnalyzer<T extends Throwable> extends AbstractFailureAnalyzer<T> {
    protected abstract FailureAnalysis analyze(Throwable rootFailure, T cause, String description);

    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    protected final FailureAnalysis analyze(Throwable rootFailure, T cause) {
        return analyze(rootFailure, cause, getDescription(rootFailure));
    }

    private String getDescription(Throwable rootFailure) {
        UnsatisfiedDependencyException unsatisfiedDependency = (UnsatisfiedDependencyException) findMostNestedCause(rootFailure, UnsatisfiedDependencyException.class);
        if (unsatisfiedDependency != null) {
            return getDescription(unsatisfiedDependency);
        }
        BeanInstantiationException beanInstantiationException = (BeanInstantiationException) findMostNestedCause(rootFailure, BeanInstantiationException.class);
        if (beanInstantiationException != null) {
            return getDescription(beanInstantiationException);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.lang.Exception] */
    private <C extends Exception> C findMostNestedCause(Throwable root, Class<C> type) {
        C result = null;
        for (Throwable candidate = root; candidate != null; candidate = candidate.getCause()) {
            if (type.isAssignableFrom(candidate.getClass())) {
                result = (Exception) candidate;
            }
        }
        return result;
    }

    private String getDescription(UnsatisfiedDependencyException ex) {
        InjectionPoint injectionPoint = ex.getInjectionPoint();
        if (injectionPoint != null) {
            if (injectionPoint.getField() != null) {
                return String.format("Field %s in %s", injectionPoint.getField().getName(), injectionPoint.getField().getDeclaringClass().getName());
            }
            if (injectionPoint.getMethodParameter() != null) {
                if (injectionPoint.getMethodParameter().getConstructor() != null) {
                    return String.format("Parameter %d of constructor in %s", Integer.valueOf(injectionPoint.getMethodParameter().getParameterIndex()), injectionPoint.getMethodParameter().getDeclaringClass().getName());
                }
                return String.format("Parameter %d of method %s in %s", Integer.valueOf(injectionPoint.getMethodParameter().getParameterIndex()), injectionPoint.getMethodParameter().getMethod().getName(), injectionPoint.getMethodParameter().getDeclaringClass().getName());
            }
        }
        return ex.getResourceDescription();
    }

    private String getDescription(BeanInstantiationException ex) {
        if (ex.getConstructingMethod() != null) {
            return String.format("Method %s in %s", ex.getConstructingMethod().getName(), ex.getConstructingMethod().getDeclaringClass().getName());
        }
        if (ex.getConstructor() != null) {
            return String.format("Constructor in %s", ClassUtils.getUserClass(ex.getConstructor().getDeclaringClass()).getName());
        }
        return ex.getBeanClass().getName();
    }
}
