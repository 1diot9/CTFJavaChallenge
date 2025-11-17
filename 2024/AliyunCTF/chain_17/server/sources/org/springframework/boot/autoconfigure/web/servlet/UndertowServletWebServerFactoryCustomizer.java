package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/UndertowServletWebServerFactoryCustomizer.class */
public class UndertowServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    private final ServerProperties serverProperties;

    public UndertowServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(UndertowServletWebServerFactory factory) {
        factory.setEagerFilterInit(this.serverProperties.getUndertow().isEagerFilterInit());
        factory.setPreservePathOnForward(this.serverProperties.getUndertow().isPreservePathOnForward());
    }
}
