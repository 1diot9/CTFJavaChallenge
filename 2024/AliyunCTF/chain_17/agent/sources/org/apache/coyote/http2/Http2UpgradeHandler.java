package org.apache.coyote.http2;

import cn.hutool.core.net.NetUtil;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.http.WebConnection;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.HpackEncoder;
import org.apache.coyote.http2.Http2Parser;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.h2.security.auth.impl.JaasCredentialsValidator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler.class */
public class Http2UpgradeHandler extends AbstractStream implements InternalHttpUpgradeHandler, Http2Parser.Input, Http2Parser.Output {
    protected static final int FLAG_END_OF_STREAM = 1;
    protected static final int FLAG_END_OF_HEADERS = 4;
    private static final String HTTP2_SETTINGS_HEADER = "HTTP2-Settings";
    protected final String connectionId;
    protected final Http2Protocol protocol;
    private final Adapter adapter;
    protected final SocketWrapperBase<?> socketWrapper;
    private volatile SSLSupport sslSupport;
    private volatile Http2Parser parser;
    private AtomicReference<ConnectionState> connectionState;
    private volatile long pausedNanoTime;
    private final ConnectionSettingsRemote remoteSettings;
    protected final ConnectionSettingsLocal localSettings;
    private HpackDecoder hpackDecoder;
    private HpackEncoder hpackEncoder;
    private final ConcurrentNavigableMap<Integer, AbstractNonZeroStream> streams;
    protected final AtomicInteger activeRemoteStreamCount;
    private volatile int maxActiveRemoteStreamId;
    private volatile int maxProcessedStreamId;
    private final AtomicInteger nextLocalStreamId;
    private final PingManager pingManager;
    private volatile int newStreamsSinceLastPrune;
    private final Set<Stream> backLogStreams;
    private long backLogSize;
    private volatile long connectionTimeout;
    private AtomicInteger streamConcurrency;
    private Queue<StreamRunnable> queuedRunnable;
    private final AtomicLong overheadCount;
    private volatile int lastNonFinalDataPayload;
    private volatile int lastWindowUpdate;
    protected final UserDataHelper userDataHelper;
    protected static final Log log = LogFactory.getLog((Class<?>) Http2UpgradeHandler.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) Http2UpgradeHandler.class);
    private static final Integer STREAM_ID_ZERO = 0;
    protected static final byte[] PING = {0, 0, 8, 6, 0, 0, 0, 0, 0};
    protected static final byte[] PING_ACK = {0, 0, 8, 6, 1, 0, 0, 0, 0};
    protected static final byte[] SETTINGS_ACK = {0, 0, 0, 4, 1, 0, 0, 0, 0};
    protected static final byte[] GOAWAY = {7, 0, 0, 0, 0, 0};
    private static final HeaderSink HEADER_SINK = new HeaderSink();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler$HeaderFrameBuffers.class */
    public interface HeaderFrameBuffers {
        void startFrame();

        void endFrame() throws IOException;

        void endHeaders() throws IOException;

        byte[] getHeader();

        ByteBuffer getPayload();

        void expandPayload();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2UpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest, SocketWrapperBase<?> socketWrapper) {
        super(STREAM_ID_ZERO);
        this.connectionState = new AtomicReference<>(ConnectionState.NEW);
        this.pausedNanoTime = Long.MAX_VALUE;
        this.streams = new ConcurrentSkipListMap();
        this.activeRemoteStreamCount = new AtomicInteger(0);
        this.maxActiveRemoteStreamId = -1;
        this.nextLocalStreamId = new AtomicInteger(2);
        this.pingManager = getPingManager();
        this.newStreamsSinceLastPrune = 0;
        this.backLogStreams = new HashSet();
        this.backLogSize = 0L;
        this.connectionTimeout = -1L;
        this.streamConcurrency = null;
        this.queuedRunnable = null;
        this.userDataHelper = new UserDataHelper(log);
        this.protocol = protocol;
        this.adapter = adapter;
        this.socketWrapper = socketWrapper;
        this.overheadCount = new AtomicLong((-10) * protocol.getOverheadCountFactor());
        this.lastNonFinalDataPayload = protocol.getOverheadDataThreshold() * 2;
        this.lastWindowUpdate = protocol.getOverheadWindowUpdateThreshold() * 2;
        this.connectionId = getServletConnection().getConnectionId();
        this.remoteSettings = new ConnectionSettingsRemote(this.connectionId);
        this.localSettings = new ConnectionSettingsLocal(this.connectionId);
        this.localSettings.set(Setting.MAX_CONCURRENT_STREAMS, protocol.getMaxConcurrentStreams());
        this.localSettings.set(Setting.INITIAL_WINDOW_SIZE, protocol.getInitialWindowSize());
        this.pingManager.initiateDisabled = protocol.getInitiatePingDisabled();
        if (coyoteRequest != null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.upgrade", this.connectionId));
            }
            Stream stream = new Stream(1, this, coyoteRequest);
            this.streams.put(1, stream);
            this.maxActiveRemoteStreamId = 1;
            this.activeRemoteStreamCount.set(1);
            this.maxProcessedStreamId = 1;
        }
    }

    protected PingManager getPingManager() {
        return new PingManager();
    }

    public void init(WebConnection webConnection) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.init", this.connectionId, this.connectionState.get()));
        }
        if (!this.connectionState.compareAndSet(ConnectionState.NEW, ConnectionState.CONNECTED)) {
            return;
        }
        if (this.protocol.getMaxConcurrentStreamExecution() < this.localSettings.getMaxConcurrentStreams()) {
            this.streamConcurrency = new AtomicInteger(0);
            this.queuedRunnable = new ConcurrentLinkedQueue();
        }
        this.parser = getParser(this.connectionId);
        Stream stream = null;
        this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
        this.socketWrapper.setWriteTimeout(this.protocol.getWriteTimeout());
        if (webConnection != null) {
            try {
                stream = getStream(1, true);
                String base64Settings = stream.getCoyoteRequest().getHeader(HTTP2_SETTINGS_HEADER);
                byte[] settings = Base64.decodeBase64URLSafe(base64Settings);
                FrameType.SETTINGS.check(0, settings.length);
                for (int i = 0; i < settings.length % 6; i++) {
                    int id = ByteUtil.getTwoBytes(settings, i * 6);
                    long value = ByteUtil.getFourBytes(settings, (i * 6) + 2);
                    Setting key = Setting.valueOf(id);
                    if (key == Setting.UNKNOWN) {
                        log.warn(sm.getString("connectionSettings.unknown", this.connectionId, Integer.toString(id), Long.toString(value)));
                    }
                    this.remoteSettings.set(key, value);
                }
            } catch (Http2Exception e) {
                throw new ProtocolException(sm.getString("upgradeHandler.upgrade.fail", this.connectionId));
            }
        }
        writeSettings();
        try {
            this.parser.readConnectionPreface(webConnection, stream);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.prefaceReceived", this.connectionId));
            }
            this.socketWrapper.setReadTimeout(-1L);
            this.socketWrapper.setWriteTimeout(-1L);
            processConnection(webConnection, stream);
        } catch (Http2Exception e2) {
            String msg = sm.getString("upgradeHandler.invalidPreface", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg, e2);
            }
            throw new ProtocolException(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processConnection(WebConnection webConnection, Stream stream) {
        try {
            this.pingManager.sendPing(true);
            if (webConnection != null) {
                processStreamOnContainerThread(stream);
            }
        } catch (IOException ioe) {
            throw new ProtocolException(sm.getString("upgradeHandler.pingFailed", this.connectionId), ioe);
        }
    }

    protected Http2Parser getParser(String connectionId) {
        return new Http2Parser(connectionId, this, this);
    }

    protected void processStreamOnContainerThread(Stream stream) {
        StreamProcessor streamProcessor = new StreamProcessor(this, stream, this.adapter, this.socketWrapper);
        streamProcessor.setSslSupport(this.sslSupport);
        processStreamOnContainerThread(streamProcessor, SocketEvent.OPEN_READ);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void decrementActiveRemoteStreamCount() {
        setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processStreamOnContainerThread(StreamProcessor streamProcessor, SocketEvent event) {
        StreamRunnable streamRunnable = new StreamRunnable(streamProcessor, event);
        if (this.streamConcurrency == null) {
            this.socketWrapper.execute(streamRunnable);
        } else if (getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution()) {
            increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        } else {
            this.queuedRunnable.offer(streamRunnable);
        }
    }

    public void setSocketWrapper(SocketWrapperBase<?> wrapper) {
    }

    public void setSslSupport(SSLSupport sslSupport) {
        this.sslSupport = sslSupport;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:55:0x0139. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x003c. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:87:0x02b8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.tomcat.util.net.AbstractEndpoint.Handler.SocketState upgradeDispatch(org.apache.tomcat.util.net.SocketEvent r10) {
        /*
            Method dump skipped, instructions count: 730
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http2.Http2UpgradeHandler.upgradeDispatch(org.apache.tomcat.util.net.SocketEvent):org.apache.tomcat.util.net.AbstractEndpoint$Handler$SocketState");
    }

    protected void setConnectionTimeoutForStreamCount(int streamCount) {
        if (streamCount == 0) {
            long keepAliveTimeout = this.protocol.getKeepAliveTimeout();
            if (keepAliveTimeout == -1) {
                setConnectionTimeout(-1L);
                return;
            } else {
                setConnectionTimeout(System.currentTimeMillis() + keepAliveTimeout);
                return;
            }
        }
        setConnectionTimeout(-1L);
    }

    private void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void timeoutAsync(long now) {
        long connectionTimeout = this.connectionTimeout;
        if (now == -1 || (connectionTimeout > -1 && now > connectionTimeout)) {
            this.socketWrapper.processSocket(SocketEvent.TIMEOUT, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsRemote getRemoteSettings() {
        return this.remoteSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionSettingsLocal getLocalSettings() {
        return this.localSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Protocol getProtocol() {
        return this.protocol;
    }

    public void pause() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.pause.entry", this.connectionId));
        }
        if (this.connectionState.compareAndSet(ConnectionState.CONNECTED, ConnectionState.PAUSING)) {
            this.pausedNanoTime = System.nanoTime();
            try {
                writeGoAwayFrame(Integer.MAX_VALUE, Http2Error.NO_ERROR.getCode(), null);
            } catch (IOException e) {
            }
        }
    }

    public void destroy() {
    }

    void checkPauseState() throws IOException {
        if (this.connectionState.get() == ConnectionState.PAUSING && this.pausedNanoTime + this.pingManager.getRoundTripTimeNano() < System.nanoTime()) {
            this.connectionState.compareAndSet(ConnectionState.PAUSING, ConnectionState.PAUSED);
            writeGoAwayFrame(this.maxProcessedStreamId, Http2Error.NO_ERROR.getCode(), null);
        }
    }

    private int increaseStreamConcurrency() {
        return this.streamConcurrency.incrementAndGet();
    }

    private int decreaseStreamConcurrency() {
        return this.streamConcurrency.decrementAndGet();
    }

    private int getStreamConcurrency() {
        return this.streamConcurrency.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void executeQueuedStream() {
        StreamRunnable streamRunnable;
        if (this.streamConcurrency == null) {
            return;
        }
        decreaseStreamConcurrency();
        if (getStreamConcurrency() < this.protocol.getMaxConcurrentStreamExecution() && (streamRunnable = this.queuedRunnable.poll()) != null) {
            increaseStreamConcurrency();
            this.socketWrapper.execute(streamRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendStreamReset(StreamStateMachine state, StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.rst.debug", this.connectionId, Integer.toString(se.getStreamId()), se.getError(), se.getMessage()));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.socketWrapper.getLock().lock();
        if (state != null) {
            try {
                boolean active = state.isActive();
                state.sendReset();
                if (active) {
                    decrementActiveRemoteStreamCount();
                }
            } catch (Throwable th) {
                this.socketWrapper.getLock().unlock();
                throw th;
            }
        }
        this.socketWrapper.write(true, rstFrame, 0, rstFrame.length);
        this.socketWrapper.flush(true);
        this.socketWrapper.getLock().unlock();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeConnection(Http2Exception ce) {
        long code;
        byte[] msg;
        if (ce == null) {
            code = Http2Error.NO_ERROR.getCode();
            msg = null;
        } else {
            code = ce.getError().getCode();
            msg = ce.getMessage().getBytes(StandardCharsets.UTF_8);
        }
        try {
            writeGoAwayFrame(this.maxProcessedStreamId, code, msg);
        } catch (IOException e) {
        }
        close();
    }

    protected void writeSettings() {
        try {
            byte[] settings = this.localSettings.getSettingsFrameForPending();
            this.socketWrapper.write(true, settings, 0, settings.length);
            byte[] windowUpdateFrame = createWindowUpdateForSettings();
            if (windowUpdateFrame.length > 0) {
                this.socketWrapper.write(true, windowUpdateFrame, 0, windowUpdateFrame.length);
            }
            this.socketWrapper.flush(true);
        } catch (IOException ioe) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
            throw new ProtocolException(msg, ioe);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] createWindowUpdateForSettings() {
        byte[] windowUpdateFrame;
        int increment = this.protocol.getInitialWindowSize() - NetUtil.PORT_RANGE_MAX;
        if (increment > 0) {
            windowUpdateFrame = new byte[13];
            ByteUtil.setThreeBytes(windowUpdateFrame, 0, 4);
            windowUpdateFrame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(windowUpdateFrame, 9, increment);
        } else {
            windowUpdateFrame = new byte[0];
        }
        return windowUpdateFrame;
    }

    protected void writeGoAwayFrame(int maxStreamId, long errorCode, byte[] debugMsg) throws IOException {
        byte[] fixedPayload = new byte[8];
        ByteUtil.set31Bits(fixedPayload, 0, maxStreamId);
        ByteUtil.setFourBytes(fixedPayload, 4, errorCode);
        int len = 8;
        if (debugMsg != null) {
            len = 8 + debugMsg.length;
        }
        byte[] payloadLength = new byte[3];
        ByteUtil.setThreeBytes(payloadLength, 0, len);
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            this.socketWrapper.write(true, payloadLength, 0, payloadLength.length);
            this.socketWrapper.write(true, GOAWAY, 0, GOAWAY.length);
            this.socketWrapper.write(true, fixedPayload, 0, 8);
            if (debugMsg != null) {
                this.socketWrapper.write(true, debugMsg, 0, debugMsg.length);
            }
            this.socketWrapper.flush(true);
            lock.unlock();
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
            lock.unlock();
            stream.sentHeaders();
            if (endOfStream) {
                sentEndOfStream(stream);
            }
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HeaderFrameBuffers doWriteHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        if (log.isDebugEnabled()) {
            if (pushedStreamId == 0) {
                log.debug(sm.getString("upgradeHandler.writeHeaders", this.connectionId, stream.getIdAsString(), Boolean.valueOf(endOfStream)));
            } else {
                log.debug(sm.getString("upgradeHandler.writePushHeaders", this.connectionId, stream.getIdAsString(), Integer.valueOf(pushedStreamId), Boolean.valueOf(endOfStream)));
            }
        }
        if (!stream.canWrite()) {
            return null;
        }
        HeaderFrameBuffers headerFrameBuffers = getHeaderFrameBuffers(payloadSize);
        byte[] pushedStreamIdBytes = null;
        if (pushedStreamId > 0) {
            pushedStreamIdBytes = new byte[4];
            ByteUtil.set31Bits(pushedStreamIdBytes, 0, pushedStreamId);
        }
        boolean first = true;
        HpackEncoder.State state = null;
        while (state != HpackEncoder.State.COMPLETE) {
            headerFrameBuffers.startFrame();
            if (first && pushedStreamIdBytes != null) {
                headerFrameBuffers.getPayload().put(pushedStreamIdBytes);
            }
            state = getHpackEncoder().encode(mimeHeaders, headerFrameBuffers.getPayload());
            headerFrameBuffers.getPayload().flip();
            if (state == HpackEncoder.State.COMPLETE || headerFrameBuffers.getPayload().limit() > 0) {
                ByteUtil.setThreeBytes(headerFrameBuffers.getHeader(), 0, headerFrameBuffers.getPayload().limit());
                if (first) {
                    first = false;
                    if (pushedStreamIdBytes == null) {
                        headerFrameBuffers.getHeader()[3] = FrameType.HEADERS.getIdByte();
                    } else {
                        headerFrameBuffers.getHeader()[3] = FrameType.PUSH_PROMISE.getIdByte();
                    }
                    if (endOfStream) {
                        headerFrameBuffers.getHeader()[4] = 1;
                    }
                } else {
                    headerFrameBuffers.getHeader()[3] = FrameType.CONTINUATION.getIdByte();
                }
                if (state == HpackEncoder.State.COMPLETE) {
                    byte[] header = headerFrameBuffers.getHeader();
                    header[4] = (byte) (header[4] + 4);
                }
                if (log.isDebugEnabled()) {
                    log.debug(headerFrameBuffers.getPayload().limit() + " bytes");
                }
                ByteUtil.set31Bits(headerFrameBuffers.getHeader(), 5, stream.getIdAsInt());
                headerFrameBuffers.endFrame();
            } else if (state == HpackEncoder.State.UNDERFLOW) {
                headerFrameBuffers.expandPayload();
            }
        }
        headerFrameBuffers.endHeaders();
        return headerFrameBuffers;
    }

    protected HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new DefaultHeaderFrameBuffers(initialPayloadSize);
    }

    protected HpackEncoder getHpackEncoder() {
        if (this.hpackEncoder == null) {
            this.hpackEncoder = new HpackEncoder();
        }
        this.hpackEncoder.setMaxTableSize(this.remoteSettings.getHeaderTableSize());
        return this.hpackEncoder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeBody(Stream stream, ByteBuffer data, int len, boolean finished) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.writeBody", this.connectionId, stream.getIdAsString(), Integer.toString(len), Boolean.valueOf(finished)));
        }
        reduceOverheadCount(FrameType.DATA);
        boolean writable = stream.canWrite();
        byte[] header = new byte[9];
        ByteUtil.setThreeBytes(header, 0, len);
        header[3] = FrameType.DATA.getIdByte();
        if (finished) {
            header[4] = 1;
            sentEndOfStream(stream);
        }
        if (writable) {
            ByteUtil.set31Bits(header, 5, stream.getIdAsInt());
            this.socketWrapper.getLock().lock();
            try {
                try {
                    this.socketWrapper.write(true, header, 0, header.length);
                    int orgLimit = data.limit();
                    data.limit(data.position() + len);
                    this.socketWrapper.write(true, data);
                    data.limit(orgLimit);
                    this.socketWrapper.flush(true);
                    this.socketWrapper.getLock().unlock();
                } catch (IOException ioe) {
                    handleAppInitiatedIOException(ioe);
                    this.socketWrapper.getLock().unlock();
                }
            } catch (Throwable th) {
                this.socketWrapper.getLock().unlock();
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sentEndOfStream(Stream stream) {
        stream.sentEndOfStream();
        if (!stream.isActive()) {
            decrementActiveRemoteStreamCount();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleAppInitiatedIOException(IOException ioe) throws IOException {
        close();
        throw ioe;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeWindowUpdate(AbstractNonZeroStream stream, int increment, boolean applicationInitiated) throws IOException {
        int streamIncrement;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.windowUpdateConnection", getConnectionId(), Integer.valueOf(increment)));
        }
        this.socketWrapper.getLock().lock();
        try {
            byte[] frame = new byte[13];
            ByteUtil.setThreeBytes(frame, 0, 4);
            frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(frame, 9, increment);
            this.socketWrapper.write(true, frame, 0, frame.length);
            boolean needFlush = true;
            if ((stream instanceof Stream) && ((Stream) stream).canWrite() && (streamIncrement = ((Stream) stream).getWindowUpdateSizeToWrite(increment)) > 0) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.windowUpdateStream", getConnectionId(), getIdAsString(), Integer.valueOf(streamIncrement)));
                }
                ByteUtil.set31Bits(frame, 5, stream.getIdAsInt());
                ByteUtil.set31Bits(frame, 9, streamIncrement);
                try {
                    this.socketWrapper.write(true, frame, 0, frame.length);
                    this.socketWrapper.flush(true);
                    needFlush = false;
                } catch (IOException ioe) {
                    if (applicationInitiated) {
                        handleAppInitiatedIOException(ioe);
                    } else {
                        throw ioe;
                    }
                }
            }
            if (needFlush) {
                this.socketWrapper.flush(true);
            }
        } finally {
            this.socketWrapper.getLock().unlock();
        }
    }

    protected void processWrites() throws IOException {
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            if (this.socketWrapper.flush(false)) {
                this.socketWrapper.registerWriteInterest();
            } else {
                this.pingManager.sendPing(false);
            }
        } finally {
            lock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Finally extract failed */
    public int reserveWindowSize(Stream stream, int reservation, boolean block) throws IOException {
        String msg;
        Http2Error error;
        int allocation = 0;
        stream.windowAllocationLock.lock();
        try {
            this.windowAllocationLock.lock();
            try {
                if (!stream.canWrite()) {
                    stream.doStreamCancel(sm.getString("upgradeHandler.stream.notWritable", stream.getConnectionId(), stream.getIdAsString(), stream.state.getCurrentStateName()), Http2Error.STREAM_CLOSED);
                }
                long windowSize = getWindowSize();
                if (stream.getConnectionAllocationMade() > 0) {
                    allocation = stream.getConnectionAllocationMade();
                    stream.setConnectionAllocationMade(0);
                } else if (windowSize < 1) {
                    if (stream.getConnectionAllocationMade() == 0) {
                        stream.setConnectionAllocationRequested(reservation);
                        this.backLogSize += reservation;
                        this.backLogStreams.add(stream);
                    }
                } else if (windowSize < reservation) {
                    allocation = (int) windowSize;
                    decrementWindowSize(allocation);
                } else {
                    allocation = reservation;
                    decrementWindowSize(allocation);
                }
                this.windowAllocationLock.unlock();
                if (allocation == 0) {
                    if (block) {
                        try {
                            long writeTimeout = this.protocol.getWriteTimeout();
                            stream.waitForConnectionAllocation(writeTimeout);
                            if (stream.getConnectionAllocationMade() == 0) {
                                if (stream.isActive()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug(sm.getString("upgradeHandler.noAllocation", this.connectionId, stream.getIdAsString()));
                                    }
                                    close();
                                    msg = sm.getString("stream.writeTimeout");
                                    error = Http2Error.ENHANCE_YOUR_CALM;
                                } else {
                                    msg = sm.getString("stream.clientCancel");
                                    error = Http2Error.STREAM_CLOSED;
                                }
                                stream.doStreamCancel(msg, error);
                            } else {
                                allocation = stream.getConnectionAllocationMade();
                                stream.setConnectionAllocationMade(0);
                            }
                        } catch (InterruptedException e) {
                            throw new IOException(sm.getString("upgradeHandler.windowSizeReservationInterrupted", this.connectionId, stream.getIdAsString(), Integer.toString(reservation)), e);
                        }
                    } else {
                        stream.waitForConnectionAllocationNonBlocking();
                        stream.windowAllocationLock.unlock();
                        return 0;
                    }
                }
                return allocation;
            } catch (Throwable th) {
                this.windowAllocationLock.unlock();
                throw th;
            }
        } finally {
            stream.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http2.AbstractStream
    public void incrementWindowSize(int increment) throws Http2Exception {
        Set<AbstractStream> streamsToNotify = null;
        this.windowAllocationLock.lock();
        try {
            long windowSize = getWindowSize();
            if (windowSize < 1 && windowSize + increment > 0) {
                streamsToNotify = releaseBackLog((int) (windowSize + increment));
            } else {
                super.incrementWindowSize(increment);
            }
            if (streamsToNotify != null) {
                for (AbstractStream stream : streamsToNotify) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("upgradeHandler.releaseBacklog", this.connectionId, stream.getIdAsString()));
                    }
                    if (this != stream) {
                        ((Stream) stream).notifyConnection();
                    }
                }
            }
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SendfileState processSendfile(SendfileData sendfileData) {
        return SendfileState.DONE;
    }

    private Set<AbstractStream> releaseBackLog(int increment) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            Set<AbstractStream> result = new HashSet<>();
            if (this.backLogSize < increment) {
                for (AbstractStream stream : this.backLogStreams) {
                    if (stream.getConnectionAllocationRequested() > 0) {
                        stream.setConnectionAllocationMade(stream.getConnectionAllocationRequested());
                        stream.setConnectionAllocationRequested(0);
                        result.add(stream);
                    }
                }
                int remaining = increment - ((int) this.backLogSize);
                this.backLogSize = 0L;
                super.incrementWindowSize(remaining);
                this.backLogStreams.clear();
            } else {
                Set<Stream> orderedStreams = new ConcurrentSkipListSet<>((Comparator<? super Stream>) Comparator.comparingInt((v0) -> {
                    return v0.getUrgency();
                }).thenComparing((v0) -> {
                    return v0.getIncremental();
                }).thenComparing((v0) -> {
                    return v0.getIdAsInt();
                }));
                orderedStreams.addAll(this.backLogStreams);
                long urgencyWhereAllocationIsExhausted = 0;
                long requestedAllocationForIncrementalStreams = 0;
                int remaining2 = increment;
                for (Stream s : orderedStreams) {
                    if (urgencyWhereAllocationIsExhausted < s.getUrgency()) {
                        if (remaining2 < 1) {
                            break;
                        }
                        requestedAllocationForIncrementalStreams = 0;
                    }
                    urgencyWhereAllocationIsExhausted = s.getUrgency();
                    if (s.getIncremental()) {
                        requestedAllocationForIncrementalStreams += s.getConnectionAllocationRequested();
                        remaining2 -= s.getConnectionAllocationRequested();
                    } else {
                        remaining2 -= s.getConnectionAllocationRequested();
                        if (remaining2 < 1) {
                            break;
                        }
                    }
                }
                int remaining3 = increment;
                Iterator<Stream> orderedStreamsIterator = orderedStreams.iterator();
                while (orderedStreamsIterator.hasNext()) {
                    Stream s2 = orderedStreamsIterator.next();
                    if (s2.getUrgency() < urgencyWhereAllocationIsExhausted) {
                        remaining3 = allocate(s2, remaining3);
                        result.add(s2);
                        orderedStreamsIterator.remove();
                        this.backLogStreams.remove(s2);
                    } else if (requestedAllocationForIncrementalStreams == 0) {
                        remaining3 = allocate(s2, remaining3);
                        result.add(s2);
                        if (s2.getConnectionAllocationRequested() == 0) {
                            orderedStreamsIterator.remove();
                            this.backLogStreams.remove(s2);
                        }
                        if (remaining3 < 1) {
                            break;
                        }
                    } else {
                        if (s2.getUrgency() != urgencyWhereAllocationIsExhausted) {
                            break;
                        }
                        int share = (int) ((s2.getConnectionAllocationRequested() * remaining3) / requestedAllocationForIncrementalStreams);
                        if (share == 0) {
                            share = 1;
                        }
                        allocate(s2, share);
                        result.add(s2);
                        if (s2.getConnectionAllocationRequested() == 0) {
                            orderedStreamsIterator.remove();
                            this.backLogStreams.remove(s2);
                        }
                    }
                }
            }
            return result;
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    private int allocate(AbstractStream stream, int allocation) {
        int allocatedThisTime;
        this.windowAllocationLock.lock();
        try {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.allocate.debug", getConnectionId(), stream.getIdAsString(), Integer.toString(allocation)));
            }
            int leftToAllocate = allocation;
            if (stream.getConnectionAllocationRequested() > 0) {
                if (allocation >= stream.getConnectionAllocationRequested()) {
                    allocatedThisTime = stream.getConnectionAllocationRequested();
                } else {
                    allocatedThisTime = allocation;
                }
                stream.setConnectionAllocationRequested(stream.getConnectionAllocationRequested() - allocatedThisTime);
                stream.setConnectionAllocationMade(stream.getConnectionAllocationMade() + allocatedThisTime);
                leftToAllocate -= allocatedThisTime;
            }
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.allocate.left", getConnectionId(), stream.getIdAsString(), Integer.toString(leftToAllocate)));
            }
            return leftToAllocate;
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    private Stream getStream(int streamId) {
        Integer key = Integer.valueOf(streamId);
        AbstractStream result = (AbstractStream) this.streams.get(key);
        if (result instanceof Stream) {
            return (Stream) result;
        }
        return null;
    }

    private Stream getStream(int streamId, boolean unknownIsError) throws ConnectionException {
        Stream result = getStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId) {
        Integer key = Integer.valueOf(streamId);
        return (AbstractNonZeroStream) this.streams.get(key);
    }

    private AbstractNonZeroStream getAbstractNonZeroStream(int streamId, boolean unknownIsError) throws ConnectionException {
        AbstractNonZeroStream result = getAbstractNonZeroStream(streamId);
        if (result == null && unknownIsError) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.closed", Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        return result;
    }

    private Stream createRemoteStream(int streamId) throws ConnectionException {
        Integer key = Integer.valueOf(streamId);
        if (streamId % 2 != 1) {
            throw new ConnectionException(sm.getString("upgradeHandler.stream.even", key), Http2Error.PROTOCOL_ERROR);
        }
        pruneClosedStreams(streamId);
        Stream result = new Stream(key, this);
        this.streams.put(key, result);
        return result;
    }

    private Stream createLocalStream(Request request) {
        int streamId = this.nextLocalStreamId.getAndAdd(2);
        Integer key = Integer.valueOf(streamId);
        Stream result = new Stream(key, this, request);
        this.streams.put(key, result);
        return result;
    }

    private void close() {
        ConnectionState previous = this.connectionState.getAndSet(ConnectionState.CLOSED);
        if (previous == ConnectionState.CLOSED) {
            return;
        }
        for (AbstractNonZeroStream stream : this.streams.values()) {
            if (stream instanceof Stream) {
                ((Stream) stream).receiveReset(Http2Error.CANCEL.getCode());
            }
        }
        try {
            this.socketWrapper.close();
        } catch (Exception e) {
            log.debug(sm.getString("upgradeHandler.socketCloseFailed"), e);
        }
    }

    private void pruneClosedStreams(int streamId) {
        if (this.newStreamsSinceLastPrune < 9) {
            this.newStreamsSinceLastPrune++;
            return;
        }
        this.newStreamsSinceLastPrune = 0;
        long max = this.localSettings.getMaxConcurrentStreams() * 5;
        if (max > 2147483647L) {
            max = 2147483647L;
        }
        int size = this.streams.size();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.pruneStart", this.connectionId, Long.toString(max), Integer.toString(size)));
        }
        int toClose = size - ((int) max);
        for (AbstractNonZeroStream stream : this.streams.values()) {
            if (toClose < 1) {
                return;
            }
            if (!(stream instanceof Stream) || !((Stream) stream).isActive()) {
                this.streams.remove(stream.getIdentifier());
                toClose--;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("upgradeHandler.pruned", this.connectionId, stream.getIdAsString()));
                }
            }
        }
        if (toClose > 0) {
            log.warn(sm.getString("upgradeHandler.pruneIncomplete", this.connectionId, Integer.toString(streamId), Integer.toString(toClose)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void push(Request request, Stream associatedStream) throws IOException {
        if (this.localSettings.getMaxConcurrentStreams() < this.activeRemoteStreamCount.incrementAndGet()) {
            setConnectionTimeoutForStreamCount(this.activeRemoteStreamCount.decrementAndGet());
            return;
        }
        Lock lock = this.socketWrapper.getLock();
        lock.lock();
        try {
            Stream pushStream = createLocalStream(request);
            writeHeaders(associatedStream, pushStream.getIdAsInt(), request.getMimeHeaders(), false, 1024);
            lock.unlock();
            pushStream.sentPushPromise();
            processStreamOnContainerThread(pushStream);
        } catch (Throwable th) {
            lock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http2.AbstractStream
    public final String getConnectionId() {
        return this.connectionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reduceOverheadCount(FrameType frameType) {
        updateOverheadCount(frameType, -20);
    }

    public void increaseOverheadCount(FrameType frameType) {
        updateOverheadCount(frameType, getProtocol().getOverheadCountFactor());
    }

    private void increaseOverheadCount(FrameType frameType, int increment) {
        updateOverheadCount(frameType, increment);
    }

    private void updateOverheadCount(FrameType frameType, int increment) {
        long newOverheadCount = this.overheadCount.addAndGet(increment);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.overheadChange", this.connectionId, getIdAsString(), frameType.name(), Long.valueOf(newOverheadCount)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOverheadLimitExceeded() {
        return this.overheadCount.get() > 0;
    }

    public boolean fill(boolean block, byte[] data, int offset, int length) throws IOException {
        int len = length;
        int pos = offset;
        boolean nextReadBlock = block;
        while (len > 0) {
            if (nextReadBlock) {
                this.socketWrapper.setReadTimeout(this.protocol.getReadTimeout());
            } else {
                this.socketWrapper.setReadTimeout(-1L);
            }
            int thisRead = this.socketWrapper.read(nextReadBlock, data, pos, len);
            if (thisRead == 0) {
                if (nextReadBlock) {
                    throw new IllegalStateException();
                }
                return false;
            }
            if (thisRead == -1) {
                if (this.connectionState.get().isNewStreamAllowed()) {
                    throw new EOFException();
                }
                return false;
            }
            pos += thisRead;
            len -= thisRead;
            nextReadBlock = true;
        }
        return true;
    }

    public int getMaxFrameSize() {
        return this.localSettings.getMaxFrameSize();
    }

    public HpackDecoder getHpackDecoder() {
        if (this.hpackDecoder == null) {
            this.hpackDecoder = new HpackDecoder(this.localSettings.getHeaderTableSize());
        }
        return this.hpackDecoder;
    }

    public ByteBuffer startRequestBodyFrame(int streamId, int payloadSize, boolean endOfStream) throws Http2Exception {
        reduceOverheadCount(FrameType.DATA);
        if (!endOfStream) {
            int overheadThreshold = this.protocol.getOverheadDataThreshold();
            int average = (this.lastNonFinalDataPayload >> 1) + (payloadSize >> 1);
            this.lastNonFinalDataPayload = payloadSize;
            if (average == 0) {
                average = 1;
            }
            if (average < overheadThreshold) {
                increaseOverheadCount(FrameType.DATA, overheadThreshold / average);
            }
        }
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.DATA);
        abstractNonZeroStream.receivedData(payloadSize);
        ByteBuffer result = abstractNonZeroStream.getInputByteBuffer();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.startRequestBodyFrame.result", getConnectionId(), abstractNonZeroStream.getIdAsString(), result));
        }
        return result;
    }

    public void endRequestBodyFrame(int streamId, int dataLength) throws Http2Exception, IOException {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        if (abstractNonZeroStream instanceof Stream) {
            ((Stream) abstractNonZeroStream).getInputBuffer().onDataAvailable();
        } else if (dataLength > 0) {
            onSwallowedDataFramePayload(streamId, dataLength);
        }
    }

    public void onSwallowedDataFramePayload(int streamId, int swallowedDataBytesCount) throws IOException {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId);
        writeWindowUpdate(abstractNonZeroStream, swallowedDataBytesCount, false);
    }

    public HpackDecoder.HeaderEmitter headersStart(int streamId, boolean headersEndStream) throws Http2Exception, IOException {
        checkPauseState();
        if (this.connectionState.get().isNewStreamAllowed()) {
            Stream stream = getStream(streamId, false);
            if (stream == null) {
                stream = createRemoteStream(streamId);
            }
            if (streamId < this.maxActiveRemoteStreamId) {
                throw new ConnectionException(sm.getString("upgradeHandler.stream.old", Integer.valueOf(streamId), Integer.valueOf(this.maxActiveRemoteStreamId)), Http2Error.PROTOCOL_ERROR);
            }
            stream.checkState(FrameType.HEADERS);
            stream.receivedStartOfHeaders(headersEndStream);
            closeIdleStreams(streamId);
            return stream;
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.noNewStreams", this.connectionId, Integer.toString(streamId)));
        }
        reduceOverheadCount(FrameType.HEADERS);
        return HEADER_SINK;
    }

    private void closeIdleStreams(int newMaxActiveRemoteStreamId) {
        ConcurrentNavigableMap<Integer, AbstractNonZeroStream> subMap = this.streams.subMap((boolean) Integer.valueOf(this.maxActiveRemoteStreamId), false, (boolean) Integer.valueOf(newMaxActiveRemoteStreamId), false);
        for (AbstractNonZeroStream stream : subMap.values()) {
            if (stream instanceof Stream) {
                ((Stream) stream).closeIfIdle();
            }
        }
        this.maxActiveRemoteStreamId = newMaxActiveRemoteStreamId;
    }

    @Deprecated
    public void reprioritise(int streamId, int parentStreamId, boolean exclusive, int weight) throws Http2Exception {
    }

    public void headersContinue(int payloadSize, boolean endOfHeaders) {
        int overheadThreshold;
        if (!endOfHeaders && payloadSize < (overheadThreshold = getProtocol().getOverheadContinuationThreshold())) {
            if (payloadSize == 0) {
                increaseOverheadCount(FrameType.HEADERS, overheadThreshold);
            } else {
                increaseOverheadCount(FrameType.HEADERS, overheadThreshold / payloadSize);
            }
        }
    }

    public void headersEnd(int streamId, boolean endOfStream) throws Http2Exception {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            boolean processStream = false;
            setMaxProcessedStream(streamId);
            Stream stream = (Stream) abstractNonZeroStream;
            if (stream.isActive() && stream.receivedEndOfHeaders()) {
                if (this.localSettings.getMaxConcurrentStreams() < this.activeRemoteStreamCount.incrementAndGet()) {
                    decrementActiveRemoteStreamCount();
                    increaseOverheadCount(FrameType.HEADERS);
                    throw new StreamException(sm.getString("upgradeHandler.tooManyRemoteStreams", Long.toString(this.localSettings.getMaxConcurrentStreams())), Http2Error.REFUSED_STREAM, streamId);
                }
                reduceOverheadCount(FrameType.HEADERS);
                processStream = true;
            }
            if (endOfStream) {
                receivedEndOfStream(stream);
            }
            if (processStream) {
                processStreamOnContainerThread(stream);
            }
        }
    }

    public void receivedEndOfStream(int streamId) throws ConnectionException {
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, this.connectionState.get().isNewStreamAllowed());
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream) abstractNonZeroStream;
            receivedEndOfStream(stream);
        }
    }

    private void receivedEndOfStream(Stream stream) throws ConnectionException {
        stream.receivedEndOfStream();
        if (!stream.isActive()) {
            decrementActiveRemoteStreamCount();
        }
    }

    private void setMaxProcessedStream(int streamId) {
        if (this.maxProcessedStreamId < streamId) {
            this.maxProcessedStreamId = streamId;
        }
    }

    public void reset(int streamId, long errorCode) throws Http2Exception {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.reset.receive", getConnectionId(), Integer.toString(streamId), Long.toString(errorCode)));
        }
        increaseOverheadCount(FrameType.RST, getProtocol().getOverheadResetFactor());
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(streamId, true);
        abstractNonZeroStream.checkState(FrameType.RST);
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream) abstractNonZeroStream;
            boolean active = stream.isActive();
            stream.receiveReset(errorCode);
            if (active) {
                decrementActiveRemoteStreamCount();
            }
        }
    }

    public void setting(Setting setting, long value) throws ConnectionException {
        increaseOverheadCount(FrameType.SETTINGS);
        if (setting == null) {
            return;
        }
        if (setting != Setting.INITIAL_WINDOW_SIZE) {
            if (setting != Setting.NO_RFC7540_PRIORITIES) {
                this.remoteSettings.set(setting, value);
                return;
            } else {
                if (value != 1) {
                    throw new ConnectionException(sm.getString("upgradeHandler.enableRfc7450Priorities", this.connectionId), Http2Error.PROTOCOL_ERROR);
                }
                return;
            }
        }
        long oldValue = this.remoteSettings.getInitialWindowSize();
        this.remoteSettings.set(setting, value);
        int diff = (int) (value - oldValue);
        for (AbstractNonZeroStream stream : this.streams.values()) {
            try {
                stream.incrementWindowSize(diff);
            } catch (Http2Exception h2e) {
                ((Stream) stream).close(new StreamException(sm.getString("upgradeHandler.windowSizeTooBig", this.connectionId, stream.getIdAsString()), h2e.getError(), stream.getIdAsInt()));
            }
        }
    }

    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn(sm.getString("upgradeHandler.unexpectedAck", this.connectionId, getIdAsString()));
            }
        } else {
            this.socketWrapper.getLock().lock();
            try {
                this.socketWrapper.write(true, SETTINGS_ACK, 0, SETTINGS_ACK.length);
                this.socketWrapper.flush(true);
            } finally {
                this.socketWrapper.getLock().unlock();
            }
        }
    }

    public void pingReceive(byte[] payload, boolean ack) throws IOException {
        if (!ack) {
            increaseOverheadCount(FrameType.PING);
        }
        this.pingManager.receivePing(payload, ack);
    }

    public void goaway(int lastStreamId, long errorCode, String debugData) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.goaway.debug", this.connectionId, Integer.toString(lastStreamId), Long.toHexString(errorCode), debugData));
        }
        close();
    }

    public void incrementWindowSize(int streamId, int increment) throws Http2Exception {
        int average = (this.lastWindowUpdate >> 1) + (increment >> 1);
        int overheadThreshold = this.protocol.getOverheadWindowUpdateThreshold();
        this.lastWindowUpdate = increment;
        if (average == 0) {
            average = 1;
        }
        if (streamId == 0) {
            if (average < overheadThreshold) {
                increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            incrementWindowSize(increment);
        } else {
            AbstractNonZeroStream stream = getAbstractNonZeroStream(streamId, true);
            if (average < overheadThreshold && increment < stream.getConnectionAllocationRequested()) {
                increaseOverheadCount(FrameType.WINDOW_UPDATE, overheadThreshold / average);
            }
            stream.checkState(FrameType.WINDOW_UPDATE);
            stream.incrementWindowSize(increment);
        }
    }

    public void priorityUpdate(int prioritizedStreamID, Priority p) throws Http2Exception {
        increaseOverheadCount(FrameType.PRIORITY_UPDATE);
        AbstractNonZeroStream abstractNonZeroStream = getAbstractNonZeroStream(prioritizedStreamID, true);
        if (abstractNonZeroStream instanceof Stream) {
            Stream stream = (Stream) abstractNonZeroStream;
            stream.setUrgency(p.getUrgency());
            stream.setIncremental(p.getIncremental());
        }
    }

    public void onSwallowedUnknownFrame(int streamId, int frameTypeId, int flags, int size) throws IOException {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void replaceStream(AbstractNonZeroStream original, AbstractNonZeroStream replacement) {
        AbstractNonZeroStream current = (AbstractNonZeroStream) this.streams.get(original.getIdentifier());
        if (current instanceof Stream) {
            this.streams.put(original.getIdentifier(), replacement);
        }
    }

    public ServletConnection getServletConnection() {
        if (this.socketWrapper.getSslSupport() == null) {
            return this.socketWrapper.getServletConnection("h2c", "");
        }
        return this.socketWrapper.getServletConnection(JaasCredentialsValidator.DEFAULT_APPNAME, "");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler$PingManager.class */
    public class PingManager {
        protected boolean initiateDisabled = false;
        protected final long pingIntervalNano = 10000000000L;
        protected int sequence = 0;
        protected long lastPingNanoTime = Long.MIN_VALUE;
        protected Queue<PingRecord> inflightPings = new ConcurrentLinkedQueue();
        protected Queue<Long> roundTripTimes = new ConcurrentLinkedQueue();

        /* JADX INFO: Access modifiers changed from: protected */
        public PingManager() {
        }

        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                Http2UpgradeHandler.this.socketWrapper.getLock().lock();
                try {
                    int sentSequence = this.sequence + 1;
                    this.sequence = sentSequence;
                    PingRecord pingRecord = new PingRecord(sentSequence, now);
                    this.inflightPings.add(pingRecord);
                    ByteUtil.set31Bits(payload, 4, sentSequence);
                    Http2UpgradeHandler.this.socketWrapper.write(true, Http2UpgradeHandler.PING, 0, Http2UpgradeHandler.PING.length);
                    Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                    Http2UpgradeHandler.this.socketWrapper.flush(true);
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                } catch (Throwable th) {
                    Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                    throw th;
                }
            }
        }

        public void receivePing(byte[] payload, boolean ack) throws IOException {
            PingRecord pingRecord;
            if (ack) {
                int receivedSequence = ByteUtil.get31Bits(payload, 4);
                PingRecord poll = this.inflightPings.poll();
                while (true) {
                    pingRecord = poll;
                    if (pingRecord == null || pingRecord.getSequence() >= receivedSequence) {
                        break;
                    } else {
                        poll = this.inflightPings.poll();
                    }
                }
                if (pingRecord != null) {
                    long roundTripTime = System.nanoTime() - pingRecord.getSentNanoTime();
                    this.roundTripTimes.add(Long.valueOf(roundTripTime));
                    while (this.roundTripTimes.size() > 3) {
                        this.roundTripTimes.poll();
                    }
                    if (Http2UpgradeHandler.log.isDebugEnabled()) {
                        Http2UpgradeHandler.log.debug(Http2UpgradeHandler.sm.getString("pingManager.roundTripTime", Http2UpgradeHandler.this.connectionId, Long.valueOf(roundTripTime)));
                        return;
                    }
                    return;
                }
                return;
            }
            Http2UpgradeHandler.this.socketWrapper.getLock().lock();
            try {
                Http2UpgradeHandler.this.socketWrapper.write(true, Http2UpgradeHandler.PING_ACK, 0, Http2UpgradeHandler.PING_ACK.length);
                Http2UpgradeHandler.this.socketWrapper.write(true, payload, 0, payload.length);
                Http2UpgradeHandler.this.socketWrapper.flush(true);
                Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
            } catch (Throwable th) {
                Http2UpgradeHandler.this.socketWrapper.getLock().unlock();
                throw th;
            }
        }

        public long getRoundTripTimeNano() {
            return (long) this.roundTripTimes.stream().mapToLong((v0) -> {
                return v0.longValue();
            }).average().orElse(0.0d);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler$PingRecord.class */
    public static class PingRecord {
        private final int sequence;
        private final long sentNanoTime;

        public PingRecord(int sequence, long sentNanoTime) {
            this.sequence = sequence;
            this.sentNanoTime = sentNanoTime;
        }

        public int getSequence() {
            return this.sequence;
        }

        public long getSentNanoTime() {
            return this.sentNanoTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler$ConnectionState.class */
    public enum ConnectionState {
        NEW(true),
        CONNECTED(true),
        PAUSING(true),
        PAUSED(false),
        CLOSED(false);

        private final boolean newStreamsAllowed;

        ConnectionState(boolean newStreamsAllowed) {
            this.newStreamsAllowed = newStreamsAllowed;
        }

        public boolean isNewStreamAllowed() {
            return this.newStreamsAllowed;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2UpgradeHandler$DefaultHeaderFrameBuffers.class */
    public class DefaultHeaderFrameBuffers implements HeaderFrameBuffers {
        private final byte[] header = new byte[9];
        private ByteBuffer payload;

        DefaultHeaderFrameBuffers(int initialPayloadSize) {
            this.payload = ByteBuffer.allocate(initialPayloadSize);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void startFrame() {
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endFrame() throws IOException {
            try {
                Http2UpgradeHandler.this.socketWrapper.write(true, this.header, 0, this.header.length);
                Http2UpgradeHandler.this.socketWrapper.write(true, this.payload);
                Http2UpgradeHandler.this.socketWrapper.flush(true);
            } catch (IOException ioe) {
                Http2UpgradeHandler.this.handleAppInitiatedIOException(ioe);
            }
            this.payload.clear();
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endHeaders() {
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public byte[] getHeader() {
            return this.header;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public ByteBuffer getPayload() {
            return this.payload;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void expandPayload() {
            this.payload = ByteBuffer.allocate(this.payload.capacity() * 2);
        }
    }
}
