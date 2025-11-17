package org.apache.tomcat.websocket;

import jakarta.websocket.EncodeException;
import jakarta.websocket.RemoteEndpoint;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointBasic.class */
public class WsRemoteEndpointBasic extends WsRemoteEndpointBase implements RemoteEndpoint.Basic {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WsRemoteEndpointBasic(WsRemoteEndpointImplBase base) {
        super(base);
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public void sendText(String text) throws IOException {
        this.base.sendString(text);
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public void sendBinary(ByteBuffer data) throws IOException {
        this.base.sendBytes(data);
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public void sendText(String fragment, boolean isLast) throws IOException {
        this.base.sendPartialString(fragment, isLast);
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public void sendBinary(ByteBuffer partialByte, boolean isLast) throws IOException {
        this.base.sendPartialBytes(partialByte, isLast);
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public OutputStream getSendStream() throws IOException {
        return this.base.getSendStream();
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public Writer getSendWriter() throws IOException {
        return this.base.getSendWriter();
    }

    @Override // jakarta.websocket.RemoteEndpoint.Basic
    public void sendObject(Object o) throws IOException, EncodeException {
        this.base.sendObject(o);
    }
}
