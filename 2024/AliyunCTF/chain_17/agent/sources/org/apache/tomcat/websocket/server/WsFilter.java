package org.apache.tomcat.websocket.server;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsFilter.class */
public class WsFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    private transient WsServerContainer sc;

    @Override // jakarta.servlet.GenericFilter
    public void init() throws ServletException {
        this.sc = (WsServerContainer) getServletContext().getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
    }

    @Override // jakarta.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path;
        if (!this.sc.areEndpointsRegistered() || !UpgradeUtil.isWebSocketUpgradeRequest(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            path = req.getServletPath();
        } else {
            path = req.getServletPath() + pathInfo;
        }
        WsMappingResult mappingResult = this.sc.findMapping(path);
        if (mappingResult == null) {
            chain.doFilter(request, response);
        } else {
            UpgradeUtil.doUpgrade(this.sc, req, resp, mappingResult.getConfig(), mappingResult.getPathParams());
        }
    }
}
