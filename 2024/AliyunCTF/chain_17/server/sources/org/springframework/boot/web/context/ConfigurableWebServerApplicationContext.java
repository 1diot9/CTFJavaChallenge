package org.springframework.boot.web.context;

import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/ConfigurableWebServerApplicationContext.class */
public interface ConfigurableWebServerApplicationContext extends ConfigurableApplicationContext, WebServerApplicationContext {
    void setServerNamespace(String serverNamespace);
}
