package org.springframework.web.servlet.resource;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/DefaultServletHttpRequestHandler.class */
public class DefaultServletHttpRequestHandler implements HttpRequestHandler, ServletContextAware {
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
    private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
    private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";
    private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";
    private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

    @Nullable
    private String defaultServletName;

    @Nullable
    private ServletContext servletContext;

    public void setDefaultServletName(String defaultServletName) {
        this.defaultServletName = defaultServletName;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        if (!StringUtils.hasText(this.defaultServletName)) {
            if (this.servletContext.getNamedDispatcher("default") != null) {
                this.defaultServletName = "default";
                return;
            }
            if (this.servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = GAE_DEFAULT_SERVLET_NAME;
                return;
            }
            if (this.servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
            } else if (this.servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
                this.defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
            } else {
                if (this.servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
                    this.defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
                    return;
                }
                throw new IllegalStateException("Unable to locate the default servlet for serving static content. Please set the 'defaultServletName' property explicitly.");
            }
        }
    }

    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state(this.servletContext != null, "No ServletContext set");
        RequestDispatcher rd = this.servletContext.getNamedDispatcher(this.defaultServletName);
        if (rd == null) {
            throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" + this.defaultServletName + "'");
        }
        if (request.getDispatcherType() != DispatcherType.INCLUDE) {
            rd.forward(request, response);
        } else {
            rd.include(request, response);
        }
    }
}
