package jakarta.websocket;

import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/EndpointConfig.class */
public interface EndpointConfig {
    List<Class<? extends Encoder>> getEncoders();

    List<Class<? extends Decoder>> getDecoders();

    Map<String, Object> getUserProperties();
}
