package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/jetty/JettyEmbeddedErrorHandler.class */
class JettyEmbeddedErrorHandler extends ErrorPageErrorHandler {
    public boolean errorPageForMethod(String method) {
        return true;
    }
}
