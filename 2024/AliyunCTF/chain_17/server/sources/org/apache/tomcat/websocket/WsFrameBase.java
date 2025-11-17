package org.apache.tomcat.websocket;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.PongMessage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase.class */
public abstract class WsFrameBase {
    protected final WsSession wsSession;
    private final Transformation transformation;
    private ByteBuffer messageBufferBinary;
    private CharBuffer messageBufferText;
    private static final StringManager sm = StringManager.getManager((Class<?>) WsFrameBase.class);
    private static final AtomicReferenceFieldUpdater<WsFrameBase, ReadState> READ_STATE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(WsFrameBase.class, ReadState.class, "readState");
    private final ByteBuffer controlBufferBinary = ByteBuffer.allocate(125);
    private final CharBuffer controlBufferText = CharBuffer.allocate(125);
    private final CharsetDecoder utf8DecoderControl = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    private final CharsetDecoder utf8DecoderMessage = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    private boolean continuationExpected = false;
    private boolean textMessage = false;
    private MessageHandler binaryMsgHandler = null;
    private MessageHandler textMsgHandler = null;
    private boolean fin = false;
    private int rsv = 0;
    private byte opCode = 0;
    private final byte[] mask = new byte[4];
    private int maskIndex = 0;
    private long payloadLength = 0;
    private volatile long payloadWritten = 0;
    private volatile State state = State.NEW_FRAME;
    private volatile boolean open = true;
    private volatile ReadState readState = ReadState.WAITING;
    protected final ByteBuffer inputBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase$State.class */
    public enum State {
        NEW_FRAME,
        PARTIAL_HEADER,
        DATA
    }

    protected abstract boolean isMasked();

    protected abstract Log getLog();

    protected abstract void resumeProcessing();

    public WsFrameBase(WsSession wsSession, Transformation transformation) {
        Transformation finalTransformation;
        this.inputBuffer.position(0).limit(0);
        this.messageBufferBinary = ByteBuffer.allocate(wsSession.getMaxBinaryMessageBufferSize());
        this.messageBufferText = CharBuffer.allocate(wsSession.getMaxTextMessageBufferSize());
        wsSession.setWsFrame(this);
        this.wsSession = wsSession;
        if (isMasked()) {
            finalTransformation = new UnmaskTransformation();
        } else {
            finalTransformation = new NoopTransformation();
        }
        if (transformation == null) {
            this.transformation = finalTransformation;
        } else {
            transformation.setNext(finalTransformation);
            this.transformation = transformation;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processInputBuffer() throws IOException {
        while (!isSuspended()) {
            this.wsSession.updateLastActiveRead();
            if (this.state == State.NEW_FRAME) {
                if (processInitialHeader()) {
                    if (!this.open) {
                        throw new IOException(sm.getString("wsFrame.closed"));
                    }
                } else {
                    return;
                }
            }
            if (this.state != State.PARTIAL_HEADER || processRemainingHeader()) {
                if (this.state == State.DATA && !processData()) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private boolean processInitialHeader() throws IOException {
        if (this.inputBuffer.remaining() < 2) {
            return false;
        }
        int b = this.inputBuffer.get();
        this.fin = (b & 128) != 0;
        this.rsv = (b & 112) >>> 4;
        this.opCode = (byte) (b & 15);
        if (!this.transformation.validateRsv(this.rsv, this.opCode)) {
            throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.wrongRsv", Integer.valueOf(this.rsv), Integer.valueOf(this.opCode))));
        }
        if (Util.isControl(this.opCode)) {
            if (!this.fin) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlFragmented")));
            }
            if (this.opCode != 9 && this.opCode != 10 && this.opCode != 8) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", Integer.valueOf(this.opCode))));
            }
        } else {
            if (this.continuationExpected) {
                if (!Util.isContinuation(this.opCode)) {
                    throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.noContinuation")));
                }
            } else {
                try {
                    if (this.opCode == 2) {
                        this.textMessage = false;
                        int size = this.wsSession.getMaxBinaryMessageBufferSize();
                        if (size != this.messageBufferBinary.capacity()) {
                            this.messageBufferBinary = ByteBuffer.allocate(size);
                        }
                        this.binaryMsgHandler = this.wsSession.getBinaryMessageHandler();
                        this.textMsgHandler = null;
                    } else if (this.opCode == 1) {
                        this.textMessage = true;
                        int size2 = this.wsSession.getMaxTextMessageBufferSize();
                        if (size2 != this.messageBufferText.capacity()) {
                            this.messageBufferText = CharBuffer.allocate(size2);
                        }
                        this.binaryMsgHandler = null;
                        this.textMsgHandler = this.wsSession.getTextMessageHandler();
                    } else {
                        throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", Integer.valueOf(this.opCode))));
                    }
                } catch (IllegalStateException e) {
                    throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.sessionClosed")));
                }
            }
            this.continuationExpected = !this.fin;
        }
        if ((this.inputBuffer.get() & 128) == 0 && isMasked()) {
            throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.notMasked")));
        }
        this.payloadLength = r0 & Opcodes.LAND;
        this.state = State.PARTIAL_HEADER;
        if (getLog().isDebugEnabled()) {
            getLog().debug(sm.getString("wsFrame.partialHeaderComplete", Boolean.toString(this.fin), Integer.toString(this.rsv), Integer.toString(this.opCode), Long.toString(this.payloadLength)));
            return true;
        }
        return true;
    }

    private boolean processRemainingHeader() throws IOException {
        int headerLength;
        if (isMasked()) {
            headerLength = 4;
        } else {
            headerLength = 0;
        }
        if (this.payloadLength == 126) {
            headerLength += 2;
        } else if (this.payloadLength == 127) {
            headerLength += 8;
        }
        if (this.inputBuffer.remaining() < headerLength) {
            return false;
        }
        if (this.payloadLength == 126) {
            this.payloadLength = byteArrayToLong(this.inputBuffer.array(), this.inputBuffer.arrayOffset() + this.inputBuffer.position(), 2);
            this.inputBuffer.position(this.inputBuffer.position() + 2);
        } else if (this.payloadLength == 127) {
            this.payloadLength = byteArrayToLong(this.inputBuffer.array(), this.inputBuffer.arrayOffset() + this.inputBuffer.position(), 8);
            if (this.payloadLength < 0) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.payloadMsbInvalid")));
            }
            this.inputBuffer.position(this.inputBuffer.position() + 8);
        }
        if (Util.isControl(this.opCode)) {
            if (this.payloadLength > 125) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlPayloadTooBig", Long.valueOf(this.payloadLength))));
            }
            if (!this.fin) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.controlNoFin")));
            }
        }
        if (isMasked()) {
            this.inputBuffer.get(this.mask, 0, 4);
        }
        this.state = State.DATA;
        return true;
    }

