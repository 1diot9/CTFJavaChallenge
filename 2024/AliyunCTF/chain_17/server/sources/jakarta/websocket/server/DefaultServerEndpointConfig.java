package jakarta.websocket.server;

import jakarta.websocket.Decoder;
import jakarta.websocket.Encoder;
import jakarta.websocket.Extension;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/server/DefaultServerEndpointConfig.class */
final class DefaultServerEndpointConfig implements ServerEndpointConfig {
    private final Class<?> endpointClass;
    private final String path;
    private final List<String> subprotocols;
    private final List<Extension> extensions;
    private final List<Class<? extends Encoder>> encoders;
    private final List<Class<? extends Decoder>> decoders;
    private final ServerEndpointConfig.Configurator serverEndpointConfigurator;
    private final Map<String, Object> userProperties = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultServerEndpointConfig(Class<?> endpointClass, String path, List<String> subprotocols, List<Extension> extensions, List<Class<? extends Encoder>> encoders, List<Class<? extends Decoder>> decoders, ServerEndpointConfig.Configurator serverEndpointConfigurator) {
        this.endpointClass = endpointClass;
        this.path = path;
        this.subprotocols = subprotocols;
        this.extensions = extensions;
        this.encoders = encoders;
        this.decoders = decoders;
        this.serverEndpointConfigurator = serverEndpointConfigurator;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public Class<?> getEndpointClass() {
        return this.endpointClass;
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Encoder>> getEncoders() {
        return this.encoders;
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Decoder>> getDecoders() {
        return this.decoders;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public String getPath() {
        return this.path;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.serverEndpointConfigurator;
    }

    @Override // jakarta.websocket.EndpointConfig
    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public List<String> getSubprotocols() {
        return this.subprotocols;
    }

    @Override // jakarta.websocket.server.ServerEndpointConfig
    public List<Extension> getExtensions() {
        return this.extensions;
    }
}
