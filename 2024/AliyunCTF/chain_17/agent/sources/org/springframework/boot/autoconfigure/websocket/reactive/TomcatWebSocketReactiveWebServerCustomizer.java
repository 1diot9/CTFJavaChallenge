package org.springframework.boot.autoconfigure.websocket.reactive;

import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/reactive/TomcatWebSocketReactiveWebServerCustomizer.class */
public class TomcatWebSocketReactiveWebServerCustomizer implements WebServerFactoryCustomizer<TomcatReactiveWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(TomcatReactiveWebServerFactory factory) {
        factory.addContextCustomizers(context -> {
            context.addServletContainerInitializer(new WsSci(), null);
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
