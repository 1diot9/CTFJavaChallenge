package org.springframework.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/DefaultPropertiesPropertySource.class */
public class DefaultPropertiesPropertySource extends MapPropertySource {
    public static final String NAME = "defaultProperties";

    public DefaultPropertiesPropertySource(Map<String, Object> source) {
        super(NAME, source);
    }

    public static boolean hasMatchingName(PropertySource<?> propertySource) {
        return propertySource != null && propertySource.getName().equals(NAME);
    }

    public static void ifNotEmpty(Map<String, Object> source, Consumer<DefaultPropertiesPropertySource> action) {
        if (!CollectionUtils.isEmpty(source) && action != null) {
            action.accept(new DefaultPropertiesPropertySource(source));
        }
    }

    public static void addOrMerge(Map<String, Object> source, MutablePropertySources sources) {
        if (!CollectionUtils.isEmpty(source)) {
            Map<String, Object> resultingSource = new HashMap<>();
            DefaultPropertiesPropertySource propertySource = new DefaultPropertiesPropertySource(resultingSource);
            if (sources.contains(NAME)) {
                mergeIfPossible(source, sources, resultingSource);
                sources.replace(NAME, propertySource);
            } else {
                resultingSource.putAll(source);
                sources.addLast(propertySource);
            }
        }
    }

    private static void mergeIfPossible(Map<String, Object> source, MutablePropertySources sources, Map<String, Object> resultingSource) {
        PropertySource<?> existingSource = sources.get(NAME);
        if (existingSource != null) {
            Object underlyingSource = existingSource.getSource();
            if (underlyingSource instanceof Map) {
                resultingSource.putAll((Map) underlyingSource);
            }
            resultingSource.putAll(source);
        }
    }

    public static void moveToEnd(ConfigurableEnvironment environment) {
        moveToEnd(environment.getPropertySources());
    }

    public static void moveToEnd(MutablePropertySources propertySources) {
        PropertySource<?> propertySource = propertySources.remove(NAME);
        if (propertySource != null) {
            propertySources.addLast(propertySource);
        }
    }
}
