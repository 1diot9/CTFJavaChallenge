package org.springframework.aop.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.function.SingletonSupplier;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/AsyncExecutionAspectSupport.class */
public abstract class AsyncExecutionAspectSupport implements BeanFactoryAware {
    public static final String DEFAULT_TASK_EXECUTOR_BEAN_NAME = "taskExecutor";
    protected final Log logger;
    private SingletonSupplier<Executor> defaultExecutor;
    private SingletonSupplier<AsyncUncaughtExceptionHandler> exceptionHandler;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private StringValueResolver embeddedValueResolver;
    private final Map<Method, AsyncTaskExecutor> executors;

    @Nullable
    protected abstract String getExecutorQualifier(Method method);

    public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor) {
        this.logger = LogFactory.getLog(getClass());
        this.executors = new ConcurrentHashMap(16);
        this.defaultExecutor = new SingletonSupplier<>(defaultExecutor, (Supplier<? extends Executor>) () -> {
            return getDefaultExecutor(this.beanFactory);
        });
        this.exceptionHandler = SingletonSupplier.of(SimpleAsyncUncaughtExceptionHandler::new);
    }

    public AsyncExecutionAspectSupport(@Nullable Executor defaultExecutor, AsyncUncaughtExceptionHandler exceptionHandler) {
        this.logger = LogFactory.getLog(getClass());
        this.executors = new ConcurrentHashMap(16);
        this.defaultExecutor = new SingletonSupplier<>(defaultExecutor, (Supplier<? extends Executor>) () -> {
            return getDefaultExecutor(this.beanFactory);
        });
        this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
    }

    public void configure(@Nullable Supplier<Executor> defaultExecutor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {
        this.defaultExecutor = new SingletonSupplier<>((Supplier) defaultExecutor, () -> {
            return getDefaultExecutor(this.beanFactory);
        });
        this.exceptionHandler = new SingletonSupplier<>((Supplier) exceptionHandler, SimpleAsyncUncaughtExceptionHandler::new);
    }

    public void setExecutor(Executor defaultExecutor) {
        this.defaultExecutor = SingletonSupplier.of(defaultExecutor);
    }

    public void setExceptionHandler(AsyncUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            this.embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);
        }
        this.executors.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public AsyncTaskExecutor determineAsyncExecutor(Method method) {
        Executor targetExecutor;
        AsyncTaskExecutor taskExecutorAdapter;
        AsyncTaskExecutor executor = this.executors.get(method);
        if (executor == null) {
            String qualifier = getExecutorQualifier(method);
            if (this.embeddedValueResolver != null && StringUtils.hasLength(qualifier)) {
                qualifier = this.embeddedValueResolver.resolveStringValue(qualifier);
            }
            if (StringUtils.hasLength(qualifier)) {
                targetExecutor = findQualifiedExecutor(this.beanFactory, qualifier);
            } else {
                targetExecutor = this.defaultExecutor.get();
            }
            if (targetExecutor == null) {
                return null;
            }
            if (targetExecutor instanceof AsyncTaskExecutor) {
                AsyncTaskExecutor asyncTaskExecutor = (AsyncTaskExecutor) targetExecutor;
                taskExecutorAdapter = asyncTaskExecutor;
            } else {
                taskExecutorAdapter = new TaskExecutorAdapter(targetExecutor);
            }
            executor = taskExecutorAdapter;
            this.executors.put(method, executor);
        }
        return executor;
    }

    @Nullable
    protected Executor findQualifiedExecutor(@Nullable BeanFactory beanFactory, String qualifier) {
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory must be set on " + getClass().getSimpleName() + " to access qualified executor '" + qualifier + "'");
        }
        return (Executor) BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Executor.class, qualifier);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Executor getDefaultExecutor(@Nullable BeanFactory beanFactory) {
        if (beanFactory != null) {
            try {
                return (Executor) beanFactory.getBean(TaskExecutor.class);
            } catch (NoUniqueBeanDefinitionException ex) {
                this.logger.debug("Could not find unique TaskExecutor bean. Continuing search for an Executor bean named 'taskExecutor'", ex);
                try {
                    return (Executor) beanFactory.getBean("taskExecutor", Executor.class);
                } catch (NoSuchBeanDefinitionException e) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("More than one TaskExecutor bean found within the context, and none is named 'taskExecutor'. Mark one of them as primary or name it 'taskExecutor' (possibly as an alias) in order to use it for async processing: " + ex.getBeanNamesFound());
                        return null;
                    }
                    return null;
                }
            } catch (NoSuchBeanDefinitionException ex2) {
                this.logger.debug("Could not find default TaskExecutor bean. Continuing search for an Executor bean named 'taskExecutor'", ex2);
                try {
                    return (Executor) beanFactory.getBean("taskExecutor", Executor.class);
                } catch (NoSuchBeanDefinitionException e2) {
                    this.logger.info("No task executor bean found for async processing: no bean of type TaskExecutor and no bean named 'taskExecutor' either");
                    return null;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
        if (CompletableFuture.class.isAssignableFrom(returnType)) {
            return executor.submitCompletable(task);
        }
        if (ListenableFuture.class.isAssignableFrom(returnType)) {
            return ((AsyncListenableTaskExecutor) executor).submitListenable(task);
        }
        if (Future.class.isAssignableFrom(returnType)) {
            return executor.submit(task);
        }
        if (Void.TYPE == returnType || "kotlin.Unit".equals(returnType.getName())) {
            executor.submit(task);
            return null;
        }
        throw new IllegalArgumentException("Invalid return type for async method (only Future and void supported): " + returnType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleError(Throwable ex, Method method, Object... params) throws Exception {
        if (Future.class.isAssignableFrom(method.getReturnType())) {
            ReflectionUtils.rethrowException(ex);
            return;
        }
        try {
            this.exceptionHandler.obtain().handleUncaughtException(ex, method, params);
        } catch (Throwable ex2) {
            this.logger.warn("Exception handler for async method '" + method.toGenericString() + "' threw unexpected exception itself", ex2);
        }
    }
}
