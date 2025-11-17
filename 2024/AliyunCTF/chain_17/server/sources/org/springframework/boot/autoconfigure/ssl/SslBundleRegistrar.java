package org.springframework.boot.autoconfigure.ssl;

import org.springframework.boot.ssl.SslBundleRegistry;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/SslBundleRegistrar.class */
public interface SslBundleRegistrar {
    void registerBundles(SslBundleRegistry registry);
}
