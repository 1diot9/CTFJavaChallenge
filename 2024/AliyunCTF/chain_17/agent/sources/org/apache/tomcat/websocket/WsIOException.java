package org.apache.tomcat.websocket;

import jakarta.websocket.CloseReason;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsIOException.class */
public class WsIOException extends IOException {
    private static final long serialVersionUID = 1;
    private final CloseReason closeReason;

    public WsIOException(CloseReason closeReason) {
        this.closeReason = closeReason;
    }

    public CloseReason getCloseReason() {
        return this.closeReason;
    }
}
