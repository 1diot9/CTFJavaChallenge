package jakarta.websocket.server;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/server/HandshakeRequest.class */
public interface HandshakeRequest {
    public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
    public static final String SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

    Map<String, List<String>> getHeaders();

    Principal getUserPrincipal();

    URI getRequestURI();

    boolean isUserInRole(String str);

    Object getHttpSession();

    Map<String, List<String>> getParameterMap();

    String getQueryString();
}
