package jakarta.servlet;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/Filter.class */
public interface Filter {
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;

    default void init(FilterConfig filterConfig) throws ServletException {
    }

    default void destroy() {
    }
}
