package org.springframework.beans.factory.annotation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/InitDestroyAnnotationBeanPostProcessor.class */
public class InitDestroyAnnotationBeanPostProcessor implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, BeanRegistrationAotProcessor, PriorityOrdered, Serializable {
    private final transient LifecycleMetadata emptyLifecycleMetadata = new LifecycleMetadata(Object.class, Collections.emptyList(), Collections.emptyList()) { // from class: org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.1
        @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata
        public void checkInitDestroyMethods(RootBeanDefinition beanDefinition) {
        }

        @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata
        public void invokeInitMethods(Object target, String beanName) {
        }

        @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata
        public void invokeDestroyMethods(Object target, String beanName) {
        }

        @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata
        public boolean hasDestroyMethods() {
            return false;
        }
    };
    protected transient Log logger = LogFactory.getLog(getClass());
    private final Set<Class<? extends Annotation>> initAnnotationTypes = new LinkedHashSet(2);
    private final Set<Class<? extends Annotation>> destroyAnnotationTypes = new LinkedHashSet(2);
    private int order = Integer.MAX_VALUE;

    @Nullable
    private final transient Map<Class<?>, LifecycleMetadata> lifecycleMetadataCache = new ConcurrentHashMap(256);

    public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
        this.initAnnotationTypes.clear();
        this.initAnnotationTypes.add(initAnnotationType);
    }

    public void addInitAnnotationType(@Nullable Class<? extends Annotation> initAnnotationType) {
        if (initAnnotationType != null) {
            this.initAnnotationTypes.add(initAnnotationType);
        }
    }

    public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
        this.destroyAnnotationTypes.clear();
        this.destroyAnnotationTypes.add(destroyAnnotationType);
    }

    public void addDestroyAnnotationType(@Nullable Class<? extends Annotation> destroyAnnotationType) {
        if (destroyAnnotationType != null) {
            this.destroyAnnotationTypes.add(destroyAnnotationType);
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanClass, String beanName) {
        findLifecycleMetadata(beanDefinition, beanClass);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        RootBeanDefinition beanDefinition = registeredBean.getMergedBeanDefinition();
        beanDefinition.resolveDestroyMethodIfNecessary();
        LifecycleMetadata metadata = findLifecycleMetadata(beanDefinition, registeredBean.getBeanClass());
        if (!CollectionUtils.isEmpty(metadata.initMethods)) {
            String[] initMethodNames = safeMerge(beanDefinition.getInitMethodNames(), metadata.initMethods);
            beanDefinition.setInitMethodNames(initMethodNames);
        }
        if (!CollectionUtils.isEmpty(metadata.destroyMethods)) {
            String[] destroyMethodNames = safeMerge(beanDefinition.getDestroyMethodNames(), metadata.destroyMethods);
            beanDefinition.setDestroyMethodNames(destroyMethodNames);
            return null;
        }
        return null;
    }

    private LifecycleMetadata findLifecycleMetadata(RootBeanDefinition beanDefinition, Class<?> beanClass) {
        LifecycleMetadata metadata = findLifecycleMetadata(beanClass);
        metadata.checkInitDestroyMethods(beanDefinition);
        return metadata;
    }

    private static String[] safeMerge(@Nullable String[] existingNames, Collection<LifecycleMethod> detectedMethods) {
        Stream map = detectedMethods.stream().map((v0) -> {
            return v0.getIdentifier();
        });
        return (String[]) (existingNames != null ? Stream.concat(map, Stream.of((Object[]) existingNames)) : map).distinct().toArray(x$0 -> {
            return new String[x$0];
        });
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeInitMethods(bean, beanName);
            return bean;
        } catch (InvocationTargetException ex) {
            throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
        } catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "Failed to invoke init method", ex2);
        }
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeDestroyMethods(bean, beanName);
        } catch (InvocationTargetException ex) {
            String msg = "Destroy method on bean with name '" + beanName + "' threw an exception";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex.getTargetException());
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn(msg + ": " + ex.getTargetException());
            }
        } catch (Throwable ex2) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Failed to invoke destroy method on bean with name '" + beanName + "'", ex2);
            }
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        return findLifecycleMetadata(bean.getClass()).hasDestroyMethods();
    }

    private LifecycleMetadata findLifecycleMetadata(Class<?> beanClass) {
        LifecycleMetadata lifecycleMetadata;
        if (this.lifecycleMetadataCache == null) {
            return buildLifecycleMetadata(beanClass);
        }
        LifecycleMetadata metadata = this.lifecycleMetadataCache.get(beanClass);
        if (metadata == null) {
            synchronized (this.lifecycleMetadataCache) {
                LifecycleMetadata metadata2 = this.lifecycleMetadataCache.get(beanClass);
                if (metadata2 == null) {
                    metadata2 = buildLifecycleMetadata(beanClass);
                    this.lifecycleMetadataCache.put(beanClass, metadata2);
                }
                lifecycleMetadata = metadata2;
            }
            return lifecycleMetadata;
        }
        return metadata;
    }

    private LifecycleMetadata buildLifecycleMetadata(final Class<?> beanClass) {
        if (!AnnotationUtils.isCandidateClass(beanClass, this.initAnnotationTypes) && !AnnotationUtils.isCandidateClass(beanClass, this.destroyAnnotationTypes)) {
            return this.emptyLifecycleMetadata;
        }
        List<LifecycleMethod> initMethods = new ArrayList<>();
        List<LifecycleMethod> destroyMethods = new ArrayList<>();
        Class<?> currentClass = beanClass;
        do {
            List<LifecycleMethod> currInitMethods = new ArrayList<>();
            List<LifecycleMethod> currDestroyMethods = new ArrayList<>();
            ReflectionUtils.doWithLocalMethods(currentClass, method -> {
                for (Class<? extends Annotation> initAnnotationType : this.initAnnotationTypes) {
                    if (initAnnotationType != null && method.isAnnotationPresent(initAnnotationType)) {
                        currInitMethods.add(new LifecycleMethod(method, beanClass));
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Found init method on class [" + beanClass.getName() + "]: " + method);
                        }
                    }
                }
                for (Class<? extends Annotation> destroyAnnotationType : this.destroyAnnotationTypes) {
                    if (destroyAnnotationType != null && method.isAnnotationPresent(destroyAnnotationType)) {
                        currDestroyMethods.add(new LifecycleMethod(method, beanClass));
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Found destroy method on class [" + beanClass.getName() + "]: " + method);
                        }
                    }
                }
            });
            initMethods.addAll(0, currInitMethods);
            destroyMethods.addAll(currDestroyMethods);
            currentClass = currentClass.getSuperclass();
            if (currentClass == null) {
                break;
            }
        } while (currentClass != Object.class);
        return (initMethods.isEmpty() && destroyMethods.isEmpty()) ? this.emptyLifecycleMetadata : new LifecycleMetadata(beanClass, initMethods, destroyMethods);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(getClass());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/InitDestroyAnnotationBeanPostProcessor$LifecycleMetadata.class */
    public class LifecycleMetadata {
        private final Class<?> beanClass;
        private final Collection<LifecycleMethod> initMethods;
        private final Collection<LifecycleMethod> destroyMethods;

        @Nullable
        private volatile Set<LifecycleMethod> checkedInitMethods;

        @Nullable
        private volatile Set<LifecycleMethod> checkedDestroyMethods;

        public LifecycleMetadata(Class<?> beanClass, Collection<LifecycleMethod> initMethods, Collection<LifecycleMethod> destroyMethods) {
            this.beanClass = beanClass;
            this.initMethods = initMethods;
            this.destroyMethods = destroyMethods;
        }

        public void checkInitDestroyMethods(RootBeanDefinition beanDefinition) {
            Set<LifecycleMethod> checkedInitMethods = new LinkedHashSet<>(this.initMethods.size());
            for (LifecycleMethod lifecycleMethod : this.initMethods) {
                String methodIdentifier = lifecycleMethod.getIdentifier();
                if (!beanDefinition.isExternallyManagedInitMethod(methodIdentifier)) {
                    beanDefinition.registerExternallyManagedInitMethod(methodIdentifier);
                    checkedInitMethods.add(lifecycleMethod);
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Registered init method on class [" + this.beanClass.getName() + "]: " + methodIdentifier);
                    }
                }
            }
            Set<LifecycleMethod> checkedDestroyMethods = new LinkedHashSet<>(this.destroyMethods.size());
            for (LifecycleMethod lifecycleMethod2 : this.destroyMethods) {
                String methodIdentifier2 = lifecycleMethod2.getIdentifier();
                if (!beanDefinition.isExternallyManagedDestroyMethod(methodIdentifier2)) {
                    beanDefinition.registerExternallyManagedDestroyMethod(methodIdentifier2);
                    checkedDestroyMethods.add(lifecycleMethod2);
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Registered destroy method on class [" + this.beanClass.getName() + "]: " + methodIdentifier2);
                    }
                }
            }
            this.checkedInitMethods = checkedInitMethods;
            this.checkedDestroyMethods = checkedDestroyMethods;
        }

        public void invokeInitMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleMethod> checkedInitMethods = this.checkedInitMethods;
            Collection<LifecycleMethod> initMethodsToIterate = checkedInitMethods != null ? checkedInitMethods : this.initMethods;
            if (!initMethodsToIterate.isEmpty()) {
                for (LifecycleMethod lifecycleMethod : initMethodsToIterate) {
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Invoking init method on bean '" + beanName + "': " + lifecycleMethod.getMethod());
                    }
                    lifecycleMethod.invoke(target);
                }
            }
        }

        public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleMethod> checkedDestroyMethods = this.checkedDestroyMethods;
            Collection<LifecycleMethod> destroyMethodsToUse = checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods;
            if (!destroyMethodsToUse.isEmpty()) {
                for (LifecycleMethod lifecycleMethod : destroyMethodsToUse) {
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace("Invoking destroy method on bean '" + beanName + "': " + lifecycleMethod.getMethod());
                    }
                    lifecycleMethod.invoke(target);
                }
            }
        }

        public boolean hasDestroyMethods() {
            Collection<LifecycleMethod> checkedDestroyMethods = this.checkedDestroyMethods;
            Collection<LifecycleMethod> destroyMethodsToUse = checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods;
            return !destroyMethodsToUse.isEmpty();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/InitDestroyAnnotationBeanPostProcessor$LifecycleMethod.class */
    public static class LifecycleMethod {
        private final Method method;
        private final String identifier;

        public LifecycleMethod(Method method, Class<?> beanClass) {
            if (method.getParameterCount() != 0) {
                throw new IllegalStateException("Lifecycle annotation requires a no-arg method: " + method);
            }
            this.method = method;
            this.identifier = isPrivateOrNotVisible(method, beanClass) ? ClassUtils.getQualifiedMethodName(method) : method.getName();
        }

        public Method getMethod() {
            return this.method;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public void invoke(Object target) throws Throwable {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(target, new Object[0]);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof LifecycleMethod) {
                    LifecycleMethod that = (LifecycleMethod) other;
                    if (this.identifier.equals(that.identifier)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.identifier.hashCode();
        }

        private static boolean isPrivateOrNotVisible(Method method, Class<?> beanClass) {
            int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                return true;
            }
            return (method.getDeclaringClass().getPackageName().equals(beanClass.getPackageName()) || Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) ? false : true;
        }
    }
}
