package org.apache.coyote.http2;

import java.nio.ByteBuffer;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/RecycledStream.class */
class RecycledStream extends AbstractNonZeroStream {
    private final String connectionId;
    private int remainingFlowControlWindow;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecycledStream(String connectionId, Integer identifier, StreamStateMachine state, int remainingFlowControlWindow) {
        super(identifier, state);
        this.connectionId = connectionId;
        this.remainingFlowControlWindow = remainingFlowControlWindow;
    }

    @Override // org.apache.coyote.http2.AbstractStream
    String getConnectionId() {
        return this.connectionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public void incrementWindowSize(int increment) throws Http2Exception {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    public void receivedData(int payloadSize) throws ConnectionException {
        this.remainingFlowControlWindow -= payloadSize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    public ByteBuffer getInputByteBuffer() {
        if (this.remainingFlowControlWindow < 0) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return null;
    }
}