    private boolean processData() throws IOException {
        boolean result;
        if (Util.isControl(this.opCode)) {
            result = processDataControl();
        } else if (this.textMessage) {
            if (this.textMsgHandler == null) {
                result = swallowInput();
            } else {
                result = processDataText();
            }
        } else if (this.binaryMsgHandler == null) {
            result = swallowInput();
        } else {
            result = processDataBinary();
        }
        if (result) {
            updateStats(this.payloadLength);
        }
        checkRoomPayload();
        return result;
    }

    protected void updateStats(long payloadLength) {
    }

    private boolean processDataControl() throws IOException {
        TransformationResult tr = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.controlBufferBinary);
        if (TransformationResult.UNDERFLOW.equals(tr)) {
            return false;
        }
        this.controlBufferBinary.flip();
        if (this.opCode == 8) {
            this.open = false;
            String reason = null;
            int code = CloseReason.CloseCodes.NORMAL_CLOSURE.getCode();
            if (this.controlBufferBinary.remaining() == 1) {
                this.controlBufferBinary.clear();
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.oneByteCloseCode")));
            }
            if (this.controlBufferBinary.remaining() > 1) {
                code = this.controlBufferBinary.getShort();
                if (this.controlBufferBinary.remaining() > 0) {
                    CoderResult cr = this.utf8DecoderControl.decode(this.controlBufferBinary, this.controlBufferText, true);
                    if (cr.isError()) {
                        this.controlBufferBinary.clear();
                        this.controlBufferText.clear();
                        throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidUtf8Close")));
                    }
                    this.controlBufferText.flip();
                    reason = this.controlBufferText.toString();
                }
            }
            this.wsSession.onClose(new CloseReason(Util.getCloseCode(code), reason));
        } else if (this.opCode == 9) {
            if (this.wsSession.isOpen()) {
                this.wsSession.getBasicRemote().sendPong(this.controlBufferBinary);
            }
        } else if (this.opCode == 10) {
            MessageHandler.Whole<PongMessage> mhPong = this.wsSession.getPongMessageHandler();
            try {
                if (mhPong != null) {
                    try {
                        mhPong.onMessage(new WsPongMessage(this.controlBufferBinary));
                        this.controlBufferBinary.clear();
                    } catch (Throwable t) {
                        handleThrowableOnSend(t);
                        this.controlBufferBinary.clear();
                    }
                }
            } catch (Throwable th) {
                this.controlBufferBinary.clear();
                throw th;
            }
        } else {
            this.controlBufferBinary.clear();
            throw new WsIOException(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, sm.getString("wsFrame.invalidOpCode", Integer.valueOf(this.opCode))));
        }
        this.controlBufferBinary.clear();
        newFrame();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendMessageText(boolean last) throws WsIOException {
        if (this.textMsgHandler instanceof WrappedMessageHandler) {
            long maxMessageSize = ((WrappedMessageHandler) this.textMsgHandler).getMaxMessageSize();
            if (maxMessageSize > -1 && this.messageBufferText.remaining() > maxMessageSize) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.messageTooBig", Long.valueOf(this.messageBufferText.remaining()), Long.valueOf(maxMessageSize))));
            }
        }
        try {
            try {
                if (this.textMsgHandler instanceof MessageHandler.Partial) {
                    ((MessageHandler.Partial) this.textMsgHandler).onMessage(this.messageBufferText.toString(), last);
                } else {
                    ((MessageHandler.Whole) this.textMsgHandler).onMessage(this.messageBufferText.toString());
                }
                this.messageBufferText.clear();
            } catch (Throwable t) {
                handleThrowableOnSend(t);
                this.messageBufferText.clear();
            }
        } catch (Throwable th) {
            this.messageBufferText.clear();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x00a7, code lost:            r8.messageBufferBinary.compact();     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00b6, code lost:            if (org.apache.tomcat.websocket.TransformationResult.OVERFLOW.equals(r9) == false) goto L56;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00bc, code lost:            return false;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x019d, code lost:            r8.messageBufferText.flip();        sendMessageText(true);        newMessage();     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x01af, code lost:            return true;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean processDataText() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 435
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.websocket.WsFrameBase.processDataText():boolean");
    }

    private boolean processDataBinary() throws IOException {
        TransformationResult moreData = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
        while (true) {
            TransformationResult tr = moreData;
            if (!TransformationResult.END_OF_FRAME.equals(tr)) {
                if (TransformationResult.UNDERFLOW.equals(tr)) {
                    return false;
                }
                if (!usePartial()) {
                    CloseReason cr = new CloseReason(CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.bufferTooSmall", Integer.valueOf(this.messageBufferBinary.capacity()), Long.valueOf(this.payloadLength)));
                    throw new WsIOException(cr);
                }
                this.messageBufferBinary.flip();
                ByteBuffer copy = ByteBuffer.allocate(this.messageBufferBinary.limit());
                copy.put(this.messageBufferBinary);
                copy.flip();
                sendMessageBinary(copy, false);
                this.messageBufferBinary.clear();
                moreData = this.transformation.getMoreData(this.opCode, this.fin, this.rsv, this.messageBufferBinary);
            } else {
                if (usePartial() || !this.continuationExpected) {
                    this.messageBufferBinary.flip();
                    ByteBuffer copy2 = ByteBuffer.allocate(this.messageBufferBinary.limit());
                    copy2.put(this.messageBufferBinary);
                    copy2.flip();
                    sendMessageBinary(copy2, !this.continuationExpected);
                    this.messageBufferBinary.clear();
                }
                if (this.continuationExpected) {
                    newFrame();
                    return true;
                }
                newMessage();
                return true;
            }
        }
    }

    private void handleThrowableOnSend(Throwable t) throws WsIOException {
        ExceptionUtils.handleThrowable(t);
        this.wsSession.getLocal().onError(this.wsSession, t);
        CloseReason cr = new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, sm.getString("wsFrame.ioeTriggeredClose"));
        throw new WsIOException(cr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendMessageBinary(ByteBuffer msg, boolean last) throws WsIOException {
        if (this.binaryMsgHandler instanceof WrappedMessageHandler) {
            long maxMessageSize = ((WrappedMessageHandler) this.binaryMsgHandler).getMaxMessageSize();
            if (maxMessageSize > -1 && msg.remaining() > maxMessageSize) {
                throw new WsIOException(new CloseReason(CloseReason.CloseCodes.TOO_BIG, sm.getString("wsFrame.messageTooBig", Long.valueOf(msg.remaining()), Long.valueOf(maxMessageSize))));
            }
        }
        try {
            if (this.binaryMsgHandler instanceof MessageHandler.Partial) {
                ((MessageHandler.Partial) this.binaryMsgHandler).onMessage(msg, last);
            } else {
                ((MessageHandler.Whole) this.binaryMsgHandler).onMessage(msg);
            }
        } catch (Throwable t) {
            handleThrowableOnSend(t);
        }
    }

    private void newMessage() {
        this.messageBufferBinary.clear();
        this.messageBufferText.clear();
        this.utf8DecoderMessage.reset();
        this.continuationExpected = false;
        newFrame();
    }

    private void newFrame() {
        if (this.inputBuffer.remaining() == 0) {
            this.inputBuffer.position(0).limit(0);
        }
        this.maskIndex = 0;
        this.payloadWritten = 0L;
        this.state = State.NEW_FRAME;
        checkRoomHeaders();
    }

    private void checkRoomHeaders() {
        if (this.inputBuffer.capacity() - this.inputBuffer.position() < 131) {
            makeRoom();
        }
    }

    private void checkRoomPayload() {
        if (((this.inputBuffer.capacity() - this.inputBuffer.position()) - this.payloadLength) + this.payloadWritten < 0) {
            makeRoom();
        }
    }

    private void makeRoom() {
        this.inputBuffer.compact();
        this.inputBuffer.flip();
    }

    private boolean usePartial() {
        if (Util.isControl(this.opCode)) {
            return false;
        }
        if (this.textMessage) {
            return this.textMsgHandler instanceof MessageHandler.Partial;
        }
        return this.binaryMsgHandler instanceof MessageHandler.Partial;
    }

    private boolean swallowInput() {
        long toSkip = Math.min(this.payloadLength - this.payloadWritten, this.inputBuffer.remaining());
        this.inputBuffer.position(this.inputBuffer.position() + ((int) toSkip));
        this.payloadWritten += toSkip;
        if (this.payloadWritten == this.payloadLength) {
            if (this.continuationExpected) {
                newFrame();
                return true;
            }
            newMessage();
            return true;
        }
        return false;
    }

    protected static long byteArrayToLong(byte[] b, int start, int len) throws IOException {
        if (len > 8) {
            throw new IOException(sm.getString("wsFrame.byteToLongFail", Long.valueOf(len)));
        }
        int shift = 0;
        long result = 0;
        for (int i = (start + len) - 1; i >= start; i--) {
            result += (b[i] & 255) << shift;
            shift += 8;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOpen() {
        return this.open;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Transformation getTransformation() {
        return this.transformation;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase$ReadState.class */
    public enum ReadState {
        WAITING(false),
        PROCESSING(false),
        SUSPENDING_WAIT(true),
        SUSPENDING_PROCESS(true),
        SUSPENDED(true),
        CLOSING(false);

        private final boolean isSuspended;

        ReadState(boolean isSuspended) {
            this.isSuspended = isSuspended;
        }

        public boolean isSuspended() {
            return this.isSuspended;
        }
    }

    public void suspend() {
        while (true) {
            switch (this.readState) {
                case WAITING:
                    if (!READ_STATE_UPDATER.compareAndSet(this, ReadState.WAITING, ReadState.SUSPENDING_WAIT)) {
                        break;
                    } else {
                        return;
                    }
                case PROCESSING:
                    if (!READ_STATE_UPDATER.compareAndSet(this, ReadState.PROCESSING, ReadState.SUSPENDING_PROCESS)) {
                        break;
                    } else {
                        return;
                    }
                case SUSPENDING_WAIT:
                    if (this.readState == ReadState.SUSPENDING_WAIT) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn(sm.getString("wsFrame.suspendRequested"));
                            return;
                        }
                        return;
                    }
                    break;
                case SUSPENDING_PROCESS:
                    if (this.readState == ReadState.SUSPENDING_PROCESS) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn(sm.getString("wsFrame.suspendRequested"));
                            return;
                        }
                        return;
                    }
                    break;
                case SUSPENDED:
                    if (this.readState == ReadState.SUSPENDED) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn(sm.getString("wsFrame.alreadySuspended"));
                            return;
                        }
                        return;
                    }
                    break;
                case CLOSING:
                    return;
                default:
                    throw new IllegalStateException(sm.getString("wsFrame.illegalReadState", this.state));
            }
        }
    }

    public void resume() {
        while (true) {
            switch (this.readState) {
                case WAITING:
                    if (this.readState == ReadState.WAITING) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn(sm.getString("wsFrame.alreadyResumed"));
                            return;
                        }
                        return;
                    }
                    break;
                case PROCESSING:
                    if (this.readState == ReadState.PROCESSING) {
                        if (getLog().isWarnEnabled()) {
                            getLog().warn(sm.getString("wsFrame.alreadyResumed"));
                            return;
                        }
                        return;
                    }
                    break;
                case SUSPENDING_WAIT:
                    if (!READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDING_WAIT, ReadState.WAITING)) {
                        break;
                    } else {
                        return;
                    }
                case SUSPENDING_PROCESS:
                    if (!READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDING_PROCESS, ReadState.PROCESSING)) {
                        break;
                    } else {
                        return;
                    }
                case SUSPENDED:
                    if (!READ_STATE_UPDATER.compareAndSet(this, ReadState.SUSPENDED, ReadState.WAITING)) {
                        break;
                    } else {
                        resumeProcessing();
                        return;
                    }
                case CLOSING:
                    return;
                default:
                    throw new IllegalStateException(sm.getString("wsFrame.illegalReadState", this.state));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isSuspended() {
        return this.readState.isSuspended();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ReadState getReadState() {
        return this.readState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void changeReadState(ReadState newState) {
        READ_STATE_UPDATER.set(this, newState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean changeReadState(ReadState oldState, ReadState newState) {
        return READ_STATE_UPDATER.compareAndSet(this, oldState, newState);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase$TerminalTransformation.class */
    private static abstract class TerminalTransformation implements Transformation {
        private TerminalTransformation() {
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public boolean validateRsvBits(int i) {
            return true;
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public Extension getExtensionResponse() {
            return null;
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public void setNext(Transformation t) {
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public boolean validateRsv(int rsv, byte opCode) {
            return rsv == 0;
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public void close() {
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase$NoopTransformation.class */
    private final class NoopTransformation extends TerminalTransformation {
        private NoopTransformation() {
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) {
            long toWrite = Math.min(Math.min(WsFrameBase.this.payloadLength - WsFrameBase.this.payloadWritten, WsFrameBase.this.inputBuffer.remaining()), dest.remaining());
            int orgLimit = WsFrameBase.this.inputBuffer.limit();
            WsFrameBase.this.inputBuffer.limit(WsFrameBase.this.inputBuffer.position() + ((int) toWrite));
            dest.put(WsFrameBase.this.inputBuffer);
            WsFrameBase.this.inputBuffer.limit(orgLimit);
            WsFrameBase.this.payloadWritten += toWrite;
            if (WsFrameBase.this.payloadWritten == WsFrameBase.this.payloadLength) {
                return TransformationResult.END_OF_FRAME;
            }
            if (WsFrameBase.this.inputBuffer.remaining() == 0) {
                return TransformationResult.UNDERFLOW;
            }
            return TransformationResult.OVERFLOW;
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public List<MessagePart> sendMessagePart(List<MessagePart> messageParts) {
            return messageParts;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsFrameBase$UnmaskTransformation.class */
    private final class UnmaskTransformation extends TerminalTransformation {
        private UnmaskTransformation() {
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) {
            while (WsFrameBase.this.payloadWritten < WsFrameBase.this.payloadLength && WsFrameBase.this.inputBuffer.remaining() > 0 && dest.hasRemaining()) {
                byte b = (byte) ((WsFrameBase.this.inputBuffer.get() ^ WsFrameBase.this.mask[WsFrameBase.this.maskIndex]) & 255);
                WsFrameBase.this.maskIndex++;
                if (WsFrameBase.this.maskIndex == 4) {
                    WsFrameBase.this.maskIndex = 0;
                }
                WsFrameBase.this.payloadWritten++;
                dest.put(b);
            }
            if (WsFrameBase.this.payloadWritten == WsFrameBase.this.payloadLength) {
                return TransformationResult.END_OF_FRAME;
            }
            if (WsFrameBase.this.inputBuffer.remaining() == 0) {
                return TransformationResult.UNDERFLOW;
            }
            return TransformationResult.OVERFLOW;
        }

        @Override // org.apache.tomcat.websocket.Transformation
        public List<MessagePart> sendMessagePart(List<MessagePart> messageParts) {
            return messageParts;
        }
    }
}
