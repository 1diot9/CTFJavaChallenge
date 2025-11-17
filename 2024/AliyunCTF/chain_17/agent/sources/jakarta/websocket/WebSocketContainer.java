package jakarta.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/WebSocketContainer.class */
public interface WebSocketContainer {
    long getDefaultAsyncSendTimeout();

    void setAsyncSendTimeout(long j);

    Session connectToServer(Object obj, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Class<?> cls, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfig, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Class<? extends Endpoint> cls, ClientEndpointConfig clientEndpointConfig, URI uri) throws DeploymentException, IOException;

    long getDefaultMaxSessionIdleTimeout();

    void setDefaultMaxSessionIdleTimeout(long j);

    int getDefaultMaxBinaryMessageBufferSize();

    void setDefaultMaxBinaryMessageBufferSize(int i);

    int getDefaultMaxTextMessageBufferSize();

    void setDefaultMaxTextMessageBufferSize(int i);

    Set<Extension> getInstalledExtensions();
}
