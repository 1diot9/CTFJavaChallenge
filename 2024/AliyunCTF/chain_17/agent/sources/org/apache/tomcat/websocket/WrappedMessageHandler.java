package org.apache.tomcat.websocket;

import jakarta.websocket.MessageHandler;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WrappedMessageHandler.class */
public interface WrappedMessageHandler {
    long getMaxMessageSize();

    MessageHandler getWrappedHandler();
}
