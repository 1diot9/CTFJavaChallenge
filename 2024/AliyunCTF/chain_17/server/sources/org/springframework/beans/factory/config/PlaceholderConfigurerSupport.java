package org.springframework.beans.factory.config;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/PlaceholderConfigurerSupport.class */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer implements BeanNameAware, BeanFactoryAware {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
    public static final String DEFAULT_VALUE_SEPARATOR = ":";

    @Nullable
    protected String nullValue;

    @Nullable
    private String beanName;

    @Nullable
    private BeanFactory beanFactory;
    protected String placeholderPrefix = "${";
    protected String placeholderSuffix = "}";

    @Nullable
    protected String valueSeparator = ":";
    protected boolean trimValues = false;
    protected boolean ignoreUnresolvablePlaceholders = false;

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    public void setValueSeparator(@Nullable String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public void setTrimValues(boolean trimValues) {
        this.trimValues = trimValues;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (String curName : beanNames) {
            if (!curName.equals(this.beanName) || !beanFactoryToProcess.equals(this.beanFactory)) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
                try {
                    visitor.visitBeanDefinition(bd);
                } catch (Exception ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
                }
            }
        }
        beanFactoryToProcess.resolveAliases(valueResolver);
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }
}
