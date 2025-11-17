package org.springframework.boot.web.embedded.jetty;

import jakarta.servlet.ServletException;
import org.eclipse.jetty.ee10.webapp.AbstractConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/ServletContextInitializerConfiguration.class */
public class ServletContextInitializerConfiguration extends AbstractConfiguration {
    private final ServletContextInitializer[] initializers;

    public ServletContextInitializerConfiguration(ServletContextInitializer... initializers) {
        super(new AbstractConfiguration.Builder());
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers = initializers;
    }

    public void configure(WebAppContext context) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            callInitializers(context);
            Thread.currentThread().setContextClassLoader(classLoader);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(classLoader);
            throw th;
        }
    }

    private void callInitializers(WebAppContext context) throws ServletException {
        try {
            context.getContext().setExtendedListenerTypes(true);
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(context.getServletContext());
            }
        } finally {
            context.getContext().setExtendedListenerTypes(false);
        }
    }
}
