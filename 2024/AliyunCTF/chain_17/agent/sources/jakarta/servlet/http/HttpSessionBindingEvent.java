package jakarta.servlet.http;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/http/HttpSessionBindingEvent.class */
public class HttpSessionBindingEvent extends HttpSessionEvent {
    private static final long serialVersionUID = 1;
    private final String name;
    private final Object value;

    public HttpSessionBindingEvent(HttpSession session, String name) {
        super(session);
        this.name = name;
        this.value = null;
    }

    public HttpSessionBindingEvent(HttpSession session, String name, Object value) {
        super(session);
        this.name = name;
        this.value = value;
    }

    @Override // jakarta.servlet.http.HttpSessionEvent
    public HttpSession getSession() {
        return super.getSession();
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
