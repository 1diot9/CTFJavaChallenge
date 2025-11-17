package org.springframework.context.support;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/GenericGroovyApplicationContext.class */
public class GenericGroovyApplicationContext extends GenericApplicationContext implements GroovyObject {
    private final GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(this);
    private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());

    public GenericGroovyApplicationContext() {
    }

    public GenericGroovyApplicationContext(Resource... resources) {
        load(resources);
        refresh();
    }

    public GenericGroovyApplicationContext(String... resourceLocations) {
        load(resourceLocations);
        refresh();
    }

    public GenericGroovyApplicationContext(Class<?> relativeClass, String... resourceNames) {
        load(relativeClass, resourceNames);
        refresh();
    }

    public final GroovyBeanDefinitionReader getReader() {
        return this.reader;
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ConfigurableApplicationContext
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(getEnvironment());
    }

    public void load(Resource... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    public void load(String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; i++) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        load(resources);
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    public Object invokeMethod(String name, Object args) {
        return this.metaClass.invokeMethod(this, name, args);
    }

    public void setProperty(String property, Object newValue) {
        if (newValue instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) newValue;
            registerBeanDefinition(property, beanDefinition);
        } else {
            this.metaClass.setProperty(this, property, newValue);
        }
    }

    @Nullable
    public Object getProperty(String property) {
        if (containsBean(property)) {
            return getBean(property);
        }
        if (this.contextWrapper.isReadableProperty(property)) {
            return this.contextWrapper.getPropertyValue(property);
        }
        throw new NoSuchBeanDefinitionException(property);
    }
}
