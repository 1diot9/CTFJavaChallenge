package jakarta.servlet;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/Servlet.class */
public interface Servlet {
    void init(ServletConfig servletConfig) throws ServletException;

    ServletConfig getServletConfig();

    void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    String getServletInfo();

    void destroy();
}
