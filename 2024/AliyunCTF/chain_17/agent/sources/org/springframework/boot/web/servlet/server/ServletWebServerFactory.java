package org.springframework.boot.web.servlet.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/server/ServletWebServerFactory.class */
public interface ServletWebServerFactory extends WebServerFactory {
    WebServer getWebServer(ServletContextInitializer... initializers);
}
