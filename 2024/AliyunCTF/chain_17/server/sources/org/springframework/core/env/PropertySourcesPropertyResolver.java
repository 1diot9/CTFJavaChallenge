package org.springframework.core.env;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/PropertySourcesPropertyResolver.class */
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

    @Nullable
    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (propertySource.containsProperty(key)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    @Nullable
    public String getProperty(String key) {
        return (String) getProperty(key, String.class, true);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public <T> T getProperty(String str, Class<T> cls) {
        return (T) getProperty(str, (Class) cls, true);
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver
    @Nullable
    protected String getPropertyAsRawString(String key) {
        return (String) getProperty(key, String.class, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public <T> T getProperty(String str, Class<T> cls, boolean z) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Searching for key '" + str + "' in PropertySource '" + propertySource.getName() + "'");
                }
                Object property = propertySource.getProperty(str);
                if (property != null) {
                    if (z && (property instanceof String)) {
                        property = resolveNestedPlaceholders((String) property);
                    }
                    logKeyFound(str, propertySource, property);
                    return (T) convertValueIfNecessary(property, cls);
                }
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Could not find key '" + str + "' in any property source");
            return null;
        }
        return null;
    }

    protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() + "' with value of type " + value.getClass().getSimpleName());
        }
    }
}
