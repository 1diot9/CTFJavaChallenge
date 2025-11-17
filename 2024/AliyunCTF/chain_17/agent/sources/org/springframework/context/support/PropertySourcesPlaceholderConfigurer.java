package org.springframework.context.support;

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/PropertySourcesPlaceholderConfigurer.class */
public class PropertySourcesPlaceholderConfigurer extends PlaceholderConfigurerSupport implements EnvironmentAware {
    public static final String LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME = "localProperties";
    public static final String ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME = "environmentProperties";

    @Nullable
    private MutablePropertySources propertySources;

    @Nullable
    private PropertySources appliedPropertySources;

    @Nullable
    private Environment environment;

    public void setPropertySources(PropertySources propertySources) {
        this.propertySources = new MutablePropertySources(propertySources);
    }

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override // org.springframework.beans.factory.config.PropertyResourceConfigurer, org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertySources == null) {
            this.propertySources = new MutablePropertySources();
            if (this.environment != null) {
                PropertyResolver propertyResolver = this.environment;
                if (this.ignoreUnresolvablePlaceholders) {
                    Environment environment = this.environment;
                    if (environment instanceof ConfigurableEnvironment) {
                        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
                        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(configurableEnvironment.getPropertySources());
                        resolver.setIgnoreUnresolvableNestedPlaceholders(true);
                        propertyResolver = resolver;
                    }
                }
                final PropertyResolver propertyResolverToUse = propertyResolver;
                this.propertySources.addLast(new PropertySource<Environment>(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, this.environment) { // from class: org.springframework.context.support.PropertySourcesPlaceholderConfigurer.1
                    @Override // org.springframework.core.env.PropertySource
                    @Nullable
                    public String getProperty(String key) {
                        return propertyResolverToUse.getProperty(key);
                    }
                });
            }
            try {
                PropertySource<?> localPropertySource = new PropertiesPropertySource(LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, mergeProperties());
                if (this.localOverride) {
                    this.propertySources.addFirst(localPropertySource);
                } else {
                    this.propertySources.addLast(localPropertySource);
                }
            } catch (IOException ex) {
                throw new BeanInitializationException("Could not load properties", ex);
            }
        }
        processProperties(beanFactory, createPropertyResolver(this.propertySources));
        this.appliedPropertySources = this.propertySources;
    }

    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new PropertySourcesPropertyResolver(propertySources);
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);
        StringValueResolver valueResolver = strVal -> {
            String resolveRequiredPlaceholders;
            if (this.ignoreUnresolvablePlaceholders) {
                resolveRequiredPlaceholders = propertyResolver.resolvePlaceholders(strVal);
            } else {
                resolveRequiredPlaceholders = propertyResolver.resolveRequiredPlaceholders(strVal);
            }
            String resolved = resolveRequiredPlaceholders;
            if (this.trimValues) {
                resolved = resolved.trim();
            }
            if (resolved.equals(this.nullValue)) {
                return null;
            }
            return resolved;
        };
        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    @Override // org.springframework.beans.factory.config.PropertyResourceConfigurer
    @Deprecated
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {
        throw new UnsupportedOperationException("Call processProperties(ConfigurableListableBeanFactory, ConfigurablePropertyResolver) instead");
    }

    public PropertySources getAppliedPropertySources() throws IllegalStateException {
        Assert.state(this.appliedPropertySources != null, "PropertySources have not yet been applied");
        return this.appliedPropertySources;
    }
}
