package jakarta.servlet;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletRequestAttributeEvent.class */
public class ServletRequestAttributeEvent extends ServletRequestEvent {
    private static final long serialVersionUID = 1;
    private final String name;
    private final Object value;

    public ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value) {
        super(sc, request);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}
