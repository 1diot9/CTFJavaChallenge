package org.apache.tomcat.websocket;

import aQute.bnd.annotation.spi.ServiceProvider;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

@ServiceProvider(ContainerProvider.class)
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsContainerProvider.class */
public class WsContainerProvider extends ContainerProvider {
    @Override // jakarta.websocket.ContainerProvider
    protected WebSocketContainer getContainer() {
        return new WsWebSocketContainer();
    }
}
