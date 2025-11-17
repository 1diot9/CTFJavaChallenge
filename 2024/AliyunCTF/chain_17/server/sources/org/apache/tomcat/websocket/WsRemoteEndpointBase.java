package org.apache.tomcat.websocket;

import jakarta.websocket.RemoteEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointBase.class */
public abstract class WsRemoteEndpointBase implements RemoteEndpoint {
    protected final WsRemoteEndpointImplBase base;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsRemoteEndpointBase(WsRemoteEndpointImplBase base) {
        this.base = base;
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public final void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        this.base.setBatchingAllowed(batchingAllowed);
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public final boolean getBatchingAllowed() {
        return this.base.getBatchingAllowed();
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public final void flushBatch() throws IOException {
        this.base.flushBatch();
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public final void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPing(applicationData);
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public final void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        this.base.sendPong(applicationData);
    }
}
