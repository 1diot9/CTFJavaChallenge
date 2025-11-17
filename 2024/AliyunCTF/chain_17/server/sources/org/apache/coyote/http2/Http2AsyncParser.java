package org.apache.coyote.http2;

import jakarta.servlet.http.WebConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.http2.Http2Parser;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncParser.class */
public class Http2AsyncParser extends Http2Parser {
    private final SocketWrapperBase<?> socketWrapper;
    private final Http2AsyncUpgradeHandler upgradeHandler;
    private volatile Throwable error;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2AsyncParser(String connectionId, Http2Parser.Input input, Http2Parser.Output output, SocketWrapperBase<?> socketWrapper, Http2AsyncUpgradeHandler upgradeHandler) {
        super(connectionId, input, output);
        this.error = null;
        this.socketWrapper = socketWrapper;
        socketWrapper.getSocketBufferHandler().expand(input.getMaxFrameSize());
        this.upgradeHandler = upgradeHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2Parser
    public void readConnectionPreface(WebConnection webConnection, Stream stream) throws Http2Exception {
        byte[] prefaceData = new byte[CLIENT_PREFACE_START.length];
        ByteBuffer preface = ByteBuffer.wrap(prefaceData);
        ByteBuffer header = ByteBuffer.allocate(9);
        ByteBuffer framePayload = ByteBuffer.allocate(this.input.getMaxFrameSize());
        PrefaceCompletionHandler handler = new PrefaceCompletionHandler(webConnection, stream, prefaceData, preface, header, framePayload);
        this.socketWrapper.read(SocketWrapperBase.BlockingMode.NON_BLOCK, this.socketWrapper.getReadTimeout(), TimeUnit.MILLISECONDS, null, handler, handler, preface, header, framePayload);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncParser$PrefaceCompletionHandler.class */
    private class PrefaceCompletionHandler extends FrameCompletionHandler {
        private final WebConnection webConnection;
        private final Stream stream;
        private final byte[] prefaceData;
        private volatile boolean prefaceValidated;

        private PrefaceCompletionHandler(WebConnection webConnection, Stream stream, byte[] prefaceData, ByteBuffer... buffers) {
            super(FrameType.SETTINGS, buffers);
            this.prefaceValidated = false;
            this.webConnection = webConnection;
            this.stream = stream;
            this.prefaceData = prefaceData;
        }

        @Override // org.apache.coyote.http2.Http2AsyncParser.FrameCompletionHandler, org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public SocketWrapperBase.CompletionHandlerCall callHandler(SocketWrapperBase.CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            if (offset != 0 || length != 3) {
                try {
                    throw new IllegalArgumentException(Http2Parser.sm.getString("http2Parser.invalidBuffers"));
                } catch (IllegalArgumentException e) {
                    Http2AsyncParser.this.error = e;
                    return SocketWrapperBase.CompletionHandlerCall.DONE;
                }
            }
            if (!this.prefaceValidated) {
                if (buffers[0].hasRemaining()) {
                    return SocketWrapperBase.CompletionHandlerCall.CONTINUE;
                }
                for (int i = 0; i < Http2Parser.CLIENT_PREFACE_START.length; i++) {
                    if (Http2Parser.CLIENT_PREFACE_START[i] != this.prefaceData[i]) {
                        Http2AsyncParser.this.error = new ProtocolException(Http2Parser.sm.getString("http2Parser.preface.invalid"));
                        return SocketWrapperBase.CompletionHandlerCall.DONE;
                    }
                }
                this.prefaceValidated = true;
            }
            return validate(state, buffers[1], buffers[2]);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.apache.coyote.http2.Http2AsyncParser.FrameCompletionHandler, java.nio.channels.CompletionHandler
        public void completed(Long result, Void attachment) {
            if (this.streamException || Http2AsyncParser.this.error == null) {
                ByteBuffer payload = this.buffers[2];
                payload.flip();
                try {
                    if (this.streamException) {
                        Http2AsyncParser.this.swallowPayload(this.streamId, this.frameTypeId, this.payloadSize, false, payload);
                    } else {
                        Http2AsyncParser.this.readSettingsFrame(this.flags, this.payloadSize, payload);
                    }
                } catch (IOException | RuntimeException | Http2Exception e) {
                    Http2AsyncParser.this.error = e;
                }
                if (payload.hasRemaining()) {
                    Http2AsyncParser.this.socketWrapper.unRead(payload);
                }
                Http2AsyncParser.this.upgradeHandler.processConnectionCallback(this.webConnection, this.stream);
            } else {
                Http2AsyncParser.this.upgradeHandler.closeConnection(new ConnectionException(Http2AsyncParser.this.error.getMessage(), Http2Error.PROTOCOL_ERROR, Http2AsyncParser.this.error));
            }
            Http2AsyncParser.this.upgradeHandler.upgradeDispatch(SocketEvent.OPEN_READ);
        }
    }

    @Override // org.apache.coyote.http2.Http2Parser
    protected boolean readFrame(boolean block, FrameType expected) throws IOException, Http2Exception {
        handleAsyncException();
        ByteBuffer header = ByteBuffer.allocate(9);
        ByteBuffer framePayload = ByteBuffer.allocate(this.input.getMaxFrameSize());
        FrameCompletionHandler handler = new FrameCompletionHandler(expected, header, framePayload);
        SocketWrapperBase.CompletionState state = this.socketWrapper.read(block ? SocketWrapperBase.BlockingMode.BLOCK : SocketWrapperBase.BlockingMode.NON_BLOCK, block ? this.socketWrapper.getReadTimeout() : 0L, TimeUnit.MILLISECONDS, null, handler, handler, header, framePayload);
        if (state == SocketWrapperBase.CompletionState.ERROR || state == SocketWrapperBase.CompletionState.INLINE) {
            handleAsyncException();
            return true;
        }
        return false;
    }

    private void handleAsyncException() throws IOException, Http2Exception {
        if (this.error != null) {
            Throwable error = this.error;
            this.error = null;
            if (error instanceof Http2Exception) {
                throw ((Http2Exception) error);
            }
            if (error instanceof IOException) {
                throw ((IOException) error);
            }
            if (error instanceof RuntimeException) {
                throw ((RuntimeException) error);
            }
            throw new RuntimeException(error);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncParser$FrameCompletionHandler.class */
    public class FrameCompletionHandler implements SocketWrapperBase.CompletionCheck, CompletionHandler<Long, Void> {
        private final FrameType expected;
        protected final ByteBuffer[] buffers;
        protected volatile int payloadSize;
        protected volatile int frameTypeId;
        protected volatile FrameType frameType;
        protected volatile int flags;
        protected volatile int streamId;
        private volatile boolean parsedFrameHeader = false;
        private volatile boolean validated = false;
        private volatile SocketWrapperBase.CompletionState state = null;
        protected volatile boolean streamException = false;

        private FrameCompletionHandler(FrameType expected, ByteBuffer... buffers) {
            this.expected = expected;
            this.buffers = buffers;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public SocketWrapperBase.CompletionHandlerCall callHandler(SocketWrapperBase.CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            if (offset != 0 || length != 2) {
                try {
                    throw new IllegalArgumentException(Http2Parser.sm.getString("http2Parser.invalidBuffers"));
                } catch (IllegalArgumentException e) {
                    Http2AsyncParser.this.error = e;
                    return SocketWrapperBase.CompletionHandlerCall.DONE;
                }
            }
            return validate(state, buffers[0], buffers[1]);
        }

        protected SocketWrapperBase.CompletionHandlerCall validate(SocketWrapperBase.CompletionState state, ByteBuffer frameHeaderBuffer, ByteBuffer payload) {
            if (!this.parsedFrameHeader) {
                if (frameHeaderBuffer.position() < 9) {
                    return SocketWrapperBase.CompletionHandlerCall.CONTINUE;
                }
                this.parsedFrameHeader = true;
                this.payloadSize = ByteUtil.getThreeBytes(frameHeaderBuffer, 0);
                this.frameTypeId = ByteUtil.getOneByte(frameHeaderBuffer, 3);
                this.frameType = FrameType.valueOf(this.frameTypeId);
                this.flags = ByteUtil.getOneByte(frameHeaderBuffer, 4);
                this.streamId = ByteUtil.get31Bits(frameHeaderBuffer, 5);
            }
            this.state = state;
            if (!this.validated) {
                this.validated = true;
                try {
                    Http2AsyncParser.this.validateFrame(this.expected, this.frameType, this.streamId, this.flags, this.payloadSize);
                } catch (StreamException e) {
                    Http2AsyncParser.this.error = e;
                    this.streamException = true;
                } catch (Http2Exception e2) {
                    Http2AsyncParser.this.error = e2;
                    return SocketWrapperBase.CompletionHandlerCall.DONE;
                }
            }
            if (payload.position() < this.payloadSize) {
                return SocketWrapperBase.CompletionHandlerCall.CONTINUE;
            }
            return SocketWrapperBase.CompletionHandlerCall.DONE;
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:39:0x004a. Please report as an issue. */
        @Override // java.nio.channels.CompletionHandler
        public void completed(Long result, Void attachment) {
            boolean continueParsing;
            if (this.streamException || Http2AsyncParser.this.error == null) {
                ByteBuffer payload = this.buffers[1];
                payload.flip();
                do {
                    try {
                        try {
                            continueParsing = false;
                            if (this.streamException) {
                                Http2AsyncParser.this.swallowPayload(this.streamId, this.frameTypeId, this.payloadSize, false, payload);
                            } else {
                                switch (this.frameType) {
                                    case DATA:
                                        Http2AsyncParser.this.readDataFrame(this.streamId, this.flags, this.payloadSize, payload);
                                        break;
                                    case HEADERS:
                                        Http2AsyncParser.this.readHeadersFrame(this.streamId, this.flags, this.payloadSize, payload);
                                        break;
                                    case PRIORITY:
                                        Http2AsyncParser.this.readPriorityFrame(this.streamId, payload);
                                        break;
                                    case RST:
                                        Http2AsyncParser.this.readRstFrame(this.streamId, payload);
                                        break;
                                    case SETTINGS:
                                        Http2AsyncParser.this.readSettingsFrame(this.flags, this.payloadSize, payload);
                                        break;
                                    case PUSH_PROMISE:
                                        Http2AsyncParser.this.readPushPromiseFrame(this.streamId, this.flags, this.payloadSize, payload);
                                        break;
                                    case PING:
                                        Http2AsyncParser.this.readPingFrame(this.flags, payload);
                                        break;
                                    case GOAWAY:
                                        Http2AsyncParser.this.readGoawayFrame(this.payloadSize, payload);
                                        break;
                                    case WINDOW_UPDATE:
                                        Http2AsyncParser.this.readWindowUpdateFrame(this.streamId, payload);
                                        break;
                                    case CONTINUATION:
                                        Http2AsyncParser.this.readContinuationFrame(this.streamId, this.flags, this.payloadSize, payload);
                                        break;
                                    case PRIORITY_UPDATE:
                                        Http2AsyncParser.this.readPriorityUpdateFrame(this.payloadSize, payload);
                                        break;
                                    case UNKNOWN:
                                        Http2AsyncParser.this.readUnknownFrame(this.streamId, this.frameTypeId, this.flags, this.payloadSize, payload);
                                        break;
                                }
                            }
                            if (!Http2AsyncParser.this.upgradeHandler.isOverheadLimitExceeded() && payload.remaining() >= 9) {
                                int position = payload.position();
                                this.payloadSize = ByteUtil.getThreeBytes(payload, position);
                                this.frameTypeId = ByteUtil.getOneByte(payload, position + 3);
                                this.frameType = FrameType.valueOf(this.frameTypeId);
                                this.flags = ByteUtil.getOneByte(payload, position + 4);
                                this.streamId = ByteUtil.get31Bits(payload, position + 5);
                                this.streamException = false;
                                if (payload.remaining() - 9 >= this.payloadSize) {
                                    continueParsing = true;
                                    payload.position(payload.position() + 9);
                                    try {
                                        Http2AsyncParser.this.validateFrame(null, this.frameType, this.streamId, this.flags, this.payloadSize);
                                    } catch (StreamException e) {
                                        Http2AsyncParser.this.error = e;
                                        this.streamException = true;
                                    } catch (Http2Exception e2) {
                                        Http2AsyncParser.this.error = e2;
                                        continueParsing = false;
                                    }
                                }
                            }
                        } catch (IOException | RuntimeException | Http2Exception e3) {
                            Http2AsyncParser.this.error = e3;
                            if (payload.hasRemaining()) {
                                Http2AsyncParser.this.socketWrapper.unRead(payload);
                            }
                        }
                    } finally {
                        if (payload.hasRemaining()) {
                            Http2AsyncParser.this.socketWrapper.unRead(payload);
                        }
                    }
                } while (continueParsing);
            }
            if (this.state == SocketWrapperBase.CompletionState.DONE) {
                Http2AsyncParser.this.upgradeHandler.upgradeDispatch(SocketEvent.OPEN_READ);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable e, Void attachment) {
            Http2AsyncParser.this.error = e;
            if (Http2Parser.log.isDebugEnabled()) {
                Http2Parser.log.debug(Http2Parser.sm.getString("http2Parser.error", Http2AsyncParser.this.connectionId, Integer.valueOf(this.streamId), this.frameType), e);
            }
            if (this.state == null || this.state == SocketWrapperBase.CompletionState.DONE) {
                Http2AsyncParser.this.upgradeHandler.upgradeDispatch(SocketEvent.ERROR);
            }
        }
    }
}
