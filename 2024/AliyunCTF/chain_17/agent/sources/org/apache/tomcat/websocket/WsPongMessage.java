package org.apache.tomcat.websocket;

import jakarta.websocket.PongMessage;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsPongMessage.class */
public class WsPongMessage implements PongMessage {
    private final ByteBuffer applicationData;

    public WsPongMessage(ByteBuffer applicationData) {
        byte[] dst = new byte[applicationData.limit()];
        applicationData.get(dst);
        this.applicationData = ByteBuffer.wrap(dst);
    }

    @Override // jakarta.websocket.PongMessage
    public ByteBuffer getApplicationData() {
        return this.applicationData;
    }
}
