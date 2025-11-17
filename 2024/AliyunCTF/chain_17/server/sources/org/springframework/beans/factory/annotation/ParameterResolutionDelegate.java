package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/ParameterResolutionDelegate.class */
public final class ParameterResolutionDelegate {
    private static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new AnnotatedElement() { // from class: org.springframework.beans.factory.annotation.ParameterResolutionDelegate.1
        @Override // java.lang.reflect.AnnotatedElement
        @Nullable
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            return null;
        }

        @Override // java.lang.reflect.AnnotatedElement
        public Annotation[] getAnnotations() {
            return new Annotation[0];
        }

        @Override // java.lang.reflect.AnnotatedElement
        public Annotation[] getDeclaredAnnotations() {
            return new Annotation[0];
        }
    };

    private ParameterResolutionDelegate() {
    }

    public static boolean isAutowirable(Parameter parameter, int parameterIndex) {
        Assert.notNull(parameter, "Parameter must not be null");
        AnnotatedElement annotatedParameter = getEffectiveAnnotatedParameter(parameter, parameterIndex);
        return AnnotatedElementUtils.hasAnnotation(annotatedParameter, Autowired.class) || AnnotatedElementUtils.hasAnnotation(annotatedParameter, Qualifier.class) || AnnotatedElementUtils.hasAnnotation(annotatedParameter, Value.class);
    }

    @Nullable
    public static Object resolveDependency(Parameter parameter, int parameterIndex, Class<?> containingClass, AutowireCapableBeanFactory beanFactory) throws BeansException {
        Assert.notNull(parameter, "Parameter must not be null");
        Assert.notNull(containingClass, "Containing class must not be null");
        Assert.notNull(beanFactory, "AutowireCapableBeanFactory must not be null");
        AnnotatedElement annotatedParameter = getEffectiveAnnotatedParameter(parameter, parameterIndex);
        Autowired autowired = (Autowired) AnnotatedElementUtils.findMergedAnnotation(annotatedParameter, Autowired.class);
        boolean required = autowired == null || autowired.required();
        MethodParameter methodParameter = SynthesizingMethodParameter.forExecutable(parameter.getDeclaringExecutable(), parameterIndex);
        DependencyDescriptor descriptor = new DependencyDescriptor(methodParameter, required);
        descriptor.setContainingClass(containingClass);
        return beanFactory.resolveDependency(descriptor, null);
    }

    private static AnnotatedElement getEffectiveAnnotatedParameter(Parameter parameter, int index) {
        Executable executable = parameter.getDeclaringExecutable();
        if ((executable instanceof Constructor) && ClassUtils.isInnerClass(executable.getDeclaringClass()) && executable.getParameterAnnotations().length == executable.getParameterCount() - 1) {
            return index == 0 ? EMPTY_ANNOTATED_ELEMENT : executable.getParameters()[index - 1];
        }
        return parameter;
    }
}
