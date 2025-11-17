package org.springframework.boot.context.properties.source;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/CachingConfigurationPropertySource.class */
interface CachingConfigurationPropertySource {
    ConfigurationPropertyCaching getCaching();

    static ConfigurationPropertyCaching find(ConfigurationPropertySource source) {
        if (source instanceof CachingConfigurationPropertySource) {
            CachingConfigurationPropertySource cachingSource = (CachingConfigurationPropertySource) source;
            return cachingSource.getCaching();
        }
        return null;
    }
}
