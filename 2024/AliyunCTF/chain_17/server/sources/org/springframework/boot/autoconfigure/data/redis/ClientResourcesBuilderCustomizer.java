package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.resource.ClientResources;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/redis/ClientResourcesBuilderCustomizer.class */
public interface ClientResourcesBuilderCustomizer {
    void customize(ClientResources.Builder clientResourcesBuilder);
}
