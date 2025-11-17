package org.springframework.boot.context.properties.source;

import org.springframework.core.env.AbstractPropertyResolver;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySourcesPropertyResolver.class */
public class ConfigurationPropertySourcesPropertyResolver extends AbstractPropertyResolver {
    private final MutablePropertySources propertySources;
    private final DefaultResolver defaultResolver;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationPropertySourcesPropertyResolver(MutablePropertySources propertySources) {
        this.propertySources = propertySources;
        this.defaultResolver = new DefaultResolver(propertySources);
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        ConfigurationPropertyName name;
        ConfigurationPropertySourcesPropertySource attached = getAttached();
        if (attached != null && (name = ConfigurationPropertyName.of(key, true)) != null) {
            try {
                return attached.findConfigurationProperty(name) != null;
            } catch (Exception e) {
            }
        }
        return this.defaultResolver.containsProperty(key);
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    public String getProperty(String key) {
        return (String) getProperty(key, String.class, true);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getProperty(String str, Class<T> cls) {
        return (T) getProperty(str, (Class) cls, true);
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver
    protected String getPropertyAsRawString(String key) {
        return (String) getProperty(key, String.class, false);
    }

    private <T> T getProperty(String str, Class<T> cls, boolean z) {
        Object findPropertyValue = findPropertyValue(str);
        if (findPropertyValue == null) {
            return null;
        }
        if (z && (findPropertyValue instanceof String)) {
            findPropertyValue = resolveNestedPlaceholders((String) findPropertyValue);
        }
        return (T) convertValueIfNecessary(findPropertyValue, cls);
    }

    private Object findPropertyValue(String key) {
        ConfigurationPropertyName name;
        ConfigurationPropertySourcesPropertySource attached = getAttached();
        if (attached != null && (name = ConfigurationPropertyName.of(key, true)) != null) {
            try {
                ConfigurationProperty configurationProperty = attached.findConfigurationProperty(name);
                if (configurationProperty != null) {
                    return configurationProperty.getValue();
                }
                return null;
            } catch (Exception e) {
            }
        }
        return this.defaultResolver.getProperty(key, Object.class, false);
    }

    private ConfigurationPropertySourcesPropertySource getAttached() {
        ConfigurationPropertySourcesPropertySource attached = (ConfigurationPropertySourcesPropertySource) ConfigurationPropertySources.getAttached(this.propertySources);
        Iterable<ConfigurationPropertySource> attachedSource = attached != null ? attached.getSource() : null;
        if (attachedSource instanceof SpringConfigurationPropertySources) {
            SpringConfigurationPropertySources springSource = (SpringConfigurationPropertySources) attachedSource;
            if (springSource.isUsingSources(this.propertySources)) {
                return attached;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySourcesPropertyResolver$DefaultResolver.class */
    public static class DefaultResolver extends PropertySourcesPropertyResolver {
        DefaultResolver(PropertySources propertySources) {
            super(propertySources);
        }

        @Override // org.springframework.core.env.PropertySourcesPropertyResolver
        public <T> T getProperty(String str, Class<T> cls, boolean z) {
            return (T) super.getProperty(str, cls, z);
        }
    }
}
