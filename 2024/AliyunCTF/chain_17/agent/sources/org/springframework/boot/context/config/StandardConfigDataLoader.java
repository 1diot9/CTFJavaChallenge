package org.springframework.boot.context.config;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginTrackedResource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/StandardConfigDataLoader.class */
public class StandardConfigDataLoader implements ConfigDataLoader<StandardConfigDataResource> {
    private static final ConfigData.PropertySourceOptions PROFILE_SPECIFIC = ConfigData.PropertySourceOptions.always(ConfigData.Option.PROFILE_SPECIFIC);
    private static final ConfigData.PropertySourceOptions NON_PROFILE_SPECIFIC = ConfigData.PropertySourceOptions.ALWAYS_NONE;

    @Override // org.springframework.boot.context.config.ConfigDataLoader
    public ConfigData load(ConfigDataLoaderContext context, StandardConfigDataResource resource) throws IOException, ConfigDataNotFoundException {
        if (resource.isEmptyDirectory()) {
            return ConfigData.EMPTY;
        }
        ConfigDataResourceNotFoundException.throwIfDoesNotExist(resource, resource.getResource());
        StandardConfigDataReference reference = resource.getReference();
        Resource originTrackedResource = OriginTrackedResource.of(resource.getResource(), Origin.from(reference.getConfigDataLocation()));
        String name = String.format("Config resource '%s' via location '%s'", resource, reference.getConfigDataLocation());
        List<PropertySource<?>> propertySources = reference.getPropertySourceLoader().load(name, originTrackedResource);
        ConfigData.PropertySourceOptions options = resource.getProfile() != null ? PROFILE_SPECIFIC : NON_PROFILE_SPECIFIC;
        return new ConfigData(propertySources, options);
    }
}
