package org.springframework.boot.context.properties.bind;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/PropertySourcesPlaceholdersResolver.class */
public class PropertySourcesPlaceholdersResolver implements PlaceholdersResolver {
    private final Iterable<PropertySource<?>> sources;
    private final PropertyPlaceholderHelper helper;

    public PropertySourcesPlaceholdersResolver(Environment environment) {
        this(getSources(environment), null);
    }

    public PropertySourcesPlaceholdersResolver(Iterable<PropertySource<?>> sources) {
        this(sources, null);
    }

    public PropertySourcesPlaceholdersResolver(Iterable<PropertySource<?>> sources, PropertyPlaceholderHelper helper) {
        this.sources = sources;
        this.helper = helper != null ? helper : new PropertyPlaceholderHelper("${", "}", ":", true);
    }

    @Override // org.springframework.boot.context.properties.bind.PlaceholdersResolver
    public Object resolvePlaceholders(Object value) {
        if (value instanceof String) {
            String string = (String) value;
            return this.helper.replacePlaceholders(string, this::resolvePlaceholder);
        }
        return value;
    }

    protected String resolvePlaceholder(String placeholder) {
        if (this.sources != null) {
            for (PropertySource<?> source : this.sources) {
                Object value = source.getProperty(placeholder);
                if (value != null) {
                    return String.valueOf(value);
                }
            }
            return null;
        }
        return null;
    }

    private static PropertySources getSources(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        Assert.isInstanceOf((Class<?>) ConfigurableEnvironment.class, environment, "Environment must be a ConfigurableEnvironment");
        return ((ConfigurableEnvironment) environment).getPropertySources();
    }
}
