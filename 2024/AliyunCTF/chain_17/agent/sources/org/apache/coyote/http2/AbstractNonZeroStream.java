package org.apache.coyote.http2;

import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/AbstractNonZeroStream.class */
abstract class AbstractNonZeroStream extends AbstractStream {
    protected static final ByteBuffer ZERO_LENGTH_BYTEBUFFER = ByteBuffer.allocate(0);
    protected final StreamStateMachine state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract ByteBuffer getInputByteBuffer();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void receivedData(int i) throws Http2Exception;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractNonZeroStream(String connectionId, Integer identifier) {
        super(identifier);
        this.state = new StreamStateMachine(connectionId, getIdAsString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractNonZeroStream(Integer identifier, StreamStateMachine state) {
        super(identifier);
        this.state = state;
    }

    final boolean isClosedFinal() {
        return this.state.isClosedFinal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void checkState(FrameType frameType) throws Http2Exception {
        this.state.checkFrameType(frameType);
    }
}
