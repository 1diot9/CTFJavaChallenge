package org.springframework.boot.ssl;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslBundleRegistry.class */
public interface SslBundleRegistry {
    void registerBundle(String name, SslBundle bundle);

    void updateBundle(String name, SslBundle updatedBundle) throws NoSuchSslBundleException;
}
