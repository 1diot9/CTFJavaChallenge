package jakarta.servlet;

import java.util.EventListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletRequestAttributeListener.class */
public interface ServletRequestAttributeListener extends EventListener {
    default void attributeAdded(ServletRequestAttributeEvent srae) {
    }

    default void attributeRemoved(ServletRequestAttributeEvent srae) {
    }

    default void attributeReplaced(ServletRequestAttributeEvent srae) {
    }
}
