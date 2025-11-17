package jakarta.websocket;

import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/HandshakeResponse.class */
public interface HandshakeResponse {
    public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

    Map<String, List<String>> getHeaders();
}
