package jakarta.websocket.server;

import jakarta.websocket.Endpoint;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/server/ServerApplicationConfig.class */
public interface ServerApplicationConfig {
    Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set);

    Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set);
}
