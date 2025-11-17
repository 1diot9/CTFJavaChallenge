package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.webapp.ClassMatcher;
import org.eclipse.jetty.ee10.webapp.WebAppContext;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedWebAppContext.class */
class JettyEmbeddedWebAppContext extends WebAppContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public JettyEmbeddedWebAppContext() {
        setServerClassMatcher(new ClassMatcher("org.springframework.boot.loader."));
    }

    protected ServletHandler newServletHandler() {
        return new JettyEmbeddedServletHandler();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deferredInitialize() throws Exception {
        ((JettyEmbeddedServletHandler) getServletHandler()).deferredInitialize();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedWebAppContext$JettyEmbeddedServletHandler.class */
    private static final class JettyEmbeddedServletHandler extends ServletHandler {
        private JettyEmbeddedServletHandler() {
        }

        public void initialize() throws Exception {
        }

        void deferredInitialize() throws Exception {
            super.initialize();
        }
    }
}
