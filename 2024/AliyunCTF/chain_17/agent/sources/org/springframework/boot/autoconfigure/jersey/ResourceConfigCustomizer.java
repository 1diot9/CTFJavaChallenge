package org.springframework.boot.autoconfigure.jersey;

import org.glassfish.jersey.server.ResourceConfig;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jersey/ResourceConfigCustomizer.class */
public interface ResourceConfigCustomizer {
    void customize(ResourceConfig config);
}
