package jakarta.servlet.http;

import java.util.EventObject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionEvent.class */
public class HttpSessionEvent extends EventObject {
    private static final long serialVersionUID = 1;

    public HttpSessionEvent(HttpSession source) {
        super(source);
    }

    public HttpSession getSession() {
        return (HttpSession) super.getSource();
    }
}
