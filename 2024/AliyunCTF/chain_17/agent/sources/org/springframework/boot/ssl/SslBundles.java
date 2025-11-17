package org.springframework.boot.ssl;

import java.util.function.Consumer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ssl/SslBundles.class */
public interface SslBundles {
    SslBundle getBundle(String name) throws NoSuchSslBundleException;

    void addBundleUpdateHandler(String name, Consumer<SslBundle> updateHandler) throws NoSuchSslBundleException;
}
