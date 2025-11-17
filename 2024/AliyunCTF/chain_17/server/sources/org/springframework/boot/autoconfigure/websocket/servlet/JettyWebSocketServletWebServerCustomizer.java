package org.springframework.boot.autoconfigure.websocket.servlet;

import org.eclipse.jetty.ee10.webapp.AbstractConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.ee10.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.servlet.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.eclipse.jetty.websocket.core.server.WebSocketServerComponents;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/JettyWebSocketServletWebServerCustomizer.class */
public class JettyWebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(JettyServletWebServerFactory factory) {
        factory.addConfigurations(new AbstractConfiguration(new AbstractConfiguration.Builder()) { // from class: org.springframework.boot.autoconfigure.websocket.servlet.JettyWebSocketServletWebServerCustomizer.1
            public void configure(WebAppContext context) throws Exception {
                if (JettyWebSocketServerContainer.getContainer(context.getServletContext()) == null) {
                    WebSocketServerComponents.ensureWebSocketComponents(context.getServer(), context.getContext().getContextHandler());
                    JettyWebSocketServerContainer.ensureContainer(context.getServletContext());
                }
                if (JakartaWebSocketServerContainer.getContainer(context.getServletContext()) == null) {
                    WebSocketServerComponents.ensureWebSocketComponents(context.getServer(), context.getContext().getContextHandler());
                    WebSocketUpgradeFilter.ensureFilter(context.getServletContext());
                    WebSocketMappings.ensureMappings(context.getContext().getContextHandler());
                    JakartaWebSocketServerContainer.ensureContainer(context.getServletContext());
                }
            }
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
