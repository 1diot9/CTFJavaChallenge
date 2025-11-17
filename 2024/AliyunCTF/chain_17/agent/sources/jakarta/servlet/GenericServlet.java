package jakarta.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/GenericServlet.class */
public abstract class GenericServlet implements Servlet, ServletConfig, Serializable {
    private static final long serialVersionUID = 1;
    private transient ServletConfig config;

    @Override // jakarta.servlet.Servlet
    public abstract void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    @Override // jakarta.servlet.Servlet
    public void destroy() {
    }

    @Override // jakarta.servlet.ServletConfig
    public String getInitParameter(String name) {
        return getServletConfig().getInitParameter(name);
    }

    @Override // jakarta.servlet.ServletConfig
    public Enumeration<String> getInitParameterNames() {
        return getServletConfig().getInitParameterNames();
    }

    @Override // jakarta.servlet.Servlet
    public ServletConfig getServletConfig() {
        return this.config;
    }

    @Override // jakarta.servlet.ServletConfig
    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }

    @Override // jakarta.servlet.Servlet
    public String getServletInfo() {
        return "";
    }

    @Override // jakarta.servlet.Servlet
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        init();
    }

    public void init() throws ServletException {
    }

    public void log(String message) {
        getServletContext().log(getServletName() + ": " + message);
    }

    public void log(String message, Throwable t) {
        getServletContext().log(getServletName() + ": " + message, t);
    }

    @Override // jakarta.servlet.ServletConfig
    public String getServletName() {
        return this.config.getServletName();
    }
}
