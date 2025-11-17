package org.springframework.web.context.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/AnnotationConfigWebApplicationContext.class */
public class AnnotationConfigWebApplicationContext extends AbstractRefreshableWebApplicationContext implements AnnotationConfigRegistry {

    @Nullable
    private BeanNameGenerator beanNameGenerator;

    @Nullable
    private ScopeMetadataResolver scopeMetadataResolver;
    private final Set<Class<?>> componentClasses = new LinkedHashSet();
    private final Set<String> basePackages = new LinkedHashSet();

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    @Nullable
    protected BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver;
    }

    @Nullable
    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return this.scopeMetadataResolver;
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public void register(Class<?>... componentClasses) {
        Assert.notEmpty(componentClasses, "At least one component class must be specified");
        Collections.addAll(this.componentClasses, componentClasses);
    }

    @Override // org.springframework.context.annotation.AnnotationConfigRegistry
    public void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Collections.addAll(this.basePackages, basePackages);
    }

    @Override // org.springframework.context.support.AbstractRefreshableApplicationContext
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);
        BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
            beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
        }
        ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
        if (scopeMetadataResolver != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }
        if (!this.componentClasses.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Registering component classes: [" + StringUtils.collectionToCommaDelimitedString(this.componentClasses) + "]");
            }
            reader.register(ClassUtils.toClassArray(this.componentClasses));
        }
        if (!this.basePackages.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Scanning base packages: [" + StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
            }
            scanner.scan(StringUtils.toStringArray(this.basePackages));
        }
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                try {
                    Class<?> clazz = ClassUtils.forName(configLocation, getClassLoader());
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Registering [" + configLocation + "]");
                    }
                    reader.register(clazz);
                } catch (ClassNotFoundException ex) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Could not load class for config location [" + configLocation + "] - trying package scan. " + ex);
                    }
                    int count = scanner.scan(configLocation);
                    if (count == 0 && this.logger.isDebugEnabled()) {
                        this.logger.debug("No component classes found for specified class/package [" + configLocation + "]");
                    }
                }
            }
        }
    }

    protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        return new AnnotatedBeanDefinitionReader(beanFactory, getEnvironment());
    }

    protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        return new ClassPathBeanDefinitionScanner(beanFactory, true, getEnvironment());
    }
}
