package org.springframework.boot;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/BootstrapRegistryInitializer.class */
public interface BootstrapRegistryInitializer {
    void initialize(BootstrapRegistry registry);
}
