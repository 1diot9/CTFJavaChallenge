package org.apache.tomcat.websocket;

import jakarta.websocket.MessageHandler;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/MessageHandlerResult.class */
public class MessageHandlerResult {
    private final MessageHandler handler;
    private final MessageHandlerResultType type;

    public MessageHandlerResult(MessageHandler handler, MessageHandlerResultType type) {
        this.handler = handler;
        this.type = type;
    }

    public MessageHandler getHandler() {
        return this.handler;
    }

    public MessageHandlerResultType getType() {
        return this.type;
    }
}
