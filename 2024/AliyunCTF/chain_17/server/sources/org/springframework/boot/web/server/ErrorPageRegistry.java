package org.springframework.boot.web.server;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/ErrorPageRegistry.class */
public interface ErrorPageRegistry {
    void addErrorPages(ErrorPage... errorPages);
}
