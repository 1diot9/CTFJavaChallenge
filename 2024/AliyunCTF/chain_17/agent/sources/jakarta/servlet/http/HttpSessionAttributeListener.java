package jakarta.servlet.http;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionAttributeListener.class */
public interface HttpSessionAttributeListener extends EventListener {
    default void attributeAdded(HttpSessionBindingEvent se) {
    }

    default void attributeRemoved(HttpSessionBindingEvent se) {
    }

    default void attributeReplaced(HttpSessionBindingEvent se) {
    }
}
