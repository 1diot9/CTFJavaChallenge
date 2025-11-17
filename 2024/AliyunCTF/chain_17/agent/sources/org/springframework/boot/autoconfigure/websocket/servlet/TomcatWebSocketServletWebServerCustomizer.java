package org.springframework.boot.autoconfigure.websocket.servlet;

import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/TomcatWebSocketServletWebServerCustomizer.class */
public class TomcatWebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(context -> {
            context.addServletContainerInitializer(new WsSci(), null);
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
