package org.springframework.boot.autoconfigure.websocket.reactive;

import jakarta.servlet.ServletContext;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.servlet.WebSocketUpgradeFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.eclipse.jetty.websocket.core.server.WebSocketServerComponents;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/reactive/JettyWebSocketReactiveWebServerCustomizer.class */
public class JettyWebSocketReactiveWebServerCustomizer implements WebServerFactoryCustomizer<JettyReactiveWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(JettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(server -> {
            ServletContextHandler servletContextHandler = findServletContextHandler(server);
            if (servletContextHandler != null) {
                ServletContext servletContext = servletContextHandler.getServletContext();
                if (JettyWebSocketServerContainer.getContainer(servletContext) == null) {
                    WebSocketServerComponents.ensureWebSocketComponents(server, servletContextHandler);
                    JettyWebSocketServerContainer.ensureContainer(servletContext);
                }
                if (JakartaWebSocketServerContainer.getContainer(servletContext) == null) {
                    WebSocketServerComponents.ensureWebSocketComponents(server, servletContextHandler);
                    WebSocketUpgradeFilter.ensureFilter(servletContext);
                    WebSocketMappings.ensureMappings(servletContextHandler);
                    JakartaWebSocketServerContainer.ensureContainer(servletContext);
                }
            }
        });
    }

    private ServletContextHandler findServletContextHandler(Handler handler) {
        if (handler instanceof ServletContextHandler) {
            return (ServletContextHandler) handler;
        }
        if (handler instanceof Handler.Wrapper) {
            Handler.Wrapper handlerWrapper = (Handler.Wrapper) handler;
            return findServletContextHandler(handlerWrapper.getHandler());
        }
        if (handler instanceof Handler.Collection) {
            Handler.Collection handlerCollection = (Handler.Collection) handler;
            for (Handler contained : handlerCollection.getHandlers()) {
                ServletContextHandler servletContextHandler = findServletContextHandler(contained);
                if (servletContextHandler != null) {
                    return servletContextHandler;
                }
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}
