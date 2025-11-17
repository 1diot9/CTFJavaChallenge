package org.springframework.boot.web.servlet.context;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/ServletWebServerInitializedEvent.class */
public class ServletWebServerInitializedEvent extends WebServerInitializedEvent {
    private final ServletWebServerApplicationContext applicationContext;

    public ServletWebServerInitializedEvent(WebServer webServer, ServletWebServerApplicationContext applicationContext) {
        super(webServer);
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.boot.web.context.WebServerInitializedEvent
    public ServletWebServerApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
