package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/DefaultPropertySourceFactory.class */
public class DefaultPropertySourceFactory implements PropertySourceFactory {
    @Override // org.springframework.core.io.support.PropertySourceFactory
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        return name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource);
    }
}
