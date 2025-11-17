package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/WebdavFixFilter.class */
public class WebdavFixFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    protected static final StringManager sm = StringManager.getManager((Class<?>) WebdavFixFilter.class);
    private static final String UA_MINIDIR_START = "Microsoft-WebDAV-MiniRedir";
    private static final String UA_MINIDIR_5_1_2600 = "Microsoft-WebDAV-MiniRedir/5.1.2600";
    private static final String UA_MINIDIR_5_2_3790 = "Microsoft-WebDAV-MiniRedir/5.2.3790";

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ua = httpRequest.getHeader(HttpHeaders.USER_AGENT);
        if (ua == null || ua.length() == 0 || !ua.startsWith(UA_MINIDIR_START)) {
            chain.doFilter(request, response);
            return;
        }
        if (ua.startsWith(UA_MINIDIR_5_1_2600)) {
            httpResponse.sendRedirect(buildRedirect(httpRequest));
            return;
        }
        if (ua.startsWith(UA_MINIDIR_5_2_3790)) {
            if (!httpRequest.getContextPath().isEmpty()) {
                getServletContext().log(sm.getString("webDavFilter.xpRootContext"));
            }
            getServletContext().log(sm.getString("webDavFilter.xpProblem"));
            chain.doFilter(request, response);
            return;
        }
        httpResponse.sendRedirect(buildRedirect(httpRequest));
    }

    private String buildRedirect(HttpServletRequest request) {
        StringBuilder location = new StringBuilder(request.getRequestURL().length());
        location.append(request.getScheme());
        location.append("://");
        location.append(request.getServerName());
        location.append(':');
        location.append(request.getServerPort());
        location.append(request.getRequestURI());
        return location.toString();
    }
}
