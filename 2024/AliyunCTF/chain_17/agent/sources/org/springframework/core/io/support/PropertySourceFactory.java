package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/PropertySourceFactory.class */
public interface PropertySourceFactory {
    PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException;
}
