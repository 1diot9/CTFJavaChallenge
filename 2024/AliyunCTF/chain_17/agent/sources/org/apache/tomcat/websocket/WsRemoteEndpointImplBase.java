package org.apache.tomcat.websocket;

import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import javax.naming.NamingException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.bcel.Const;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase.class */
public abstract class WsRemoteEndpointImplBase implements RemoteEndpoint {
    protected static final StringManager sm = StringManager.getManager((Class<?>) WsRemoteEndpointImplBase.class);
    protected static final SendResult SENDRESULT_OK = new SendResult();
    private WsSession wsSession;
    private final Log log = LogFactory.getLog((Class<?>) WsRemoteEndpointImplBase.class);
    private final StateMachine stateMachine = new StateMachine();
    private final IntermediateMessageHandler intermediateMessageHandler = new IntermediateMessageHandler(this);
    private Transformation transformation = null;
    protected final Semaphore messagePartInProgress = new Semaphore(1);
    private final Queue<MessagePart> messagePartQueue = new ArrayDeque();
    private final Object messagePartLock = new Object();
    private volatile boolean closed = false;
    private boolean fragmented = false;
    private boolean nextFragmented = false;
    private boolean text = false;
    private boolean nextText = false;
    private final ByteBuffer headerBuffer = ByteBuffer.allocate(14);
    private final ByteBuffer outputBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final CharsetEncoder encoder = new Utf8Encoder();
    private final ByteBuffer encoderBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final AtomicBoolean batchingAllowed = new AtomicBoolean(false);
    private volatile long sendTimeout = -1;
    private List<EncoderEntry> encoderEntries = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$State.class */
    public enum State {
        OPEN,
        STREAM_WRITING,
        WRITER_WRITING,
        BINARY_PARTIAL_WRITING,
        BINARY_PARTIAL_READY,
        BINARY_FULL_WRITING,
        TEXT_PARTIAL_WRITING,
        TEXT_PARTIAL_READY,
        TEXT_FULL_WRITING
    }

    protected abstract void doWrite(SendHandler sendHandler, long j, ByteBuffer... byteBufferArr);

    protected abstract boolean isMasked();

    protected abstract void doClose();

    protected abstract Lock getLock();

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public long getSendTimeout() {
        return this.sendTimeout;
    }

