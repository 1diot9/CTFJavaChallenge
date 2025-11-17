package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.generate.AccessControl;
import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.support.ClassHintUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.aot.AutowiredArgumentsCodeGenerator;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.aot.AutowiredMethodArgumentsResolver;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultBindingErrorProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor.class */
public class AutowiredAnnotationBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, BeanRegistrationAotProcessor, PriorityOrdered, BeanFactoryAware {
    private static final Constructor<?>[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor[0];

    @Nullable
    private ConfigurableListableBeanFactory beanFactory;

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    protected final Log logger = LogFactory.getLog(getClass());
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet(4);
    private String requiredParameterName = DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE;
    private boolean requiredParameterValue = true;
    private int order = 2147483645;
    private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = new ConcurrentHashMap(256);
    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap(256);

    /* JADX WARN: Multi-variable type inference failed */
    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
        ClassLoader classLoader = AutowiredAnnotationBeanPostProcessor.class.getClassLoader();
        try {
            this.autowiredAnnotationTypes.add(ClassUtils.forName("jakarta.inject.Inject", classLoader));
            this.logger.trace("'jakarta.inject.Inject' annotation found and supported for autowiring");
        } catch (ClassNotFoundException e) {
        }
        try {
            this.autowiredAnnotationTypes.add(ClassUtils.forName("javax.inject.Inject", classLoader));
            this.logger.trace("'javax.inject.Inject' annotation found and supported for autowiring");
        } catch (ClassNotFoundException e2) {
        }
    }

