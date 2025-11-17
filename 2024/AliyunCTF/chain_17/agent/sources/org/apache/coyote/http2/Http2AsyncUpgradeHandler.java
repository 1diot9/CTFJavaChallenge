package org.apache.coyote.http2;

import jakarta.servlet.ServletConnection;
import jakarta.servlet.http.WebConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.coyote.Adapter;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.Request;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.coyote.http2.Http2UpgradeHandler;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler.class */
public class Http2AsyncUpgradeHandler extends Http2UpgradeHandler {
    private static final ByteBuffer[] BYTEBUFFER_ARRAY = new ByteBuffer[0];
    private final Lock headerWriteLock;
    private final Lock sendResetLock;
    private final AtomicReference<Throwable> error;
    private final AtomicReference<IOException> applicationIOE;
    private final CompletionHandler<Long, Void> errorCompletion;
    private final CompletionHandler<Long, Void> applicationErrorCompletion;

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public /* bridge */ /* synthetic */ ServletConnection getServletConnection() {
        return super.getServletConnection();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void onSwallowedUnknownFrame(int i, int i2, int i3, int i4) throws IOException {
        super.onSwallowedUnknownFrame(i, i2, i3, i4);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void priorityUpdate(int i, Priority priority) throws Http2Exception {
        super.priorityUpdate(i, priority);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void incrementWindowSize(int i, int i2) throws Http2Exception {
        super.incrementWindowSize(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void goaway(int i, long j, String str) {
        super.goaway(i, j, str);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void pingReceive(byte[] bArr, boolean z) throws IOException {
        super.pingReceive(bArr, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void setting(Setting setting, long j) throws ConnectionException {
        super.setting(setting, j);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void reset(int i, long j) throws Http2Exception {
        super.reset(i, j);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void receivedEndOfStream(int i) throws ConnectionException {
        super.receivedEndOfStream(i);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void headersEnd(int i, boolean z) throws Http2Exception {
        super.headersEnd(i, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void headersContinue(int i, boolean z) {
        super.headersContinue(i, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    @Deprecated
    public /* bridge */ /* synthetic */ void reprioritise(int i, int i2, boolean z, int i3) throws Http2Exception {
        super.reprioritise(i, i2, z, i3);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ HpackDecoder.HeaderEmitter headersStart(int i, boolean z) throws Http2Exception, IOException {
        return super.headersStart(i, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void onSwallowedDataFramePayload(int i, int i2) throws IOException {
        super.onSwallowedDataFramePayload(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void endRequestBodyFrame(int i, int i2) throws Http2Exception, IOException {
        super.endRequestBodyFrame(i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ ByteBuffer startRequestBodyFrame(int i, int i2, boolean z) throws Http2Exception {
        return super.startRequestBodyFrame(i, i2, z);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ HpackDecoder getHpackDecoder() {
        return super.getHpackDecoder();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Input
    public /* bridge */ /* synthetic */ int getMaxFrameSize() {
        return super.getMaxFrameSize();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Input
    public /* bridge */ /* synthetic */ boolean fill(boolean z, byte[] bArr, int i, int i2) throws IOException {
        return super.fill(z, bArr, i, i2);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public /* bridge */ /* synthetic */ void increaseOverheadCount(FrameType frameType) {
        super.increaseOverheadCount(frameType);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, jakarta.servlet.http.HttpUpgradeHandler
    public /* bridge */ /* synthetic */ void destroy() {
        super.destroy();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void pause() {
        super.pause();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void timeoutAsync(long j) {
        super.timeoutAsync(j);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent socketEvent) {
        return super.upgradeDispatch(socketEvent);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void setSslSupport(SSLSupport sSLSupport) {
        super.setSslSupport(sSLSupport);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public /* bridge */ /* synthetic */ void setSocketWrapper(SocketWrapperBase socketWrapperBase) {
        super.setSocketWrapper(socketWrapperBase);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, jakarta.servlet.http.HttpUpgradeHandler
    public /* bridge */ /* synthetic */ void init(WebConnection webConnection) {
        super.init(webConnection);
    }

    public Http2AsyncUpgradeHandler(Http2Protocol protocol, Adapter adapter, Request coyoteRequest, SocketWrapperBase<?> socketWrapper) {
        super(protocol, adapter, coyoteRequest, socketWrapper);
        this.headerWriteLock = new ReentrantLock();
        this.sendResetLock = new ReentrantLock();
        this.error = new AtomicReference<>();
        this.applicationIOE = new AtomicReference<>();
        this.errorCompletion = new CompletionHandler<Long, Void>() { // from class: org.apache.coyote.http2.Http2AsyncUpgradeHandler.1
            @Override // java.nio.channels.CompletionHandler
            public void completed(Long result, Void attachment) {
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable t, Void attachment) {
                Http2AsyncUpgradeHandler.this.error.set(t);
            }
        };
        this.applicationErrorCompletion = new CompletionHandler<Long, Void>() { // from class: org.apache.coyote.http2.Http2AsyncUpgradeHandler.2
            @Override // java.nio.channels.CompletionHandler
            public void completed(Long result, Void attachment) {
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable t, Void attachment) {
                if (t instanceof IOException) {
                    Http2AsyncUpgradeHandler.this.applicationIOE.set((IOException) t);
                }
                Http2AsyncUpgradeHandler.this.error.set(t);
            }
        };
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2Parser getParser(String connectionId) {
        return new Http2AsyncParser(connectionId, this, this, this.socketWrapper, this);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2UpgradeHandler.PingManager getPingManager() {
        return new AsyncPingManager();
    }

    @Override // org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
    public boolean hasAsyncIO() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void processConnection(WebConnection webConnection, Stream stream) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processConnectionCallback(WebConnection webConnection, Stream stream) {
        super.processConnection(webConnection, stream);
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected void writeSettings() {
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(this.localSettings.getSettingsFrameForPending()), ByteBuffer.wrap(createWindowUpdateForSettings()));
        Throwable err = this.error.get();
        if (err != null) {
            String msg = sm.getString("upgradeHandler.sendPrefaceFail", this.connectionId);
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
            throw new ProtocolException(msg, err);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void sendStreamReset(StreamStateMachine state, StreamException se) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.rst.debug", this.connectionId, Integer.toString(se.getStreamId()), se.getError(), se.getMessage()));
        }
        byte[] rstFrame = new byte[13];
        ByteUtil.setThreeBytes(rstFrame, 0, 4);
        rstFrame[3] = FrameType.RST.getIdByte();
        ByteUtil.set31Bits(rstFrame, 5, se.getStreamId());
        ByteUtil.setFourBytes(rstFrame, 9, se.getError().getCode());
        this.sendResetLock.lock();
        if (state != null) {
            try {
                boolean active = state.isActive();
                state.sendReset();
                if (active) {
                    decrementActiveRemoteStreamCount();
                }
            } catch (Throwable th) {
                this.sendResetLock.unlock();
                throw th;
            }
        }
        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(rstFrame));
        this.sendResetLock.unlock();
        handleAsyncException();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
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
        if (debugMsg != null) {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload), ByteBuffer.wrap(debugMsg));
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(payloadLength), ByteBuffer.wrap(GOAWAY), ByteBuffer.wrap(fixedPayload));
        }
        handleAsyncException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void writeHeaders(Stream stream, int pushedStreamId, MimeHeaders mimeHeaders, boolean endOfStream, int payloadSize) throws IOException {
        this.headerWriteLock.lock();
        try {
            AsyncHeaderFrameBuffers headerFrameBuffers = (AsyncHeaderFrameBuffers) doWriteHeaders(stream, pushedStreamId, mimeHeaders, endOfStream, payloadSize);
            if (headerFrameBuffers != null) {
                this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, (ByteBuffer[]) headerFrameBuffers.bufs.toArray(BYTEBUFFER_ARRAY));
                handleAsyncException();
            }
            if (endOfStream) {
                sentEndOfStream(stream);
            }
        } finally {
            this.headerWriteLock.unlock();
        }
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    protected Http2UpgradeHandler.HeaderFrameBuffers getHeaderFrameBuffers(int initialPayloadSize) {
        return new AsyncHeaderFrameBuffers(initialPayloadSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
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
            int orgLimit = data.limit();
            data.limit(data.position() + len);
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.applicationErrorCompletion, ByteBuffer.wrap(header), data);
            data.limit(orgLimit);
            handleAsyncException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public void writeWindowUpdate(AbstractNonZeroStream stream, int increment, boolean applicationInitiated) throws IOException {
        int streamIncrement;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("upgradeHandler.windowUpdateConnection", getConnectionId(), Integer.valueOf(increment)));
        }
        byte[] frame = new byte[13];
        ByteUtil.setThreeBytes(frame, 0, 4);
        frame[3] = FrameType.WINDOW_UPDATE.getIdByte();
        ByteUtil.set31Bits(frame, 9, increment);
        boolean neetToWriteConnectionUpdate = true;
        if ((stream instanceof Stream) && ((Stream) stream).canWrite() && (streamIncrement = ((Stream) stream).getWindowUpdateSizeToWrite(increment)) > 0) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeHandler.windowUpdateStream", getConnectionId(), getIdAsString(), Integer.valueOf(streamIncrement)));
            }
            byte[] frame2 = new byte[13];
            ByteUtil.setThreeBytes(frame2, 0, 4);
            frame2[3] = FrameType.WINDOW_UPDATE.getIdByte();
            ByteUtil.set31Bits(frame2, 9, streamIncrement);
            ByteUtil.set31Bits(frame2, 5, stream.getIdAsInt());
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(frame), ByteBuffer.wrap(frame2));
            neetToWriteConnectionUpdate = false;
        }
        if (neetToWriteConnectionUpdate) {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(frame));
        }
        handleAsyncException();
    }

    @Override // org.apache.coyote.http2.Http2UpgradeHandler, org.apache.coyote.http2.Http2Parser.Output
    public void settingsEnd(boolean ack) throws IOException {
        if (ack) {
            if (!this.localSettings.ack()) {
                log.warn(sm.getString("upgradeHandler.unexpectedAck", this.connectionId, getIdAsString()));
            }
        } else {
            this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, this.errorCompletion, ByteBuffer.wrap(SETTINGS_ACK));
        }
        handleAsyncException();
    }

    private void handleAsyncException() throws IOException {
        IOException ioe = this.applicationIOE.getAndSet(null);
        if (ioe != null) {
            handleAppInitiatedIOException(ioe);
            return;
        }
        Throwable err = this.error.getAndSet(null);
        if (err != null) {
            if (err instanceof IOException) {
                throw ((IOException) err);
            }
            throw new IOException(err);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.http2.Http2UpgradeHandler
    public SendfileState processSendfile(SendfileData sendfile) {
        if (sendfile != null) {
            try {
                FileChannel channel = FileChannel.open(sendfile.path, StandardOpenOption.READ);
                try {
                    sendfile.mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, sendfile.pos, sendfile.end - sendfile.pos);
                    if (channel != null) {
                        channel.close();
                    }
                    int reservation = sendfile.end - sendfile.pos > 2147483647L ? Integer.MAX_VALUE : (int) (sendfile.end - sendfile.pos);
                    sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                    sendfile.connectionReservation = reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("upgradeHandler.sendfile.reservation", this.connectionId, sendfile.stream.getIdAsString(), Integer.valueOf(sendfile.connectionReservation), Integer.valueOf(sendfile.streamReservation)));
                    }
                    int frameSize = Integer.min(getMaxFrameSize(), sendfile.connectionReservation);
                    boolean finished = ((long) frameSize) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
                    boolean writable = sendfile.stream.canWrite();
                    byte[] header = new byte[9];
                    ByteUtil.setThreeBytes(header, 0, frameSize);
                    header[3] = FrameType.DATA.getIdByte();
                    if (finished) {
                        header[4] = 1;
                        sentEndOfStream(sendfile.stream);
                    }
                    if (writable) {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("upgradeHandler.writeBody", this.connectionId, sendfile.stream.getIdAsString(), Integer.toString(frameSize), Boolean.valueOf(finished)));
                        }
                        ByteUtil.set31Bits(header, 5, sendfile.stream.getIdAsInt());
                        sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                        this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE_WITH_COMPLETION, new SendfileCompletionHandler(), ByteBuffer.wrap(header), sendfile.mappedBuffer);
                        try {
                            handleAsyncException();
                        } catch (IOException e) {
                            return SendfileState.ERROR;
                        }
                    }
                    return SendfileState.PENDING;
                } finally {
                }
            } catch (IOException e2) {
                return SendfileState.ERROR;
            }
        }
        return SendfileState.DONE;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$SendfileCompletionHandler.class */
    protected class SendfileCompletionHandler implements CompletionHandler<Long, SendfileData> {
        protected SendfileCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Long nBytes, SendfileData sendfile) {
            SocketWrapperBase.CompletionState completionState = null;
            long bytesWritten = nBytes.longValue() - 9;
            do {
                sendfile.left -= bytesWritten;
                if (sendfile.left == 0) {
                    try {
                        sendfile.stream.getOutputBuffer().end();
                        return;
                    } catch (IOException e) {
                        failed((Throwable) e, sendfile);
                        return;
                    }
                }
                sendfile.streamReservation = (int) (sendfile.streamReservation - bytesWritten);
                sendfile.connectionReservation = (int) (sendfile.connectionReservation - bytesWritten);
                sendfile.pos += bytesWritten;
                try {
                    if (sendfile.connectionReservation == 0) {
                        if (sendfile.streamReservation == 0) {
                            int reservation = sendfile.end - sendfile.pos > 2147483647L ? Integer.MAX_VALUE : (int) (sendfile.end - sendfile.pos);
                            sendfile.streamReservation = sendfile.stream.reserveWindowSize(reservation, true);
                        }
                        sendfile.connectionReservation = Http2AsyncUpgradeHandler.this.reserveWindowSize(sendfile.stream, sendfile.streamReservation, true);
                    }
                    if (Http2UpgradeHandler.log.isDebugEnabled()) {
                        Http2UpgradeHandler.log.debug(Http2UpgradeHandler.sm.getString("upgradeHandler.sendfile.reservation", Http2AsyncUpgradeHandler.this.connectionId, sendfile.stream.getIdAsString(), Integer.valueOf(sendfile.connectionReservation), Integer.valueOf(sendfile.streamReservation)));
                    }
                    int frameSize = Integer.min(Http2AsyncUpgradeHandler.this.getMaxFrameSize(), sendfile.connectionReservation);
                    boolean finished = ((long) frameSize) == sendfile.left && sendfile.stream.getCoyoteResponse().getTrailerFields() == null;
                    boolean writable = sendfile.stream.canWrite();
                    byte[] header = new byte[9];
                    ByteUtil.setThreeBytes(header, 0, frameSize);
                    header[3] = FrameType.DATA.getIdByte();
                    if (finished) {
                        header[4] = 1;
                        Http2AsyncUpgradeHandler.this.sentEndOfStream(sendfile.stream);
                    }
                    if (writable) {
                        if (Http2UpgradeHandler.log.isDebugEnabled()) {
                            Http2UpgradeHandler.log.debug(Http2UpgradeHandler.sm.getString("upgradeHandler.writeBody", Http2AsyncUpgradeHandler.this.connectionId, sendfile.stream.getIdAsString(), Integer.toString(frameSize), Boolean.valueOf(finished)));
                        }
                        ByteUtil.set31Bits(header, 5, sendfile.stream.getIdAsInt());
                        sendfile.mappedBuffer.limit(sendfile.mappedBuffer.position() + frameSize);
                        completionState = Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, sendfile, SocketWrapperBase.COMPLETE_WRITE, this, ByteBuffer.wrap(header), sendfile.mappedBuffer);
                        try {
                            Http2AsyncUpgradeHandler.this.handleAsyncException();
                        } catch (IOException e2) {
                            failed((Throwable) e2, sendfile);
                            return;
                        }
                    }
                    bytesWritten = frameSize;
                } catch (IOException e3) {
                    failed((Throwable) e3, sendfile);
                    return;
                }
            } while (completionState == SocketWrapperBase.CompletionState.INLINE);
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable t, SendfileData sendfile) {
            Http2AsyncUpgradeHandler.this.applicationErrorCompletion.failed(t, null);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$AsyncPingManager.class */
    protected class AsyncPingManager extends Http2UpgradeHandler.PingManager {
        protected AsyncPingManager() {
            super();
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.PingManager
        public void sendPing(boolean force) throws IOException {
            if (this.initiateDisabled) {
                return;
            }
            long now = System.nanoTime();
            if (force || now - this.lastPingNanoTime > 10000000000L) {
                this.lastPingNanoTime = now;
                byte[] payload = new byte[8];
                int sentSequence = this.sequence + 1;
                this.sequence = sentSequence;
                Http2UpgradeHandler.PingRecord pingRecord = new Http2UpgradeHandler.PingRecord(sentSequence, now);
                this.inflightPings.add(pingRecord);
                ByteUtil.set31Bits(payload, 4, sentSequence);
                Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING), ByteBuffer.wrap(payload));
                Http2AsyncUpgradeHandler.this.handleAsyncException();
            }
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.PingManager
        public void receivePing(byte[] payload, boolean ack) throws IOException {
            if (ack) {
                super.receivePing(payload, ack);
            } else {
                Http2AsyncUpgradeHandler.this.socketWrapper.write(SocketWrapperBase.BlockingMode.SEMI_BLOCK, Http2AsyncUpgradeHandler.this.protocol.getWriteTimeout(), TimeUnit.MILLISECONDS, null, SocketWrapperBase.COMPLETE_WRITE, Http2AsyncUpgradeHandler.this.errorCompletion, ByteBuffer.wrap(Http2UpgradeHandler.PING_ACK), ByteBuffer.wrap(payload));
                Http2AsyncUpgradeHandler.this.handleAsyncException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2AsyncUpgradeHandler$AsyncHeaderFrameBuffers.class */
    private static class AsyncHeaderFrameBuffers implements Http2UpgradeHandler.HeaderFrameBuffers {
        int payloadSize;
        private byte[] header;
        private ByteBuffer payload;
        private final List<ByteBuffer> bufs = new ArrayList();

        AsyncHeaderFrameBuffers(int initialPayloadSize) {
            this.payloadSize = initialPayloadSize;
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void startFrame() {
            this.header = new byte[9];
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endFrame() throws IOException {
            this.bufs.add(ByteBuffer.wrap(this.header));
            this.bufs.add(this.payload);
        }

        @Override // org.apache.coyote.http2.Http2UpgradeHandler.HeaderFrameBuffers
        public void endHeaders() throws IOException {
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
            this.payloadSize *= 2;
            this.payload = ByteBuffer.allocate(this.payloadSize);
        }
    }
}
