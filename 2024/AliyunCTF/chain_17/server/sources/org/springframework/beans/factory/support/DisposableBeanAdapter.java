package org.springframework.beans.factory.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DisposableBeanAdapter.class */
public class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable {
    private static final String DESTROY_METHOD_NAME = "destroy";
    private static final String CLOSE_METHOD_NAME = "close";
    private static final String SHUTDOWN_METHOD_NAME = "shutdown";
    private static final Log logger = LogFactory.getLog((Class<?>) DisposableBeanAdapter.class);
    private static final boolean reactiveStreamsPresent = ClassUtils.isPresent("org.reactivestreams.Publisher", DisposableBeanAdapter.class.getClassLoader());
    private final Object bean;
    private final String beanName;
    private final boolean nonPublicAccessAllowed;
    private final boolean invokeDisposableBean;
    private boolean invokeAutoCloseable;

    @Nullable
    private String[] destroyMethodNames;

    @Nullable
    private transient Method[] destroyMethods;

    @Nullable
    private final List<DestructionAwareBeanPostProcessor> beanPostProcessors;

    public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition, List<DestructionAwareBeanPostProcessor> postProcessors) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.invokeDisposableBean = (bean instanceof DisposableBean) && !beanDefinition.hasAnyExternallyManagedDestroyMethod(DESTROY_METHOD_NAME);
        String[] destroyMethodNames = inferDestroyMethodsIfNecessary(bean.getClass(), beanDefinition);
        if (!ObjectUtils.isEmpty((Object[]) destroyMethodNames) && ((!this.invokeDisposableBean || !DESTROY_METHOD_NAME.equals(destroyMethodNames[0])) && !beanDefinition.hasAnyExternallyManagedDestroyMethod(destroyMethodNames[0]))) {
            this.invokeAutoCloseable = (bean instanceof AutoCloseable) && "close".equals(destroyMethodNames[0]);
            if (!this.invokeAutoCloseable) {
                this.destroyMethodNames = destroyMethodNames;
                List<Method> destroyMethods = new ArrayList<>(destroyMethodNames.length);
                for (String destroyMethodName : destroyMethodNames) {
                    Method destroyMethod = determineDestroyMethod(destroyMethodName);
                    if (destroyMethod == null) {
                        if (beanDefinition.isEnforceDestroyMethod()) {
                            throw new BeanDefinitionValidationException("Could not find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
                        }
                    } else {
                        if (destroyMethod.getParameterCount() > 0) {
                            Class<?>[] paramTypes = destroyMethod.getParameterTypes();
                            if (paramTypes.length > 1) {
                                throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
                            }
                            if (paramTypes.length == 1 && Boolean.TYPE != paramTypes[0]) {
                                throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
                            }
                        }
                        destroyMethods.add(ClassUtils.getInterfaceMethodIfPossible(destroyMethod, bean.getClass()));
                    }
                }
                this.destroyMethods = (Method[]) destroyMethods.toArray(x$0 -> {
                    return new Method[x$0];
                });
            }
        }
        this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
    }

    public DisposableBeanAdapter(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = bean.getClass().getName();
        this.nonPublicAccessAllowed = true;
        this.invokeDisposableBean = this.bean instanceof DisposableBean;
        this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
    }

    private DisposableBeanAdapter(Object bean, String beanName, boolean nonPublicAccessAllowed, boolean invokeDisposableBean, boolean invokeAutoCloseable, @Nullable String[] destroyMethodNames, @Nullable List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean;
        this.beanName = beanName;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.invokeDisposableBean = invokeDisposableBean;
        this.invokeAutoCloseable = invokeAutoCloseable;
        this.destroyMethodNames = destroyMethodNames;
        this.beanPostProcessors = postProcessors;
    }

    @Override // java.lang.Runnable
    public void run() {
        destroy();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
            for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
                processor.postProcessBeforeDestruction(this.bean, this.beanName);
            }
        }
        if (this.invokeDisposableBean) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
            }
            try {
                ((DisposableBean) this.bean).destroy();
            } catch (Throwable ex) {
                if (logger.isWarnEnabled()) {
                    String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                    if (logger.isDebugEnabled()) {
                        logger.warn(msg, ex);
                    } else {
                        logger.warn(msg + ": " + ex);
                    }
                }
            }
        }
        if (this.invokeAutoCloseable) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking close() on bean with name '" + this.beanName + "'");
            }
            try {
                ((AutoCloseable) this.bean).close();
                return;
            } catch (Throwable ex2) {
                if (logger.isWarnEnabled()) {
                    String msg2 = "Invocation of close method failed on bean with name '" + this.beanName + "'";
                    if (logger.isDebugEnabled()) {
                        logger.warn(msg2, ex2);
                        return;
                    } else {
                        logger.warn(msg2 + ": " + ex2);
                        return;
                    }
                }
                return;
            }
        }
        if (this.destroyMethods != null) {
            for (Method method : this.destroyMethods) {
                invokeCustomDestroyMethod(method);
            }
            return;
        }
        if (this.destroyMethodNames != null) {
            for (String destroyMethodName : this.destroyMethodNames) {
                Method destroyMethod = determineDestroyMethod(destroyMethodName);
                if (destroyMethod != null) {
                    invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(destroyMethod, this.bean.getClass()));
                }
            }
        }
    }

    @Nullable
    private Method determineDestroyMethod(String destroyMethodName) {
        try {
            Class<?> beanClass = this.bean.getClass();
            MethodDescriptor descriptor = MethodDescriptor.create(this.beanName, beanClass, destroyMethodName);
            String methodName = descriptor.methodName();
            Method destroyMethod = findDestroyMethod(descriptor.declaringClass(), methodName);
            if (destroyMethod != null) {
                return destroyMethod;
            }
            for (Class<?> beanInterface : beanClass.getInterfaces()) {
                Method destroyMethod2 = findDestroyMethod(beanInterface, methodName);
                if (destroyMethod2 != null) {
                    return destroyMethod2;
                }
            }
            return null;
        } catch (IllegalArgumentException ex) {
            throw new BeanDefinitionValidationException("Could not find unique destroy method on bean with name '" + this.beanName + ": " + ex.getMessage());
        }
    }

    @Nullable
    private Method findDestroyMethod(Class<?> clazz, String name) {
        if (this.nonPublicAccessAllowed) {
            return BeanUtils.findMethodWithMinimalParameters(clazz, name);
        }
        return BeanUtils.findMethodWithMinimalParameters(clazz.getMethods(), name);
    }

    private void invokeCustomDestroyMethod(Method destroyMethod) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking custom destroy method '" + destroyMethod.getName() + "' on bean with name '" + this.beanName + "': " + destroyMethod);
        }
        int paramCount = destroyMethod.getParameterCount();
        Object[] args = new Object[paramCount];
        if (paramCount == 1) {
            args[0] = Boolean.TRUE;
        }
        try {
            ReflectionUtils.makeAccessible(destroyMethod);
            Object returnValue = destroyMethod.invoke(this.bean, args);
            if (returnValue == null) {
                logDestroyMethodCompletion(destroyMethod, false);
            } else if (returnValue instanceof Future) {
                Future<?> future = (Future) returnValue;
                future.get();
                logDestroyMethodCompletion(destroyMethod, true);
            } else if ((!reactiveStreamsPresent || !new ReactiveDestroyMethodHandler().await(destroyMethod, returnValue)) && logger.isDebugEnabled()) {
                logger.debug("Unknown return value type from custom destroy method '" + destroyMethod.getName() + "' on bean with name '" + this.beanName + "': " + returnValue.getClass());
            }
        } catch (InvocationTargetException | ExecutionException ex) {
            logDestroyMethodException(destroyMethod, ex.getCause());
        } catch (Throwable ex2) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to invoke custom destroy method '" + destroyMethod.getName() + "' on bean with name '" + this.beanName + "'", ex2);
            }
        }
    }

    void logDestroyMethodException(Method destroyMethod, Throwable ex) {
        if (logger.isWarnEnabled()) {
            String msg = "Custom destroy method '" + destroyMethod.getName() + "' on bean with name '" + this.beanName + "' propagated an exception";
            if (logger.isDebugEnabled()) {
                logger.warn(msg, ex);
            } else {
                logger.warn(msg + ": " + ex);
            }
        }
    }

    void logDestroyMethodCompletion(Method destroyMethod, boolean async) {
        if (logger.isDebugEnabled()) {
            logger.debug("Custom destroy method '" + destroyMethod.getName() + "' on bean with name '" + this.beanName + "' completed" + (async ? " asynchronously" : ""));
        }
    }

    protected Object writeReplace() {
        List<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList<>();
            for (DestructionAwareBeanPostProcessor postProcessor : this.beanPostProcessors) {
                if (postProcessor instanceof Serializable) {
                    serializablePostProcessors.add(postProcessor);
                }
            }
        }
        return new DisposableBeanAdapter(this.bean, this.beanName, this.nonPublicAccessAllowed, this.invokeDisposableBean, this.invokeAutoCloseable, this.destroyMethodNames, serializablePostProcessors);
    }

    public static boolean hasDestroyMethod(Object bean, RootBeanDefinition beanDefinition) {
        return (bean instanceof DisposableBean) || inferDestroyMethodsIfNecessary(bean.getClass(), beanDefinition) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static String[] inferDestroyMethodsIfNecessary(Class<?> target, RootBeanDefinition beanDefinition) {
        String[] destroyMethodNames = beanDefinition.getDestroyMethodNames();
        if (destroyMethodNames != null && destroyMethodNames.length > 1) {
            return destroyMethodNames;
        }
        String destroyMethodName = beanDefinition.resolvedDestroyMethodName;
        if (destroyMethodName == null) {
            destroyMethodName = beanDefinition.getDestroyMethodName();
            boolean autoCloseable = AutoCloseable.class.isAssignableFrom(target);
            if (AbstractBeanDefinition.INFER_METHOD.equals(destroyMethodName) || (destroyMethodName == null && autoCloseable)) {
                destroyMethodName = null;
                if (!DisposableBean.class.isAssignableFrom(target)) {
                    if (autoCloseable) {
                        destroyMethodName = "close";
                    } else {
                        try {
                            destroyMethodName = target.getMethod("close", new Class[0]).getName();
                        } catch (NoSuchMethodException e) {
                            try {
                                destroyMethodName = target.getMethod(SHUTDOWN_METHOD_NAME, new Class[0]).getName();
                            } catch (NoSuchMethodException e2) {
                            }
                        }
                    }
                }
            }
            beanDefinition.resolvedDestroyMethodName = destroyMethodName != null ? destroyMethodName : "";
        }
        if (StringUtils.hasLength(destroyMethodName)) {
            return new String[]{destroyMethodName};
        }
        return null;
    }

    public static boolean hasApplicableProcessors(Object bean, List<DestructionAwareBeanPostProcessor> postProcessors) {
        if (!CollectionUtils.isEmpty(postProcessors)) {
            for (DestructionAwareBeanPostProcessor processor : postProcessors) {
                if (processor.requiresDestruction(bean)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Nullable
    private static List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<DestructionAwareBeanPostProcessor> processors, Object bean) {
        List<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (!CollectionUtils.isEmpty(processors)) {
            filteredPostProcessors = new ArrayList<>(processors.size());
            for (DestructionAwareBeanPostProcessor processor : processors) {
                if (processor.requiresDestruction(bean)) {
                    filteredPostProcessors.add(processor);
                }
            }
        }
        return filteredPostProcessors;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DisposableBeanAdapter$ReactiveDestroyMethodHandler.class */
    public class ReactiveDestroyMethodHandler {
        private ReactiveDestroyMethodHandler() {
        }

        public boolean await(Method destroyMethod, Object returnValue) throws InterruptedException {
            ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(returnValue.getClass());
            if (adapter != null) {
                CountDownLatch latch = new CountDownLatch(1);
                adapter.toPublisher(returnValue).subscribe(new DestroyMethodSubscriber(destroyMethod, latch));
                latch.await();
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DisposableBeanAdapter$DestroyMethodSubscriber.class */
    public class DestroyMethodSubscriber implements Subscriber<Object> {
        private final Method destroyMethod;
        private final CountDownLatch latch;

        public DestroyMethodSubscriber(Method destroyMethod, CountDownLatch latch) {
            this.destroyMethod = destroyMethod;
            this.latch = latch;
        }

        @Override // org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            s.request(2147483647L);
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(Object o) {
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.latch.countDown();
            DisposableBeanAdapter.this.logDestroyMethodException(this.destroyMethod, t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.latch.countDown();
            DisposableBeanAdapter.this.logDestroyMethodCompletion(this.destroyMethod, true);
        }
    }
}
