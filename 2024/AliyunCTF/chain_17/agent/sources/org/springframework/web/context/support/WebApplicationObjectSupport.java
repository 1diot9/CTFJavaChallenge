package org.springframework.web.context.support;

import jakarta.servlet.ServletContext;
import java.io.File;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/support/WebApplicationObjectSupport.class */
public abstract class WebApplicationObjectSupport extends ApplicationObjectSupport implements ServletContextAware {

    @Nullable
    private ServletContext servletContext;

    @Override // org.springframework.web.context.ServletContextAware
    public final void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            initServletContext(servletContext);
        }
    }

    @Override // org.springframework.context.support.ApplicationObjectSupport
    protected boolean isContextRequired() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        if (this.servletContext == null && (context instanceof WebApplicationContext)) {
            WebApplicationContext wac = (WebApplicationContext) context;
            this.servletContext = wac.getServletContext();
            if (this.servletContext != null) {
                initServletContext(this.servletContext);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initServletContext(ServletContext servletContext) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
        ApplicationContext ctx = getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            WebApplicationContext wac = (WebApplicationContext) ctx;
            return wac;
        }
        if (isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run in a WebApplicationContext but in: " + ctx);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        ServletContext servletContext = null;
        WebApplicationContext wac = getWebApplicationContext();
        if (wac != null) {
            servletContext = wac.getServletContext();
        }
        if (servletContext == null && isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }

    protected final File getTempDir() throws IllegalStateException {
        ServletContext servletContext = getServletContext();
        Assert.state(servletContext != null, "ServletContext is required");
        return WebUtils.getTempDir(servletContext);
    }
}
