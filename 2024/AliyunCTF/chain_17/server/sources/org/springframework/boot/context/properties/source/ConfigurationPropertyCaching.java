package org.springframework.boot.context.properties.source;

import java.time.Duration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyCaching.class */
public interface ConfigurationPropertyCaching {
    void enable();

    void disable();

    void setTimeToLive(Duration timeToLive);

    void clear();

    static ConfigurationPropertyCaching get(Environment environment) {
        return get(environment, (Object) null);
    }

    static ConfigurationPropertyCaching get(Environment environment, Object underlyingSource) {
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
        return get(sources, underlyingSource);
    }

    static ConfigurationPropertyCaching get(Iterable<ConfigurationPropertySource> sources) {
        return get(sources, (Object) null);
    }

    static ConfigurationPropertyCaching get(Iterable<ConfigurationPropertySource> sources, Object underlyingSource) {
        ConfigurationPropertyCaching caching;
        Assert.notNull(sources, "Sources must not be null");
        if (underlyingSource == null) {
            return new ConfigurationPropertySourcesCaching(sources);
        }
        for (ConfigurationPropertySource source : sources) {
            if (source.getUnderlyingSource() == underlyingSource && (caching = CachingConfigurationPropertySource.find(source)) != null) {
                return caching;
            }
        }
        throw new IllegalStateException("Unable to find cache from configuration property sources");
    }
}
