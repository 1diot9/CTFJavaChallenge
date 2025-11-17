package org.springframework.boot.web.reactive.context;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/ReactiveWebServerInitializedEvent.class */
public class ReactiveWebServerInitializedEvent extends WebServerInitializedEvent {
    private final ReactiveWebServerApplicationContext applicationContext;

    public ReactiveWebServerInitializedEvent(WebServer webServer, ReactiveWebServerApplicationContext applicationContext) {
        super(webServer);
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.boot.web.context.WebServerInitializedEvent
    public ReactiveWebServerApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}
