package org.springframework.boot.autoconfigure.data;

import java.lang.annotation.Annotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.util.Streamable;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/AbstractRepositoryConfigurationSourceSupport.class */
public abstract class AbstractRepositoryConfigurationSourceSupport implements ImportBeanDefinitionRegistrar, BeanFactoryAware, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;
    private Environment environment;

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract Class<?> getConfiguration();

    protected abstract RepositoryConfigurationExtension getRepositoryConfigurationExtension();

    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(getConfigurationSource(registry, importBeanNameGenerator), this.resourceLoader, this.environment);
        delegate.registerRepositoriesIn(registry, getRepositoryConfigurationExtension());
    }

    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeanDefinitions(importingClassMetadata, registry, null);
    }

    private AnnotationRepositoryConfigurationSource getConfigurationSource(BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationMetadata metadata = AnnotationMetadata.introspect(getConfiguration());
        return new AutoConfiguredAnnotationRepositoryConfigurationSource(metadata, getAnnotation(), this.resourceLoader, this.environment, registry, importBeanNameGenerator) { // from class: org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport.1
        };
    }

    protected Streamable<String> getBasePackages() {
        return Streamable.of(AutoConfigurationPackages.get(this.beanFactory));
    }

    protected BootstrapMode getBootstrapMode() {
        return BootstrapMode.DEFAULT;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/AbstractRepositoryConfigurationSourceSupport$AutoConfiguredAnnotationRepositoryConfigurationSource.class */
    private class AutoConfiguredAnnotationRepositoryConfigurationSource extends AnnotationRepositoryConfigurationSource {
        AutoConfiguredAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata, Class<? extends Annotation> annotation, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
            super(metadata, annotation, resourceLoader, environment, registry, generator);
        }

        public Streamable<String> getBasePackages() {
            return AbstractRepositoryConfigurationSourceSupport.this.getBasePackages();
        }

        public BootstrapMode getBootstrapMode() {
            return AbstractRepositoryConfigurationSourceSupport.this.getBootstrapMode();
        }
    }
}
