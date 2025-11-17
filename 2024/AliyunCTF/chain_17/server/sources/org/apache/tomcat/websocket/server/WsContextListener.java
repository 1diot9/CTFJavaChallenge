package org.apache.tomcat.websocket.server;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsContextListener.class */
public class WsContextListener implements ServletContextListener {
    @Override // jakarta.servlet.ServletContextListener
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        if (sc.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE) == null) {
            WsSci.init(sce.getServletContext(), false);
        }
    }

    @Override // jakarta.servlet.ServletContextListener
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        Object obj = sc.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
        if (obj instanceof WsServerContainer) {
            ((WsServerContainer) obj).destroy();
        }
    }
}
