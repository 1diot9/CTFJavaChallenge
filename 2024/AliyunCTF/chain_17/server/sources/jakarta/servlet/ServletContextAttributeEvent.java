package jakarta.servlet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletContextAttributeEvent.class */
public class ServletContextAttributeEvent extends ServletContextEvent {
    private static final long serialVersionUID = 1;
    private final String name;
    private final Object value;

    public ServletContextAttributeEvent(ServletContext source, String name, Object value) {
        super(source);
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
