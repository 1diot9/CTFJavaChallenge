package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.origin.Origin;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLocationBindHandler.class */
class ConfigDataLocationBindHandler extends AbstractBindHandler {
    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        if (result instanceof ConfigDataLocation) {
            ConfigDataLocation location = (ConfigDataLocation) result;
            return withOrigin(context, location);
        }
        if (result instanceof List) {
            List<?> list = (List) result;
            return list.stream().filter(Objects::nonNull).map(element -> {
                if (!(element instanceof ConfigDataLocation)) {
                    return element;
                }
                ConfigDataLocation location2 = (ConfigDataLocation) element;
                return withOrigin(context, location2);
            }).collect(Collectors.toCollection(ArrayList::new));
        }
        if (result instanceof ConfigDataLocation[]) {
            ConfigDataLocation[] unfilteredLocations = (ConfigDataLocation[]) result;
            return Arrays.stream(unfilteredLocations).filter((v0) -> {
                return Objects.nonNull(v0);
            }).map(element2 -> {
                return withOrigin(context, element2);
            }).toArray(x$0 -> {
                return new ConfigDataLocation[x$0];
            });
        }
        return result;
    }

    private ConfigDataLocation withOrigin(BindContext context, ConfigDataLocation result) {
        if (result.getOrigin() != null) {
            return result;
        }
        Origin origin = Origin.from(context.getConfigurationProperty());
        return result.withOrigin(origin);
    }
}
