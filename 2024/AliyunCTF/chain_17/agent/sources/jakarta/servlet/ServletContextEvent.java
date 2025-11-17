package jakarta.servlet;

import java.util.EventObject;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/ServletContextEvent.class */
public class ServletContextEvent extends EventObject {
    private static final long serialVersionUID = 1;

    public ServletContextEvent(ServletContext source) {
        super(source);
    }

    public ServletContext getServletContext() {
        return (ServletContext) super.getSource();
    }
}
