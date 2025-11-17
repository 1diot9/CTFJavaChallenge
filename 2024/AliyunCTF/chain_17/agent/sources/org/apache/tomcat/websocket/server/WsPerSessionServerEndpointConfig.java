package org.apache.tomcat.websocket.server;

import jakarta.websocket.Decoder;
import jakarta.websocket.Encoder;
import jakarta.websocket.Extension;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsPerSessionServerEndpointConfig.class */
class WsPerSessionServerEndpointConfig implements ServerEndpointConfig {
    private final ServerEndpointConfig perEndpointConfig;
    private final Map<String, Object> perSessionUserProperties = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsPerSessionServerEndpointConfig(ServerEndpointConfig perEndpointConfig) {
        this.perEndpointConfig = perEndpointConfig;
        this.perSessionUserProperties.putAll(perEndpointConfig.getUserProperties());
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Encoder>> getEncoders() {
        return this.perEndpointConfig.getEncoders();
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Decoder>> getDecoders() {
        return this.perEndpointConfig.getDecoders();
    }

    @Override // jakarta.websocket.EndpointConfig
    public Map<String, Object> getUserProperties() {
        return this.perSessionUserProperties;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public Class<?> getEndpointClass() {
        return this.perEndpointConfig.getEndpointClass();
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public String getPath() {
        return this.perEndpointConfig.getPath();
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public List<String> getSubprotocols() {
        return this.perEndpointConfig.getSubprotocols();
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public List<Extension> getExtensions() {
        return this.perEndpointConfig.getExtensions();
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.perEndpointConfig.getConfigurator();
    }
}
