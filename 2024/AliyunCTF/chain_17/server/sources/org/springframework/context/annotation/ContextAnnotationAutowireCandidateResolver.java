package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ContextAnnotationAutowireCandidateResolver.class */
public class ContextAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {
    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    @Nullable
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        if (isLazy(descriptor)) {
            return buildLazyResolutionProxy(descriptor, beanName);
        }
        return null;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    @Nullable
    public Class<?> getLazyResolutionProxyClass(DependencyDescriptor descriptor, @Nullable String beanName) {
        if (isLazy(descriptor)) {
            return (Class) buildLazyResolutionProxy(descriptor, beanName, true);
        }
        return null;
    }

    protected boolean isLazy(DependencyDescriptor descriptor) {
        Lazy lazy;
        for (Annotation ann : descriptor.getAnnotations()) {
            Lazy lazy2 = (Lazy) AnnotationUtils.getAnnotation(ann, Lazy.class);
            if (lazy2 != null && lazy2.value()) {
                return true;
            }
        }
        MethodParameter methodParam = descriptor.getMethodParameter();
        if (methodParam != null) {
            Method method = methodParam.getMethod();
            if ((method == null || Void.TYPE == method.getReturnType()) && (lazy = (Lazy) AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class)) != null && lazy.value()) {
                return true;
            }
            return false;
        }
        return false;
    }

    protected Object buildLazyResolutionProxy(DependencyDescriptor descriptor, @Nullable String beanName) {
        return buildLazyResolutionProxy(descriptor, beanName, false);
    }

    private Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, @Nullable final String beanName, boolean classOnly) {
        BeanFactory beanFactory = getBeanFactory();
        Assert.state(beanFactory instanceof DefaultListableBeanFactory, "BeanFactory needs to be a DefaultListableBeanFactory");
        final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        TargetSource ts = new TargetSource() { // from class: org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver.1
            @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
            public Class<?> getTargetClass() {
                return descriptor.getDependencyType();
            }

            @Override // org.springframework.aop.TargetSource
            public Object getTarget() {
                Set<String> autowiredBeanNames = beanName != null ? new LinkedHashSet<>(1) : null;
                Object target = dlbf.doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
                if (target == null) {
                    Class<?> type = getTargetClass();
                    if (Map.class == type) {
                        return Collections.emptyMap();
                    }
                    if (List.class == type) {
                        return Collections.emptyList();
                    }
                    if (Set.class == type || Collection.class == type) {
                        return Collections.emptySet();
                    }
                    throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(), "Optional dependency not present for lazy injection point");
                }
                if (autowiredBeanNames != null) {
                    for (String autowiredBeanName : autowiredBeanNames) {
                        if (dlbf.containsBean(autowiredBeanName)) {
                            dlbf.registerDependentBean(autowiredBeanName, beanName);
                        }
                    }
                }
                return target;
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType.isInterface()) {
            pf.addInterface(dependencyType);
        }
        ClassLoader classLoader = dlbf.getBeanClassLoader();
        return classOnly ? pf.getProxyClass(classLoader) : pf.getProxy(classLoader);
    }
}
