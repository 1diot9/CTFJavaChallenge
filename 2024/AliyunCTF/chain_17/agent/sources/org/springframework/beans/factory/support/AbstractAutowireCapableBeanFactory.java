package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.AutowiredPropertyMarker;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory.class */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    private InstantiationStrategy instantiationStrategy;

    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private boolean allowCircularReferences;
    private boolean allowRawInjectionDespiteWrapping;
    private final Set<Class<?>> ignoredDependencyTypes;
    private final Set<Class<?>> ignoredDependencyInterfaces;
    private final NamedThreadLocal<String> currentlyCreatedBean;
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache;
    private final ConcurrentMap<Class<?>, Method[]> factoryMethodCandidateCache;
    private final ConcurrentMap<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache;

    public AbstractAutowireCapableBeanFactory() {
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.allowCircularReferences = true;
        this.allowRawInjectionDespiteWrapping = false;
        this.ignoredDependencyTypes = new HashSet();
        this.ignoredDependencyInterfaces = new HashSet();
        this.currentlyCreatedBean = new NamedThreadLocal<>("Currently created bean");
        this.factoryBeanInstanceCache = new ConcurrentHashMap();
        this.factoryMethodCandidateCache = new ConcurrentHashMap();
        this.filteredPropertyDescriptorsCache = new ConcurrentHashMap();
        ignoreDependencyInterface(BeanNameAware.class);
        ignoreDependencyInterface(BeanFactoryAware.class);
        ignoreDependencyInterface(BeanClassLoaderAware.class);
        this.instantiationStrategy = new CglibSubclassingInstantiationStrategy();
    }

    public AbstractAutowireCapableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this();
        setParentBeanFactory(parentBeanFactory);
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }

    public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    public boolean isAllowCircularReferences() {
        return this.allowCircularReferences;
    }

    public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
        this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
    }

    public boolean isAllowRawInjectionDespiteWrapping() {
        return this.allowRawInjectionDespiteWrapping;
    }

    public void ignoreDependencyType(Class<?> type) {
        this.ignoredDependencyTypes.add(type);
    }

    public void ignoreDependencyInterface(Class<?> ifc) {
        this.ignoredDependencyInterfaces.add(ifc);
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory) otherFactory;
            this.instantiationStrategy = otherAutowireFactory.instantiationStrategy;
            this.allowCircularReferences = otherAutowireFactory.allowCircularReferences;
            this.ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
            this.ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
        }
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public <T> T createBean(Class<T> cls) throws BeansException {
        CreateFromClassBeanDefinition createFromClassBeanDefinition = new CreateFromClassBeanDefinition((Class<?>) cls);
        createFromClassBeanDefinition.setScope("prototype");
        createFromClassBeanDefinition.allowCaching = ClassUtils.isCacheSafe(cls, getBeanClassLoader());
        return (T) createBean(cls.getName(), createFromClassBeanDefinition, (Object[]) null);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public void autowireBean(Object existingBean) {
        RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean));
        bd.setScope("prototype");
        bd.allowCaching = ClassUtils.isCacheSafe(bd.getBeanClass(), getBeanClassLoader());
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        initBeanWrapper(bw);
        populateBean(bd.getBeanClass().getName(), bd, bw);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        markBeanAsCreated(beanName);
        BeanDefinition mbd = getMergedBeanDefinition(beanName);
        RootBeanDefinition bd = null;
        if (mbd instanceof RootBeanDefinition) {
            RootBeanDefinition rbd = (RootBeanDefinition) mbd;
            bd = rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition();
        }
        if (bd == null) {
            bd = new RootBeanDefinition(mbd);
        }
        if (!bd.isPrototype()) {
            bd.setScope("prototype");
            bd.allowCaching = ClassUtils.isCacheSafe(ClassUtils.getUserClass(existingBean), getBeanClassLoader());
        }
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        initBeanWrapper(bw);
        populateBean(beanName, bd, bw);
        return initializeBean(beanName, existingBean, bd);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Deprecated
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        return createBean(beanClass.getName(), bd, (Object[]) null);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        if (bd.getResolvedAutowireMode() == 3) {
            return autowireConstructor(beanClass.getName(), bd, null, null).getWrappedInstance();
        }
        Object bean = getInstantiationStrategy().instantiate(bd, null, this);
        populateBean(beanClass.getName(), bd, new BeanWrapperImpl(bean));
        return bean;
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        if (autowireMode == 3) {
            throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
        }
        RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean), autowireMode, dependencyCheck);
        bd.setScope("prototype");
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        initBeanWrapper(bw);
        populateBean(bd.getBeanClass().getName(), bd, bw);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        markBeanAsCreated(beanName);
        BeanDefinition bd = getMergedBeanDefinition(beanName);
        BeanWrapper bw = new BeanWrapperImpl(existingBean);
        initBeanWrapper(bw);
        applyPropertyValues(beanName, bd, bw, bd.getPropertyValues());
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public Object initializeBean(Object existingBean, String beanName) {
        return initializeBean(beanName, existingBean, null);
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Deprecated(since = "6.1")
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Deprecated(since = "6.1")
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public void destroyBean(Object existingBean) {
        new DisposableBeanAdapter(existingBean, getBeanPostProcessorCache().destructionAware).destroy();
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    public Object resolveBeanByName(String name, DependencyDescriptor descriptor) {
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object bean = getBean(name, descriptor.getDependencyType());
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
            return bean;
        } catch (Throwable th) {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
            throw th;
        }
    }

    @Override // org.springframework.beans.factory.config.AutowireCapableBeanFactory
    @Nullable
    public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException {
        return resolveDependency(descriptor, requestingBeanName, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    public Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating instance of bean '" + beanName + "'");
        }
        RootBeanDefinition mbdToUse = mbd;
        Class<?> resolvedClass = resolveBeanClass(mbd, beanName, new Class[0]);
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
            try {
                mbdToUse.prepareMethodOverrides();
            } catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", ex);
            }
        }
        try {
            Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
            if (bean != null) {
                return bean;
            }
            try {
                Object beanInstance = doCreateBean(beanName, mbdToUse, args);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Finished creating instance of bean '" + beanName + "'");
                }
                return beanInstance;
            } catch (BeanCreationException | ImplicitlyAppearedSingletonException ex2) {
                throw ex2;
            } catch (Throwable ex3) {
                throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex3);
            }
        } catch (Throwable ex4) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", ex4);
        }
    }

    protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        Object earlySingletonReference;
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }
        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }
        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }
        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                try {
                    applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                    mbd.markAsPostProcessed();
                } catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", ex);
                }
            }
        }
        boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
            }
            addSingletonFactory(beanName, () -> {
                return getEarlyBeanReference(beanName, mbd, bean);
            });
        }
        try {
            populateBean(beanName, mbd, instanceWrapper);
            Object exposedObject = initializeBean(beanName, bean, mbd);
            if (earlySingletonExposure && (earlySingletonReference = getSingleton(beanName, false)) != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                } else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
                    String[] dependentBeans = getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
                    for (String dependentBean : dependentBeans) {
                        if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }
                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
            try {
                registerDisposableBeanIfNecessary(beanName, bean, mbd);
                return exposedObject;
            } catch (BeanDefinitionValidationException ex2) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex2);
            }
        } catch (Throwable ex3) {
            if (ex3 instanceof BeanCreationException) {
                BeanCreationException bce = (BeanCreationException) ex3;
                if (beanName.equals(bce.getBeanName())) {
                    throw bce;
                }
            }
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, ex3.getMessage(), ex3);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    @Nullable
    public Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = determineTargetType(beanName, mbd, typesToMatch);
        if (targetType != null && !mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            boolean matchingOnlyFactoryBean = typesToMatch.length == 1 && typesToMatch[0] == FactoryBean.class;
            for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
                Class<?> predicted = bp.predictBeanType(targetType, beanName);
                if (predicted != null && (!matchingOnlyFactoryBean || FactoryBean.class.isAssignableFrom(predicted))) {
                    return predicted;
                }
            }
        }
        return targetType;
    }

    @Nullable
    protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType == null) {
            if (mbd.getFactoryMethodName() != null) {
                targetType = getTypeForFactoryMethod(beanName, mbd, typesToMatch);
            } else {
                targetType = resolveBeanClass(mbd, beanName, typesToMatch);
                if (mbd.hasBeanClass()) {
                    targetType = getInstantiationStrategy().getActualBeanClass(mbd, beanName, this);
                }
            }
            if (ObjectUtils.isEmpty((Object[]) typesToMatch) || getTempClassLoader() == null) {
                mbd.resolvedTargetType = targetType;
            }
        }
        return targetType;
    }

    @Nullable
    protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> factoryClass;
        ParameterNameDiscoverer pnd;
        ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
        if (cachedReturnType != null) {
            return cachedReturnType.resolve();
        }
        Class<?> commonType = null;
        Method uniqueCandidate = mbd.factoryMethodToIntrospect;
        if (uniqueCandidate == null) {
            boolean isStatic = true;
            String factoryBeanName = mbd.getFactoryBeanName();
            if (factoryBeanName != null) {
                if (factoryBeanName.equals(beanName)) {
                    throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
                }
                factoryClass = getType(factoryBeanName);
                isStatic = false;
            } else {
                factoryClass = resolveBeanClass(mbd, beanName, typesToMatch);
            }
            if (factoryClass == null) {
                return null;
            }
            Class<?> factoryClass2 = ClassUtils.getUserClass(factoryClass);
            int minNrOfArgs = mbd.hasConstructorArgumentValues() ? mbd.getConstructorArgumentValues().getArgumentCount() : 0;
            Method[] candidates = this.factoryMethodCandidateCache.computeIfAbsent(factoryClass2, clazz -> {
                return ReflectionUtils.getUniqueDeclaredMethods(clazz, ReflectionUtils.USER_DECLARED_METHODS);
            });
            for (Method candidate : candidates) {
                if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate) && candidate.getParameterCount() >= minNrOfArgs) {
                    if (candidate.getTypeParameters().length > 0) {
                        try {
                            ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
                            Class<?>[] paramTypes = candidate.getParameterTypes();
                            String[] paramNames = null;
                            if (cav.containsNamedArgument() && (pnd = getParameterNameDiscoverer()) != null) {
                                paramNames = pnd.getParameterNames(candidate);
                            }
                            Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
                            Object[] args = new Object[paramTypes.length];
                            for (int i = 0; i < args.length; i++) {
                                ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], paramNames != null ? paramNames[i] : null, usedValueHolders);
                                if (valueHolder == null) {
                                    valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
                                }
                                if (valueHolder != null) {
                                    args[i] = valueHolder.getValue();
                                    usedValueHolders.add(valueHolder);
                                }
                            }
                            Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(candidate, args, getBeanClassLoader());
                            uniqueCandidate = (commonType == null && returnType == candidate.getReturnType()) ? candidate : null;
                            commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
                            if (commonType == null) {
                                return null;
                            }
                        } catch (Throwable ex) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Failed to resolve generic return type for factory method: " + ex);
                            }
                        }
                    } else {
                        uniqueCandidate = commonType == null ? candidate : null;
                        commonType = ClassUtils.determineCommonAncestor(candidate.getReturnType(), commonType);
                        if (commonType == null) {
                            return null;
                        }
                    }
                }
            }
            mbd.factoryMethodToIntrospect = uniqueCandidate;
            if (commonType == null) {
                return null;
            }
        }
        ResolvableType cachedReturnType2 = uniqueCandidate != null ? ResolvableType.forMethodReturnType(uniqueCandidate) : ResolvableType.forClass(commonType);
        mbd.factoryMethodReturnType = cachedReturnType2;
        return cachedReturnType2.resolve();
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00ad  */
    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected org.springframework.core.ResolvableType getTypeForFactoryBean(java.lang.String r6, org.springframework.beans.factory.support.RootBeanDefinition r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 362
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getTypeForFactoryBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, boolean):org.springframework.core.ResolvableType");
    }

    private ResolvableType getTypeForFactoryBeanFromMethod(Class<?> beanClass, String factoryMethodName) {
        Class<?> factoryBeanClass = ClassUtils.getUserClass(beanClass);
        FactoryBeanMethodTypeFinder finder = new FactoryBeanMethodTypeFinder(factoryMethodName);
        ReflectionUtils.doWithMethods(factoryBeanClass, finder, ReflectionUtils.USER_DECLARED_METHODS);
        return finder.getResult();
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
                exposedObject = bp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }

    /* JADX WARN: Finally extract failed */
    @Nullable
    private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        synchronized (getSingletonMutex()) {
            BeanWrapper bw = this.factoryBeanInstanceCache.get(beanName);
            if (bw != null) {
                return (FactoryBean) bw.getWrappedInstance();
            }
            Object beanInstance = getSingleton(beanName, false);
            if (beanInstance instanceof FactoryBean) {
                FactoryBean<?> factoryBean = (FactoryBean) beanInstance;
                return factoryBean;
            }
            if (isSingletonCurrentlyInCreation(beanName) || (mbd.getFactoryBeanName() != null && isSingletonCurrentlyInCreation(mbd.getFactoryBeanName()))) {
                return null;
            }
            try {
                try {
                    beforeSingletonCreation(beanName);
                    Object instance = resolveBeforeInstantiation(beanName, mbd);
                    if (instance == null) {
                        bw = createBeanInstance(beanName, mbd, null);
                        instance = bw.getWrappedInstance();
                    }
                    afterSingletonCreation(beanName);
                    FactoryBean<?> fb = getFactoryBean(beanName, instance);
                    if (bw != null) {
                        this.factoryBeanInstanceCache.put(beanName, bw);
                    }
                    return fb;
                } catch (UnsatisfiedDependencyException ex) {
                    throw ex;
                } catch (BeanCreationException ex2) {
                    if (ex2.contains(LinkageError.class)) {
                        throw ex2;
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Bean creation exception on singleton FactoryBean type check: " + ex2);
                    }
                    onSuppressedException(ex2);
                    afterSingletonCreation(beanName);
                    return null;
                }
            } catch (Throwable th) {
                afterSingletonCreation(beanName);
                throw th;
            }
        }
    }

    @Nullable
    private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        try {
            if (isPrototypeCurrentlyInCreation(beanName)) {
                return null;
            }
            try {
                beforePrototypeCreation(beanName);
                Object instance = resolveBeforeInstantiation(beanName, mbd);
                if (instance == null) {
                    BeanWrapper bw = createBeanInstance(beanName, mbd, null);
                    instance = bw.getWrappedInstance();
                }
                afterPrototypeCreation(beanName);
                return getFactoryBean(beanName, instance);
            } catch (UnsatisfiedDependencyException ex) {
                throw ex;
            } catch (BeanCreationException ex2) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean creation exception on non-singleton FactoryBean type check: " + ex2);
                }
                onSuppressedException(ex2);
                afterPrototypeCreation(beanName);
                return null;
            }
        } catch (Throwable th) {
            afterPrototypeCreation(beanName);
            throw th;
        }
    }

    protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
        for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
            processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
        }
    }

    @Nullable
    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Class<?> targetType;
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors() && (targetType = determineTargetType(beanName, mbd, new Class[0])) != null) {
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                if (bean != null) {
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
            mbd.beforeInstantiationResolved = Boolean.valueOf(bean != null);
        }
        return bean;
    }

    @Nullable
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
        Class<?> beanClass = resolveBeanClass(mbd, beanName, new Class[0]);
        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        }
        Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
        if (instanceSupplier != null) {
            return obtainFromSupplier(instanceSupplier, beanName, mbd);
        }
        if (mbd.getFactoryMethodName() != null) {
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            synchronized (mbd.constructorArgumentLock) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        if (resolved) {
            if (autowireNecessary) {
                return autowireConstructor(beanName, mbd, null, null);
            }
            return instantiateBean(beanName, mbd);
        }
        Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        if (ctors != null || mbd.getResolvedAutowireMode() == 3 || mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
            return autowireConstructor(beanName, mbd, ctors, args);
        }
        Constructor<?>[] ctors2 = mbd.getPreferredConstructors();
        if (ctors2 != null) {
            return autowireConstructor(beanName, mbd, ctors2, null);
        }
        return instantiateBean(beanName, mbd);
    }

    private BeanWrapper obtainFromSupplier(Supplier<?> supplier, String beanName, RootBeanDefinition mbd) {
        String outerBean = this.currentlyCreatedBean.get();
        this.currentlyCreatedBean.set(beanName);
        try {
            try {
                Object instance = obtainInstanceFromSupplier(supplier, beanName, mbd);
                if (outerBean != null) {
                    this.currentlyCreatedBean.set(outerBean);
                } else {
                    this.currentlyCreatedBean.remove();
                }
                if (instance == null) {
                    instance = new NullBean();
                }
                BeanWrapper bw = new BeanWrapperImpl(instance);
                initBeanWrapper(bw);
                return bw;
            } catch (Throwable ex) {
                if (ex instanceof BeansException) {
                    BeansException beansException = (BeansException) ex;
                    throw beansException;
                }
                throw new BeanCreationException(beanName, "Instantiation of supplied bean failed", ex);
            }
        } catch (Throwable th) {
            if (outerBean != null) {
                this.currentlyCreatedBean.set(outerBean);
            } else {
                this.currentlyCreatedBean.remove();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Object obtainInstanceFromSupplier(Supplier<?> supplier, String beanName, RootBeanDefinition mbd) throws Exception {
        if (supplier instanceof ThrowingSupplier) {
            ThrowingSupplier<?> throwingSupplier = (ThrowingSupplier) supplier;
            return throwingSupplier.getWithException();
        }
        return supplier.get();
    }

    @Override // org.springframework.beans.factory.support.AbstractBeanFactory
    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
        String currentlyCreatedBean = this.currentlyCreatedBean.get();
        if (currentlyCreatedBean != null) {
            registerDependentBean(beanName, currentlyCreatedBean);
        }
        return super.getObjectForBeanInstance(beanInstance, name, beanName, mbd);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
            for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
                Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
                if (ctors != null) {
                    return ctors;
                }
            }
            return null;
        }
        return null;
    }

    protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        try {
            Object beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            initBeanWrapper(bw);
            return bw;
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, ex.getMessage(), ex);
        }
    }

    protected BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
        return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
    }

    protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }

    protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
        if (bw == null) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
            return;
        }
        if (bw.getWrappedClass().isRecord()) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to a record");
            }
            return;
        }
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
                if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                    return;
                }
            }
        }
        PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
        int resolvedAutowireMode = mbd.getResolvedAutowireMode();
        if (resolvedAutowireMode == 1 || resolvedAutowireMode == 2) {
            MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
            if (resolvedAutowireMode == 1) {
                autowireByName(beanName, mbd, bw, newPvs);
            }
            if (resolvedAutowireMode == 2) {
                autowireByType(beanName, mbd, bw, newPvs);
            }
            pvs = newPvs;
        }
        if (hasInstantiationAwareBeanPostProcessors()) {
            if (pvs == null) {
                pvs = mbd.getPropertyValues();
            }
            for (InstantiationAwareBeanPostProcessor bp2 : getBeanPostProcessorCache().instantiationAware) {
                PropertyValues pvsToUse = bp2.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
                if (pvsToUse == null) {
                    return;
                } else {
                    pvs = pvsToUse;
                }
            }
        }
        boolean needsDepCheck = mbd.getDependencyCheck() != 0;
        if (needsDepCheck) {
            PropertyDescriptor[] filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
            checkDependencies(beanName, mbd, filteredPds, pvs);
        }
        if (pvs != null) {
            applyPropertyValues(beanName, mbd, bw, pvs);
        }
    }

    protected void autowireByName(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        for (String propertyName : propertyNames) {
            if (containsBean(propertyName)) {
                Object bean = getBean(propertyName);
                pvs.add(propertyName, bean);
                registerDependentBean(propertyName, beanName);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found");
            }
        }
    }

    protected void autowireByType(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        TypeConverter converter = getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        Set<String> autowiredBeanNames = new LinkedHashSet<>(propertyNames.length * 2);
        for (String propertyName : propertyNames) {
            try {
                PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                if (Object.class != pd.getPropertyType()) {
                    MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                    boolean eager = !(bw.getWrappedInstance() instanceof PriorityOrdered);
                    DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
                    Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
                    if (autowiredArgument != null) {
                        pvs.add(propertyName, autowiredArgument);
                    }
                    for (String autowiredBeanName : autowiredBeanNames) {
                        registerDependentBean(autowiredBeanName, beanName);
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Autowiring by type from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + autowiredBeanName + "'");
                        }
                    }
                    autowiredBeanNames.clear();
                }
            } catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
            }
        }
    }

    protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
        Set<String> result = new TreeSet<>();
        PropertyValues pvs = mbd.getPropertyValues();
        PropertyDescriptor[] pds = bw.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null && !isExcludedFromDependencyCheck(pd) && !pvs.contains(pd.getName()) && !BeanUtils.isSimpleProperty(pd.getPropertyType())) {
                result.add(pd.getName());
            }
        }
        return StringUtils.toStringArray(result);
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw, boolean cache) {
        PropertyDescriptor[] existing;
        PropertyDescriptor[] filtered = this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
        if (filtered == null) {
            filtered = filterPropertyDescriptorsForDependencyCheck(bw);
            if (cache && (existing = this.filteredPropertyDescriptorsCache.putIfAbsent(bw.getWrappedClass(), filtered)) != null) {
                filtered = existing;
            }
        }
        return filtered;
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
        List<PropertyDescriptor> pds = new ArrayList<>(Arrays.asList(bw.getPropertyDescriptors()));
        pds.removeIf(this::isExcludedFromDependencyCheck);
        return (PropertyDescriptor[]) pds.toArray(new PropertyDescriptor[0]);
    }

    protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        return AutowireUtils.isExcludedFromDependencyCheck(pd) || this.ignoredDependencyTypes.contains(pd.getPropertyType()) || AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces);
    }

    protected void checkDependencies(String beanName, AbstractBeanDefinition mbd, PropertyDescriptor[] pds, @Nullable PropertyValues pvs) throws UnsatisfiedDependencyException {
        int dependencyCheck = mbd.getDependencyCheck();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null && (pvs == null || !pvs.contains(pd.getName()))) {
                boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
                boolean unsatisfied = dependencyCheck == 3 || (isSimple && dependencyCheck == 2) || (!isSimple && dependencyCheck == 1);
                if (unsatisfied) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(), "Set this property value or disable dependency checking for this bean.");
                }
            }
        }
    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        List<PropertyValue> original;
        if (pvs.isEmpty()) {
            return;
        }
        MutablePropertyValues mpvs = null;
        if (pvs instanceof MutablePropertyValues) {
            MutablePropertyValues _mpvs = (MutablePropertyValues) pvs;
            mpvs = _mpvs;
            if (mpvs.isConverted()) {
                try {
                    bw.setPropertyValues(mpvs);
                    return;
                } catch (BeansException ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", ex);
                }
            }
            original = mpvs.getPropertyValueList();
        } else {
            original = Arrays.asList(pvs.getPropertyValues());
        }
        TypeConverter converter = getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
        List<PropertyValue> deepCopy = new ArrayList<>(original.size());
        boolean resolveNecessary = false;
        for (PropertyValue pv : original) {
            if (pv.isConverted()) {
                deepCopy.add(pv);
            } else {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                if (originalValue == AutowiredPropertyMarker.INSTANCE) {
                    Method writeMethod = bw.getPropertyDescriptor(propertyName).getWriteMethod();
                    if (writeMethod == null) {
                        throw new IllegalArgumentException("Autowire marker for property without write method: " + pv);
                    }
                    originalValue = new DependencyDescriptor(new MethodParameter(writeMethod, 0), true);
                }
                Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                Object convertedValue = resolvedValue;
                boolean convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
                if (convertible) {
                    convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
                }
                if (resolvedValue == originalValue) {
                    if (convertible) {
                        pv.setConvertedValue(convertedValue);
                    }
                    deepCopy.add(pv);
                } else {
                    if (convertible && (originalValue instanceof TypedStringValue)) {
                        TypedStringValue typedStringValue = (TypedStringValue) originalValue;
                        if (!typedStringValue.isDynamic() && !(convertedValue instanceof Collection) && !ObjectUtils.isArray(convertedValue)) {
                            pv.setConvertedValue(convertedValue);
                            deepCopy.add(pv);
                        }
                    }
                    resolveNecessary = true;
                    deepCopy.add(new PropertyValue(pv, convertedValue));
                }
            }
        }
        if (mpvs != null && !resolveNecessary) {
            mpvs.setConverted();
        }
        try {
            bw.setPropertyValues(new MutablePropertyValues(deepCopy));
        } catch (BeansException ex2) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, ex2.getMessage(), ex2);
        }
    }

    @Nullable
    private Object convertForProperty(@Nullable Object value, String propertyName, BeanWrapper bw, TypeConverter converter) {
        if (converter instanceof BeanWrapperImpl) {
            BeanWrapperImpl beanWrapper = (BeanWrapperImpl) converter;
            return beanWrapper.convertForProperty(value, propertyName);
        }
        PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
        MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
        return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
    }

    protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        invokeAwareMethods(beanName, bean);
        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }
        try {
            invokeInitMethods(beanName, wrappedBean, mbd);
            if (mbd == null || !mbd.isSynthetic()) {
                wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
            }
            return wrappedBean;
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, ex.getMessage(), ex);
        }
    }

    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                BeanNameAware beanNameAware = (BeanNameAware) bean;
                beanNameAware.setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                BeanClassLoaderAware beanClassLoaderAware = (BeanClassLoaderAware) bean;
                ClassLoader bcl = getBeanClassLoader();
                if (bcl != null) {
                    beanClassLoaderAware.setBeanClassLoader(bcl);
                }
            }
            if (bean instanceof BeanFactoryAware) {
                BeanFactoryAware beanFactoryAware = (BeanFactoryAware) bean;
                beanFactoryAware.setBeanFactory(this);
            }
        }
    }

    protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd) throws Throwable {
        String[] initMethodNames;
        boolean isInitializingBean = bean instanceof InitializingBean;
        if (isInitializingBean && (mbd == null || !mbd.hasAnyExternallyManagedInitMethod("afterPropertiesSet"))) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
            }
            ((InitializingBean) bean).afterPropertiesSet();
        }
        if (mbd != null && bean.getClass() != NullBean.class && (initMethodNames = mbd.getInitMethodNames()) != null) {
            for (String initMethodName : initMethodNames) {
                if (StringUtils.hasLength(initMethodName) && ((!isInitializingBean || !"afterPropertiesSet".equals(initMethodName)) && !mbd.hasAnyExternallyManagedInitMethod(initMethodName))) {
                    invokeCustomInitMethod(beanName, bean, mbd, initMethodName);
                }
            }
        }
    }

    protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd, String initMethodName) throws Throwable {
        Method methodIfAvailable;
        Class<?> beanClass = bean.getClass();
        MethodDescriptor descriptor = MethodDescriptor.create(beanName, beanClass, initMethodName);
        String methodName = descriptor.methodName();
        if (mbd.isNonPublicAccessAllowed()) {
            methodIfAvailable = BeanUtils.findMethod(descriptor.declaringClass(), methodName, new Class[0]);
        } else {
            methodIfAvailable = ClassUtils.getMethodIfAvailable(beanClass, methodName, new Class[0]);
        }
        Method initMethod = methodIfAvailable;
        if (initMethod == null) {
            if (mbd.isEnforceInitMethod()) {
                throw new BeanDefinitionValidationException("Could not find an init method named '" + methodName + "' on bean with name '" + beanName + "'");
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No default init method named '" + methodName + "' found on bean with name '" + beanName + "'");
                return;
            }
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Invoking init method '" + methodName + "' on bean with name '" + beanName + "'");
        }
        Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod, beanClass);
        try {
            ReflectionUtils.makeAccessible(methodToInvoke);
            methodToInvoke.invoke(bean, new Object[0]);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    @Override // org.springframework.beans.factory.support.FactoryBeanRegistrySupport
    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
        return applyBeanPostProcessorsAfterInitialization(object, beanName);
    }

    @Override // org.springframework.beans.factory.support.FactoryBeanRegistrySupport, org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    protected void removeSingleton(String beanName) {
        synchronized (getSingletonMutex()) {
            super.removeSingleton(beanName);
            this.factoryBeanInstanceCache.remove(beanName);
        }
    }

    @Override // org.springframework.beans.factory.support.FactoryBeanRegistrySupport, org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    protected void clearSingletonCache() {
        synchronized (getSingletonMutex()) {
            super.clearSingletonCache();
            this.factoryBeanInstanceCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Log getLogger() {
        return this.logger;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory$CreateFromClassBeanDefinition.class */
    private static class CreateFromClassBeanDefinition extends RootBeanDefinition {
        public CreateFromClassBeanDefinition(Class<?> beanClass) {
            super(beanClass);
        }

        public CreateFromClassBeanDefinition(CreateFromClassBeanDefinition original) {
            super((RootBeanDefinition) original);
        }

        @Override // org.springframework.beans.factory.support.RootBeanDefinition
        @Nullable
        public Constructor<?>[] getPreferredConstructors() {
            Constructor<?>[] fromAttribute = super.getPreferredConstructors();
            if (fromAttribute != null) {
                return fromAttribute;
            }
            return ConstructorResolver.determinePreferredConstructors(getBeanClass());
        }

        @Override // org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.factory.support.AbstractBeanDefinition
        public RootBeanDefinition cloneBeanDefinition() {
            return new CreateFromClassBeanDefinition(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory$AutowireByTypeDependencyDescriptor.class */
    public static class AutowireByTypeDependencyDescriptor extends DependencyDescriptor {
        public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
            super(methodParameter, false, eager);
        }

        @Override // org.springframework.beans.factory.config.DependencyDescriptor
        @Nullable
        public String getDependencyName() {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractAutowireCapableBeanFactory$FactoryBeanMethodTypeFinder.class */
    public static class FactoryBeanMethodTypeFinder implements ReflectionUtils.MethodCallback {
        private final String factoryMethodName;
        private ResolvableType result = ResolvableType.NONE;

        FactoryBeanMethodTypeFinder(String factoryMethodName) {
            this.factoryMethodName = factoryMethodName;
        }

        @Override // org.springframework.util.ReflectionUtils.MethodCallback
        public void doWith(Method method) throws IllegalArgumentException {
            if (isFactoryBeanMethod(method)) {
                ResolvableType returnType = ResolvableType.forMethodReturnType(method);
                ResolvableType candidate = returnType.as(FactoryBean.class).getGeneric(new int[0]);
                if (this.result == ResolvableType.NONE) {
                    this.result = candidate;
                    return;
                }
                Class<?> resolvedResult = this.result.resolve();
                Class<?> commonAncestor = ClassUtils.determineCommonAncestor(candidate.resolve(), resolvedResult);
                if (!ObjectUtils.nullSafeEquals(resolvedResult, commonAncestor)) {
                    this.result = ResolvableType.forClass(commonAncestor);
                }
            }
        }

        private boolean isFactoryBeanMethod(Method method) {
            return method.getName().equals(this.factoryMethodName) && FactoryBean.class.isAssignableFrom(method.getReturnType());
        }

        ResolvableType getResult() {
            Class<?> resolved = this.result.resolve();
            boolean foundResult = (resolved == null || resolved == Object.class) ? false : true;
            return foundResult ? this.result : ResolvableType.NONE;
        }
    }
}
