package jakarta.servlet.http;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionBindingListener.class */
public interface HttpSessionBindingListener extends EventListener {
    default void valueBound(HttpSessionBindingEvent event) {
    }

    default void valueUnbound(HttpSessionBindingEvent event) {
    }
}
