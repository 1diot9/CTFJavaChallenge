package jakarta.websocket.server;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.WebSocketContainer;
import java.io.IOException;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/server/ServerContainer.class */
public interface ServerContainer extends WebSocketContainer {
    void addEndpoint(Class<?> cls) throws DeploymentException;

    void addEndpoint(ServerEndpointConfig serverEndpointConfig) throws DeploymentException;

    void upgradeHttpToWebSocket(Object obj, Object obj2, ServerEndpointConfig serverEndpointConfig, Map<String, String> map) throws IOException, DeploymentException;
}
