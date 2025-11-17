package org.springframework.boot.context.properties.source;

import java.util.function.Predicate;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySource.class */
public interface ConfigurationPropertySource {
    ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name);

    default ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        return ConfigurationPropertyState.UNKNOWN;
    }

    default ConfigurationPropertySource filter(Predicate<ConfigurationPropertyName> filter) {
        return new FilteredConfigurationPropertiesSource(this, filter);
    }

    default ConfigurationPropertySource withAliases(ConfigurationPropertyNameAliases aliases) {
        return new AliasedConfigurationPropertySource(this, aliases);
    }

    default ConfigurationPropertySource withPrefix(String prefix) {
        return StringUtils.hasText(prefix) ? new PrefixedConfigurationPropertySource(this, prefix) : this;
    }

    default Object getUnderlyingSource() {
        return null;
    }

    static ConfigurationPropertySource from(PropertySource<?> source) {
        if (source instanceof ConfigurationPropertySourcesPropertySource) {
            return null;
        }
        return SpringConfigurationPropertySource.from(source);
    }
}
