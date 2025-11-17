package org.springframework.boot.context.properties.source;

import java.time.Duration;
import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertySourcesCaching.class */
class ConfigurationPropertySourcesCaching implements ConfigurationPropertyCaching {
    private final Iterable<ConfigurationPropertySource> sources;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigurationPropertySourcesCaching(Iterable<ConfigurationPropertySource> sources) {
        this.sources = sources;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void enable() {
        forEach((v0) -> {
            v0.enable();
        });
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void disable() {
        forEach((v0) -> {
            v0.disable();
        });
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void setTimeToLive(Duration timeToLive) {
        forEach(caching -> {
            caching.setTimeToLive(timeToLive);
        });
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertyCaching
    public void clear() {
        forEach((v0) -> {
            v0.clear();
        });
    }

    private void forEach(Consumer<ConfigurationPropertyCaching> action) {
        if (this.sources != null) {
            for (ConfigurationPropertySource source : this.sources) {
                ConfigurationPropertyCaching caching = CachingConfigurationPropertySource.find(source);
                if (caching != null) {
                    action.accept(caching);
                }
            }
        }
    }
}
