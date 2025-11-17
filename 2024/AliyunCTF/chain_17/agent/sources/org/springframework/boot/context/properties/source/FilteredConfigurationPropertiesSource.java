package org.springframework.boot.context.properties.source;

import java.util.function.Predicate;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/FilteredConfigurationPropertiesSource.class */
public class FilteredConfigurationPropertiesSource implements ConfigurationPropertySource {
    private final ConfigurationPropertySource source;
    private final Predicate<ConfigurationPropertyName> filter;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FilteredConfigurationPropertiesSource(ConfigurationPropertySource source, Predicate<ConfigurationPropertyName> filter) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(filter, "Filter must not be null");
        this.source = source;
        this.filter = filter;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        boolean filtered = getFilter().test(name);
        if (filtered) {
            return getSource().getConfigurationProperty(name);
        }
        return null;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        ConfigurationPropertyState result = this.source.containsDescendantOf(name);
        if (result == ConfigurationPropertyState.PRESENT) {
            return ConfigurationPropertyState.UNKNOWN;
        }
        return result;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.source.getUnderlyingSource();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConfigurationPropertySource getSource() {
        return this.source;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Predicate<ConfigurationPropertyName> getFilter() {
        return this.filter;
    }

    public String toString() {
        return this.source.toString() + " (filtered)";
    }
}
