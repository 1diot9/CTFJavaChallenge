package org.apache.tomcat.websocket.server;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsSessionListener.class */
public class WsSessionListener implements HttpSessionListener {
    private final WsServerContainer wsServerContainer;

    public WsSessionListener(WsServerContainer wsServerContainer) {
        this.wsServerContainer = wsServerContainer;
    }

    @Override // jakarta.servlet.http.HttpSessionListener
    public void sessionDestroyed(HttpSessionEvent se) {
        this.wsServerContainer.closeAuthenticatedSession(se.getSession().getId());
    }
}
