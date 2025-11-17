package org.springframework.boot.web.embedded.tomcat;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/tomcat/TomcatStarter.class */
class TomcatStarter implements ServletContainerInitializer {
    private static final Log logger = LogFactory.getLog((Class<?>) TomcatStarter.class);
    private final ServletContextInitializer[] initializers;
    private volatile Exception startUpException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatStarter(ServletContextInitializer[] initializers) {
        this.initializers = initializers;
    }

    @Override // jakarta.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        try {
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(servletContext);
            }
        } catch (Exception ex) {
            this.startUpException = ex;
            if (logger.isErrorEnabled()) {
                logger.error("Error starting Tomcat context. Exception: " + ex.getClass().getName() + ". Message: " + ex.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Exception getStartUpException() {
        return this.startUpException;
    }
}
