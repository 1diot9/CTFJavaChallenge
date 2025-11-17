package org.apache.coyote.http2;

import jakarta.servlet.http.WebConnection;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.ProtocolException;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2Parser.class */
public class Http2Parser {
    protected static final Log log = LogFactory.getLog((Class<?>) Http2Parser.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) Http2Parser.class);
    static final byte[] CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    protected final String connectionId;
    protected final Input input;
    private final Output output;
    private volatile HpackDecoder hpackDecoder;
    private final byte[] frameHeaderBuffer = new byte[9];
    private volatile ByteBuffer headerReadBuffer = ByteBuffer.allocate(1024);
    private volatile int headersCurrentStream = -1;
    private volatile boolean headersEndStream = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2Parser$Output.class */
    public interface Output {
        HpackDecoder getHpackDecoder();

        ByteBuffer startRequestBodyFrame(int i, int i2, boolean z) throws Http2Exception;

        void endRequestBodyFrame(int i, int i2) throws Http2Exception, IOException;

        void receivedEndOfStream(int i) throws ConnectionException;

        void onSwallowedDataFramePayload(int i, int i2) throws ConnectionException, IOException;

        HpackDecoder.HeaderEmitter headersStart(int i, boolean z) throws Http2Exception, IOException;

        void headersContinue(int i, boolean z);

        void headersEnd(int i, boolean z) throws Http2Exception;

        void reset(int i, long j) throws Http2Exception;

        void setting(Setting setting, long j) throws ConnectionException;

        void settingsEnd(boolean z) throws IOException;

        void pingReceive(byte[] bArr, boolean z) throws IOException;

        void goaway(int i, long j, String str);

        void incrementWindowSize(int i, int i2) throws Http2Exception;

        void priorityUpdate(int i, Priority priority) throws Http2Exception;

        void onSwallowedUnknownFrame(int i, int i2, int i3, int i4) throws IOException;

        void increaseOverheadCount(FrameType frameType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Http2Parser(String connectionId, Input input, Output output) {
        this.connectionId = connectionId;
        this.input = input;
        this.output = output;
    }

    @Deprecated
    boolean readFrame(boolean block) throws Http2Exception, IOException {
        return readFrame(block, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean readFrame() throws Http2Exception, IOException {
        return readFrame(false, null);
    }

    protected boolean readFrame(boolean block, FrameType expected) throws IOException, Http2Exception {
        if (!this.input.fill(block, this.frameHeaderBuffer)) {
            return false;
        }
        int payloadSize = ByteUtil.getThreeBytes(this.frameHeaderBuffer, 0);
        int frameTypeId = ByteUtil.getOneByte(this.frameHeaderBuffer, 3);
        FrameType frameType = FrameType.valueOf(frameTypeId);
        int flags = ByteUtil.getOneByte(this.frameHeaderBuffer, 4);
        int streamId = ByteUtil.get31Bits(this.frameHeaderBuffer, 5);
        try {
            validateFrame(expected, frameType, streamId, flags, payloadSize);
            switch (frameType) {
                case DATA:
                    readDataFrame(streamId, flags, payloadSize, null);
                    return true;
                case HEADERS:
                    readHeadersFrame(streamId, flags, payloadSize, null);
                    return true;
                case PRIORITY:
                    readPriorityFrame(streamId, null);
                    return true;
                case RST:
                    readRstFrame(streamId, null);
                    return true;
                case SETTINGS:
                    readSettingsFrame(flags, payloadSize, null);
                    return true;
                case PUSH_PROMISE:
                    readPushPromiseFrame(streamId, flags, payloadSize, null);
                    return true;
                case PING:
                    readPingFrame(flags, null);
                    return true;
                case GOAWAY:
                    readGoawayFrame(payloadSize, null);
                    return true;
                case WINDOW_UPDATE:
                    readWindowUpdateFrame(streamId, null);
                    return true;
                case CONTINUATION:
                    readContinuationFrame(streamId, flags, payloadSize, null);
                    return true;
                case PRIORITY_UPDATE:
                    readPriorityUpdateFrame(payloadSize, null);
                    return true;
                case UNKNOWN:
                    readUnknownFrame(streamId, frameTypeId, flags, payloadSize, null);
                    return true;
                default:
                    return true;
            }
        } catch (StreamException se) {
            swallowPayload(streamId, frameTypeId, payloadSize, false, null);
            throw se;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readDataFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        int dataLength;
        String padding;
        int padLength = 0;
        boolean endOfStream = Flags.isEndOfStream(flags);
        if (Flags.hasPadding(flags)) {
            if (buffer == null) {
                byte[] b = new byte[1];
                this.input.fill(true, b);
                padLength = b[0] & 255;
            } else {
                padLength = buffer.get() & 255;
            }
            if (padLength >= payloadSize) {
                throw new ConnectionException(sm.getString("http2Parser.processFrame.tooMuchPadding", this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize)), Http2Error.PROTOCOL_ERROR);
            }
            dataLength = payloadSize - (padLength + 1);
        } else {
            dataLength = payloadSize;
        }
        if (log.isDebugEnabled()) {
            if (Flags.hasPadding(flags)) {
                padding = Integer.toString(padLength);
            } else {
                padding = "none";
            }
            log.debug(sm.getString("http2Parser.processFrameData.lengths", this.connectionId, Integer.toString(streamId), Integer.toString(dataLength), padding));
        }
        ByteBuffer dest = this.output.startRequestBodyFrame(streamId, payloadSize, endOfStream);
        if (dest == null) {
            swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false, buffer);
            if (Flags.hasPadding(flags)) {
                swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
            }
            if (endOfStream) {
                this.output.receivedEndOfStream(streamId);
                return;
            }
            return;
        }
        synchronized (dest) {
            if (dest.remaining() < payloadSize) {
                swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false, buffer);
                if (Flags.hasPadding(flags)) {
                    swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
                }
                throw new StreamException(sm.getString("http2Parser.processFrameData.window", this.connectionId), Http2Error.FLOW_CONTROL_ERROR, streamId);
            }
            if (buffer == null) {
                this.input.fill(true, dest, dataLength);
            } else {
                int oldLimit = buffer.limit();
                buffer.limit(buffer.position() + dataLength);
                dest.put(buffer);
                buffer.limit(oldLimit);
            }
            if (Flags.hasPadding(flags)) {
                swallowPayload(streamId, FrameType.DATA.getId(), padLength, true, buffer);
            }
            if (endOfStream) {
                this.output.receivedEndOfStream(streamId);
            }
            this.output.endRequestBodyFrame(streamId, dataLength);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readHeadersFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        this.headersEndStream = Flags.isEndOfStream(flags);
        if (this.hpackDecoder == null) {
            this.hpackDecoder = this.output.getHpackDecoder();
        }
        try {
            this.hpackDecoder.setHeaderEmitter(this.output.headersStart(streamId, this.headersEndStream));
            int padLength = 0;
            boolean padding = Flags.hasPadding(flags);
            boolean priority = Flags.hasPriority(flags);
            int optionalLen = 0;
            if (padding) {
                optionalLen = 1;
            }
            if (priority) {
                optionalLen += 5;
            }
            if (optionalLen > 0) {
                byte[] optional = new byte[optionalLen];
                if (buffer == null) {
                    this.input.fill(true, optional);
                } else {
                    buffer.get(optional);
                }
                if (padding) {
                    padLength = ByteUtil.getOneByte(optional, 0);
                    if (padLength >= payloadSize) {
                        throw new ConnectionException(sm.getString("http2Parser.processFrame.tooMuchPadding", this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize)), Http2Error.PROTOCOL_ERROR);
                    }
                }
                payloadSize = (payloadSize - optionalLen) - padLength;
            }
            readHeaderPayload(streamId, payloadSize, buffer);
            swallowPayload(streamId, FrameType.HEADERS.getId(), padLength, true, buffer);
            if (Flags.isEndOfHeaders(flags)) {
                onHeadersComplete(streamId);
            } else {
                this.headersCurrentStream = streamId;
            }
        } catch (StreamException se) {
            swallowPayload(streamId, FrameType.HEADERS.getId(), payloadSize, false, buffer);
            throw se;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readPriorityFrame(int streamId, ByteBuffer buffer) throws IOException {
        try {
            swallowPayload(streamId, FrameType.PRIORITY.getId(), 5, false, buffer);
        } catch (ConnectionException e) {
        }
        this.output.increaseOverheadCount(FrameType.PRIORITY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readRstFrame(int streamId, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[4];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        long errorCode = ByteUtil.getFourBytes(payload, 0);
        this.output.reset(streamId, errorCode);
        this.headersCurrentStream = -1;
        this.headersEndStream = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readSettingsFrame(int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        boolean ack = Flags.isAck(flags);
        if (payloadSize > 0 && ack) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameSettings.ackWithNonZeroPayload"), Http2Error.FRAME_SIZE_ERROR);
        }
        if (payloadSize == 0 && !ack) {
            this.output.setting(null, 0L);
        } else {
            byte[] setting = new byte[6];
            for (int i = 0; i < payloadSize / 6; i++) {
                if (buffer == null) {
                    this.input.fill(true, setting);
                } else {
                    buffer.get(setting);
                }
                int id = ByteUtil.getTwoBytes(setting, 0);
                long value = ByteUtil.getFourBytes(setting, 2);
                Setting key = Setting.valueOf(id);
                if (key == Setting.UNKNOWN) {
                    log.warn(sm.getString("connectionSettings.unknown", this.connectionId, Integer.toString(id), Long.toString(value)));
                }
                this.output.setting(key, value);
            }
        }
        this.output.settingsEnd(ack);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readPushPromiseFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        throw new ConnectionException(sm.getString("http2Parser.processFramePushPromise", this.connectionId, Integer.valueOf(streamId)), Http2Error.PROTOCOL_ERROR);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readPingFrame(int flags, ByteBuffer buffer) throws IOException {
        byte[] payload = new byte[8];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        this.output.pingReceive(payload, Flags.isAck(flags));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readGoawayFrame(int payloadSize, ByteBuffer buffer) throws IOException {
        byte[] payload = new byte[payloadSize];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int lastStreamId = ByteUtil.get31Bits(payload, 0);
        long errorCode = ByteUtil.getFourBytes(payload, 4);
        String debugData = null;
        if (payloadSize > 8) {
            debugData = new String(payload, 8, payloadSize - 8, StandardCharsets.UTF_8);
        }
        this.output.goaway(lastStreamId, errorCode, debugData);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readWindowUpdateFrame(int streamId, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[4];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int windowSizeIncrement = ByteUtil.get31Bits(payload, 0);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("http2Parser.processFrameWindowUpdate.debug", this.connectionId, Integer.toString(streamId), Integer.toString(windowSizeIncrement)));
        }
        if (windowSizeIncrement == 0) {
            if (streamId == 0) {
                throw new ConnectionException(sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement", this.connectionId, Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
            }
            throw new StreamException(sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement", this.connectionId, Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR, streamId);
        }
        this.output.incrementWindowSize(streamId, windowSizeIncrement);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readContinuationFrame(int streamId, int flags, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        if (this.headersCurrentStream == -1) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameContinuation.notExpected", this.connectionId, Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
        }
        boolean endOfHeaders = Flags.isEndOfHeaders(flags);
        this.output.headersContinue(payloadSize, endOfHeaders);
        readHeaderPayload(streamId, payloadSize, buffer);
        if (endOfHeaders) {
            this.headersCurrentStream = -1;
            onHeadersComplete(streamId);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readPriorityUpdateFrame(int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        byte[] payload = new byte[payloadSize];
        if (buffer == null) {
            this.input.fill(true, payload);
        } else {
            buffer.get(payload);
        }
        int prioritizedStreamID = ByteUtil.get31Bits(payload, 0);
        if (prioritizedStreamID == 0) {
            throw new ConnectionException(sm.getString("http2Parser.processFramePriorityUpdate.streamZero"), Http2Error.PROTOCOL_ERROR);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(payload, 4, payloadSize - 4);
        Reader r = new BufferedReader(new InputStreamReader(bais, StandardCharsets.US_ASCII));
        Priority p = Priority.parsePriority(r);
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("http2Parser.processFramePriorityUpdate.debug", this.connectionId, Integer.toString(prioritizedStreamID), Integer.toString(p.getUrgency()), Boolean.valueOf(p.getIncremental())));
        }
        this.output.priorityUpdate(prioritizedStreamID, p);
    }

    protected void readHeaderPayload(int streamId, int payloadSize, ByteBuffer buffer) throws Http2Exception, IOException {
        int newSize;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("http2Parser.processFrameHeaders.payload", this.connectionId, Integer.valueOf(streamId), Integer.valueOf(payloadSize)));
        }
        int remaining = payloadSize;
        while (remaining > 0) {
            if (this.headerReadBuffer.remaining() == 0) {
                if (this.headerReadBuffer.capacity() < payloadSize) {
                    newSize = payloadSize;
                } else {
                    newSize = this.headerReadBuffer.capacity() * 2;
                }
                this.headerReadBuffer = ByteBufferUtils.expand(this.headerReadBuffer, newSize);
            }
            int toRead = Math.min(this.headerReadBuffer.remaining(), remaining);
            if (buffer == null) {
                this.input.fill(true, this.headerReadBuffer, toRead);
            } else {
                int oldLimit = buffer.limit();
                buffer.limit(buffer.position() + toRead);
                this.headerReadBuffer.put(buffer);
                buffer.limit(oldLimit);
            }
            this.headerReadBuffer.flip();
            try {
                this.hpackDecoder.decode(this.headerReadBuffer);
                this.headerReadBuffer.compact();
                remaining -= toRead;
                if (this.hpackDecoder.isHeaderCountExceeded()) {
                    StreamException headerException = new StreamException(sm.getString("http2Parser.headerLimitCount", this.connectionId, Integer.valueOf(streamId)), Http2Error.ENHANCE_YOUR_CALM, streamId);
                    this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException);
                }
                if (this.hpackDecoder.isHeaderSizeExceeded(this.headerReadBuffer.position())) {
                    StreamException headerException2 = new StreamException(sm.getString("http2Parser.headerLimitSize", this.connectionId, Integer.valueOf(streamId)), Http2Error.ENHANCE_YOUR_CALM, streamId);
                    this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException2);
                }
                if (this.hpackDecoder.isHeaderSwallowSizeExceeded(this.headerReadBuffer.position())) {
                    throw new ConnectionException(sm.getString("http2Parser.headerLimitSize", this.connectionId, Integer.valueOf(streamId)), Http2Error.ENHANCE_YOUR_CALM);
                }
            } catch (HpackException hpe) {
                throw new ConnectionException(sm.getString("http2Parser.processFrameHeaders.decodingFailed"), Http2Error.COMPRESSION_ERROR, hpe);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readUnknownFrame(int streamId, int frameTypeId, int flags, int payloadSize, ByteBuffer buffer) throws IOException {
        try {
            swallowPayload(streamId, frameTypeId, payloadSize, false, buffer);
            this.output.onSwallowedUnknownFrame(streamId, frameTypeId, flags, payloadSize);
        } catch (ConnectionException e) {
            this.output.onSwallowedUnknownFrame(streamId, frameTypeId, flags, payloadSize);
        } catch (Throwable th) {
            this.output.onSwallowedUnknownFrame(streamId, frameTypeId, flags, payloadSize);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void swallowPayload(int streamId, int frameTypeId, int len, boolean isPadding, ByteBuffer byteBuffer) throws IOException, ConnectionException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("http2Parser.swallow.debug", this.connectionId, Integer.toString(streamId), Integer.toString(len)));
        }
        try {
            if (len != 0) {
                if (!isPadding && byteBuffer != null) {
                    byteBuffer.position(byteBuffer.position() + len);
                } else {
                    int read = 0;
                    byte[] buffer = new byte[1024];
                    while (read < len) {
                        int thisTime = Math.min(buffer.length, len - read);
                        if (byteBuffer == null) {
                            this.input.fill(true, buffer, 0, thisTime);
                        } else {
                            byteBuffer.get(buffer, 0, thisTime);
                        }
                        if (isPadding) {
                            for (int i = 0; i < thisTime; i++) {
                                if (buffer[i] != 0) {
                                    throw new ConnectionException(sm.getString("http2Parser.nonZeroPadding", this.connectionId, Integer.toString(streamId)), Http2Error.PROTOCOL_ERROR);
                                }
                            }
                        }
                        read += thisTime;
                    }
                }
                if (FrameType.DATA.getIdByte() == frameTypeId) {
                    if (isPadding) {
                        len++;
                    }
                    if (len > 0) {
                        this.output.onSwallowedDataFramePayload(streamId, len);
                        return;
                    }
                    return;
                }
                return;
            }
            if (FrameType.DATA.getIdByte() != frameTypeId) {
                return;
            }
            if (isPadding) {
                len++;
            }
            if (len <= 0) {
                return;
            }
            this.output.onSwallowedDataFramePayload(streamId, len);
        } catch (Throwable th) {
            if (FrameType.DATA.getIdByte() == frameTypeId) {
                if (isPadding) {
                    len++;
                }
                if (len > 0) {
                    this.output.onSwallowedDataFramePayload(streamId, len);
                }
            }
            throw th;
        }
    }

    protected void onHeadersComplete(int streamId) throws Http2Exception {
        if (this.headerReadBuffer.position() > 0) {
            throw new ConnectionException(sm.getString("http2Parser.processFrameHeaders.decodingDataLeft"), Http2Error.COMPRESSION_ERROR);
        }
        this.hpackDecoder.getHeaderEmitter().validateHeaders();
        synchronized (this.output) {
            this.output.headersEnd(streamId, this.headersEndStream);
            if (this.headersEndStream) {
                this.headersEndStream = false;
            }
        }
        if (this.headerReadBuffer.capacity() > 1024) {
            this.headerReadBuffer = ByteBuffer.allocate(1024);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void validateFrame(FrameType expected, FrameType frameType, int streamId, int flags, int payloadSize) throws Http2Exception {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("http2Parser.processFrame", this.connectionId, Integer.toString(streamId), frameType, Integer.toString(flags), Integer.toString(payloadSize)));
        }
        if (expected != null && frameType != expected) {
            throw new StreamException(sm.getString("http2Parser.processFrame.unexpectedType", expected, frameType), Http2Error.PROTOCOL_ERROR, streamId);
        }
        int maxFrameSize = this.input.getMaxFrameSize();
        if (payloadSize > maxFrameSize) {
            throw new ConnectionException(sm.getString("http2Parser.payloadTooBig", Integer.toString(payloadSize), Integer.toString(maxFrameSize)), Http2Error.FRAME_SIZE_ERROR);
        }
        if (this.headersCurrentStream != -1) {
            if (this.headersCurrentStream != streamId) {
                throw new ConnectionException(sm.getString("http2Parser.headers.wrongStream", this.connectionId, Integer.toString(this.headersCurrentStream), Integer.toString(streamId)), Http2Error.COMPRESSION_ERROR);
            }
            if (frameType != FrameType.RST && frameType != FrameType.CONTINUATION) {
                throw new ConnectionException(sm.getString("http2Parser.headers.wrongFrameType", this.connectionId, Integer.toString(this.headersCurrentStream), frameType), Http2Error.COMPRESSION_ERROR);
            }
        }
        frameType.check(streamId, payloadSize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readConnectionPreface(WebConnection webConnection, Stream stream) throws Http2Exception {
        byte[] data = new byte[CLIENT_PREFACE_START.length];
        try {
            this.input.fill(true, data);
            for (int i = 0; i < CLIENT_PREFACE_START.length; i++) {
                if (CLIENT_PREFACE_START[i] != data[i]) {
                    throw new ProtocolException(sm.getString("http2Parser.preface.invalid"));
                }
            }
            readFrame(true, FrameType.SETTINGS);
        } catch (IOException ioe) {
            throw new ProtocolException(sm.getString("http2Parser.preface.io"), ioe);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2Parser$Input.class */
    public interface Input {
        boolean fill(boolean z, byte[] bArr, int i, int i2) throws IOException;

        int getMaxFrameSize();

        default boolean fill(boolean block, byte[] data) throws IOException {
            return fill(block, data, 0, data.length);
        }

        default boolean fill(boolean block, ByteBuffer data, int len) throws IOException {
            boolean result = fill(block, data.array(), data.arrayOffset() + data.position(), len);
            if (result) {
                data.position(data.position() + len);
            }
            return result;
        }
    }
}
