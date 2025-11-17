package org.springframework.boot.context.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.env.ConfigTreePropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigTreeConfigDataLoader.class */
public class ConfigTreeConfigDataLoader implements ConfigDataLoader<ConfigTreeConfigDataResource> {
    @Override // org.springframework.boot.context.config.ConfigDataLoader
    public ConfigData load(ConfigDataLoaderContext context, ConfigTreeConfigDataResource resource) throws IOException, ConfigDataResourceNotFoundException {
        Path path = resource.getPath();
        ConfigDataResourceNotFoundException.throwIfDoesNotExist(resource, path);
        String name = "Config tree '" + path + "'";
        ConfigTreePropertySource source = new ConfigTreePropertySource(name, path, ConfigTreePropertySource.Option.AUTO_TRIM_TRAILING_NEW_LINE);
        return new ConfigData(Collections.singletonList(source), new ConfigData.Option[0]);
    }
}
