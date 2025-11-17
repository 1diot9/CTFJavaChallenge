package org.springframework.context.annotation;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aot.generate.AccessControl;
import org.springframework.aot.generate.GeneratedClass;
import org.springframework.aot.generate.GeneratedMethod;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.support.ClassHintUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ResourceElementResolver;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.CodeBlock;
import org.springframework.jndi.support.SimpleJndiBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor.class */
public class CommonAnnotationBeanPostProcessor extends InitDestroyAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware, Serializable {
    private static final boolean jndiPresent = ClassUtils.isPresent("javax.naming.InitialContext", CommonAnnotationBeanPostProcessor.class.getClassLoader());
    private static final Set<Class<? extends Annotation>> resourceAnnotationTypes = new LinkedHashSet(4);

    @Nullable
    private static final Class<? extends Annotation> jakartaResourceType = loadAnnotationType("jakarta.annotation.Resource");

    @Nullable
    private static final Class<? extends Annotation> javaxResourceType;

    @Nullable
    private static final Class<? extends Annotation> ejbAnnotationType;

    @Nullable
    private transient BeanFactory jndiFactory;

    @Nullable
    private transient BeanFactory resourceFactory;

    @Nullable
    private transient BeanFactory beanFactory;

    @Nullable
    private transient StringValueResolver embeddedValueResolver;
    private final Set<String> ignoredResourceTypes = new HashSet(1);
    private boolean fallbackToDefaultTypeMatch = true;
    private boolean alwaysUseJndiLookup = false;
    private final transient Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap(256);

    static {
        if (jakartaResourceType != null) {
            resourceAnnotationTypes.add(jakartaResourceType);
        }
        javaxResourceType = loadAnnotationType("javax.annotation.Resource");
        if (javaxResourceType != null) {
            resourceAnnotationTypes.add(javaxResourceType);
        }
        ejbAnnotationType = loadAnnotationType("jakarta.ejb.EJB");
        if (ejbAnnotationType != null) {
            resourceAnnotationTypes.add(ejbAnnotationType);
        }
    }

    public CommonAnnotationBeanPostProcessor() {
        setOrder(2147483644);
        addInitAnnotationType(loadAnnotationType("jakarta.annotation.PostConstruct"));
        addDestroyAnnotationType(loadAnnotationType("jakarta.annotation.PreDestroy"));
        addInitAnnotationType(loadAnnotationType("javax.annotation.PostConstruct"));
        addDestroyAnnotationType(loadAnnotationType("javax.annotation.PreDestroy"));
        if (jndiPresent) {
            this.jndiFactory = new SimpleJndiBeanFactory();
        }
    }

    public void ignoreResourceType(String resourceType) {
        Assert.notNull(resourceType, "Ignored resource type must not be null");
        this.ignoredResourceTypes.add(resourceType);
    }

    public void setFallbackToDefaultTypeMatch(boolean fallbackToDefaultTypeMatch) {
        this.fallbackToDefaultTypeMatch = fallbackToDefaultTypeMatch;
    }

    public void setAlwaysUseJndiLookup(boolean alwaysUseJndiLookup) {
        this.alwaysUseJndiLookup = alwaysUseJndiLookup;
    }

    public void setJndiFactory(BeanFactory jndiFactory) {
        Assert.notNull(jndiFactory, "BeanFactory must not be null");
        this.jndiFactory = jndiFactory;
    }

