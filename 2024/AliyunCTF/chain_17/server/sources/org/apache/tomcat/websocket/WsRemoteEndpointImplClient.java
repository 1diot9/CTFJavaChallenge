package org.apache.tomcat.websocket;

import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplClient.class */
public class WsRemoteEndpointImplClient extends WsRemoteEndpointImplBase {
    private final AsyncChannelWrapper channel;
    private final ReentrantLock lock = new ReentrantLock();

    public WsRemoteEndpointImplClient(AsyncChannelWrapper channel) {
        this.channel = channel;
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected boolean isMasked() {
        return true;
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected void doWrite(SendHandler handler, long blockingWriteTimeoutExpiry, ByteBuffer... data) {
        long timeout;
        for (ByteBuffer byteBuffer : data) {
            if (blockingWriteTimeoutExpiry == -1) {
                timeout = getSendTimeout();
                if (timeout < 1) {
                    timeout = Long.MAX_VALUE;
                }
            } else {
                timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout < 0) {
                    SendResult sr = new SendResult(new IOException(sm.getString("wsRemoteEndpoint.writeTimeout")));
                    handler.onResult(sr);
                }
            }
            try {
                this.channel.write(byteBuffer).get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                handler.onResult(new SendResult(e));
                return;
            }
        }
        handler.onResult(SENDRESULT_OK);
    }

    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    protected void doClose() {
        this.channel.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsRemoteEndpointImplBase
    public ReentrantLock getLock() {
        return this.lock;
    }
}
