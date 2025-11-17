package org.springframework.boot.autoconfigure;

import java.util.function.Supplier;
import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer.class */
class SharedMetadataReaderFactoryContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered, BeanRegistrationExcludeFilter {
    public static final String BEAN_NAME = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";

    SharedMetadataReaderFactoryContextInitializer() {
    }

    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (AotDetector.useGeneratedArtifacts()) {
            return;
        }
        BeanFactoryPostProcessor postProcessor = new CachingMetadataReaderFactoryPostProcessor(applicationContext);
        applicationContext.addBeanFactoryPostProcessor(postProcessor);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter
    public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
        return BEAN_NAME.equals(registeredBean.getBeanName());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer$CachingMetadataReaderFactoryPostProcessor.class */
    static class CachingMetadataReaderFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
        private final ConfigurableApplicationContext context;

        CachingMetadataReaderFactoryPostProcessor(ConfigurableApplicationContext context) {
            this.context = context;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return Integer.MIN_VALUE;
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor, org.springframework.beans.factory.config.BeanFactoryPostProcessor
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override // org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            register(registry);
            configureConfigurationClassPostProcessor(registry);
        }

        private void register(BeanDefinitionRegistry registry) {
            if (!registry.containsBeanDefinition(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME)) {
                BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(SharedMetadataReaderFactoryBean.class, SharedMetadataReaderFactoryBean::new).getBeanDefinition();
                registry.registerBeanDefinition(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME, definition);
            }
        }

        private void configureConfigurationClassPostProcessor(BeanDefinitionRegistry registry) {
            try {
                configureConfigurationClassPostProcessor(registry.getBeanDefinition(AnnotationConfigUtils.CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
            } catch (NoSuchBeanDefinitionException e) {
            }
        }

        private void configureConfigurationClassPostProcessor(BeanDefinition definition) {
            if (definition instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) definition;
                configureConfigurationClassPostProcessor(abstractBeanDefinition);
            } else {
                configureConfigurationClassPostProcessor(definition.getPropertyValues());
            }
        }

        private void configureConfigurationClassPostProcessor(AbstractBeanDefinition definition) {
            Supplier<?> instanceSupplier = definition.getInstanceSupplier();
            if (instanceSupplier != null) {
                definition.setInstanceSupplier(new ConfigurationClassPostProcessorCustomizingSupplier(this.context, instanceSupplier));
            } else {
                configureConfigurationClassPostProcessor(definition.getPropertyValues());
            }
        }

        private void configureConfigurationClassPostProcessor(MutablePropertyValues propertyValues) {
            propertyValues.add("metadataReaderFactory", new RuntimeBeanReference(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer$ConfigurationClassPostProcessorCustomizingSupplier.class */
    public static class ConfigurationClassPostProcessorCustomizingSupplier implements Supplier<Object> {
        private final ConfigurableApplicationContext context;
        private final Supplier<?> instanceSupplier;

        ConfigurationClassPostProcessorCustomizingSupplier(ConfigurableApplicationContext context, Supplier<?> instanceSupplier) {
            this.context = context;
            this.instanceSupplier = instanceSupplier;
        }

        @Override // java.util.function.Supplier
        public Object get() {
            Object instance = this.instanceSupplier.get();
            if (instance instanceof ConfigurationClassPostProcessor) {
                ConfigurationClassPostProcessor postProcessor = (ConfigurationClassPostProcessor) instance;
                configureConfigurationClassPostProcessor(postProcessor);
            }
            return instance;
        }

        private void configureConfigurationClassPostProcessor(ConfigurationClassPostProcessor instance) {
            instance.setMetadataReaderFactory((MetadataReaderFactory) this.context.getBean(SharedMetadataReaderFactoryContextInitializer.BEAN_NAME, MetadataReaderFactory.class));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/SharedMetadataReaderFactoryContextInitializer$SharedMetadataReaderFactoryBean.class */
    public static class SharedMetadataReaderFactoryBean implements FactoryBean<ConcurrentReferenceCachingMetadataReaderFactory>, BeanClassLoaderAware, ApplicationListener<ContextRefreshedEvent> {
        private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

        SharedMetadataReaderFactoryBean() {
        }

        @Override // org.springframework.beans.factory.BeanClassLoaderAware
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(classLoader);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.beans.factory.FactoryBean
        public ConcurrentReferenceCachingMetadataReaderFactory getObject() throws Exception {
            return this.metadataReaderFactory;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public Class<?> getObjectType() {
            return CachingMetadataReaderFactory.class;
        }

        @Override // org.springframework.beans.factory.FactoryBean
        public boolean isSingleton() {
            return true;
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextRefreshedEvent event) {
            this.metadataReaderFactory.clearCache();
        }
    }
}