    public void setResourceFactory(BeanFactory resourceFactory) {
        Assert.notNull(resourceFactory, "BeanFactory must not be null");
        this.resourceFactory = resourceFactory;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        if (this.resourceFactory == null) {
            this.resourceFactory = beanFactory;
        }
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            this.embeddedValueResolver = new EmbeddedValueResolver(configurableBeanFactory);
        }
    }

    @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor, org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
        InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override // org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor, org.springframework.beans.factory.aot.BeanRegistrationAotProcessor
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        BeanRegistrationAotContribution parentAotContribution = super.processAheadOfTime(registeredBean);
        Class<?> beanClass = registeredBean.getBeanClass();
        String beanName = registeredBean.getBeanName();
        RootBeanDefinition beanDefinition = registeredBean.getMergedBeanDefinition();
        InjectionMetadata metadata = findResourceMetadata(beanName, beanClass, beanDefinition.getPropertyValues());
        Collection<LookupElement> injectedElements = getInjectedElements(metadata, beanDefinition.getPropertyValues());
        if (!ObjectUtils.isEmpty(injectedElements)) {
            AotContribution aotContribution = new AotContribution(beanClass, injectedElements, getAutowireCandidateResolver(registeredBean));
            return BeanRegistrationAotContribution.concat(parentAotContribution, aotContribution);
        }
        return parentAotContribution;
    }

    @Nullable
    private AutowireCandidateResolver getAutowireCandidateResolver(RegisteredBean registeredBean) {
        ConfigurableListableBeanFactory beanFactory = registeredBean.getBeanFactory();
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory lbf = (DefaultListableBeanFactory) beanFactory;
            return lbf.getAutowireCandidateResolver();
        }
        return null;
    }

    private Collection<LookupElement> getInjectedElements(InjectionMetadata metadata, PropertyValues propertyValues) {
        return metadata.getInjectedElements(propertyValues);
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void resetBeanDefinition(String beanName) {
        this.injectionMetadataCache.remove(beanName);
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findResourceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
            return pvs;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
    }

    public void processInjection(Object bean) throws BeanCreationException {
        Class<?> clazz = bean.getClass();
        InjectionMetadata metadata = findResourceMetadata(clazz.getName(), clazz, null);
        try {
            metadata.inject(bean, null, null);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex2) {
            throw new BeanCreationException("Injection of resource dependencies failed for class [" + clazz + "]", ex2);
        }
    }

    private InjectionMetadata findResourceMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildResourceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildResourceMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, resourceAnnotationTypes)) {
            return InjectionMetadata.EMPTY;
        }
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;
        do {
            List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                if (ejbAnnotationType != null && field.isAnnotationPresent(ejbAnnotationType)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@EJB annotation is not supported on static fields");
                    }
                    currElements.add(new EjbRefElement(field, field, null));
                    return;
                }
                if (jakartaResourceType != null && field.isAnnotationPresent(jakartaResourceType)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@Resource annotation is not supported on static fields");
                    }
                    if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                        currElements.add(new ResourceElement(field, field, null));
                        return;
                    }
                    return;
                }
                if (javaxResourceType != null && field.isAnnotationPresent(javaxResourceType)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@Resource annotation is not supported on static fields");
                    }
                    if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                        currElements.add(new LegacyResourceElement(field, field, null));
                    }
                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                if (ejbAnnotationType != null && bridgedMethod.isAnnotationPresent(ejbAnnotationType)) {
                    if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@EJB annotation is not supported on static methods");
                        }
                        if (method.getParameterCount() != 1) {
                            throw new IllegalStateException("@EJB annotation requires a single-arg method: " + method);
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new EjbRefElement(method, bridgedMethod, pd));
                        return;
                    }
                    return;
                }
                if (jakartaResourceType != null && bridgedMethod.isAnnotationPresent(jakartaResourceType)) {
                    if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@Resource annotation is not supported on static methods");
                        }
                        Class<?>[] paramTypes = method.getParameterTypes();
                        if (paramTypes.length != 1) {
                            throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                        }
                        if (!this.ignoredResourceTypes.contains(paramTypes[0].getName())) {
                            PropertyDescriptor pd2 = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                            currElements.add(new ResourceElement(method, bridgedMethod, pd2));
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (javaxResourceType != null && bridgedMethod.isAnnotationPresent(javaxResourceType) && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("@Resource annotation is not supported on static methods");
                    }
                    Class<?>[] paramTypes2 = method.getParameterTypes();
                    if (paramTypes2.length != 1) {
                        throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                    }
                    if (!this.ignoredResourceTypes.contains(paramTypes2[0].getName())) {
                        PropertyDescriptor pd3 = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new LegacyResourceElement(method, bridgedMethod, pd3));
                    }
                }
            });
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
            if (targetClass == null) {
                break;
            }
        } while (targetClass != Object.class);
        return InjectionMetadata.forElements(elements, clazz);
    }

    protected Object buildLazyResourceProxy(final LookupElement element, @Nullable final String requestingBeanName) {
        ClassLoader classLoader;
        TargetSource ts = new TargetSource() { // from class: org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.1
            @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
            public Class<?> getTargetClass() {
                return element.lookupType;
            }

            @Override // org.springframework.aop.TargetSource
            public Object getTarget() {
                return CommonAnnotationBeanPostProcessor.this.getResource(element, requestingBeanName);
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        if (element.lookupType.isInterface()) {
            pf.addInterface(element.lookupType);
        }
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            classLoader = configurableBeanFactory.getBeanClassLoader();
        } else {
            classLoader = null;
        }
        ClassLoader classLoader2 = classLoader;
        return pf.getProxy(classLoader2);
    }

    protected Object getResource(LookupElement element, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        String jndiName = null;
        if (StringUtils.hasLength(element.mappedName)) {
            jndiName = element.mappedName;
        } else if (this.alwaysUseJndiLookup) {
            jndiName = element.name;
        }
        if (jndiName != null) {
            if (this.jndiFactory == null) {
                throw new NoSuchBeanDefinitionException(element.lookupType, "No JNDI factory configured - specify the 'jndiFactory' property");
            }
            return this.jndiFactory.getBean(jndiName, element.lookupType);
        }
        if (this.resourceFactory == null) {
            throw new NoSuchBeanDefinitionException(element.lookupType, "No resource factory configured - specify the 'resourceFactory' property");
        }
        return autowireResource(this.resourceFactory, element, requestingBeanName);
    }

    protected Object autowireResource(BeanFactory factory, LookupElement element, @Nullable String requestingBeanName) throws NoSuchBeanDefinitionException {
        Object resource;
        Set<String> autowiredBeanNames;
        String name = element.name;
        if (factory instanceof AutowireCapableBeanFactory) {
            AutowireCapableBeanFactory autowireCapableBeanFactory = (AutowireCapableBeanFactory) factory;
            if (this.fallbackToDefaultTypeMatch && element.isDefaultName && !factory.containsBean(name)) {
                autowiredBeanNames = new LinkedHashSet();
                resource = autowireCapableBeanFactory.resolveDependency(element.getDependencyDescriptor(), requestingBeanName, autowiredBeanNames, null);
                if (resource == null) {
                    throw new NoSuchBeanDefinitionException(element.getLookupType(), "No resolvable resource object");
                }
            } else {
                resource = autowireCapableBeanFactory.resolveBeanByName(name, element.getDependencyDescriptor());
                autowiredBeanNames = Collections.singleton(name);
            }
        } else {
            resource = factory.getBean(name, element.lookupType);
            autowiredBeanNames = Collections.singleton(name);
        }
        if (factory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) factory;
            for (String autowiredBeanName : autowiredBeanNames) {
                if (requestingBeanName != null && configurableBeanFactory.containsBean(autowiredBeanName)) {
                    configurableBeanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
                }
            }
        }
        return resource;
    }

    @Nullable
    private static Class<? extends Annotation> loadAnnotationType(String name) {
        try {
            return ClassUtils.forName(name, CommonAnnotationBeanPostProcessor.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor$LookupElement.class */
    public static abstract class LookupElement extends InjectionMetadata.InjectedElement {
        protected String name;
        protected boolean isDefaultName;
        protected Class<?> lookupType;

        @Nullable
        protected String mappedName;

        public LookupElement(Member member, @Nullable PropertyDescriptor pd) {
            super(member, pd);
            this.name = "";
            this.isDefaultName = false;
            this.lookupType = Object.class;
        }

        public final String getName() {
            return this.name;
        }

        public final Class<?> getLookupType() {
            return this.lookupType;
        }

        public final DependencyDescriptor getDependencyDescriptor() {
            if (this.isField) {
                return new ResourceElementResolver.LookupDependencyDescriptor((Field) this.member, this.lookupType, isLazyLookup());
            }
            return new ResourceElementResolver.LookupDependencyDescriptor((Method) this.member, this.lookupType, isLazyLookup());
        }

        boolean isLazyLookup() {
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor$ResourceElement.class */
    private class ResourceElement extends LookupElement {
        private final boolean lazyLookup;

        public ResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
            super(member, pd);
            Resource resource = (Resource) ae.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Class<?> resourceType = resource.type();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if ((this.member instanceof Method) && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = StringUtils.uncapitalizeAsProperty(resourceName.substring(3));
                }
            } else if (CommonAnnotationBeanPostProcessor.this.embeddedValueResolver != null) {
                resourceName = CommonAnnotationBeanPostProcessor.this.embeddedValueResolver.resolveStringValue(resourceName);
            }
            if (Object.class != resourceType) {
                checkResourceType(resourceType);
            } else {
                resourceType = getResourceType();
            }
            this.name = resourceName != null ? resourceName : "";
            this.lookupType = resourceType;
            String lookupValue = resource.lookup();
            this.mappedName = StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName();
            Lazy lazy = (Lazy) ae.getAnnotation(Lazy.class);
            this.lazyLookup = lazy != null && lazy.value();
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return this.lazyLookup ? CommonAnnotationBeanPostProcessor.this.buildLazyResourceProxy(this, requestingBeanName) : CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }

        @Override // org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.LookupElement
        boolean isLazyLookup() {
            return this.lazyLookup;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor$LegacyResourceElement.class */
    private class LegacyResourceElement extends LookupElement {
        private final boolean lazyLookup;

        public LegacyResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
            super(member, pd);
            javax.annotation.Resource resource = ae.getAnnotation(javax.annotation.Resource.class);
            String resourceName = resource.name();
            Class<?> resourceType = resource.type();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if ((this.member instanceof Method) && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = StringUtils.uncapitalizeAsProperty(resourceName.substring(3));
                }
            } else if (CommonAnnotationBeanPostProcessor.this.embeddedValueResolver != null) {
                resourceName = CommonAnnotationBeanPostProcessor.this.embeddedValueResolver.resolveStringValue(resourceName);
            }
            if (Object.class != resourceType) {
                checkResourceType(resourceType);
            } else {
                resourceType = getResourceType();
            }
            this.name = resourceName != null ? resourceName : "";
            this.lookupType = resourceType;
            String lookupValue = resource.lookup();
            this.mappedName = StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName();
            Lazy lazy = (Lazy) ae.getAnnotation(Lazy.class);
            this.lazyLookup = lazy != null && lazy.value();
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return this.lazyLookup ? CommonAnnotationBeanPostProcessor.this.buildLazyResourceProxy(this, requestingBeanName) : CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }

        @Override // org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.LookupElement
        boolean isLazyLookup() {
            return this.lazyLookup;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor$EjbRefElement.class */
    private class EjbRefElement extends LookupElement {
        private final String beanName;

        public EjbRefElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
            super(member, pd);
            EJB resource = ae.getAnnotation(EJB.class);
            String resourceBeanName = resource.beanName();
            String resourceName = resource.name();
            this.isDefaultName = !StringUtils.hasLength(resourceName);
            if (this.isDefaultName) {
                resourceName = this.member.getName();
                if ((this.member instanceof Method) && resourceName.startsWith("set") && resourceName.length() > 3) {
                    resourceName = StringUtils.uncapitalizeAsProperty(resourceName.substring(3));
                }
            }
            Class<?> resourceType = resource.beanInterface();
            if (Object.class != resourceType) {
                checkResourceType(resourceType);
            } else {
                resourceType = getResourceType();
            }
            this.beanName = resourceBeanName;
            this.name = resourceName;
            this.lookupType = resourceType;
            this.mappedName = resource.mappedName();
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            if (StringUtils.hasLength(this.beanName)) {
                if (CommonAnnotationBeanPostProcessor.this.beanFactory != null && CommonAnnotationBeanPostProcessor.this.beanFactory.containsBean(this.beanName)) {
                    Object bean = CommonAnnotationBeanPostProcessor.this.beanFactory.getBean(this.beanName, this.lookupType);
                    if (requestingBeanName != null) {
                        BeanFactory beanFactory = CommonAnnotationBeanPostProcessor.this.beanFactory;
                        if (beanFactory instanceof ConfigurableBeanFactory) {
                            ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
                            configurableBeanFactory.registerDependentBean(this.beanName, requestingBeanName);
                        }
                    }
                    return bean;
                }
                if (this.isDefaultName && !StringUtils.hasLength(this.mappedName)) {
                    throw new NoSuchBeanDefinitionException(this.beanName, "Cannot resolve 'beanName' in local BeanFactory. Consider specifying a general 'name' value instead.");
                }
            }
            return CommonAnnotationBeanPostProcessor.this.getResource(this, requestingBeanName);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/CommonAnnotationBeanPostProcessor$AotContribution.class */
    private static class AotContribution implements BeanRegistrationAotContribution {
        private static final String REGISTERED_BEAN_PARAMETER = "registeredBean";
        private static final String INSTANCE_PARAMETER = "instance";
        private final Class<?> target;
        private final Collection<LookupElement> lookupElements;

        @Nullable
        private final AutowireCandidateResolver candidateResolver;

        AotContribution(Class<?> target, Collection<LookupElement> lookupElements, @Nullable AutowireCandidateResolver candidateResolver) {
            this.target = target;
            this.lookupElements = lookupElements;
            this.candidateResolver = candidateResolver;
        }

        @Override // org.springframework.beans.factory.aot.BeanRegistrationAotContribution
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            GeneratedClass generatedClass = generationContext.getGeneratedClasses().addForFeatureComponent("ResourceAutowiring", this.target, type -> {
                type.addJavadoc("Resource autowiring for {@link $T}.", this.target);
                type.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
            });
            GeneratedMethod generateMethod = generatedClass.getMethods().add("apply", method -> {
                method.addJavadoc("Apply resource autowiring.", new Object[0]);
                method.addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC);
                method.addParameter(RegisteredBean.class, REGISTERED_BEAN_PARAMETER, new javax.lang.model.element.Modifier[0]);
                method.addParameter(this.target, INSTANCE_PARAMETER, new javax.lang.model.element.Modifier[0]);
                method.returns(this.target);
                method.addCode(generateMethodCode(generatedClass.getName(), generationContext.getRuntimeHints()));
            });
            beanRegistrationCode.addInstancePostProcessor(generateMethod.toMethodReference());
            registerHints(generationContext.getRuntimeHints());
        }

        private CodeBlock generateMethodCode(ClassName targetClassName, RuntimeHints hints) {
            CodeBlock.Builder code = CodeBlock.builder();
            for (LookupElement lookupElement : this.lookupElements) {
                code.addStatement(generateMethodStatementForElement(targetClassName, lookupElement, hints));
            }
            code.addStatement("return $L", INSTANCE_PARAMETER);
            return code.build();
        }

        private CodeBlock generateMethodStatementForElement(ClassName targetClassName, LookupElement lookupElement, RuntimeHints hints) {
            Member member = lookupElement.getMember();
            if (member instanceof Field) {
                Field field = (Field) member;
                return generateMethodStatementForField(targetClassName, field, lookupElement, hints);
            }
            if (member instanceof Method) {
                Method method = (Method) member;
                return generateMethodStatementForMethod(targetClassName, method, lookupElement, hints);
            }
            throw new IllegalStateException("Unsupported member type " + member.getClass().getName());
        }

        private CodeBlock generateMethodStatementForField(ClassName targetClassName, Field field, LookupElement lookupElement, RuntimeHints hints) {
            hints.reflection().registerField(field);
            CodeBlock resolver = generateFieldResolverCode(field, lookupElement);
            AccessControl accessControl = AccessControl.forMember(field);
            if (!accessControl.isAccessibleFrom(targetClassName)) {
                return CodeBlock.of("$L.resolveAndSet($L, $L)", resolver, REGISTERED_BEAN_PARAMETER, INSTANCE_PARAMETER);
            }
            return CodeBlock.of("$L.$L = $L.resolve($L)", INSTANCE_PARAMETER, field.getName(), resolver, REGISTERED_BEAN_PARAMETER);
        }

        private CodeBlock generateFieldResolverCode(Field field, LookupElement lookupElement) {
            if (lookupElement.isDefaultName) {
                return CodeBlock.of("$T.$L($S)", ResourceElementResolver.class, "forField", field.getName());
            }
            return CodeBlock.of("$T.$L($S, $S)", ResourceElementResolver.class, "forField", field.getName(), lookupElement.getName());
        }

        private CodeBlock generateMethodStatementForMethod(ClassName targetClassName, Method method, LookupElement lookupElement, RuntimeHints hints) {
            CodeBlock resolver = generateMethodResolverCode(method, lookupElement);
            AccessControl accessControl = AccessControl.forMember(method);
            if (!accessControl.isAccessibleFrom(targetClassName)) {
                hints.reflection().registerMethod(method, ExecutableMode.INVOKE);
                return CodeBlock.of("$L.resolveAndSet($L, $L)", resolver, REGISTERED_BEAN_PARAMETER, INSTANCE_PARAMETER);
            }
            hints.reflection().registerMethod(method, ExecutableMode.INTROSPECT);
            return CodeBlock.of("$L.$L($L.resolve($L))", INSTANCE_PARAMETER, method.getName(), resolver, REGISTERED_BEAN_PARAMETER);
        }

        private CodeBlock generateMethodResolverCode(Method method, LookupElement lookupElement) {
            if (lookupElement.isDefaultName) {
                return CodeBlock.of("$T.$L($S, $T.class)", ResourceElementResolver.class, "forMethod", method.getName(), lookupElement.getLookupType());
            }
            return CodeBlock.of("$T.$L($S, $T.class, $S)", ResourceElementResolver.class, "forMethod", method.getName(), lookupElement.getLookupType(), lookupElement.getName());
        }

        private void registerHints(RuntimeHints runtimeHints) {
            this.lookupElements.forEach(lookupElement -> {
                registerProxyIfNecessary(runtimeHints, lookupElement.getDependencyDescriptor());
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
