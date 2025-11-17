package org.apache.catalina.core;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import java.util.Enumeration;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardWrapperFacade.class */
public final class StandardWrapperFacade implements ServletConfig {
    private final ServletConfig config;
    private ServletContext context = null;

    public StandardWrapperFacade(StandardWrapper config) {
        this.config = config;
    }

    @Override // jakarta.servlet.ServletConfig
    public String getServletName() {
        return this.config.getServletName();
    }

    @Override // jakarta.servlet.ServletConfig
    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = this.config.getServletContext();
            if (this.context instanceof ApplicationContext) {
                this.context = ((ApplicationContext) this.context).getFacade();
            }
        }
        return this.context;
    }

    @Override // jakarta.servlet.ServletConfig
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    }

    @Override // jakarta.servlet.ServletConfig
    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    }
}
