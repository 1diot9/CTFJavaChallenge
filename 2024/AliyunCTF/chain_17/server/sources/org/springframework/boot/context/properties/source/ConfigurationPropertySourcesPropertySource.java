package org.springframework.boot.context.properties.source;

import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.core.env.PropertySource;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySourcesPropertySource.class */
public class ConfigurationPropertySourcesPropertySource extends PropertySource<Iterable<ConfigurationPropertySource>> implements OriginLookup<String> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationPropertySourcesPropertySource(String name, Iterable<ConfigurationPropertySource> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        return findConfigurationProperty(name) != null;
    }

    @Override // org.springframework.core.env.PropertySource
    public Object getProperty(String name) {
        ConfigurationProperty configurationProperty = findConfigurationProperty(name);
        if (configurationProperty != null) {
            return configurationProperty.getValue();
        }
        return null;
    }

    @Override // org.springframework.boot.origin.OriginLookup
    public Origin getOrigin(String name) {
        return Origin.from(findConfigurationProperty(name));
    }

    private ConfigurationProperty findConfigurationProperty(String name) {
        try {
            return findConfigurationProperty(ConfigurationPropertyName.of(name, true));
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationProperty findConfigurationProperty(ConfigurationPropertyName name) {
        if (name == null) {
            return null;
        }
        for (ConfigurationPropertySource configurationPropertySource : getSource()) {
            ConfigurationProperty configurationProperty = configurationPropertySource.getConfigurationProperty(name);
            if (configurationProperty != null) {
                return configurationProperty;
            }
        }
        return null;
    }
}
