package org.springframework.core.env;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/PropertySources.class */
public interface PropertySources extends Iterable<PropertySource<?>> {
    boolean contains(String name);

    @Nullable
    PropertySource<?> get(String name);

    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
