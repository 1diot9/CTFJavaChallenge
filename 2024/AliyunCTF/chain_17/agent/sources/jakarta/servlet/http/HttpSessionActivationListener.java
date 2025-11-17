package jakarta.servlet.http;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionActivationListener.class */
public interface HttpSessionActivationListener extends EventListener {
    default void sessionWillPassivate(HttpSessionEvent se) {
    }

    default void sessionDidActivate(HttpSessionEvent se) {
    }
}
