package org.springframework.boot.context.properties;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotations;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/NotConstructorBoundInjectionFailureAnalyzer.class */
class NotConstructorBoundInjectionFailureAnalyzer extends AbstractInjectionFailureAnalyzer<NoSuchBeanDefinitionException> implements Ordered {
    NotConstructorBoundInjectionFailureAnalyzer() {
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause, String description) {
        InjectionPoint injectionPoint = findInjectionPoint(rootFailure);
        if (isConstructorBindingConfigurationProperties(injectionPoint)) {
            String simpleName = injectionPoint.getMember().getDeclaringClass().getSimpleName();
            String action = "Update your configuration so that " + simpleName + " is defined via @" + ConfigurationPropertiesScan.class.getSimpleName() + " or @" + EnableConfigurationProperties.class.getSimpleName() + ".";
            return new FailureAnalysis(simpleName + " is annotated with @" + ConstructorBinding.class.getSimpleName() + " but it is defined as a regular bean which caused dependency injection to fail.", action, cause);
        }
        return null;
    }

    private boolean isConstructorBindingConfigurationProperties(InjectionPoint injectionPoint) {
        if (injectionPoint != null) {
            Member member = injectionPoint.getMember();
            if (member instanceof Constructor) {
                Constructor<?> constructor = (Constructor) member;
                return isConstructorBindingConfigurationProperties(constructor);
            }
        }
        return false;
    }

    private boolean isConstructorBindingConfigurationProperties(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        BindMethod bindMethod = ConfigurationPropertiesBean.deduceBindMethod(declaringClass);
        return MergedAnnotations.from(declaringClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).isPresent(ConfigurationProperties.class) && bindMethod == BindMethod.VALUE_OBJECT;
    }

    private InjectionPoint findInjectionPoint(Throwable failure) {
        UnsatisfiedDependencyException unsatisfiedDependencyException = (UnsatisfiedDependencyException) findCause(failure, UnsatisfiedDependencyException.class);
        if (unsatisfiedDependencyException == null) {
            return null;
        }
        return unsatisfiedDependencyException.getInjectionPoint();
    }
}
