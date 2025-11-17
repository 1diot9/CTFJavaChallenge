package org.apache.catalina.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/filters/RemoteHostFilter.class */
public final class RemoteHostFilter extends RequestFilter {
    private final Log log = LogFactory.getLog((Class<?>) RemoteHostFilter.class);

    @Override // org.apache.catalina.filters.RequestFilter, jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        process(request.getRemoteHost(), request, response, chain);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }
}
