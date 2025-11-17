package org.springframework.context.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.support.ClassHintUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/GenericApplicationContext.class */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
    private final DefaultListableBeanFactory beanFactory;

    @Nullable
    private ResourceLoader resourceLoader;
    private boolean customClassLoader;
    private final AtomicBoolean refreshed;

    public GenericApplicationContext() {
        this.customClassLoader = false;
        this.refreshed = new AtomicBoolean();
        this.beanFactory = new DefaultListableBeanFactory();
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        this.customClassLoader = false;
        this.refreshed = new AtomicBoolean();
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public GenericApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
        this(beanFactory);
        setParent(parent);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setParent(@Nullable ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        super.setApplicationStartup(applicationStartup);
        this.beanFactory.setApplicationStartup(applicationStartup);
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.beanFactory.setAllowCircularReferences(allowCircularReferences);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.core.io.DefaultResourceLoader, org.springframework.core.io.ResourceLoader
    public Resource getResource(String location) {
        if (this.resourceLoader != null) {
            for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
                Resource resource = protocolResolver.resolve(location, this);
                if (resource != null) {
                    return resource;
                }
            }
            return this.resourceLoader.getResource(location);
        }
        return super.getResource(location);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.core.io.support.ResourcePatternResolver
    public Resource[] getResources(String locationPattern) throws IOException {
        ResourceLoader resourceLoader = this.resourceLoader;
        if (resourceLoader instanceof ResourcePatternResolver) {
            ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
            return resourcePatternResolver.getResources(locationPattern);
        }
        return super.getResources(locationPattern);
    }

    @Override // org.springframework.core.io.DefaultResourceLoader, org.springframework.context.ConfigurableApplicationContext
    public void setClassLoader(@Nullable ClassLoader classLoader) {
        super.setClassLoader(classLoader);
        this.customClassLoader = true;
    }

    @Override // org.springframework.core.io.DefaultResourceLoader, org.springframework.core.io.ResourceLoader
    @Nullable
    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null && !this.customClassLoader) {
            return this.resourceLoader.getClassLoader();
        }
        return super.getClassLoader();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected final void refreshBeanFactory() throws IllegalStateException {
        if (!this.refreshed.compareAndSet(false, true)) {
            throw new IllegalStateException("GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        }
        this.beanFactory.setSerializationId(getId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.AbstractApplicationContext
    public void cancelRefresh(Throwable ex) {
        this.beanFactory.setSerializationId(null);
        super.cancelRefresh(ex);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected final void closeBeanFactory() {
        this.beanFactory.setSerializationId(null);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ApplicationContext
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        assertBeanFactoryActive();
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean isBeanDefinitionOverridable(String beanName) {
        return this.beanFactory.isBeanDefinitionOverridable(beanName);
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionRegistry
    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    @Override // org.springframework.core.AliasRegistry
    public void registerAlias(String beanName, String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }

    @Override // org.springframework.core.AliasRegistry
    public void removeAlias(String alias) {
        this.beanFactory.removeAlias(alias);
    }

    @Override // org.springframework.core.AliasRegistry
    public boolean isAlias(String beanName) {
        return this.beanFactory.isAlias(beanName);
    }

    public void refreshForAotProcessing(RuntimeHints runtimeHints) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Preparing bean factory for AOT processing");
        }
        prepareRefresh();
        obtainFreshBeanFactory();
        prepareBeanFactory(this.beanFactory);
        postProcessBeanFactory(this.beanFactory);
        invokeBeanFactoryPostProcessors(this.beanFactory);
        this.beanFactory.freezeConfiguration();
        PostProcessorRegistrationDelegate.invokeMergedBeanDefinitionPostProcessors(this.beanFactory);
        preDetermineBeanTypes(runtimeHints);
    }

    private void preDetermineBeanTypes(RuntimeHints runtimeHints) {
        List<SmartInstantiationAwareBeanPostProcessor> bpps = PostProcessorRegistrationDelegate.loadBeanPostProcessors(this.beanFactory, SmartInstantiationAwareBeanPostProcessor.class);
        for (String beanName : this.beanFactory.getBeanDefinitionNames()) {
            Class<?> beanType = this.beanFactory.getType(beanName);
            if (beanType != null) {
                ClassHintUtils.registerProxyIfNecessary(beanType, runtimeHints);
                for (SmartInstantiationAwareBeanPostProcessor bpp : bpps) {
                    Class<?> newBeanType = bpp.determineBeanType(beanType, beanName);
                    if (newBeanType != beanType) {
                        ClassHintUtils.registerProxyIfNecessary(newBeanType, runtimeHints);
                        beanType = newBeanType;
                    }
                }
            }
        }
    }

    public <T> void registerBean(Class<T> beanClass, Object... constructorArgs) {
        registerBean((String) null, beanClass, constructorArgs);
    }

    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object... constructorArgs) {
        registerBean(beanName, beanClass, (Supplier) null, bd -> {
            for (Object arg : constructorArgs) {
                bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
            }
        });
    }

    public final <T> void registerBean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
        registerBean(null, beanClass, null, customizers);
    }

    public final <T> void registerBean(@Nullable String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
        registerBean(beanName, beanClass, null, customizers);
    }

    public final <T> void registerBean(Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
        registerBean(null, beanClass, supplier, customizers);
    }

    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
        ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition((Class<?>) beanClass);
        if (supplier != null) {
            beanDefinition.setInstanceSupplier(supplier);
        }
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(beanDefinition);
        }
        String nameToUse = beanName != null ? beanName : beanClass.getName();
        registerBeanDefinition(nameToUse, beanDefinition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/GenericApplicationContext$ClassDerivedBeanDefinition.class */
    public static class ClassDerivedBeanDefinition extends RootBeanDefinition {
        public ClassDerivedBeanDefinition(Class<?> beanClass) {
            super(beanClass);
        }

        public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
            super((RootBeanDefinition) original);
        }

        @Override // org.springframework.beans.factory.support.RootBeanDefinition
        @Nullable
        public Constructor<?>[] getPreferredConstructors() {
            Constructor<?>[] fromAttribute = super.getPreferredConstructors();
            if (fromAttribute != null) {
                return fromAttribute;
            }
            Class<?> clazz = getBeanClass();
            Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
            if (primaryCtor != null) {
                return new Constructor[]{primaryCtor};
            }
            Constructor<?>[] publicCtors = clazz.getConstructors();
            if (publicCtors.length > 0) {
                return publicCtors;
            }
            return null;
        }

        @Override // org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.factory.support.AbstractBeanDefinition
        public RootBeanDefinition cloneBeanDefinition() {
            return new ClassDerivedBeanDefinition(this);
        }
    }
}
