package org.springframework.boot.context.config;

import java.io.IOException;
import org.springframework.boot.context.config.ConfigDataResource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLoader.class */
public interface ConfigDataLoader<R extends ConfigDataResource> {
    ConfigData load(ConfigDataLoaderContext context, R resource) throws IOException, ConfigDataResourceNotFoundException;

    default boolean isLoadable(ConfigDataLoaderContext context, R resource) {
        return true;
    }
}
