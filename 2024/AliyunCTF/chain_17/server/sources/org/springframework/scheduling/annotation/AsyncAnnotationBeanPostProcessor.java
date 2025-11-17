package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AsyncAnnotationBeanPostProcessor.class */
public class AsyncAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {
    public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Supplier<Executor> executor;

    @Nullable
    private Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

    @Nullable
    private Class<? extends Annotation> asyncAnnotationType;

    public AsyncAnnotationBeanPostProcessor() {
        setBeforeExistingAdvisors(true);
    }

    public void configure(@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {
        this.executor = executor;
        this.exceptionHandler = exceptionHandler;
    }

    public void setExecutor(Executor executor) {
        this.executor = SingletonSupplier.of(executor);
    }

    public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
    }

    public void setAsyncAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        this.asyncAnnotationType = asyncAnnotationType;
    }

    @Override // org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor, org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
        if (this.asyncAnnotationType != null) {
            advisor.setAsyncAnnotationType(this.asyncAnnotationType);
        }
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
