package jakarta.servlet.http;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionListener.class */
public interface HttpSessionListener extends EventListener {
    default void sessionCreated(HttpSessionEvent se) {
    }

    default void sessionDestroyed(HttpSessionEvent se) {
    }
}
