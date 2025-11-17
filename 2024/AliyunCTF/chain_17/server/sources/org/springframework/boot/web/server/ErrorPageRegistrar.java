package org.springframework.boot.web.server;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/ErrorPageRegistrar.class */
public interface ErrorPageRegistrar {
    void registerErrorPages(ErrorPageRegistry registry);
}
