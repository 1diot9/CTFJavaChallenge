package jakarta.servlet.http;

import java.util.EventListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionIdListener.class */
public interface HttpSessionIdListener extends EventListener {
    void sessionIdChanged(HttpSessionEvent httpSessionEvent, String str);
}
