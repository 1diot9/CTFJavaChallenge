package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/ServletContextResourceLoader.class */
public class ServletContextResourceLoader extends DefaultResourceLoader {
    private final ServletContext servletContext;

    public ServletContextResourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        return new ServletContextResource(this.servletContext, path);
    }
}