    public void setSendTimeout(long timeout) {
        this.sendTimeout = timeout;
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public void setBatchingAllowed(boolean batchingAllowed) throws IOException {
        boolean oldValue = this.batchingAllowed.getAndSet(batchingAllowed);
        if (oldValue && !batchingAllowed) {
            flushBatch();
        }
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public boolean getBatchingAllowed() {
        return this.batchingAllowed.get();
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public void flushBatch() throws IOException {
        sendMessageBlock((byte) 24, null, true);
    }

    public void sendBytes(ByteBuffer data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryStart();
        sendMessageBlock((byte) 2, data, true);
        this.stateMachine.complete(true);
    }

    public Future<Void> sendBytesByFuture(ByteBuffer data) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendBytesByCompletion(data, f2sh);
        return f2sh;
    }

    public void sendBytesByCompletion(ByteBuffer data, SendHandler handler) {
        if (data == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        StateUpdateSendHandler sush = new StateUpdateSendHandler(handler, this.stateMachine);
        this.stateMachine.binaryStart();
        startMessage((byte) 2, data, true, sush);
    }

    public void sendPartialBytes(ByteBuffer partialByte, boolean last) throws IOException {
        if (partialByte == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.binaryPartialStart();
        sendMessageBlock((byte) 2, partialByte, last);
        this.stateMachine.complete(last);
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        sendMessageBlock((byte) 9, applicationData, true);
    }

    @Override // jakarta.websocket.RemoteEndpoint
    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        if (applicationData.remaining() > 125) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.tooMuchData"));
        }
        sendMessageBlock((byte) 10, applicationData, true);
    }

    public void sendString(String text) throws IOException {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textStart();
        sendMessageBlock(CharBuffer.wrap(text), true);
    }

    public Future<Void> sendStringByFuture(String text) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendStringByCompletion(text, f2sh);
        return f2sh;
    }

    public void sendStringByCompletion(String text, SendHandler handler) {
        if (text == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        this.stateMachine.textStart();
        TextMessageSendHandler tmsh = new TextMessageSendHandler(handler, CharBuffer.wrap(text), true, this.encoder, this.encoderBuffer, this);
        tmsh.write();
    }

    public void sendPartialString(String fragment, boolean isLast) throws IOException {
        if (fragment == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        this.stateMachine.textPartialStart();
        sendMessageBlock(CharBuffer.wrap(fragment), isLast);
    }

    public OutputStream getSendStream() {
        this.stateMachine.streamStart();
        return new WsOutputStream(this);
    }

    public Writer getSendWriter() {
        this.stateMachine.writeStart();
        return new WsWriter(this);
    }

    void sendMessageBlock(CharBuffer part, boolean last) throws IOException {
        long timeoutExpiry = getTimeoutExpiry();
        boolean isDone = false;
        while (!isDone) {
            this.encoderBuffer.clear();
            CoderResult cr = this.encoder.encode(part, this.encoderBuffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            isDone = !cr.isOverflow();
            this.encoderBuffer.flip();
            sendMessageBlock((byte) 1, this.encoderBuffer, last && isDone, timeoutExpiry);
        }
        this.stateMachine.complete(last);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last) throws IOException {
        sendMessageBlock(opCode, payload, last, getTimeoutExpiry());
    }

    private long getTimeoutExpiry() {
        long timeout = getBlockingSendTimeout();
        if (timeout < 0) {
            return Long.MAX_VALUE;
        }
        return System.currentTimeMillis() + timeout;
    }

    private void sendMessageBlock(byte opCode, ByteBuffer payload, boolean last, long timeoutExpiry) throws IOException {
        this.wsSession.updateLastActiveWrite();
        BlockingSendHandler bsh = new BlockingSendHandler();
        List<MessagePart> messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, bsh, bsh, timeoutExpiry));
        List<MessagePart> messageParts2 = this.transformation.sendMessagePart(messageParts);
        if (messageParts2.size() == 0) {
            return;
        }
        try {
            if (!acquireMessagePartInProgressSemaphore(opCode, timeoutExpiry)) {
                String msg = sm.getString("wsRemoteEndpoint.acquireTimeout");
                this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg), true);
                throw new SocketTimeoutException(msg);
            }
            for (MessagePart mp : messageParts2) {
                try {
                    writeMessagePart(mp);
                    if (!bsh.getSendResult().isOK()) {
                        this.messagePartInProgress.release();
                        Throwable t = bsh.getSendResult().getException();
                        this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, t.getMessage()), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, t.getMessage()), true);
                        throw new IOException(t);
                    }
                    this.fragmented = this.nextFragmented;
                    this.text = this.nextText;
                } catch (Throwable t2) {
                    ExceptionUtils.handleThrowable(t2);
                    this.messagePartInProgress.release();
                    this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, t2.getMessage()), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, t2.getMessage()), true);
                    throw t2;
                }
            }
            if (payload != null) {
                payload.clear();
            }
            endMessage(null, null);
        } catch (InterruptedException e) {
            String msg2 = sm.getString("wsRemoteEndpoint.sendInterrupt");
            this.wsSession.doClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, msg2), new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, msg2), true);
            throw new IOException(msg2, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean acquireMessagePartInProgressSemaphore(byte opCode, long timeoutExpiry) throws InterruptedException {
        long timeout = timeoutExpiry - System.currentTimeMillis();
        return this.messagePartInProgress.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

    void startMessage(byte opCode, ByteBuffer payload, boolean last, SendHandler handler) {
        this.wsSession.updateLastActiveWrite();
        List<MessagePart> messageParts = new ArrayList<>();
        messageParts.add(new MessagePart(last, 0, opCode, payload, this.intermediateMessageHandler, new EndMessageHandler(this, handler), -1L));
        try {
            List<MessagePart> messageParts2 = this.transformation.sendMessagePart(messageParts);
            if (messageParts2.size() == 0) {
                handler.onResult(new SendResult());
                return;
            }
            MessagePart mp = messageParts2.remove(0);
            boolean doWrite = false;
            synchronized (this.messagePartLock) {
                if (8 == mp.getOpCode() && getBatchingAllowed()) {
                    this.log.warn(sm.getString("wsRemoteEndpoint.flushOnCloseFailed"));
                }
                if (this.messagePartInProgress.tryAcquire()) {
                    doWrite = true;
                } else {
                    this.messagePartQueue.add(mp);
                }
                this.messagePartQueue.addAll(messageParts2);
            }
            if (doWrite) {
                writeMessagePart(mp);
            }
        } catch (IOException ioe) {
            handler.onResult(new SendResult(ioe));
        }
    }

    void endMessage(SendHandler handler, SendResult result) {
        MessagePart mpNext;
        boolean doWrite = false;
        synchronized (this.messagePartLock) {
            this.fragmented = this.nextFragmented;
            this.text = this.nextText;
            mpNext = this.messagePartQueue.poll();
            if (mpNext == null) {
                this.messagePartInProgress.release();
            } else if (!this.closed) {
                doWrite = true;
            }
        }
        if (doWrite) {
            writeMessagePart(mpNext);
        }
        this.wsSession.updateLastActiveWrite();
        if (handler != null) {
            handler.onResult(result);
        }
    }

    void writeMessagePart(MessagePart mp) {
        boolean first;
        byte[] mask;
        if (this.closed) {
            throw new IllegalStateException(sm.getString("wsRemoteEndpoint.closed"));
        }
        if (24 == mp.getOpCode()) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            this.outputBuffer.flip();
            SendHandler flushHandler = new OutputBufferFlushSendHandler(this.outputBuffer, mp.getEndHandler());
            doWrite(flushHandler, mp.getBlockingWriteTimeoutExpiry(), this.outputBuffer);
            return;
        }
        if (Util.isControl(mp.getOpCode())) {
            this.nextFragmented = this.fragmented;
            this.nextText = this.text;
            if (mp.getOpCode() == 8) {
                this.closed = true;
            }
            first = true;
        } else {
            boolean isText = Util.isText(mp.getOpCode());
            if (this.fragmented) {
                if (this.text != isText) {
                    throw new IllegalStateException(sm.getString("wsRemoteEndpoint.changeType"));
                }
                this.nextText = this.text;
                this.nextFragmented = !mp.isFin();
                first = false;
            } else {
                if (mp.isFin()) {
                    this.nextFragmented = false;
                } else {
                    this.nextFragmented = true;
                    this.nextText = isText;
                }
                first = true;
            }
        }
        if (isMasked()) {
            mask = Util.generateMask();
        } else {
            mask = null;
        }
        int payloadSize = mp.getPayload().remaining();
        this.headerBuffer.clear();
        writeHeader(this.headerBuffer, mp.isFin(), mp.getRsv(), mp.getOpCode(), isMasked(), mp.getPayload(), mask, first);
        this.headerBuffer.flip();
        if (getBatchingAllowed() || isMasked()) {
            OutputBufferSendHandler obsh = new OutputBufferSendHandler(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload(), mask, this.outputBuffer, !getBatchingAllowed(), this);
            obsh.write();
        } else {
            doWrite(mp.getEndHandler(), mp.getBlockingWriteTimeoutExpiry(), this.headerBuffer, mp.getPayload());
        }
        updateStats(payloadSize);
    }

    protected void updateStats(long payloadLength) {
    }

    private long getBlockingSendTimeout() {
        Object obj = this.wsSession.getUserProperties().get(Constants.BLOCKING_SEND_TIMEOUT_PROPERTY);
        Long userTimeout = null;
        if (obj instanceof Long) {
            userTimeout = (Long) obj;
        }
        if (userTimeout == null) {
            return Constants.DEFAULT_BLOCKING_SEND_TIMEOUT;
        }
        return userTimeout.longValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$EndMessageHandler.class */
    public static class EndMessageHandler implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;
        private final SendHandler handler;

        EndMessageHandler(WsRemoteEndpointImplBase endpoint, SendHandler handler) {
            this.endpoint = endpoint;
            this.handler = handler;
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            this.endpoint.endMessage(this.handler, result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$IntermediateMessageHandler.class */
    public static class IntermediateMessageHandler implements SendHandler {
        private final WsRemoteEndpointImplBase endpoint;

        IntermediateMessageHandler(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            this.endpoint.endMessage(null, result);
        }
    }

    public void sendObject(Object obj) throws IOException, EncodeException {
        if (obj == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        Encoder encoder = findEncoder(obj);
        if (encoder == null && Util.isPrimitive(obj.getClass())) {
            String msg = obj.toString();
            sendString(msg);
            return;
        }
        if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
            ByteBuffer msg2 = ByteBuffer.wrap((byte[]) obj);
            sendBytes(msg2);
            return;
        }
        if (encoder instanceof Encoder.Text) {
            String msg3 = ((Encoder.Text) encoder).encode(obj);
            sendString(msg3);
            return;
        }
        if (encoder instanceof Encoder.TextStream) {
            Writer w = getSendWriter();
            try {
                ((Encoder.TextStream) encoder).encode(obj, w);
                if (w != null) {
                    w.close();
                    return;
                }
                return;
            } catch (Throwable th) {
                if (w != null) {
                    try {
                        w.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (encoder instanceof Encoder.Binary) {
            ByteBuffer msg4 = ((Encoder.Binary) encoder).encode(obj);
            sendBytes(msg4);
            return;
        }
        if (encoder instanceof Encoder.BinaryStream) {
            OutputStream os = getSendStream();
            try {
                ((Encoder.BinaryStream) encoder).encode(obj, os);
                if (os != null) {
                    os.close();
                    return;
                }
                return;
            } catch (Throwable th3) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        }
        throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", obj.getClass()));
    }

    public Future<Void> sendObjectByFuture(Object obj) {
        FutureToSendHandler f2sh = new FutureToSendHandler(this.wsSession);
        sendObjectByCompletion(obj, f2sh);
        return f2sh;
    }

    public void sendObjectByCompletion(Object obj, SendHandler completion) {
        if (obj == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullData"));
        }
        if (completion == null) {
            throw new IllegalArgumentException(sm.getString("wsRemoteEndpoint.nullHandler"));
        }
        Encoder encoder = findEncoder(obj);
        if (encoder == null && Util.isPrimitive(obj.getClass())) {
            String msg = obj.toString();
            sendStringByCompletion(msg, completion);
            return;
        }
        if (encoder == null && byte[].class.isAssignableFrom(obj.getClass())) {
            ByteBuffer msg2 = ByteBuffer.wrap((byte[]) obj);
            sendBytesByCompletion(msg2, completion);
            return;
        }
        try {
            if (encoder instanceof Encoder.Text) {
                String msg3 = ((Encoder.Text) encoder).encode(obj);
                sendStringByCompletion(msg3, completion);
            } else if (encoder instanceof Encoder.TextStream) {
                Writer w = getSendWriter();
                try {
                    ((Encoder.TextStream) encoder).encode(obj, w);
                    if (w != null) {
                        w.close();
                    }
                    completion.onResult(new SendResult());
                } finally {
                }
            } else if (encoder instanceof Encoder.Binary) {
                ByteBuffer msg4 = ((Encoder.Binary) encoder).encode(obj);
                sendBytesByCompletion(msg4, completion);
            } else if (encoder instanceof Encoder.BinaryStream) {
                OutputStream os = getSendStream();
                try {
                    ((Encoder.BinaryStream) encoder).encode(obj, os);
                    if (os != null) {
                        os.close();
                    }
                    completion.onResult(new SendResult());
                } finally {
                }
            } else {
                throw new EncodeException(obj, sm.getString("wsRemoteEndpoint.noEncoder", obj.getClass()));
            }
        } catch (Exception e) {
            SendResult sr = new SendResult(e);
            completion.onResult(sr);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSession(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setEncoders(EndpointConfig endpointConfig) throws DeploymentException {
        Encoder instance;
        this.encoderEntries.clear();
        for (Class<? extends Encoder> encoderClazz : endpointConfig.getEncoders()) {
            InstanceManager instanceManager = this.wsSession.getInstanceManager();
            if (instanceManager == null) {
                try {
                    instance = encoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (ReflectiveOperationException | NamingException e) {
                    throw new DeploymentException(sm.getString("wsRemoteEndpoint.invalidEncoder", encoderClazz.getName()), e);
                }
            } else {
                instance = (Encoder) instanceManager.newInstance((Class<?>) encoderClazz);
            }
            instance.init(endpointConfig);
            EncoderEntry entry = new EncoderEntry(Util.getEncoderType(encoderClazz), instance);
            this.encoderEntries.add(entry);
        }
    }

    private Encoder findEncoder(Object obj) {
        for (EncoderEntry entry : this.encoderEntries) {
            if (entry.getClazz().isAssignableFrom(obj.getClass())) {
                return entry.getEncoder();
            }
        }
        return null;
    }

    public final void close() {
        InstanceManager instanceManager = this.wsSession.getInstanceManager();
        for (EncoderEntry entry : this.encoderEntries) {
            entry.getEncoder().destroy();
            if (instanceManager != null) {
                try {
                    instanceManager.destroyInstance(entry);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    this.log.warn(sm.getString("wsRemoteEndpoint.encoderDestoryFailed", this.encoder.getClass()), e);
                }
            }
        }
        this.transformation.close();
        doClose();
    }

    private static void writeHeader(ByteBuffer headerBuffer, boolean fin, int rsv, byte opCode, boolean masked, ByteBuffer payload, byte[] mask, boolean first) {
        byte b;
        byte b2 = 0;
        if (fin) {
            b2 = (byte) (0 - 128);
        }
        byte b3 = (byte) (b2 + (rsv << 4));
        if (first) {
            b3 = (byte) (b3 + opCode);
        }
        headerBuffer.put(b3);
        if (masked) {
            b = Byte.MIN_VALUE;
        } else {
            b = 0;
        }
        if (payload.remaining() < 126) {
            headerBuffer.put((byte) (payload.remaining() | b));
        } else if (payload.remaining() < 65536) {
            headerBuffer.put((byte) (126 | b));
            headerBuffer.put((byte) (payload.remaining() >>> 8));
            headerBuffer.put((byte) (payload.remaining() & Const.MAX_ARRAY_DIMENSIONS));
        } else {
            headerBuffer.put((byte) (Byte.MAX_VALUE | b));
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) 0);
            headerBuffer.put((byte) (payload.remaining() >>> 24));
            headerBuffer.put((byte) (payload.remaining() >>> 16));
            headerBuffer.put((byte) (payload.remaining() >>> 8));
            headerBuffer.put((byte) (payload.remaining() & Const.MAX_ARRAY_DIMENSIONS));
        }
        if (masked) {
            headerBuffer.put(mask[0]);
            headerBuffer.put(mask[1]);
            headerBuffer.put(mask[2]);
            headerBuffer.put(mask[3]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$TextMessageSendHandler.class */
    public class TextMessageSendHandler implements SendHandler {
        private final SendHandler handler;
        private final CharBuffer message;
        private final boolean isLast;
        private final CharsetEncoder encoder;
        private final ByteBuffer buffer;
        private final WsRemoteEndpointImplBase endpoint;
        private volatile boolean isDone = false;

        TextMessageSendHandler(SendHandler handler, CharBuffer message, boolean isLast, CharsetEncoder encoder, ByteBuffer encoderBuffer, WsRemoteEndpointImplBase endpoint) {
            this.handler = handler;
            this.message = message;
            this.isLast = isLast;
            this.encoder = encoder.reset();
            this.buffer = encoderBuffer;
            this.endpoint = endpoint;
        }

        public void write() {
            this.buffer.clear();
            CoderResult cr = this.encoder.encode(this.message, this.buffer, true);
            if (cr.isError()) {
                throw new IllegalArgumentException(cr.toString());
            }
            this.isDone = !cr.isOverflow();
            this.buffer.flip();
            this.endpoint.startMessage((byte) 1, this.buffer, this.isDone && this.isLast, this);
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            if (this.isDone) {
                this.endpoint.stateMachine.complete(this.isLast);
                this.handler.onResult(result);
            } else if (!result.isOK()) {
                this.handler.onResult(result);
            } else if (WsRemoteEndpointImplBase.this.closed) {
                SendResult sr = new SendResult(new IOException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedDuringMessage")));
                this.handler.onResult(sr);
            } else {
                write();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$OutputBufferSendHandler.class */
    public static class OutputBufferSendHandler implements SendHandler {
        private final SendHandler handler;
        private final long blockingWriteTimeoutExpiry;
        private final ByteBuffer headerBuffer;
        private final ByteBuffer payload;
        private final byte[] mask;
        private final ByteBuffer outputBuffer;
        private final boolean flushRequired;
        private final WsRemoteEndpointImplBase endpoint;
        private volatile int maskIndex = 0;

        OutputBufferSendHandler(SendHandler completion, long blockingWriteTimeoutExpiry, ByteBuffer headerBuffer, ByteBuffer payload, byte[] mask, ByteBuffer outputBuffer, boolean flushRequired, WsRemoteEndpointImplBase endpoint) {
            this.blockingWriteTimeoutExpiry = blockingWriteTimeoutExpiry;
            this.handler = completion;
            this.headerBuffer = headerBuffer;
            this.payload = payload;
            this.mask = mask;
            this.outputBuffer = outputBuffer;
            this.flushRequired = flushRequired;
            this.endpoint = endpoint;
        }

        public void write() {
            while (this.headerBuffer.hasRemaining() && this.outputBuffer.hasRemaining()) {
                this.outputBuffer.put(this.headerBuffer.get());
            }
            if (this.headerBuffer.hasRemaining()) {
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                return;
            }
            int payloadLeft = this.payload.remaining();
            int payloadLimit = this.payload.limit();
            int outputSpace = this.outputBuffer.remaining();
            int toWrite = payloadLeft;
            if (payloadLeft > outputSpace) {
                toWrite = outputSpace;
                this.payload.limit(this.payload.position() + toWrite);
            }
            if (this.mask == null) {
                this.outputBuffer.put(this.payload);
            } else {
                for (int i = 0; i < toWrite; i++) {
                    ByteBuffer byteBuffer = this.outputBuffer;
                    byte b = this.payload.get();
                    byte[] bArr = this.mask;
                    int i2 = this.maskIndex;
                    this.maskIndex = i2 + 1;
                    byteBuffer.put((byte) (b ^ (bArr[i2] & 255)));
                    if (this.maskIndex > 3) {
                        this.maskIndex = 0;
                    }
                }
            }
            if (payloadLeft > outputSpace) {
                this.payload.limit(payloadLimit);
                this.outputBuffer.flip();
                this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
            } else {
                if (this.flushRequired) {
                    this.outputBuffer.flip();
                    if (this.outputBuffer.remaining() == 0) {
                        this.handler.onResult(WsRemoteEndpointImplBase.SENDRESULT_OK);
                        return;
                    } else {
                        this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                        return;
                    }
                }
                this.handler.onResult(WsRemoteEndpointImplBase.SENDRESULT_OK);
            }
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                if (this.outputBuffer.hasRemaining()) {
                    this.endpoint.doWrite(this, this.blockingWriteTimeoutExpiry, this.outputBuffer);
                    return;
                } else {
                    this.outputBuffer.clear();
                    write();
                    return;
                }
            }
            this.handler.onResult(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$OutputBufferFlushSendHandler.class */
    public static class OutputBufferFlushSendHandler implements SendHandler {
        private final ByteBuffer outputBuffer;
        private final SendHandler handler;

        OutputBufferFlushSendHandler(ByteBuffer outputBuffer, SendHandler handler) {
            this.outputBuffer = outputBuffer;
            this.handler = handler;
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.outputBuffer.clear();
            }
            this.handler.onResult(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$WsOutputStream.class */
    public static class WsOutputStream extends OutputStream {
        private final WsRemoteEndpointImplBase endpoint;
        private final ByteBuffer buffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        WsOutputStream(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            this.used = true;
            if (this.buffer.remaining() == 0) {
                flush();
            }
            this.buffer.put((byte) b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (len == 0) {
                return;
            }
            if (this.buffer.remaining() == 0) {
                flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(b, off + written, remaining);
                written += remaining;
                flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(b, off + written, len - written);
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedOutputStream"));
            }
            if (this.buffer.position() > 0) {
                doWrite(false);
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            synchronized (this.closeLock) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                doWrite(true);
            }
        }

        private void doWrite(boolean last) throws IOException {
            if (this.used) {
                this.buffer.flip();
                this.endpoint.sendMessageBlock((byte) 2, this.buffer, last);
            }
            this.endpoint.stateMachine.complete(last);
            this.buffer.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$WsWriter.class */
    public static class WsWriter extends Writer {
        private final WsRemoteEndpointImplBase endpoint;
        private final CharBuffer buffer = CharBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
        private final Object closeLock = new Object();
        private volatile boolean closed = false;
        private volatile boolean used = false;

        WsWriter(WsRemoteEndpointImplBase endpoint) {
            this.endpoint = endpoint;
        }

        @Override // java.io.Writer
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.used = true;
            if (len == 0) {
                return;
            }
            if (this.buffer.remaining() == 0) {
                flush();
            }
            int remaining = this.buffer.remaining();
            int written = 0;
            while (remaining < len - written) {
                this.buffer.put(cbuf, off + written, remaining);
                written += remaining;
                flush();
                remaining = this.buffer.remaining();
            }
            this.buffer.put(cbuf, off + written, len - written);
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
            if (this.closed) {
                throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.closedWriter"));
            }
            if (this.buffer.position() > 0) {
                doWrite(false);
            }
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            synchronized (this.closeLock) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
                doWrite(true);
            }
        }

        private void doWrite(boolean last) throws IOException {
            if (this.used) {
                this.buffer.flip();
                this.endpoint.sendMessageBlock(this.buffer, last);
                this.buffer.clear();
                return;
            }
            this.endpoint.stateMachine.complete(last);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$EncoderEntry.class */
    public static class EncoderEntry {
        private final Class<?> clazz;
        private final Encoder encoder;

        EncoderEntry(Class<?> clazz, Encoder encoder) {
            this.clazz = clazz;
            this.encoder = encoder;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public Encoder getEncoder() {
            return this.encoder;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$StateMachine.class */
    public static class StateMachine {
        private State state = State.OPEN;

        private StateMachine() {
        }

        public synchronized void streamStart() {
            checkState(State.OPEN);
            this.state = State.STREAM_WRITING;
        }

        public synchronized void writeStart() {
            checkState(State.OPEN);
            this.state = State.WRITER_WRITING;
        }

        public synchronized void binaryPartialStart() {
            checkState(State.OPEN, State.BINARY_PARTIAL_READY);
            this.state = State.BINARY_PARTIAL_WRITING;
        }

        public synchronized void binaryStart() {
            checkState(State.OPEN);
            this.state = State.BINARY_FULL_WRITING;
        }

        public synchronized void textPartialStart() {
            checkState(State.OPEN, State.TEXT_PARTIAL_READY);
            this.state = State.TEXT_PARTIAL_WRITING;
        }

        public synchronized void textStart() {
            checkState(State.OPEN);
            this.state = State.TEXT_FULL_WRITING;
        }

        public synchronized void complete(boolean last) {
            if (last) {
                checkState(State.TEXT_PARTIAL_WRITING, State.TEXT_FULL_WRITING, State.BINARY_PARTIAL_WRITING, State.BINARY_FULL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
                this.state = State.OPEN;
                return;
            }
            checkState(State.TEXT_PARTIAL_WRITING, State.BINARY_PARTIAL_WRITING, State.STREAM_WRITING, State.WRITER_WRITING);
            if (this.state == State.TEXT_PARTIAL_WRITING) {
                this.state = State.TEXT_PARTIAL_READY;
            } else if (this.state == State.BINARY_PARTIAL_WRITING) {
                this.state = State.BINARY_PARTIAL_READY;
            } else {
                if (this.state != State.WRITER_WRITING && this.state == State.STREAM_WRITING) {
                }
            }
        }

        private void checkState(State... required) {
            for (State state : required) {
                if (this.state == state) {
                    return;
                }
            }
            throw new IllegalStateException(WsRemoteEndpointImplBase.sm.getString("wsRemoteEndpoint.wrongState", this.state));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$StateUpdateSendHandler.class */
    public static class StateUpdateSendHandler implements SendHandler {
        private final SendHandler handler;
        private final StateMachine stateMachine;

        StateUpdateSendHandler(SendHandler handler, StateMachine stateMachine) {
            this.handler = handler;
            this.stateMachine = stateMachine;
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            if (result.isOK()) {
                this.stateMachine.complete(true);
            }
            this.handler.onResult(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/WsRemoteEndpointImplBase$BlockingSendHandler.class */
    public static class BlockingSendHandler implements SendHandler {
        private volatile SendResult sendResult = null;

        private BlockingSendHandler() {
        }

        @Override // jakarta.websocket.SendHandler
        public void onResult(SendResult result) {
            this.sendResult = result;
        }

        public SendResult getSendResult() {
            return this.sendResult;
        }
    }
}
