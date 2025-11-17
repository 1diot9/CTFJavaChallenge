package jakarta.websocket;

import java.util.Iterator;
import java.util.ServiceLoader;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:jakarta/websocket/ContainerProvider.class */
public abstract class ContainerProvider {
    private static final String DEFAULT_PROVIDER_CLASS_NAME = "org.apache.tomcat.websocket.WsWebSocketContainer";

    protected abstract WebSocketContainer getContainer();

    public static WebSocketContainer getWebSocketContainer() {
        WebSocketContainer result = null;
        ServiceLoader<ContainerProvider> serviceLoader = ServiceLoader.load(ContainerProvider.class);
        Iterator<ContainerProvider> iter = serviceLoader.iterator();
        while (result == null && iter.hasNext()) {
            result = iter.next().getContainer();
        }
        if (result == null) {
            try {
                result = (WebSocketContainer) Class.forName(DEFAULT_PROVIDER_CLASS_NAME).getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            }
        }
        return result;
    }
}
