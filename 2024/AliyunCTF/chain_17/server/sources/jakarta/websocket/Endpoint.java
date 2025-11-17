package jakarta.websocket;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/Endpoint.class */
public abstract class Endpoint {
    public abstract void onOpen(Session session, EndpointConfig endpointConfig);

    public void onClose(Session session, CloseReason closeReason) {
    }

    public void onError(Session session, Throwable throwable) {
    }
}
