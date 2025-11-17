package org.springframework.boot.context.properties.source;

import java.util.Collections;
import java.util.List;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/DefaultPropertyMapper.class */
final class DefaultPropertyMapper implements PropertyMapper {
    public static final PropertyMapper INSTANCE = new DefaultPropertyMapper();
    private LastMapping<ConfigurationPropertyName, List<String>> lastMappedConfigurationPropertyName;
    private LastMapping<String, ConfigurationPropertyName> lastMappedPropertyName;

    private DefaultPropertyMapper() {
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public List<String> map(ConfigurationPropertyName configurationPropertyName) {
        LastMapping<ConfigurationPropertyName, List<String>> last = this.lastMappedConfigurationPropertyName;
        if (last != null && last.isFrom(configurationPropertyName)) {
            return last.getMapping();
        }
        String convertedName = configurationPropertyName.toString();
        List<String> mapping = Collections.singletonList(convertedName);
        this.lastMappedConfigurationPropertyName = new LastMapping<>(configurationPropertyName, mapping);
        return mapping;
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public ConfigurationPropertyName map(String propertySourceName) {
        LastMapping<String, ConfigurationPropertyName> last = this.lastMappedPropertyName;
        if (last != null && last.isFrom(propertySourceName)) {
            return last.getMapping();
        }
        ConfigurationPropertyName mapping = tryMap(propertySourceName);
        this.lastMappedPropertyName = new LastMapping<>(propertySourceName, mapping);
        return mapping;
    }

    private ConfigurationPropertyName tryMap(String propertySourceName) {
        try {
            ConfigurationPropertyName convertedName = ConfigurationPropertyName.adapt(propertySourceName, '.');
            if (!convertedName.isEmpty()) {
                return convertedName;
            }
        } catch (Exception e) {
        }
        return ConfigurationPropertyName.EMPTY;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/DefaultPropertyMapper$LastMapping.class */
    private static class LastMapping<T, M> {
        private final T from;
        private final M mapping;

        LastMapping(T from, M mapping) {
            this.from = from;
            this.mapping = mapping;
        }

        boolean isFrom(T from) {
            return ObjectUtils.nullSafeEquals(from, this.from);
        }

        M getMapping() {
            return this.mapping;
        }
    }
}
