package jakarta.servlet;

import java.io.Serializable;
import java.util.Enumeration;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/GenericFilter.class */
public abstract class GenericFilter implements Filter, FilterConfig, Serializable {
    private static final long serialVersionUID = 1;
    private volatile FilterConfig filterConfig;

    @Override // jakarta.servlet.FilterConfig
    public String getInitParameter(String name) {
        return getFilterConfig().getInitParameter(name);
    }

    @Override // jakarta.servlet.FilterConfig
    public Enumeration<String> getInitParameterNames() {
        return getFilterConfig().getInitParameterNames();
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    @Override // jakarta.servlet.FilterConfig
    public ServletContext getServletContext() {
        return getFilterConfig().getServletContext();
    }

    @Override // jakarta.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        init();
    }

    public void init() throws ServletException {
    }

    @Override // jakarta.servlet.FilterConfig
    public String getFilterName() {
        return getFilterConfig().getFilterName();
    }
}
