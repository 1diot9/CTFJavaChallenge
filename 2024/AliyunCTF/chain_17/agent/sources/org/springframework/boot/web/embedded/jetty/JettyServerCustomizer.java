package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.server.Server;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyServerCustomizer.class */
public interface JettyServerCustomizer {
    void customize(Server server);
}
