package jakarta.servlet;

import java.util.EventListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletContextAttributeListener.class */
public interface ServletContextAttributeListener extends EventListener {
    default void attributeAdded(ServletContextAttributeEvent scae) {
    }

    default void attributeRemoved(ServletContextAttributeEvent scae) {
    }

    default void attributeReplaced(ServletContextAttributeEvent scae) {
    }
}
