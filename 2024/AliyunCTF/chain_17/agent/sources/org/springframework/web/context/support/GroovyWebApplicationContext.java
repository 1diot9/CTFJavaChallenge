package org.springframework.web.context.support;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.IOException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/GroovyWebApplicationContext.class */
public class GroovyWebApplicationContext extends AbstractRefreshableWebApplicationContext implements GroovyObject {
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.groovy";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".groovy";
    private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());

    @Override // org.springframework.context.support.AbstractRefreshableApplicationContext
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        GroovyBeanDefinitionReader beanDefinitionReader = new GroovyBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(GroovyBeanDefinitionReader beanDefinitionReader) {
    }

    protected void loadBeanDefinitions(GroovyBeanDefinitionReader reader) throws IOException {
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }

    @Override // org.springframework.context.support.AbstractRefreshableConfigApplicationContext
    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null) {
            return new String[]{"/WEB-INF/" + getNamespace() + ".groovy"};
        }
        return new String[]{DEFAULT_CONFIG_LOCATION};
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
        this.metaClass.setProperty(this, property, newValue);
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
