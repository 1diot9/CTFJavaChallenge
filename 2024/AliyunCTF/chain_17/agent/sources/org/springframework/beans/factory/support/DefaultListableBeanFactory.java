package org.springframework.beans.factory.support;

import jakarta.inject.Provider;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.log.LogMessage;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.CompositeIterator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory.class */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

    @Nullable
    private static Class<?> javaxInjectProviderClass;
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories;

    @Nullable
    private String serializationId;
    private boolean allowBeanDefinitionOverriding;
    private boolean allowEagerClassLoading;

    @Nullable
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver;
    private final Map<Class<?>, Object> resolvableDependencies;
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<String, BeanDefinitionHolder> mergedBeanDefinitionHolders;
    private final Map<Class<?>, String[]> allBeanNamesByType;
    private final Map<Class<?>, String[]> singletonBeanNamesByType;
    private volatile List<String> beanDefinitionNames;
    private volatile Set<String> manualSingletonNames;

    @Nullable
    private volatile String[] frozenBeanDefinitionNames;
    private volatile boolean configurationFrozen;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$BeanObjectProvider.class */
    private interface BeanObjectProvider<T> extends ObjectProvider<T>, Serializable {
    }

    static {
        try {
            javaxInjectProviderClass = ClassUtils.forName("jakarta.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            javaxInjectProviderClass = null;
        }
        serializableFactories = new ConcurrentHashMap(8);
    }

    public DefaultListableBeanFactory() {
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;
        this.resolvableDependencies = new ConcurrentHashMap(16);
        this.beanDefinitionMap = new ConcurrentHashMap(256);
        this.mergedBeanDefinitionHolders = new ConcurrentHashMap(256);
        this.allBeanNamesByType = new ConcurrentHashMap(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap(64);
        this.beanDefinitionNames = new ArrayList(256);
        this.manualSingletonNames = new LinkedHashSet(16);
    }

    public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        this.allowBeanDefinitionOverriding = true;
        this.allowEagerClassLoading = true;
        this.autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;
        this.resolvableDependencies = new ConcurrentHashMap(16);
        this.beanDefinitionMap = new ConcurrentHashMap(256);
        this.mergedBeanDefinitionHolders = new ConcurrentHashMap(256);
        this.allBeanNamesByType = new ConcurrentHashMap(64);
        this.singletonBeanNamesByType = new ConcurrentHashMap(64);
        this.beanDefinitionNames = new ArrayList(256);
        this.manualSingletonNames = new LinkedHashSet(16);
    }

    public void setSerializationId(@Nullable String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference(this));
        } else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }

    @Nullable
    public String getSerializationId() {
        return this.serializationId;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }

    public boolean isAllowEagerClassLoading() {
        return this.allowEagerClassLoading;
    }

    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            BeanFactoryAware beanFactoryAware = (BeanFactoryAware) autowireCandidateResolver;
            beanFactoryAware.setBeanFactory(this);
        }
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }

    @Override // org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory, org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory) otherFactory;
            this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
            this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
            this.dependencyComparator = otherListableFactory.dependencyComparator;
            setAutowireCandidateResolver(otherListableFactory.getAutowireCandidateResolver().cloneIfNecessary());
            this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
        }
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls) throws BeansException {
        return (T) getBean(cls, (Object[]) null);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(Class<T> cls, @Nullable Object... objArr) throws BeansException {
        Assert.notNull(cls, "Required type must not be null");
        T t = (T) resolveBean(ResolvableType.forRawClass(cls), objArr, false);
        if (t == null) {
            throw new NoSuchBeanDefinitionException((Class<?>) cls);
        }
        return t;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        Assert.notNull(requiredType, "Required type must not be null");
        return getBeanProvider(ResolvableType.forRawClass(requiredType), true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return getBeanProvider(requiredType, true);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean containsBeanDefinition(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public String[] getBeanDefinitionNames() {
        String[] frozenNames = this.frozenBeanDefinitionNames;
        if (frozenNames != null) {
            return (String[]) frozenNames.clone();
        }
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        Assert.notNull(requiredType, "Required type must not be null");
        return getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, final boolean allowEagerInit) {
        return new BeanObjectProvider<T>() { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.1
            @Override // org.springframework.beans.factory.ObjectFactory
            public T getObject() throws BeansException {
                T t = (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
                if (t == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return t;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public T getObject(Object... objArr) throws BeansException {
                T t = (T) DefaultListableBeanFactory.this.resolveBean(requiredType, objArr, false);
                if (t == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return t;
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfAvailable() throws BeansException {
                try {
                    return (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
                } catch (ScopeNotActiveException e) {
                    return null;
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
                T dependency = getIfAvailable();
                if (dependency != null) {
                    try {
                        dependencyConsumer.accept(dependency);
                    } catch (ScopeNotActiveException e) {
                    }
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            @Nullable
            public T getIfUnique() throws BeansException {
                try {
                    return (T) DefaultListableBeanFactory.this.resolveBean(requiredType, null, true);
                } catch (ScopeNotActiveException e) {
                    return null;
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
                T dependency = getIfUnique();
                if (dependency != null) {
                    try {
                        dependencyConsumer.accept(dependency);
                    } catch (ScopeNotActiveException e) {
                    }
                }
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> stream() {
                return Arrays.stream(DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType, allowEagerInit)).map(name -> {
                    return DefaultListableBeanFactory.this.getBean(name);
                }).filter(bean -> {
                    return !(bean instanceof NullBean);
                });
            }

            @Override // org.springframework.beans.factory.ObjectProvider
            public Stream<T> orderedStream() {
                String[] beanNames = DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType, allowEagerInit);
                if (beanNames.length == 0) {
                    return Stream.empty();
                }
                Map<String, ?> newLinkedHashMap = CollectionUtils.newLinkedHashMap(beanNames.length);
                for (String beanName : beanNames) {
                    Object beanInstance = DefaultListableBeanFactory.this.getBean(beanName);
                    if (!(beanInstance instanceof NullBean)) {
                        newLinkedHashMap.put(beanName, beanInstance);
                    }
                }
                Stream<T> stream = newLinkedHashMap.values().stream();
                return stream.sorted(DefaultListableBeanFactory.this.adaptOrderComparator(newLinkedHashMap));
            }
        };
    }

    @Nullable
    private <T> T resolveBean(ResolvableType resolvableType, @Nullable Object[] objArr, boolean z) {
        NamedBeanHolder<T> resolveNamedBean = resolveNamedBean(resolvableType, objArr, z);
        if (resolveNamedBean != null) {
            return resolveNamedBean.getBeanInstance();
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory instanceof DefaultListableBeanFactory) {
            return (T) ((DefaultListableBeanFactory) parentBeanFactory).resolveBean(resolvableType, objArr, z);
        }
        if (parentBeanFactory != null) {
            ObjectProvider<T> beanProvider = parentBeanFactory.getBeanProvider(resolvableType);
            if (objArr != null) {
                return beanProvider.getObject(objArr);
            }
            return z ? beanProvider.getIfUnique() : beanProvider.getIfAvailable();
        }
        return null;
    }

    private String[] getBeanNamesForTypedStream(ResolvableType requiredType, boolean allowEagerInit) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory) this, requiredType, true, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(ResolvableType type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        Class<?> resolved = type.resolve();
        if (resolved != null && !type.hasGenerics()) {
            return getBeanNamesForType(resolved, includeNonSingletons, allowEagerInit);
        }
        return doGetBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return getBeanNamesForType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        if (!isConfigurationFrozen() || type == null || !allowEagerInit) {
            return doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
        }
        Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
        String[] resolvedBeanNames = cache.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        }
        String[] resolvedBeanNames2 = doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
        if (ClassUtils.isCacheSafe(type, getBeanClassLoader())) {
            cache.put(type, resolvedBeanNames2);
        }
        return resolvedBeanNames2;
    }

    private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        LogMessage format;
        List<String> result = new ArrayList<>();
        Iterator<String> it = this.beanDefinitionNames.iterator();
        while (it.hasNext()) {
            String beanName = it.next();
            if (!isAlias(beanName)) {
                try {
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    if (!mbd.isAbstract() && (allowEagerInit || ((mbd.hasBeanClass() || !mbd.isLazyInit() || isAllowEagerClassLoading()) && !requiresEagerInitForType(mbd.getFactoryBeanName())))) {
                        boolean isFactoryBean = isFactoryBean(beanName, mbd);
                        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
                        boolean matchFound = false;
                        boolean allowFactoryBeanInit = allowEagerInit || containsSingleton(beanName);
                        boolean isNonLazyDecorated = (dbd == null || mbd.isLazyInit()) ? false : true;
                        if (!isFactoryBean) {
                            if (includeNonSingletons || isSingleton(beanName, mbd, dbd)) {
                                matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
                            }
                        } else {
                            if (includeNonSingletons || isNonLazyDecorated || (allowFactoryBeanInit && isSingleton(beanName, mbd, dbd))) {
                                matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
                            }
                            if (!matchFound) {
                                beanName = "&" + beanName;
                                if (includeNonSingletons || isSingleton(beanName, mbd, dbd)) {
                                    matchFound = isTypeMatch(beanName, type, allowFactoryBeanInit);
                                }
                            }
                        }
                        if (matchFound) {
                            result.add(beanName);
                        }
                    }
                } catch (BeanDefinitionStoreException | CannotLoadBeanClassException ex) {
                    if (allowEagerInit) {
                        throw ex;
                    }
                    if (ex instanceof CannotLoadBeanClassException) {
                        format = LogMessage.format("Ignoring bean class loading failure for bean '%s'", beanName);
                    } else {
                        format = LogMessage.format("Ignoring unresolvable metadata in bean definition '%s'", beanName);
                    }
                    LogMessage message = format;
                    this.logger.trace(message, ex);
                    onSuppressedException(ex);
                } catch (NoSuchBeanDefinitionException e) {
                }
            }
        }
        Iterator<String> it2 = this.manualSingletonNames.iterator();
        while (it2.hasNext()) {
            String beanName2 = it2.next();
            try {
            } catch (NoSuchBeanDefinitionException ex2) {
                this.logger.trace(LogMessage.format("Failed to check manually registered singleton with name '%s'", beanName2), ex2);
            }
            if (isFactoryBean(beanName2)) {
                if ((includeNonSingletons || isSingleton(beanName2)) && isTypeMatch(beanName2, type)) {
                    result.add(beanName2);
                } else {
                    beanName2 = "&" + beanName2;
                }
            }
            if (isTypeMatch(beanName2, type)) {
                result.add(beanName2);
            }
        }
        return StringUtils.toStringArray(result);
    }

    private boolean isSingleton(String beanName, RootBeanDefinition mbd, @Nullable BeanDefinitionHolder dbd) {
        return dbd != null ? mbd.isSingleton() : isSingleton(beanName);
    }

    private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
        return (factoryBeanName == null || !isFactoryBean(factoryBeanName) || containsSingleton(factoryBeanName)) ? false : true;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getBeansOfType(type, true, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        String[] beanNames = getBeanNamesForType((Class<?>) type, includeNonSingletons, allowEagerInit);
        LinkedHashMap newLinkedHashMap = CollectionUtils.newLinkedHashMap(beanNames.length);
        for (String beanName : beanNames) {
            try {
                Object beanInstance = getBean(beanName);
                if (!(beanInstance instanceof NullBean)) {
                    newLinkedHashMap.put(beanName, beanInstance);
                }
            } catch (BeanCreationException ex) {
                Throwable rootCause = ex.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException) {
                    BeanCurrentlyInCreationException bce = (BeanCurrentlyInCreationException) rootCause;
                    String exBeanName = bce.getBeanName();
                    if (exBeanName != null && isCurrentlyInCreation(exBeanName)) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Ignoring match to currently created bean '" + exBeanName + "': " + ex.getMessage());
                        }
                        onSuppressedException(ex);
                    }
                }
                throw ex;
            }
        }
        return newLinkedHashMap;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionNames) {
            BeanDefinition bd = this.beanDefinitionMap.get(beanName);
            if (bd != null && !bd.isAbstract() && findAnnotationOnBean(beanName, annotationType) != null) {
                result.add(beanName);
            }
        }
        for (String beanName2 : this.manualSingletonNames) {
            if (!result.contains(beanName2) && findAnnotationOnBean(beanName2, annotationType) != null) {
                result.add(beanName2);
            }
        }
        return StringUtils.toStringArray(result);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        String[] beanNames = getBeanNamesForAnnotation(annotationType);
        Map<String, Object> result = CollectionUtils.newLinkedHashMap(beanNames.length);
        for (String beanName : beanNames) {
            Object beanInstance = getBean(beanName);
            if (!(beanInstance instanceof NullBean)) {
                result.put(beanName, beanInstance);
            }
        }
        return result;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException {
        return (A) findAnnotationOnBean(str, cls, true);
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> beanClass;
        Class<?> beanType = getType(beanName, allowFactoryBeanInit);
        if (beanType != null) {
            MergedAnnotation<A> annotation = MergedAnnotations.from(beanType, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
            if (annotation.isPresent()) {
                return annotation.synthesize();
            }
        }
        if (containsBeanDefinition(beanName)) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (bd.hasBeanClass() && bd.getFactoryMethodName() == null && (beanClass = bd.getBeanClass()) != beanType) {
                MergedAnnotation<A> annotation2 = MergedAnnotations.from(beanClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
                if (annotation2.isPresent()) {
                    return annotation2.synthesize();
                }
            }
            Method factoryMethod = bd.getResolvedFactoryMethod();
            if (factoryMethod != null) {
                MergedAnnotation<A> annotation3 = MergedAnnotations.from(factoryMethod, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType);
                if (annotation3.isPresent()) {
                    return annotation3.synthesize();
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.beans.factory.ListableBeanFactory
    public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> beanClass;
        Set<A> annotations = new LinkedHashSet<>();
        Class<?> beanType = getType(beanName, allowFactoryBeanInit);
        if (beanType != null) {
            MergedAnnotations.from(beanType, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(annotationType).filter((v0) -> {
                return v0.isPresent();
            }).forEach(mergedAnnotation -> {
                annotations.add(mergedAnnotation.synthesize());
            });
        }
        if (containsBeanDefinition(beanName)) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (bd.hasBeanClass() && bd.getFactoryMethodName() == null && (beanClass = bd.getBeanClass()) != beanType) {
                MergedAnnotations.from(beanClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(annotationType).filter((v0) -> {
                    return v0.isPresent();
                }).forEach(mergedAnnotation2 -> {
                    annotations.add(mergedAnnotation2.synthesize());
                });
            }
            Method factoryMethod = bd.getResolvedFactoryMethod();
            if (factoryMethod != null) {
                MergedAnnotations.from(factoryMethod, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(annotationType).filter((v0) -> {
                    return v0.isPresent();
                }).forEach(mergedAnnotation3 -> {
                    annotations.add(mergedAnnotation3.synthesize());
                });
            }
        }
        return annotations;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
        Assert.notNull(dependencyType, "Dependency type must not be null");
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory) && !dependencyType.isInstance(autowiredValue)) {
                throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
    }

    protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
        String bdName = BeanFactoryUtils.transformedBeanName(beanName);
        if (containsBeanDefinition(bdName)) {
            return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
        }
        if (containsSingleton(beanName)) {
            return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor, resolver);
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) parent;
            return dlbf.isAutowireCandidate(beanName, descriptor, resolver);
        }
        if (parent instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) parent;
            return clbf.isAutowireCandidate(beanName, descriptor);
        }
        return true;
    }

    protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
        BeanDefinitionHolder beanDefinitionHolder;
        String bdName = BeanFactoryUtils.transformedBeanName(beanName);
        resolveBeanClass(mbd, bdName, new Class[0]);
        if (mbd.isFactoryMethodUnique && mbd.factoryMethodToIntrospect == null) {
            new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
        }
        if (beanName.equals(bdName)) {
            beanDefinitionHolder = this.mergedBeanDefinitionHolders.computeIfAbsent(beanName, key -> {
                return new BeanDefinitionHolder(mbd, beanName, getAliases(bdName));
            });
        } else {
            beanDefinitionHolder = new BeanDefinitionHolder(mbd, beanName, getAliases(bdName));
        }
        BeanDefinitionHolder holder = beanDefinitionHolder;
        return resolver.isAutowireCandidate(holder, descriptor);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableListableBeanFactory, org.springframework.beans.factory.support.BeanDefinitionRegistry
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public Iterator<String> getBeanNamesIterator() {
        CompositeIterator<String> iterator = new CompositeIterator<>();
        iterator.add(this.beanDefinitionNames.iterator());
        iterator.add(this.manualSingletonNames.iterator());
        return iterator;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    protected void clearMergedBeanDefinition(String beanName) {
        super.clearMergedBeanDefinition(beanName);
        this.mergedBeanDefinitionHolders.remove(beanName);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void clearMetadataCache() {
        super.clearMetadataCache();
        this.mergedBeanDefinitionHolders.clear();
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void freezeConfiguration() {
        clearMetadataCache();
        this.configurationFrozen = true;
        this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory
    @Nullable
    public Object obtainInstanceFromSupplier(Supplier<?> supplier, String beanName, RootBeanDefinition mbd) throws Exception {
        if (supplier instanceof InstanceSupplier) {
            InstanceSupplier<?> instanceSupplier = (InstanceSupplier) supplier;
            return instanceSupplier.get(RegisteredBean.of(this, beanName, mbd));
        }
        return super.obtainInstanceFromSupplier(supplier, beanName, mbd);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableListableBeanFactory
    public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Pre-instantiating singletons in " + this);
        }
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
        for (String beanName : beanNames) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                if (isFactoryBean(beanName)) {
                    Object bean = getBean("&" + beanName);
                    if (bean instanceof SmartFactoryBean) {
                        SmartFactoryBean<?> smartFactoryBean = (SmartFactoryBean) bean;
                        if (smartFactoryBean.isEagerInit()) {
                            getBean(beanName);
                        }
                    }
                } else {
                    getBean(beanName);
                }
            }
        }
        for (String beanName2 : beanNames) {
            Object singletonInstance = getSingleton(beanName2);
            if (singletonInstance instanceof SmartInitializingSingleton) {
                SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
                StartupStep smartInitialize = getApplicationStartup().start("spring.beans.smart-initialize").tag("beanName", beanName2);
                smartSingleton.afterSingletonsInstantiated();
                smartInitialize.end();
            }
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        if (beanDefinition instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDefinition;
            try {
                abd.validate();
            } catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", ex);
            }
        }
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
        if (existingDefinition != null) {
            if (!isBeanDefinitionOverridable(beanName)) {
                throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
            }
            if (existingDefinition.getRole() < beanDefinition.getRole()) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (!beanDefinition.equals(existingDefinition)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            if (isAlias(beanName)) {
                String aliasedName = canonicalName(beanName);
                if (!isBeanDefinitionOverridable(aliasedName)) {
                    if (containsBeanDefinition(aliasedName)) {
                        throw new BeanDefinitionOverrideException(beanName, beanDefinition, getBeanDefinition(aliasedName));
                    }
                    throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition for bean '" + beanName + "' since there is already an alias for bean '" + aliasedName + "' bound.");
                }
                removeAlias(beanName);
            }
            if (hasBeanCreationStarted()) {
                synchronized (this.beanDefinitionMap) {
                    this.beanDefinitionMap.put(beanName, beanDefinition);
                    List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
                    updatedDefinitions.addAll(this.beanDefinitionNames);
                    updatedDefinitions.add(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                    removeManualSingletonName(beanName);
                }
            } else {
                this.beanDefinitionMap.put(beanName, beanDefinition);
                this.beanDefinitionNames.add(beanName);
                removeManualSingletonName(beanName);
            }
            this.frozenBeanDefinitionNames = null;
        }
        if (existingDefinition != null || containsSingleton(beanName)) {
            resetBeanDefinition(beanName);
        } else if (isConfigurationFrozen()) {
            clearByTypeCache();
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        if (hasBeanCreationStarted()) {
            synchronized (this.beanDefinitionMap) {
                List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames);
                updatedDefinitions.remove(beanName);
                this.beanDefinitionNames = updatedDefinitions;
            }
        } else {
            this.beanDefinitionNames.remove(beanName);
        }
        this.frozenBeanDefinitionNames = null;
        resetBeanDefinition(beanName);
    }

    protected void resetBeanDefinition(String beanName) {
        BeanDefinition bd;
        clearMergedBeanDefinition(beanName);
        destroySingleton(beanName);
        for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
            processor.resetBeanDefinition(beanName);
        }
        for (String bdName : this.beanDefinitionNames) {
            if (!beanName.equals(bdName) && (bd = this.beanDefinitionMap.get(bdName)) != null && beanName.equals(bd.getParentName())) {
                resetBeanDefinition(bdName);
            }
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean isBeanDefinitionOverridable(String beanName) {
        return isAllowBeanDefinitionOverriding();
    }

    @Override // org.springframework.core.SimpleAliasRegistry
    protected boolean allowAliasOverriding() {
        return isAllowBeanDefinitionOverriding();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.SimpleAliasRegistry
    public void checkForAliasCircle(String name, String alias) {
        super.checkForAliasCircle(name, alias);
        if (!isBeanDefinitionOverridable(alias) && containsBeanDefinition(alias)) {
            throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Alias would override bean definition '" + alias + "'");
        }
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry, org.springframework.beans.factory.config.SingletonBeanRegistry
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        updateManualSingletonNames(set -> {
            set.add(beanName);
        }, set2 -> {
            return !this.beanDefinitionMap.containsKey(beanName);
        });
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void destroySingletons() {
        super.destroySingletons();
        updateManualSingletonNames((v0) -> {
            v0.clear();
        }, set -> {
            return !set.isEmpty();
        });
        clearByTypeCache();
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public void destroySingleton(String beanName) {
        super.destroySingleton(beanName);
        removeManualSingletonName(beanName);
        clearByTypeCache();
    }

    private void removeManualSingletonName(String beanName) {
        updateManualSingletonNames(set -> {
            set.remove(beanName);
        }, set2 -> {
            return set2.contains(beanName);
        });
    }

    private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
        if (hasBeanCreationStarted()) {
            synchronized (this.beanDefinitionMap) {
                if (condition.test(this.manualSingletonNames)) {
                    Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
                    action.accept(updatedSingletons);
                    this.manualSingletonNames = updatedSingletons;
                }
            }
            return;
        }
        if (condition.test(this.manualSingletonNames)) {
            action.accept(this.manualSingletonNames);
        }
    }

    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        NamedBeanHolder<T> namedBean = resolveNamedBean(ResolvableType.forRawClass(requiredType), (Object[]) null, false);
        if (namedBean != null) {
            return namedBean;
        }
        BeanFactory parent = getParentBeanFactory();
        if (parent instanceof AutowireCapableBeanFactory) {
            AutowireCapableBeanFactory acbf = (AutowireCapableBeanFactory) parent;
            return acbf.resolveNamedBean(requiredType);
        }
        throw new NoSuchBeanDefinitionException((Class<?>) requiredType);
    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        String[] candidateNames = getBeanNamesForType(requiredType);
        if (candidateNames.length > 1) {
            List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
                    autowireCandidates.add(beanName);
                }
            }
            if (!autowireCandidates.isEmpty()) {
                candidateNames = StringUtils.toStringArray(autowireCandidates);
            }
        }
        if (candidateNames.length == 1) {
            return resolveNamedBean(candidateNames[0], requiredType, args);
        }
        if (candidateNames.length > 1) {
            Map<String, Object> candidates = CollectionUtils.newLinkedHashMap(candidateNames.length);
            for (String beanName2 : candidateNames) {
                if (containsSingleton(beanName2) && args == null) {
                    Object beanInstance = getBean(beanName2);
                    candidates.put(beanName2, beanInstance instanceof NullBean ? null : beanInstance);
                } else {
                    candidates.put(beanName2, getType(beanName2));
                }
            }
            String candidateName = determinePrimaryCandidate(candidates, requiredType.toClass());
            if (candidateName == null) {
                candidateName = determineHighestPriorityCandidate(candidates, requiredType.toClass());
            }
            if (candidateName != null) {
                Object beanInstance2 = candidates.get(candidateName);
                if (beanInstance2 == null) {
                    return null;
                }
                if (beanInstance2 instanceof Class) {
                    return resolveNamedBean(candidateName, requiredType, args);
                }
                return new NamedBeanHolder<>(candidateName, beanInstance2);
            }
            if (!nonUniqueAsNull) {
                throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
            }
            return null;
        }
        return null;
    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(String beanName, ResolvableType requiredType, @Nullable Object[] args) throws BeansException {
        Object bean = getBean(beanName, null, args);
        if (bean instanceof NullBean) {
            return null;
        }
        return new NamedBeanHolder<>(beanName, adaptBeanInstance(beanName, bean, requiredType.toClass()));
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Nullable
    public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        Object result;
        descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
        if (Optional.class == descriptor.getDependencyType()) {
            return createOptionalDependency(descriptor, requestingBeanName, new Object[0]);
        }
        if (ObjectFactory.class == descriptor.getDependencyType() || ObjectProvider.class == descriptor.getDependencyType()) {
            return new DependencyObjectProvider(descriptor, requestingBeanName);
        }
        if (javaxInjectProviderClass == descriptor.getDependencyType()) {
            return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
        }
        if (descriptor.supportsLazyResolution() && (result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName)) != null) {
            return result;
        }
        return doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }

    @Nullable
    public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        String autowiredBeanName;
        Object instanceCandidate;
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object shortcut = descriptor.resolveShortcut(this);
            if (shortcut != null) {
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return shortcut;
            }
            Class<?> type = descriptor.getDependencyType();
            Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
            if (value != null) {
                if (value instanceof String) {
                    String strValue = (String) value;
                    String resolvedValue = resolveEmbeddedValue(strValue);
                    BeanDefinition bd = (beanName == null || !containsBean(beanName)) ? null : getMergedBeanDefinition(beanName);
                    value = evaluateBeanDefinitionString(resolvedValue, bd);
                }
                TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
                try {
                    Object convertIfNecessary = converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return convertIfNecessary;
                } catch (UnsupportedOperationException e) {
                    Object convertIfNecessary2 = descriptor.getField() != null ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return convertIfNecessary2;
                }
            }
            Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
            if (multipleBeans != null) {
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return multipleBeans;
            }
            Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans.isEmpty()) {
                Object multipleBeans2 = resolveMultipleBeansFallback(descriptor, beanName, autowiredBeanNames, typeConverter);
                if (multipleBeans2 != null) {
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return multipleBeans2;
                }
                if (isRequired(descriptor)) {
                    raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                return null;
            }
            if (matchingBeans.size() > 1) {
                autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
                if (autowiredBeanName == null) {
                    if (!isRequired(descriptor) && indicatesArrayCollectionOrMap(type)) {
                        ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                        return null;
                    }
                    Object resolveNotUnique = descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return resolveNotUnique;
                }
                instanceCandidate = matchingBeans.get(autowiredBeanName);
            } else {
                Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
                autowiredBeanName = entry.getKey();
                instanceCandidate = entry.getValue();
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(autowiredBeanName);
            }
            if (instanceCandidate instanceof Class) {
                instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
            }
            Object result = instanceCandidate;
            if (result instanceof NullBean) {
                if (isRequired(descriptor)) {
                    raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                result = null;
            }
            if (!ClassUtils.isAssignableValue(type, result)) {
                throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
            }
            Object obj = result;
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
            return obj;
        } catch (Throwable th) {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
            throw th;
        }
    }

    @Nullable
    private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Comparator<Object> comparator;
        Class<?> type = descriptor.getDependencyType();
        if (descriptor instanceof StreamDependencyDescriptor) {
            StreamDependencyDescriptor streamDependencyDescriptor = (StreamDependencyDescriptor) descriptor;
            Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            Stream<Object> stream = matchingBeans.keySet().stream().map(name -> {
                return descriptor.resolveCandidate(name, type, this);
            }).filter(bean -> {
                return !(bean instanceof NullBean);
            });
            if (streamDependencyDescriptor.isOrdered()) {
                stream = stream.sorted(adaptOrderComparator(matchingBeans));
            }
            return stream;
        }
        if (type.isArray()) {
            Class<?> componentType = type.componentType();
            ResolvableType resolvableType = descriptor.getResolvableType();
            Class<?> resolvedArrayType = resolvableType.resolve(type);
            if (resolvedArrayType != type) {
                componentType = resolvableType.getComponentType().resolve();
            }
            if (componentType == null) {
                return null;
            }
            Map<String, Object> matchingBeans2 = findAutowireCandidates(beanName, componentType, new MultiElementDescriptor(descriptor));
            if (matchingBeans2.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans2.keySet());
            }
            TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
            Object result = converter.convertIfNecessary(matchingBeans2.values(), resolvedArrayType);
            if (result instanceof Object[]) {
                Object[] array = (Object[]) result;
                if (array.length > 1 && (comparator = adaptDependencyComparator(matchingBeans2)) != null) {
                    Arrays.sort(array, comparator);
                }
            }
            return result;
        }
        if (Collection.class == type || Set.class == type || List.class == type) {
            return resolveMultipleBeanCollection(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        if (Map.class == type) {
            return resolveMultipleBeanMap(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        return null;
    }

    @Nullable
    private Object resolveMultipleBeansFallback(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Class<?> type = descriptor.getDependencyType();
        if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            return resolveMultipleBeanCollection(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        if (Map.class.isAssignableFrom(type) && type.isInterface()) {
            return resolveMultipleBeanMap(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        return null;
    }

    @Nullable
    private Object resolveMultipleBeanCollection(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Comparator<Object> comparator;
        Class<?> elementType = descriptor.getResolvableType().asCollection().resolveGeneric(new int[0]);
        if (elementType == null) {
            return null;
        }
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, elementType, new MultiElementDescriptor(descriptor));
        if (matchingBeans.isEmpty()) {
            return null;
        }
        if (autowiredBeanNames != null) {
            autowiredBeanNames.addAll(matchingBeans.keySet());
        }
        TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
        Object result = converter.convertIfNecessary(matchingBeans.values(), descriptor.getDependencyType());
        if (result instanceof List) {
            List<?> list = (List) result;
            if (list.size() > 1 && (comparator = adaptDependencyComparator(matchingBeans)) != null) {
                list.sort(comparator);
            }
        }
        return result;
    }

    @Nullable
    private Object resolveMultipleBeanMap(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Class<?> valueType;
        ResolvableType mapType = descriptor.getResolvableType().asMap();
        Class<?> keyType = mapType.resolveGeneric(0);
        if (String.class != keyType || (valueType = mapType.resolveGeneric(1)) == null) {
            return null;
        }
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, valueType, new MultiElementDescriptor(descriptor));
        if (matchingBeans.isEmpty()) {
            return null;
        }
        if (autowiredBeanNames != null) {
            autowiredBeanNames.addAll(matchingBeans.keySet());
        }
        TypeConverter converter = typeConverter != null ? typeConverter : getTypeConverter();
        return converter.convertIfNecessary(matchingBeans, descriptor.getDependencyType());
    }

    private boolean indicatesArrayCollectionOrMap(Class<?> type) {
        return type.isArray() || (type.isInterface() && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)));
    }

    private boolean isRequired(DependencyDescriptor descriptor) {
        return getAutowireCandidateResolver().isRequired(descriptor);
    }

    @Nullable
    private Comparator<Object> adaptDependencyComparator(Map<String, ?> matchingBeans) {
        Comparator<Object> comparator = getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            OrderComparator orderComparator = (OrderComparator) comparator;
            return orderComparator.withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
        }
        return comparator;
    }

    private Comparator<Object> adaptOrderComparator(Map<String, ?> matchingBeans) {
        OrderComparator orderComparator;
        Comparator<Object> dependencyComparator = getDependencyComparator();
        if (dependencyComparator instanceof OrderComparator) {
            OrderComparator orderComparator2 = (OrderComparator) dependencyComparator;
            orderComparator = orderComparator2;
        } else {
            orderComparator = OrderComparator.INSTANCE;
        }
        OrderComparator comparator = orderComparator;
        return comparator.withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
    }

    private OrderComparator.OrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, ?> beans) {
        IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap<>();
        beans.forEach((beanName, instance) -> {
            instancesToBeanNames.put(instance, beanName);
        });
        return new FactoryAwareOrderSourceProvider(instancesToBeanNames);
    }

    protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
        String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory) this, requiredType, true, descriptor.isEager());
        Map<String, Object> result = CollectionUtils.newLinkedHashMap(candidateNames.length);
        Iterator<Map.Entry<Class<?>, Object>> it = this.resolvableDependencies.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<Class<?>, Object> classObjectEntry = it.next();
            Class<?> autowiringType = classObjectEntry.getKey();
            if (autowiringType.isAssignableFrom(requiredType)) {
                Object autowiringValue = AutowireUtils.resolveAutowiringValue(classObjectEntry.getValue(), requiredType);
                if (requiredType.isInstance(autowiringValue)) {
                    result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                    break;
                }
            }
        }
        for (String candidate : candidateNames) {
            if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
                addCandidateEntry(result, candidate, descriptor, requiredType);
            }
        }
        if (result.isEmpty()) {
            boolean multiple = indicatesArrayCollectionOrMap(requiredType);
            DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
            for (String candidate2 : candidateNames) {
                if (!isSelfReference(beanName, candidate2) && isAutowireCandidate(candidate2, fallbackDescriptor) && (!multiple || getAutowireCandidateResolver().hasQualifier(descriptor))) {
                    addCandidateEntry(result, candidate2, descriptor, requiredType);
                }
            }
            if (result.isEmpty() && !multiple) {
                for (String candidate3 : candidateNames) {
                    if (isSelfReference(beanName, candidate3) && ((!(descriptor instanceof MultiElementDescriptor) || !beanName.equals(candidate3)) && isAutowireCandidate(candidate3, fallbackDescriptor))) {
                        addCandidateEntry(result, candidate3, descriptor, requiredType);
                    }
                }
            }
        }
        return result;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0040, code lost:            if (r0.isOrdered() != false) goto L14;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void addCandidateEntry(java.util.Map<java.lang.String, java.lang.Object> r6, java.lang.String r7, org.springframework.beans.factory.config.DependencyDescriptor r8, java.lang.Class<?> r9) {
        /*
            r5 = this;
            r0 = r8
            boolean r0 = r0 instanceof org.springframework.beans.factory.support.DefaultListableBeanFactory.MultiElementDescriptor
            if (r0 == 0) goto L26
            r0 = r8
            r1 = r7
            r2 = r9
            r3 = r5
            java.lang.Object r0 = r0.resolveCandidate(r1, r2, r3)
            r11 = r0
            r0 = r11
            boolean r0 = r0 instanceof org.springframework.beans.factory.support.NullBean
            if (r0 != 0) goto L23
            r0 = r6
            r1 = r7
            r2 = r11
            java.lang.Object r0 = r0.put(r1, r2)
        L23:
            goto L73
        L26:
            r0 = r5
            r1 = r7
            boolean r0 = r0.containsSingleton(r1)
            if (r0 != 0) goto L43
            r0 = r8
            boolean r0 = r0 instanceof org.springframework.beans.factory.support.DefaultListableBeanFactory.StreamDependencyDescriptor
            if (r0 == 0) goto L66
            r0 = r8
            org.springframework.beans.factory.support.DefaultListableBeanFactory$StreamDependencyDescriptor r0 = (org.springframework.beans.factory.support.DefaultListableBeanFactory.StreamDependencyDescriptor) r0
            r10 = r0
            r0 = r10
            boolean r0 = r0.isOrdered()
            if (r0 == 0) goto L66
        L43:
            r0 = r8
            r1 = r7
            r2 = r9
            r3 = r5
            java.lang.Object r0 = r0.resolveCandidate(r1, r2, r3)
            r11 = r0
            r0 = r6
            r1 = r7
            r2 = r11
            boolean r2 = r2 instanceof org.springframework.beans.factory.support.NullBean
            if (r2 == 0) goto L5b
            r2 = 0
            goto L5d
        L5b:
            r2 = r11
        L5d:
            java.lang.Object r0 = r0.put(r1, r2)
            goto L73
        L66:
            r0 = r6
            r1 = r7
            r2 = r5
            r3 = r7
            java.lang.Class r2 = r2.getType(r3)
            java.lang.Object r0 = r0.put(r1, r2)
        L73:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.DefaultListableBeanFactory.addCandidateEntry(java.util.Map, java.lang.String, org.springframework.beans.factory.config.DependencyDescriptor, java.lang.Class):void");
    }

    @Nullable
    protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
        Class<?> requiredType = descriptor.getDependencyType();
        String primaryCandidate = determinePrimaryCandidate(candidates, requiredType);
        if (primaryCandidate != null) {
            return primaryCandidate;
        }
        String priorityCandidate = determineHighestPriorityCandidate(candidates, requiredType);
        if (priorityCandidate != null) {
            return priorityCandidate;
        }
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateName = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance != null && this.resolvableDependencies.containsValue(beanInstance)) || matchesBeanName(candidateName, descriptor.getDependencyName())) {
                return candidateName;
            }
        }
        return null;
    }

    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String primaryBeanName = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (isPrimary(candidateBeanName, beanInstance)) {
                if (primaryBeanName != null) {
                    boolean candidateLocal = containsBeanDefinition(candidateBeanName);
                    boolean primaryLocal = containsBeanDefinition(primaryBeanName);
                    if (candidateLocal && primaryLocal) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "more than one 'primary' bean found among candidates: " + candidates.keySet());
                    }
                    if (candidateLocal) {
                        primaryBeanName = candidateBeanName;
                    }
                } else {
                    primaryBeanName = candidateBeanName;
                }
            }
        }
        return primaryBeanName;
    }

    @Nullable
    protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        Integer candidatePriority;
        String highestPriorityBeanName = null;
        Integer highestPriority = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance != null && (candidatePriority = getPriority(beanInstance)) != null) {
                if (highestPriority != null) {
                    if (candidatePriority.equals(highestPriority)) {
                        throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());
                    }
                    if (candidatePriority.intValue() < highestPriority.intValue()) {
                        highestPriorityBeanName = candidateBeanName;
                        highestPriority = candidatePriority;
                    }
                } else {
                    highestPriorityBeanName = candidateBeanName;
                    highestPriority = candidatePriority;
                }
            }
        }
        return highestPriorityBeanName;
    }

    protected boolean isPrimary(String beanName, Object beanInstance) {
        String transformedBeanName = transformedBeanName(beanName);
        if (containsBeanDefinition(transformedBeanName)) {
            return getMergedLocalBeanDefinition(transformedBeanName).isPrimary();
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory parent = (DefaultListableBeanFactory) parentBeanFactory;
            if (parent.isPrimary(transformedBeanName, beanInstance)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Integer getPriority(Object beanInstance) {
        Comparator<Object> comparator = getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            OrderComparator orderComparator = (OrderComparator) comparator;
            return orderComparator.getPriority(beanInstance);
        }
        return null;
    }

    protected boolean matchesBeanName(String beanName, @Nullable String candidateName) {
        return candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(getAliases(beanName), candidateName));
    }

    private boolean isSelfReference(@Nullable String beanName, @Nullable String candidateName) {
        return (beanName == null || candidateName == null || (!beanName.equals(candidateName) && (!containsBeanDefinition(candidateName) || !beanName.equals(getMergedLocalBeanDefinition(candidateName).getFactoryBeanName())))) ? false : true;
    }

    private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
        checkBeanNotOfRequiredType(type, descriptor);
        throw new NoSuchBeanDefinitionException(resolvableType, "expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: " + ObjectUtils.nullSafeToString((Object[]) descriptor.getAnnotations()));
    }

    private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
        for (String beanName : this.beanDefinitionNames) {
            try {
                RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                Class<?> targetType = mbd.getTargetType();
                if (targetType != null && type.isAssignableFrom(targetType) && isAutowireCandidate(beanName, mbd, descriptor, getAutowireCandidateResolver())) {
                    Object beanInstance = getSingleton(beanName, false);
                    Class<?> beanType = (beanInstance == null || beanInstance.getClass() == NullBean.class) ? predictBeanType(beanName, mbd, new Class[0]) : beanInstance.getClass();
                    if (beanType != null && !type.isAssignableFrom(beanType)) {
                        throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
                        break;
                    }
                }
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory parent = (DefaultListableBeanFactory) parentBeanFactory;
            parent.checkBeanNotOfRequiredType(type, descriptor);
        }
    }

    private Optional<?> createOptionalDependency(DependencyDescriptor descriptor, @Nullable String beanName, final Object... args) {
        DependencyDescriptor descriptorToUse = new NestedDependencyDescriptor(descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.2
            @Override // org.springframework.beans.factory.config.DependencyDescriptor
            public boolean isRequired() {
                return false;
            }

            @Override // org.springframework.beans.factory.config.DependencyDescriptor
            public Object resolveCandidate(String beanName2, Class<?> requiredType, BeanFactory beanFactory) {
                return !ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName2, args) : super.resolveCandidate(beanName2, requiredType, beanFactory);
            }
        };
        Object result = doResolveDependency(descriptorToUse, beanName, null, null);
        if (!(result instanceof Optional)) {
            return Optional.ofNullable(result);
        }
        Optional<?> optional = (Optional) result;
        return optional;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.collectionToCommaDelimitedString(this.beanDefinitionNames));
        sb.append("]; ");
        BeanFactory parent = getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        } else {
            sb.append("parent: ").append(ObjectUtils.identityToString(parent));
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.serializationId != null) {
            return new SerializedBeanFactoryReference(this.serializationId);
        }
        throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$SerializedBeanFactoryReference.class */
    private static class SerializedBeanFactoryReference implements Serializable {
        private final String id;

        public SerializedBeanFactoryReference(String id) {
            this.id = id;
        }

        private Object readResolve() {
            Object result;
            Reference<?> ref = DefaultListableBeanFactory.serializableFactories.get(this.id);
            if (ref != null && (result = ref.get()) != null) {
                return result;
            }
            DefaultListableBeanFactory dummyFactory = new DefaultListableBeanFactory();
            dummyFactory.serializationId = this.id;
            return dummyFactory;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$NestedDependencyDescriptor.class */
    private static class NestedDependencyDescriptor extends DependencyDescriptor {
        public NestedDependencyDescriptor(DependencyDescriptor original) {
            super(original);
            increaseNestingLevel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$MultiElementDescriptor.class */
    public static class MultiElementDescriptor extends NestedDependencyDescriptor {
        public MultiElementDescriptor(DependencyDescriptor original) {
            super(original);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$StreamDependencyDescriptor.class */
    public static class StreamDependencyDescriptor extends DependencyDescriptor {
        private final boolean ordered;

        public StreamDependencyDescriptor(DependencyDescriptor original, boolean ordered) {
            super(original);
            this.ordered = ordered;
        }

        public boolean isOrdered() {
            return this.ordered;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$DependencyObjectProvider.class */
    private class DependencyObjectProvider implements BeanObjectProvider<Object> {
        private final DependencyDescriptor descriptor;
        private final boolean optional;

        @Nullable
        private final String beanName;

        public DependencyObjectProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
            this.descriptor = new NestedDependencyDescriptor(descriptor);
            this.optional = this.descriptor.getDependencyType() == Optional.class;
            this.beanName = beanName;
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public Object getObject() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            Object result = DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Object getObject(final Object... args) throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, args);
            }
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.1
                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                    return beanFactory.getBean(beanName, args);
                }
            };
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        @Nullable
        public Object getIfAvailable() throws BeansException {
            try {
                if (this.optional) {
                    return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
                }
                DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.2
                    @Override // org.springframework.beans.factory.config.DependencyDescriptor
                    public boolean isRequired() {
                        return false;
                    }
                };
                return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            } catch (ScopeNotActiveException e) {
                return null;
            }
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public void ifAvailable(Consumer<Object> dependencyConsumer) throws BeansException {
            Object dependency = getIfAvailable();
            if (dependency != null) {
                try {
                    dependencyConsumer.accept(dependency);
                } catch (ScopeNotActiveException e) {
                }
            }
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        @Nullable
        public Object getIfUnique() throws BeansException {
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor) { // from class: org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider.3
                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                public boolean isRequired() {
                    return false;
                }

                @Override // org.springframework.beans.factory.config.DependencyDescriptor
                @Nullable
                public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) {
                    return null;
                }
            };
            try {
                if (this.optional) {
                    return DefaultListableBeanFactory.this.createOptionalDependency(descriptorToUse, this.beanName, new Object[0]);
                }
                return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            } catch (ScopeNotActiveException e) {
                return null;
            }
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public void ifUnique(Consumer<Object> dependencyConsumer) throws BeansException {
            Object dependency = getIfUnique();
            if (dependency != null) {
                try {
                    dependencyConsumer.accept(dependency);
                } catch (ScopeNotActiveException e) {
                }
            }
        }

        @Nullable
        protected Object getValue() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            return DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Stream<Object> stream() {
            return resolveStream(false);
        }

        @Override // org.springframework.beans.factory.ObjectProvider
        public Stream<Object> orderedStream() {
            return resolveStream(true);
        }

        private Stream<Object> resolveStream(boolean ordered) {
            DependencyDescriptor descriptorToUse = new StreamDependencyDescriptor(this.descriptor, ordered);
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            if (!(result instanceof Stream)) {
                return Stream.of(result);
            }
            Stream stream = (Stream) result;
            return stream;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$Jsr330Factory.class */
    private class Jsr330Factory implements Serializable {
        private Jsr330Factory() {
        }

        public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
            return new Jsr330Provider(descriptor, beanName);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$Jsr330Factory$Jsr330Provider.class */
        public class Jsr330Provider extends DependencyObjectProvider implements Provider<Object> {
            public Jsr330Provider(DependencyDescriptor descriptor, @Nullable String beanName) {
                super(descriptor, beanName);
            }

            @Nullable
            public Object get() throws BeansException {
                return getValue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/DefaultListableBeanFactory$FactoryAwareOrderSourceProvider.class */
    public class FactoryAwareOrderSourceProvider implements OrderComparator.OrderSourceProvider {
        private final Map<Object, String> instancesToBeanNames;

        public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
            this.instancesToBeanNames = instancesToBeanNames;
        }

        @Override // org.springframework.core.OrderComparator.OrderSourceProvider
        @Nullable
        public Object getOrderSource(Object obj) {
            String beanName = this.instancesToBeanNames.get(obj);
            if (beanName == null) {
                return null;
            }
            try {
                RootBeanDefinition beanDefinition = (RootBeanDefinition) DefaultListableBeanFactory.this.getMergedBeanDefinition(beanName);
                List<Object> sources = new ArrayList<>(3);
                Object orderAttribute = beanDefinition.getAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE);
                if (orderAttribute != null) {
                    if (orderAttribute instanceof Integer) {
                        Integer order = (Integer) orderAttribute;
                        sources.add(() -> {
                            return order.intValue();
                        });
                    } else {
                        throw new IllegalStateException("Invalid value type for attribute 'order': " + orderAttribute.getClass().getName());
                    }
                }
                Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
                if (factoryMethod != null) {
                    sources.add(factoryMethod);
                }
                Class<?> targetType = beanDefinition.getTargetType();
                if (targetType != null && targetType != obj.getClass()) {
                    sources.add(targetType);
                }
                return sources.toArray();
            } catch (NoSuchBeanDefinitionException e) {
                return null;
            }
        }
    }
}
