package org.springframework.boot.autoconfigure.cache;

import org.cache2k.Cache2kBuilder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/Cache2kBuilderCustomizer.class */
public interface Cache2kBuilderCustomizer {
    void customize(Cache2kBuilder<?, ?> builder);
}
