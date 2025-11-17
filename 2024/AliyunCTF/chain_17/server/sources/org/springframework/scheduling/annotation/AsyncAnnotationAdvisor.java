package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AsyncAnnotationAdvisor.class */
public class AsyncAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {
    private final Advice advice;
    private Pointcut pointcut;

    public AsyncAnnotationAdvisor() {
        this((Supplier<Executor>) null, (Supplier<AsyncUncaughtExceptionHandler>) null);
    }

    public AsyncAnnotationAdvisor(@Nullable Executor executor, @Nullable AsyncUncaughtExceptionHandler exceptionHandler) {
        this(SingletonSupplier.ofNullable(executor), SingletonSupplier.ofNullable(exceptionHandler));
    }

    public AsyncAnnotationAdvisor(@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {
        LinkedHashSet linkedHashSet = new LinkedHashSet(2);
        linkedHashSet.add(Async.class);
        ClassLoader classLoader = AsyncAnnotationAdvisor.class.getClassLoader();
        try {
            linkedHashSet.add(ClassUtils.forName("jakarta.ejb.Asynchronous", classLoader));
        } catch (ClassNotFoundException e) {
        }
        try {
            linkedHashSet.add(ClassUtils.forName("jakarta.enterprise.concurrent.Asynchronous", classLoader));
        } catch (ClassNotFoundException e2) {
        }
        this.advice = buildAdvice(executor, exceptionHandler);
        this.pointcut = buildPointcut(linkedHashSet);
    }

    public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        Set<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<>();
        asyncAnnotationTypes.add(asyncAnnotationType);
        this.pointcut = buildPointcut(asyncAnnotationTypes);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        Advice advice = this.advice;
        if (advice instanceof BeanFactoryAware) {
            BeanFactoryAware beanFactoryAware = (BeanFactoryAware) advice;
            beanFactoryAware.setBeanFactory(beanFactory);
        }
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.PointcutAdvisor
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    protected Advice buildAdvice(@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {
        AnnotationAsyncExecutionInterceptor interceptor = new AnnotationAsyncExecutionInterceptor(null);
        interceptor.configure(executor, exceptionHandler);
        return interceptor;
    }

    protected Pointcut buildPointcut(Set<Class<? extends Annotation>> asyncAnnotationTypes) {
        ComposablePointcut result = null;
        for (Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
            Pointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
            Pointcut mpc = new AnnotationMatchingPointcut(null, asyncAnnotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                result.union(cpc);
            }
            result = result.union(mpc);
        }
        return result != null ? result : Pointcut.TRUE;
    }
}
