package org.apache.coyote.http2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/StreamStateMachine.class */
class StreamStateMachine {
    private static final Log log = LogFactory.getLog((Class<?>) StreamStateMachine.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) StreamStateMachine.class);
    private final String connectionId;
    private final String streamId;
    private State state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamStateMachine(String connectionId, String streamId) {
        this.connectionId = connectionId;
        this.streamId = streamId;
        stateChange(null, State.IDLE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void sentPushPromise() {
        stateChange(State.IDLE, State.RESERVED_LOCAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void sentHeaders() {
        stateChange(State.RESERVED_LOCAL, State.HALF_CLOSED_REMOTE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void receivedStartOfHeaders() {
        stateChange(State.IDLE, State.OPEN);
        stateChange(State.RESERVED_REMOTE, State.HALF_CLOSED_LOCAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void sentEndOfStream() {
        stateChange(State.OPEN, State.HALF_CLOSED_LOCAL);
        stateChange(State.HALF_CLOSED_REMOTE, State.CLOSED_TX);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void receivedEndOfStream() {
        stateChange(State.OPEN, State.HALF_CLOSED_REMOTE);
        stateChange(State.HALF_CLOSED_LOCAL, State.CLOSED_RX);
    }

    public synchronized void sendReset() {
        if (this.state == State.IDLE) {
            throw new IllegalStateException(sm.getString("streamStateMachine.debug.change", this.connectionId, this.streamId, this.state));
        }
        if (this.state.canReset()) {
            stateChange(this.state, State.CLOSED_RST_TX);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void receivedReset() {
        stateChange(this.state, State.CLOSED_RST_RX);
    }

    private void stateChange(State oldState, State newState) {
        if (this.state == oldState) {
            this.state = newState;
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("streamStateMachine.debug.change", this.connectionId, this.streamId, oldState, newState));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void checkFrameType(FrameType frameType) throws Http2Exception {
        if (!isFrameTypePermitted(frameType)) {
            if (this.state.connectionErrorForInvalidFrame) {
                throw new ConnectionException(sm.getString("streamStateMachine.invalidFrame", this.connectionId, this.streamId, this.state, frameType), this.state.errorCodeForInvalidFrame);
            }
            throw new StreamException(sm.getString("streamStateMachine.invalidFrame", this.connectionId, this.streamId, this.state, frameType), this.state.errorCodeForInvalidFrame, Integer.parseInt(this.streamId));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean isFrameTypePermitted(FrameType frameType) {
        return this.state.isFrameTypePermitted(frameType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean isActive() {
        return this.state.isActive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean canRead() {
        return this.state.canRead();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean canWrite() {
        return this.state.canWrite();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized boolean isClosedFinal() {
        return this.state == State.CLOSED_FINAL;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized void closeIfIdle() {
        stateChange(State.IDLE, State.CLOSED_FINAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final synchronized String getCurrentStateName() {
        return this.state.name();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/StreamStateMachine$State.class */
    public enum State {
        IDLE(false, false, false, true, Http2Error.PROTOCOL_ERROR, FrameType.HEADERS, FrameType.PRIORITY),
        OPEN(true, true, true, true, Http2Error.PROTOCOL_ERROR, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        RESERVED_LOCAL(false, false, true, true, Http2Error.PROTOCOL_ERROR, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        RESERVED_REMOTE(false, true, true, true, Http2Error.PROTOCOL_ERROR, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST),
        HALF_CLOSED_LOCAL(true, false, true, true, Http2Error.PROTOCOL_ERROR, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        HALF_CLOSED_REMOTE(false, true, true, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        CLOSED_RX(false, false, false, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY),
        CLOSED_TX(false, false, false, true, Http2Error.STREAM_CLOSED, FrameType.PRIORITY, FrameType.RST, FrameType.WINDOW_UPDATE),
        CLOSED_RST_RX(false, false, false, false, Http2Error.STREAM_CLOSED, FrameType.PRIORITY),
        CLOSED_RST_TX(false, false, false, false, Http2Error.STREAM_CLOSED, FrameType.DATA, FrameType.HEADERS, FrameType.PRIORITY, FrameType.RST, FrameType.PUSH_PROMISE, FrameType.WINDOW_UPDATE),
        CLOSED_FINAL(false, false, false, true, Http2Error.PROTOCOL_ERROR, FrameType.PRIORITY);

        private final boolean canRead;
        private final boolean canWrite;
        private final boolean canReset;
        private final boolean connectionErrorForInvalidFrame;
        private final Http2Error errorCodeForInvalidFrame;
        private final Set<FrameType> frameTypesPermitted;

        State(boolean canRead, boolean canWrite, boolean canReset, boolean connectionErrorForInvalidFrame, Http2Error errorCode, FrameType... frameTypes) {
            this.canRead = canRead;
            this.canWrite = canWrite;
            this.canReset = canReset;
            this.connectionErrorForInvalidFrame = connectionErrorForInvalidFrame;
            this.errorCodeForInvalidFrame = errorCode;
            this.frameTypesPermitted = new HashSet(Arrays.asList(frameTypes));
        }

        public boolean isActive() {
            return this.canWrite || this.canRead;
        }

        public boolean canRead() {
            return this.canRead;
        }

        public boolean canWrite() {
            return this.canWrite;
        }

        public boolean canReset() {
            return this.canReset;
        }

        public boolean isFrameTypePermitted(FrameType frameType) {
            return this.frameTypesPermitted.contains(frameType);
        }
    }
}
