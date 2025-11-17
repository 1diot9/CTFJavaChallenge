package org.springframework.boot.web.server;

import org.springframework.boot.web.server.WebServerFactory;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/WebServerFactoryCustomizer.class */
public interface WebServerFactoryCustomizer<T extends WebServerFactory> {
    void customize(T factory);
}
