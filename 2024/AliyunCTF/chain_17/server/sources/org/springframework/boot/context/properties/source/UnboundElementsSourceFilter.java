package org.springframework.boot.context.properties.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.springframework.core.env.PropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/UnboundElementsSourceFilter.class */
public class UnboundElementsSourceFilter implements Function<ConfigurationPropertySource, Boolean> {
    private static final Set<String> BENIGN_PROPERTY_SOURCE_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList("systemEnvironment", "systemProperties")));

    @Override // java.util.function.Function
    public Boolean apply(ConfigurationPropertySource configurationPropertySource) {
        Object underlyingSource = configurationPropertySource.getUnderlyingSource();
        if (underlyingSource instanceof PropertySource) {
            String name = ((PropertySource) underlyingSource).getName();
            return Boolean.valueOf(!BENIGN_PROPERTY_SOURCE_NAMES.contains(name));
        }
        return true;
    }
}
