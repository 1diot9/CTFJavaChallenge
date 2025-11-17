package org.springframework.beans.factory.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/AbstractBeanDefinitionReader.class */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
    private final BeanDefinitionRegistry registry;

    @Nullable
    private ResourceLoader resourceLoader;

    @Nullable
    private ClassLoader beanClassLoader;
    private Environment environment;
    protected final Log logger = LogFactory.getLog(getClass());
    private BeanNameGenerator beanNameGenerator = DefaultBeanNameGenerator.INSTANCE;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        BeanDefinitionRegistry beanDefinitionRegistry = this.registry;
        if (beanDefinitionRegistry instanceof ResourceLoader) {
            ResourceLoader _resourceLoader = (ResourceLoader) beanDefinitionRegistry;
            this.resourceLoader = _resourceLoader;
        } else {
            this.resourceLoader = new PathMatchingResourcePatternResolver();
        }
        BeanDefinitionRegistry beanDefinitionRegistry2 = this.registry;
        if (beanDefinitionRegistry2 instanceof EnvironmentCapable) {
            EnvironmentCapable environmentCapable = (EnvironmentCapable) beanDefinitionRegistry2;
            this.environment = environmentCapable.getEnvironment();
        } else {
            this.environment = new StandardEnvironment();
        }
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    @Nullable
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

    @Override // org.springframework.core.env.EnvironmentCapable
    public Environment getEnvironment() {
        return this.environment;
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : DefaultBeanNameGenerator.INSTANCE;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int count = 0;
        for (Resource resource : resources) {
            count += loadBeanDefinitions(resource);
        }
        return count;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(location, null);
    }

    public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
        ResourceLoader resourceLoader = getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
        }
        if (resourceLoader instanceof ResourcePatternResolver) {
            ResourcePatternResolver resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
            try {
                Resource[] resources = resourcePatternResolver.getResources(location);
                int count = loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }
                return count;
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        Resource resource = resourceLoader.getResource(location);
        int count2 = loadBeanDefinitions(resource);
        if (actualResources != null) {
            actualResources.add(resource);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loaded " + count2 + " bean definitions from location [" + location + "]");
        }
        return count2;
    }

    @Override // org.springframework.beans.factory.support.BeanDefinitionReader
    public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int count = 0;
        for (String location : locations) {
            count += loadBeanDefinitions(location);
        }
        return count;
    }
}
