package org.springframework.boot.context.properties.source;

import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/PrefixedConfigurationPropertySource.class */
public class PrefixedConfigurationPropertySource implements ConfigurationPropertySource {
    private final ConfigurationPropertySource source;
    private final ConfigurationPropertyName prefix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrefixedConfigurationPropertySource(ConfigurationPropertySource source, String prefix) {
        Assert.notNull(source, "Source must not be null");
        Assert.hasText(prefix, "Prefix must not be empty");
        this.source = source;
        this.prefix = ConfigurationPropertyName.of(prefix);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ConfigurationPropertyName getPrefix() {
        return this.prefix;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        ConfigurationProperty configurationProperty = this.source.getConfigurationProperty(getPrefixedName(name));
        if (configurationProperty == null) {
            return null;
        }
        return ConfigurationProperty.of(configurationProperty.getSource(), name, configurationProperty.getValue(), configurationProperty.getOrigin());
    }

    private ConfigurationPropertyName getPrefixedName(ConfigurationPropertyName name) {
        return this.prefix.append(name);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        return this.source.containsDescendantOf(getPrefixedName(name));
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.source.getUnderlyingSource();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigurationPropertySource getSource() {
        return this.source;
    }
}
