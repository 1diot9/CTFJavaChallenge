package org.springframework.boot.web.servlet.context;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.ServletContextAwareProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/WebApplicationContextServletContextAwareProcessor.class */
public class WebApplicationContextServletContextAwareProcessor extends ServletContextAwareProcessor {
    private final ConfigurableWebApplicationContext webApplicationContext;

    public WebApplicationContextServletContextAwareProcessor(ConfigurableWebApplicationContext webApplicationContext) {
        Assert.notNull(webApplicationContext, "WebApplicationContext must not be null");
        this.webApplicationContext = webApplicationContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.ServletContextAwareProcessor
    public ServletContext getServletContext() {
        ServletContext servletContext = this.webApplicationContext.getServletContext();
        return servletContext != null ? servletContext : super.getServletContext();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.context.support.ServletContextAwareProcessor
    public ServletConfig getServletConfig() {
        ServletConfig servletConfig = this.webApplicationContext.getServletConfig();
        return servletConfig != null ? servletConfig : super.getServletConfig();
    }
}
