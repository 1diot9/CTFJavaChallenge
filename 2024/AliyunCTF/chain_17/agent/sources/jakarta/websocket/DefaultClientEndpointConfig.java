package jakarta.websocket;

import jakarta.websocket.ClientEndpointConfig;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/DefaultClientEndpointConfig.class */
public final class DefaultClientEndpointConfig implements ClientEndpointConfig {
    private final List<String> preferredSubprotocols;
    private final List<Extension> extensions;
    private final List<Class<? extends Encoder>> encoders;
    private final List<Class<? extends Decoder>> decoders;
    private final SSLContext sslContext;
    private final Map<String, Object> userProperties = new ConcurrentHashMap();
    private final ClientEndpointConfig.Configurator configurator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultClientEndpointConfig(List<String> preferredSubprotocols, List<Extension> extensions, List<Class<? extends Encoder>> encoders, List<Class<? extends Decoder>> decoders, SSLContext sslContext, ClientEndpointConfig.Configurator configurator) {
        this.preferredSubprotocols = preferredSubprotocols;
        this.extensions = extensions;
        this.encoders = encoders;
        this.decoders = decoders;
        this.sslContext = sslContext;
        this.configurator = configurator;
    }

    @Override // jakarta.websocket.ClientEndpointConfig
    public List<String> getPreferredSubprotocols() {
        return this.preferredSubprotocols;
    }

    @Override // jakarta.websocket.ClientEndpointConfig
    public List<Extension> getExtensions() {
        return this.extensions;
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Encoder>> getEncoders() {
        return this.encoders;
    }

    @Override // jakarta.websocket.EndpointConfig
    public List<Class<? extends Decoder>> getDecoders() {
        return this.decoders;
    }

    @Override // jakarta.websocket.ClientEndpointConfig
    public SSLContext getSSLContext() {
        return this.sslContext;
    }

    @Override // jakarta.websocket.EndpointConfig
    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    @Override // jakarta.websocket.ClientEndpointConfig
    public ClientEndpointConfig.Configurator getConfigurator() {
        return this.configurator;
    }
}