    public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
        Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.add(autowiredAnnotationType);
    }

    public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }

    public void setRequiredParameterName(String requiredParameterName) {
        this.requiredParameterName = requiredParameterName;
    }

    public void setRequiredParameterValue(boolean requiredParameterValue) {
        this.requiredParameterValue = requiredParameterValue;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
        this.beanFactory = clbf;
        this.metadataReaderFactory = new SimpleMetadataReaderFactory(clbf.getBeanClassLoader());
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        findInjectionMetadata(beanName, beanType, beanDefinition);
        if (beanDefinition.isSingleton()) {
            this.candidateConstructorsCache.remove(beanType);
            if (!beanDefinition.hasMethodOverrides()) {
                this.lookupMethodsChecked.remove(beanName);
            }
        }
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void resetBeanDefinition(String beanName) {
        this.lookupMethodsChecked.remove(beanName);
        this.injectionMetadataCache.remove(beanName);
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    @Nullable
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        String beanName = registeredBean.getBeanName();
        RootBeanDefinition beanDefinition = registeredBean.getMergedBeanDefinition();
        InjectionMetadata metadata = findInjectionMetadata(beanName, beanClass, beanDefinition);
        Collection<AutowiredElement> autowiredElements = getAutowiredElements(metadata, beanDefinition.getPropertyValues());
        if (!ObjectUtils.isEmpty(autowiredElements)) {
            return new AotContribution(beanClass, autowiredElements, getAutowireCandidateResolver());
        }
        return null;
    }

    private Collection<AutowiredElement> getAutowiredElements(InjectionMetadata metadata, PropertyValues propertyValues) {
        return metadata.getInjectedElements(propertyValues);
    }

    @Nullable
    private AutowireCandidateResolver getAutowireCandidateResolver() {
        ConfigurableListableBeanFactory configurableListableBeanFactory = this.beanFactory;
        if (configurableListableBeanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory lbf = (DefaultListableBeanFactory) configurableListableBeanFactory;
            return lbf.getAutowireCandidateResolver();
        }
        return null;
    }

    private InjectionMetadata findInjectionMetadata(String beanName, Class<?> beanType, RootBeanDefinition beanDefinition) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
        return metadata;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    public Class<?> determineBeanType(Class<?> beanClass, String beanName) throws BeanCreationException {
        checkLookupMethods(beanClass, beanName);
        HierarchicalBeanFactory hierarchicalBeanFactory = this.beanFactory;
        if (hierarchicalBeanFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory aacBeanFactory = (AbstractAutowireCapableBeanFactory) hierarchicalBeanFactory;
            RootBeanDefinition mbd = (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName);
            if (mbd.getFactoryMethodName() == null && mbd.hasBeanClass()) {
                return aacBeanFactory.getInstantiationStrategy().getActualBeanClass(mbd, beanName, aacBeanFactory);
            }
        }
        return beanClass;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, final String beanName) throws BeanCreationException {
        Class<?> userClass;
        checkLookupMethods(beanClass, beanName);
        Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
        if (candidateConstructors == null) {
            synchronized (this.candidateConstructorsCache) {
                candidateConstructors = this.candidateConstructorsCache.get(beanClass);
                if (candidateConstructors == null) {
                    try {
                        Constructor<?>[] rawCandidates = beanClass.getDeclaredConstructors();
                        List<Constructor<?>> candidates = new ArrayList<>(rawCandidates.length);
                        Constructor<?> requiredConstructor = null;
                        Constructor<?> defaultConstructor = null;
                        Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
                        int nonSyntheticConstructors = 0;
                        for (Constructor<?> candidate : rawCandidates) {
                            if (!candidate.isSynthetic()) {
                                nonSyntheticConstructors++;
                            } else if (primaryConstructor != null) {
                            }
                            MergedAnnotation<?> ann = findAutowiredAnnotation(candidate);
                            if (ann == null && (userClass = ClassUtils.getUserClass(beanClass)) != beanClass) {
                                try {
                                    Constructor<?> superCtor = userClass.getDeclaredConstructor(candidate.getParameterTypes());
                                    ann = findAutowiredAnnotation(superCtor);
                                } catch (NoSuchMethodException e) {
                                }
                            }
                            if (ann != null) {
                                if (requiredConstructor != null) {
                                    throw new BeanCreationException(beanName, "Invalid autowire-marked constructor: " + candidate + ". Found constructor with 'required' Autowired annotation already: " + requiredConstructor);
                                }
                                boolean required = determineRequiredStatus(ann);
                                if (required) {
                                    if (!candidates.isEmpty()) {
                                        throw new BeanCreationException(beanName, "Invalid autowire-marked constructors: " + candidates + ". Found constructor with 'required' Autowired annotation: " + candidate);
                                    }
                                    requiredConstructor = candidate;
                                }
                                candidates.add(candidate);
                            } else if (candidate.getParameterCount() == 0) {
                                defaultConstructor = candidate;
                            }
                        }
                        if (!candidates.isEmpty()) {
                            if (requiredConstructor == null) {
                                if (defaultConstructor != null) {
                                    candidates.add(defaultConstructor);
                                } else if (candidates.size() == 1 && this.logger.isInfoEnabled()) {
                                    this.logger.info("Inconsistent constructor declaration on bean with name '" + beanName + "': single autowire-marked constructor flagged as optional - this constructor is effectively required since there is no default constructor to fall back to: " + candidates.get(0));
                                }
                            }
                            candidateConstructors = (Constructor[]) candidates.toArray(EMPTY_CONSTRUCTOR_ARRAY);
                        } else if (rawCandidates.length == 1 && rawCandidates[0].getParameterCount() > 0) {
                            candidateConstructors = new Constructor[]{rawCandidates[0]};
                        } else if (nonSyntheticConstructors == 2 && primaryConstructor != null && defaultConstructor != null && !primaryConstructor.equals(defaultConstructor)) {
                            candidateConstructors = new Constructor[]{primaryConstructor, defaultConstructor};
                        } else if (nonSyntheticConstructors == 1 && primaryConstructor != null) {
                            candidateConstructors = new Constructor[]{primaryConstructor};
                        } else {
                            candidateConstructors = EMPTY_CONSTRUCTOR_ARRAY;
                        }
                        this.candidateConstructorsCache.put(beanClass, candidateConstructors);
                    } catch (Throwable ex) {
                        throw new BeanCreationException(beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                    }
                }
            }
        }
        if (candidateConstructors.length > 0) {
            return candidateConstructors;
        }
        return null;
    }

    private void checkLookupMethods(Class<?> beanClass, final String beanName) throws BeanCreationException {
        if (!this.lookupMethodsChecked.contains(beanName)) {
            if (AnnotationUtils.isCandidateClass(beanClass, (Class<? extends Annotation>) Lookup.class)) {
                Class<?> targetClass = beanClass;
                do {
                    try {
                        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                            Lookup lookup = (Lookup) method.getAnnotation(Lookup.class);
                            if (lookup != null) {
                                Assert.state(this.beanFactory != null, "No BeanFactory available");
                                LookupOverride override = new LookupOverride(method, lookup.value());
                                try {
                                    RootBeanDefinition mbd = (RootBeanDefinition) this.beanFactory.getMergedBeanDefinition(beanName);
                                    mbd.getMethodOverrides().addOverride(override);
                                } catch (NoSuchBeanDefinitionException e) {
                                    throw new BeanCreationException(beanName, "Cannot apply @Lookup to beans without corresponding bean definition");
                                }
                            }
                        });
                        targetClass = targetClass.getSuperclass();
                        if (targetClass == null) {
                            break;
                        }
                    } catch (IllegalStateException ex) {
                        throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
                    }
                } while (targetClass != Object.class);
            }
            this.lookupMethodsChecked.add(beanName);
        }
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
            return pvs;
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex2) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex2);
        }
    }

    public void processInjection(Object bean) throws BeanCreationException {
        Class<?> clazz = bean.getClass();
        InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz, null);
        try {
            metadata.inject(bean, null, null);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex2) {
            throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex2);
        }
    }

    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
            return InjectionMetadata.EMPTY;
        }
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            List<InjectionMetadata.InjectedElement> fieldElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findAutowiredAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Autowired annotation is not supported on static fields: " + field);
                        }
                    } else {
                        boolean required = determineRequiredStatus(ann);
                        fieldElements.add(new AutowiredFieldElement(field, required));
                    }
                }
            });
            List<InjectionMetadata.InjectedElement> methodElements = new ArrayList<>();
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                MergedAnnotation<?> ann;
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod) && (ann = findAutowiredAnnotation(bridgedMethod)) != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Autowired annotation is not supported on static methods: " + method);
                            return;
                        }
                        return;
                    }
                    if (method.getParameterCount() == 0) {
                        if (method.getDeclaringClass().isRecord()) {
                            return;
                        }
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Autowired annotation should only be used on methods with parameters: " + method);
                        }
                    }
                    boolean required = determineRequiredStatus(ann);
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    methodElements.add(new AutowiredMethodElement(method, required, pd));
                }
            });
            elements.addAll(0, sortMethodElements(methodElements, targetClass));
            elements.addAll(0, fieldElements);
            targetClass = targetClass.getSuperclass();
            if (targetClass == null) {
                break;
            }
        } while (targetClass != Object.class);
        return InjectionMetadata.forElements(elements, clazz);
    }

    @Nullable
    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent()) {
                return annotation;
            }
        }
        return null;
    }

    protected boolean determineRequiredStatus(MergedAnnotation<?> ann) {
        return ann.getValue(this.requiredParameterName).isEmpty() || this.requiredParameterValue == ann.getBoolean(this.requiredParameterName);
    }

    private List<InjectionMetadata.InjectedElement> sortMethodElements(List<InjectionMetadata.InjectedElement> methodElements, Class<?> targetClass) {
        if (this.metadataReaderFactory != null && methodElements.size() > 1) {
            try {
                AnnotationMetadata asm = this.metadataReaderFactory.getMetadataReader(targetClass.getName()).getAnnotationMetadata();
                Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Autowired.class.getName());
                if (asmMethods.size() >= methodElements.size()) {
                    List<InjectionMetadata.InjectedElement> candidateMethods = new ArrayList<>(methodElements);
                    List<InjectionMetadata.InjectedElement> selectedMethods = new ArrayList<>(asmMethods.size());
                    for (MethodMetadata asmMethod : asmMethods) {
                        Iterator<InjectionMetadata.InjectedElement> it = candidateMethods.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                InjectionMetadata.InjectedElement element = it.next();
                                if (element.getMember().getName().equals(asmMethod.getMethodName())) {
                                    selectedMethods.add(element);
                                    it.remove();
                                    break;
                                }
                            }
                        }
                    }
                    if (selectedMethods.size() == methodElements.size()) {
                        return selectedMethods;
                    }
                }
            } catch (IOException ex) {
                this.logger.debug("Failed to read class file via ASM for determining @Autowired method order", ex);
            }
        }
        return methodElements;
    }

    private void registerDependentBeans(@Nullable String beanName, Set<String> autowiredBeanNames) {
        if (beanName != null) {
            for (String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
                }
            }
        }
    }

    @Nullable
    private Object resolveCachedArgument(@Nullable String beanName, @Nullable Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
            Assert.state(this.beanFactory != null, "No BeanFactory available");
            return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
        }
        return cachedArgument;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor$AutowiredElement.class */
    public static abstract class AutowiredElement extends InjectionMetadata.InjectedElement {
        protected final boolean required;

        protected AutowiredElement(Member member, @Nullable PropertyDescriptor pd, boolean required) {
            super(member, pd);
            this.required = required;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.class */
    private class AutowiredFieldElement extends AutowiredElement {
        private volatile boolean cached;

        @Nullable
        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field, boolean required) {
            super(field, null, required);
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Object value;
            Field field = (Field) this.member;
            if (this.cached) {
                try {
                    value = AutowiredAnnotationBeanPostProcessor.this.resolveCachedArgument(beanName, this.cachedFieldValue);
                } catch (BeansException ex) {
                    this.cached = false;
                    AutowiredAnnotationBeanPostProcessor.this.logger.debug("Failed to resolve cached argument", ex);
                    value = resolveFieldValue(field, bean, beanName);
                }
            } else {
                value = resolveFieldValue(field, bean, beanName);
            }
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }

        @Nullable
        private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
            DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
            desc.setContainingClass(bean.getClass());
            Set<String> autowiredBeanNames = new LinkedHashSet<>(2);
            Assert.state(AutowiredAnnotationBeanPostProcessor.this.beanFactory != null, "No BeanFactory available");
            TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
            try {
                Object value = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                synchronized (this) {
                    if (!this.cached) {
                        if (value != null || this.required) {
                            Object cachedFieldValue = desc;
                            AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                            if (value != null && autowiredBeanNames.size() == 1) {
                                String autowiredBeanName = autowiredBeanNames.iterator().next();
                                if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                                    cachedFieldValue = new ShortcutDependencyDescriptor(desc, autowiredBeanName);
                                }
                            }
                            this.cachedFieldValue = cachedFieldValue;
                            this.cached = true;
                        } else {
                            this.cachedFieldValue = null;
                        }
                    }
                }
                return value;
            } catch (BeansException ex) {
                throw new UnsatisfiedDependencyException((String) null, beanName, new InjectionPoint(field), ex);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor$AutowiredMethodElement.class */
    private class AutowiredMethodElement extends AutowiredElement {
        private volatile boolean cached;

        @Nullable
        private volatile Object[] cachedMethodArguments;

        public AutowiredMethodElement(Method method, boolean required, @Nullable PropertyDescriptor pd) {
            super(method, pd, required);
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Object[] arguments;
            if (!shouldInject(pvs)) {
                return;
            }
            Method method = (Method) this.member;
            if (this.cached) {
                try {
                    arguments = resolveCachedArguments(beanName, this.cachedMethodArguments);
                } catch (BeansException ex) {
                    this.cached = false;
                    AutowiredAnnotationBeanPostProcessor.this.logger.debug("Failed to resolve cached argument", ex);
                    arguments = resolveMethodArguments(method, bean, beanName);
                }
            } else {
                arguments = resolveMethodArguments(method, bean, beanName);
            }
            if (arguments != null) {
                try {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(bean, arguments);
                } catch (InvocationTargetException ex2) {
                    throw ex2.getTargetException();
                }
            }
        }

        @Nullable
        private Object[] resolveCachedArguments(@Nullable String beanName, @Nullable Object[] cachedMethodArguments) {
            if (cachedMethodArguments == null) {
                return null;
            }
            Object[] arguments = new Object[cachedMethodArguments.length];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = AutowiredAnnotationBeanPostProcessor.this.resolveCachedArgument(beanName, cachedMethodArguments[i]);
            }
            return arguments;
        }

        @Nullable
        private Object[] resolveMethodArguments(Method method, Object bean, @Nullable String beanName) {
            int argumentCount = method.getParameterCount();
            Object[] arguments = new Object[argumentCount];
            DependencyDescriptor[] descriptors = new DependencyDescriptor[argumentCount];
            Set<String> autowiredBeanNames = new LinkedHashSet<>(argumentCount * 2);
            Assert.state(AutowiredAnnotationBeanPostProcessor.this.beanFactory != null, "No BeanFactory available");
            TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
            int i = 0;
            while (true) {
                if (i >= arguments.length) {
                    break;
                }
                MethodParameter methodParam = new MethodParameter(method, i);
                DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
                currDesc.setContainingClass(bean.getClass());
                descriptors[i] = currDesc;
                try {
                    Object arg = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(currDesc, beanName, autowiredBeanNames, typeConverter);
                    if (arg == null && !this.required && !methodParam.isOptional()) {
                        arguments = null;
                        break;
                    }
                    arguments[i] = arg;
                    i++;
                } catch (BeansException ex) {
                    throw new UnsatisfiedDependencyException((String) null, beanName, new InjectionPoint(methodParam), ex);
                }
            }
            synchronized (this) {
                if (!this.cached) {
                    if (arguments != null) {
                        DependencyDescriptor[] cachedMethodArguments = (DependencyDescriptor[]) Arrays.copyOf(descriptors, argumentCount);
                        AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                        if (autowiredBeanNames.size() == argumentCount) {
                            Iterator<String> it = autowiredBeanNames.iterator();
                            Class<?>[] paramTypes = method.getParameterTypes();
                            for (int i2 = 0; i2 < paramTypes.length; i2++) {
                                String autowiredBeanName = it.next();
                                if (arguments[i2] != null && AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i2])) {
                                    cachedMethodArguments[i2] = new ShortcutDependencyDescriptor(descriptors[i2], autowiredBeanName);
                                }
                            }
                        }
                        this.cachedMethodArguments = cachedMethodArguments;
                        this.cached = true;
                    } else {
                        this.cachedMethodArguments = null;
                    }
                }
            }
            return arguments;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor$ShortcutDependencyDescriptor.class */
    public static class ShortcutDependencyDescriptor extends DependencyDescriptor {
        private final String shortcut;

        public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut) {
            super(original);
            this.shortcut = shortcut;
        }

        @Override // org.springframework.beans.factory.config.DependencyDescriptor
        public Object resolveShortcut(BeanFactory beanFactory) {
            return beanFactory.getBean(this.shortcut, getDependencyType());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/AutowiredAnnotationBeanPostProcessor$AotContribution.class */
    private static class AotContribution implements BeanRegistrationAotContribution {
        private static final String REGISTERED_BEAN_PARAMETER = "registeredBean";
        private static final String INSTANCE_PARAMETER = "instance";
        private final Class<?> target;
        private final Collection<AutowiredElement> autowiredElements;

        @Nullable
        private final AutowireCandidateResolver candidateResolver;

        AotContribution(Class<?> target, Collection<AutowiredElement> autowiredElements, @Nullable AutowireCandidateResolver candidateResolver) {
            this.target = target;
            this.autowiredElements = autowiredElements;
            this.candidateResolver = candidateResolver;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            GeneratedClass generatedClass = generationContext.getGeneratedClasses().addForFeatureComponent("Autowiring", this.target, type -> {
                type.addJavadoc("Autowiring for {@link $T}.", this.target);
                type.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
            });
            GeneratedMethod generateMethod = generatedClass.getMethods().add("apply", method -> {
                method.addJavadoc("Apply the autowiring.", new Object[0]);
                method.addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC);
                method.addParameter(RegisteredBean.class, REGISTERED_BEAN_PARAMETER, new javax.lang.model.element.Modifier[0]);
                method.addParameter(this.target, INSTANCE_PARAMETER, new javax.lang.model.element.Modifier[0]);
                method.returns(this.target);
                method.addCode(generateMethodCode(generatedClass.getName(), generationContext.getRuntimeHints()));
            });
            beanRegistrationCode.addInstancePostProcessor(generateMethod.toMethodReference());
            if (this.candidateResolver != null) {
                registerHints(generationContext.getRuntimeHints());
            }
        }

        private CodeBlock generateMethodCode(ClassName targetClassName, RuntimeHints hints) {
            CodeBlock.Builder code = CodeBlock.builder();
            for (AutowiredElement autowiredElement : this.autowiredElements) {
                code.addStatement(generateMethodStatementForElement(targetClassName, autowiredElement, hints));
            }
            code.addStatement("return $L", INSTANCE_PARAMETER);
            return code.build();
        }

        private CodeBlock generateMethodStatementForElement(ClassName targetClassName, AutowiredElement autowiredElement, RuntimeHints hints) {
            Member member = autowiredElement.getMember();
            boolean required = autowiredElement.required;
            if (member instanceof Field) {
                Field field = (Field) member;
                return generateMethodStatementForField(targetClassName, field, required, hints);
            }
            if (member instanceof Method) {
                Method method = (Method) member;
                return generateMethodStatementForMethod(targetClassName, method, required, hints);
            }
            throw new IllegalStateException("Unsupported member type " + member.getClass().getName());
        }

        private CodeBlock generateMethodStatementForField(ClassName targetClassName, Field field, boolean required, RuntimeHints hints) {
            hints.reflection().registerField(field);
            Object[] objArr = new Object[3];
            objArr[0] = AutowiredFieldValueResolver.class;
            objArr[1] = !required ? "forField" : "forRequiredField";
            objArr[2] = field.getName();
            CodeBlock resolver = CodeBlock.of("$T.$L($S)", objArr);
            AccessControl accessControl = AccessControl.forMember(field);
            if (!accessControl.isAccessibleFrom(targetClassName)) {
                return CodeBlock.of("$L.resolveAndSet($L, $L)", resolver, REGISTERED_BEAN_PARAMETER, INSTANCE_PARAMETER);
            }
            return CodeBlock.of("$L.$L = $L.resolve($L)", INSTANCE_PARAMETER, field.getName(), resolver, REGISTERED_BEAN_PARAMETER);
        }

        private CodeBlock generateMethodStatementForMethod(ClassName targetClassName, Method method, boolean required, RuntimeHints hints) {
            CodeBlock.Builder code = CodeBlock.builder();
            Object[] objArr = new Object[2];
            objArr[0] = AutowiredMethodArgumentsResolver.class;
            objArr[1] = !required ? "forMethod" : "forRequiredMethod";
            code.add("$T.$L", objArr);
            code.add("($S", method.getName());
            if (method.getParameterCount() > 0) {
                code.add(", $L", generateParameterTypesCode(method.getParameterTypes()));
            }
            code.add(")", new Object[0]);
            AccessControl accessControl = AccessControl.forMember(method);
            if (!accessControl.isAccessibleFrom(targetClassName)) {
                hints.reflection().registerMethod(method, ExecutableMode.INVOKE);
                code.add(".resolveAndInvoke($L, $L)", REGISTERED_BEAN_PARAMETER, INSTANCE_PARAMETER);
            } else {
                hints.reflection().registerMethod(method, ExecutableMode.INTROSPECT);
                CodeBlock arguments = new AutowiredArgumentsCodeGenerator(this.target, method).generateCode(method.getParameterTypes());
                CodeBlock injectionCode = CodeBlock.of("args -> $L.$L($L)", INSTANCE_PARAMETER, method.getName(), arguments);
                code.add(".resolve($L, $L)", REGISTERED_BEAN_PARAMETER, injectionCode);
            }
            return code.build();
        }

        private CodeBlock generateParameterTypesCode(Class<?>[] parameterTypes) {
            return CodeBlock.join(Arrays.stream(parameterTypes).map(parameterType -> {
                return CodeBlock.of("$T.class", parameterType);
            }).toList(), ", ");
        }

        private void registerHints(RuntimeHints runtimeHints) {
            this.autowiredElements.forEach(autowiredElement -> {
                boolean required = autowiredElement.required;
                Member member = autowiredElement.getMember();
                if (member instanceof Field) {
                    Field field = (Field) member;
                    DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(field, required);
                    registerProxyIfNecessary(runtimeHints, dependencyDescriptor);
                }
                if (member instanceof Method) {
                    Method method = (Method) member;
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        MethodParameter methodParam = new MethodParameter(method, i);
                        DependencyDescriptor dependencyDescriptor2 = new DependencyDescriptor(methodParam, required);
                        registerProxyIfNecessary(runtimeHints, dependencyDescriptor2);
                    }
                }
            });
        }

        private void registerProxyIfNecessary(RuntimeHints runtimeHints, DependencyDescriptor dependencyDescriptor) {
            Class<?> proxyClass;
            if (this.candidateResolver != null && (proxyClass = this.candidateResolver.getLazyResolutionProxyClass(dependencyDescriptor, null)) != null) {
                ClassHintUtils.registerProxyIfNecessary(proxyClass, runtimeHints);
            }
        }
    }
}
