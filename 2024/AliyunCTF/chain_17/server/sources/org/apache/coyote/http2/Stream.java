package org.apache.coyote.http2;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.coyote.http11.filters.VoidOutputFilter;
import org.apache.coyote.http2.HpackDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.http.parser.Priority;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.servlet.support.WebContentGenerator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream.class */
public class Stream extends AbstractNonZeroStream implements HpackDecoder.HeaderEmitter {
    private static final int HEADER_STATE_START = 0;
    private static final int HEADER_STATE_PSEUDO = 1;
    private static final int HEADER_STATE_REGULAR = 2;
    private static final int HEADER_STATE_TRAILER = 3;
    private static final MimeHeaders ACK_HEADERS;
    private volatile long contentLengthReceived;
    private final Http2UpgradeHandler handler;
    private final WindowAllocationManager allocationManager;
    private final Request coyoteRequest;
    private final Response coyoteResponse;
    private final StreamInputBuffer inputBuffer;
    private final StreamOutputBuffer streamOutputBuffer;
    private final Http2OutputBuffer http2OutputBuffer;
    private int headerState;
    private StreamException headerException;
    private volatile StringBuilder cookieHeader;
    private volatile boolean hostHeaderSeen;
    private Object pendingWindowUpdateForStreamLock;
    private int pendingWindowUpdateForStream;
    private volatile int urgency;
    private volatile boolean incremental;
    private static final Log log = LogFactory.getLog((Class<?>) Stream.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Stream.class);
    private static final Integer HTTP_UPGRADE_STREAM = 1;
    private static final Set<String> HTTP_CONNECTION_SPECIFIC_HEADERS = new HashSet();

    static {
        Response response = new Response();
        response.setStatus(100);
        StreamProcessor.prepareHeaders(null, response, true, null, null);
        ACK_HEADERS = response.getMimeHeaders();
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("proxy-connection");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add(org.apache.coyote.http11.Constants.KEEP_ALIVE_HEADER_VALUE_TOKEN);
        HTTP_CONNECTION_SPECIFIC_HEADERS.add("transfer-encoding");
        HTTP_CONNECTION_SPECIFIC_HEADERS.add(org.apache.tomcat.websocket.Constants.CONNECTION_HEADER_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Stream(Integer identifier, Http2UpgradeHandler handler) {
        this(identifier, handler, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Stream(Integer identifier, Http2UpgradeHandler handler, Request coyoteRequest) {
        super(handler.getConnectionId(), identifier);
        this.contentLengthReceived = 0L;
        this.allocationManager = new WindowAllocationManager(this);
        this.coyoteResponse = new Response();
        this.streamOutputBuffer = new StreamOutputBuffer();
        this.http2OutputBuffer = new Http2OutputBuffer(this.coyoteResponse, this.streamOutputBuffer);
        this.headerState = 0;
        this.headerException = null;
        this.cookieHeader = null;
        this.hostHeaderSeen = false;
        this.pendingWindowUpdateForStreamLock = new Object();
        this.pendingWindowUpdateForStream = 0;
        this.urgency = 3;
        this.incremental = false;
        this.handler = handler;
        setWindowSize(handler.getRemoteSettings().getInitialWindowSize());
        if (coyoteRequest == null) {
            this.coyoteRequest = new Request();
            this.inputBuffer = new StandardStreamInputBuffer();
            this.coyoteRequest.setInputBuffer(this.inputBuffer);
        } else {
            this.coyoteRequest = coyoteRequest;
            this.inputBuffer = new SavedRequestStreamInputBuffer((SavedRequestInputFilter) coyoteRequest.getInputBuffer());
            this.state.receivedStartOfHeaders();
            if (HTTP_UPGRADE_STREAM.equals(identifier)) {
                try {
                    prepareRequest();
                } catch (IllegalArgumentException e) {
                    this.coyoteResponse.setStatus(400);
                    this.coyoteResponse.setError();
                }
            }
            this.state.receivedEndOfStream();
        }
        this.coyoteRequest.setSendfile(handler.hasAsyncIO() && handler.getProtocol().getUseSendfile());
        this.coyoteResponse.setOutputBuffer(this.http2OutputBuffer);
        this.coyoteRequest.setResponse(this.coyoteResponse);
        this.coyoteRequest.protocol().setString("HTTP/2.0");
        if (this.coyoteRequest.getStartTimeNanos() < 0) {
            this.coyoteRequest.setStartTimeNanos(System.nanoTime());
        }
    }

    private void prepareRequest() {
        if (this.coyoteRequest.scheme().isNull()) {
            if (this.handler.getProtocol().getHttp11Protocol().isSSLEnabled()) {
                this.coyoteRequest.scheme().setString("https");
            } else {
                this.coyoteRequest.scheme().setString("http");
            }
        }
        MessageBytes hostValueMB = this.coyoteRequest.getMimeHeaders().getUniqueValue("host");
        if (hostValueMB == null) {
            throw new IllegalArgumentException();
        }
        hostValueMB.toBytes();
        ByteChunk valueBC = hostValueMB.getByteChunk();
        byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        int valueS = valueBC.getStart();
        int colonPos = Host.parse(hostValueMB);
        if (colonPos != -1) {
            int port = 0;
            for (int i = colonPos + 1; i < valueL; i++) {
                char c = (char) valueB[i + valueS];
                if (c < '0' || c > '9') {
                    throw new IllegalArgumentException();
                }
                port = ((port * 10) + c) - 48;
            }
            this.coyoteRequest.setServerPort(port);
            valueL = colonPos;
        }
        char[] hostNameC = new char[valueL];
        for (int i2 = 0; i2 < valueL; i2++) {
            hostNameC[i2] = (char) valueB[i2 + valueS];
        }
        this.coyoteRequest.serverName().setChars(hostNameC, 0, valueL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receiveReset(long errorCode) {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.reset.receive", getConnectionId(), getIdAsString(), Long.toString(errorCode)));
        }
        this.state.receivedReset();
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        cancelAllocationRequests();
    }

    final void cancelAllocationRequests() {
        this.allocationManager.notifyAny();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public final void incrementWindowSize(int windowSizeIncrement) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            boolean notify = getWindowSize() < 1;
            super.incrementWindowSize(windowSizeIncrement);
            if (notify && getWindowSize() > 0) {
                this.allocationManager.notifyStream();
            }
        } finally {
            this.windowAllocationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int reserveWindowSize(int reservation, boolean block) throws IOException {
        int allocation;
        this.windowAllocationLock.lock();
        try {
            long windowSize = getWindowSize();
            while (windowSize < 1) {
                if (!canWrite()) {
                    throw new CloseNowException(sm.getString("stream.notWritable", getConnectionId(), getIdAsString()));
                }
                if (block) {
                    try {
                        long writeTimeout = this.handler.getProtocol().getStreamWriteTimeout();
                        this.allocationManager.waitForStream(writeTimeout);
                        windowSize = getWindowSize();
                        if (windowSize == 0) {
                            doStreamCancel(sm.getString("stream.writeTimeout"), Http2Error.ENHANCE_YOUR_CALM);
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else {
                    this.allocationManager.waitForStreamNonBlocking();
                    this.windowAllocationLock.unlock();
                    return 0;
                }
            }
            if (windowSize < reservation) {
                allocation = (int) windowSize;
            } else {
                allocation = reservation;
            }
            decrementWindowSize(allocation);
            int i = allocation;
            this.windowAllocationLock.unlock();
            return i;
        } catch (Throwable th) {
            this.windowAllocationLock.unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doStreamCancel(String msg, Http2Error error) throws CloseNowException {
        StreamException se = new StreamException(msg, error, getIdAsInt());
        this.streamOutputBuffer.closed = true;
        this.coyoteResponse.setError();
        this.coyoteResponse.setErrorReported();
        this.streamOutputBuffer.reset = se;
        throw new CloseNowException(msg, se);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForConnectionAllocation(long timeout) throws InterruptedException {
        this.allocationManager.waitForConnection(timeout);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitForConnectionAllocationNonBlocking() {
        this.allocationManager.waitForConnectionNonBlocking();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyConnection() {
        this.allocationManager.notifyConnection();
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public final void emitHeader(String name, String value) throws HpackException {
        String uri;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.header.debug", getConnectionId(), getIdAsString(), name, value));
        }
        if (!name.toLowerCase(Locale.US).equals(name)) {
            throw new HpackException(sm.getString("stream.header.case", getConnectionId(), getIdAsString(), name));
        }
        if (HTTP_CONNECTION_SPECIFIC_HEADERS.contains(name)) {
            throw new HpackException(sm.getString("stream.header.connection", getConnectionId(), getIdAsString(), name));
        }
        if ("te".equals(name) && !"trailers".equals(value)) {
            throw new HpackException(sm.getString("stream.header.te", getConnectionId(), getIdAsString(), value));
        }
        if (this.headerException != null) {
            return;
        }
        if (name.length() == 0) {
            throw new HpackException(sm.getString("stream.header.empty", getConnectionId(), getIdAsString()));
        }
        boolean pseudoHeader = name.charAt(0) == ':';
        if (pseudoHeader && this.headerState != 1) {
            this.headerException = new StreamException(sm.getString("stream.header.unexpectedPseudoHeader", getConnectionId(), getIdAsString(), name), Http2Error.PROTOCOL_ERROR, getIdAsInt());
            return;
        }
        if (this.headerState == 1 && !pseudoHeader) {
            this.headerState = 2;
        }
        boolean z = -1;
        switch (name.hashCode()) {
            case -1354757532:
                if (name.equals("cookie")) {
                    z = 4;
                    break;
                }
                break;
            case -1332238263:
                if (name.equals(":authority")) {
                    z = 3;
                    break;
                }
                break;
            case -1165461084:
                if (name.equals("priority")) {
                    z = 6;
                    break;
                }
                break;
            case -1141949029:
                if (name.equals(":method")) {
                    z = false;
                    break;
                }
                break;
            case -972381601:
                if (name.equals(":scheme")) {
                    z = true;
                    break;
                }
                break;
            case 3208616:
                if (name.equals("host")) {
                    z = 5;
                    break;
                }
                break;
            case 56997727:
                if (name.equals(":path")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (this.coyoteRequest.method().isNull()) {
                    this.coyoteRequest.method().setString(value);
                    if (WebContentGenerator.METHOD_HEAD.equals(value)) {
                        configureVoidOutputFilter();
                        return;
                    }
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":method"));
            case true:
                if (this.coyoteRequest.scheme().isNull()) {
                    this.coyoteRequest.scheme().setString(value);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":scheme"));
            case true:
                if (!this.coyoteRequest.requestURI().isNull()) {
                    throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":path"));
                }
                if (value.length() == 0) {
                    throw new HpackException(sm.getString("stream.header.noPath", getConnectionId(), getIdAsString()));
                }
                int queryStart = value.indexOf(63);
                if (queryStart == -1) {
                    uri = value;
                } else {
                    uri = value.substring(0, queryStart);
                    String query = value.substring(queryStart + 1);
                    this.coyoteRequest.queryString().setString(query);
                }
                byte[] uriBytes = uri.getBytes(StandardCharsets.ISO_8859_1);
                this.coyoteRequest.requestURI().setBytes(uriBytes, 0, uriBytes.length);
                return;
            case true:
                if (this.coyoteRequest.serverName().isNull()) {
                    parseAuthority(value, false);
                    return;
                }
                throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), ":authority"));
            case true:
                if (this.cookieHeader == null) {
                    this.cookieHeader = new StringBuilder();
                } else {
                    this.cookieHeader.append("; ");
                }
                this.cookieHeader.append(value);
                return;
            case true:
                if (this.coyoteRequest.serverName().isNull()) {
                    this.hostHeaderSeen = true;
                    parseAuthority(value, true);
                    return;
                } else {
                    if (!this.hostHeaderSeen) {
                        this.hostHeaderSeen = true;
                        compareAuthority(value);
                        return;
                    }
                    throw new HpackException(sm.getString("stream.header.duplicate", getConnectionId(), getIdAsString(), "host"));
                }
            case true:
                try {
                    Priority p = Priority.parsePriority(new StringReader(value));
                    setUrgency(p.getUrgency());
                    setIncremental(p.getIncremental());
                    return;
                } catch (IOException e) {
                    return;
                }
            default:
                if (this.headerState != 3 || this.handler.getProtocol().isTrailerHeaderAllowed(name)) {
                    if ("expect".equals(name) && "100-continue".equals(value)) {
                        this.coyoteRequest.setExpectation(true);
                    }
                    if (pseudoHeader) {
                        this.headerException = new StreamException(sm.getString("stream.header.unknownPseudoHeader", getConnectionId(), getIdAsString(), name), Http2Error.PROTOCOL_ERROR, getIdAsInt());
                    }
                    if (this.headerState == 3) {
                        this.coyoteRequest.getTrailerFields().put(name, value);
                        return;
                    } else {
                        this.coyoteRequest.getMimeHeaders().addValue(name).setString(value);
                        return;
                    }
                }
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void configureVoidOutputFilter() {
        addOutputFilter(new VoidOutputFilter());
        this.streamOutputBuffer.closed = true;
    }

    private void parseAuthority(String value, boolean host) throws HpackException {
        try {
            int i = Host.parse(value);
            if (i > -1) {
                this.coyoteRequest.serverName().setString(value.substring(0, i));
                this.coyoteRequest.setServerPort(Integer.parseInt(value.substring(i + 1)));
            } else {
                this.coyoteRequest.serverName().setString(value);
            }
        } catch (IllegalArgumentException e) {
            StringManager stringManager = sm;
            Object[] objArr = new Object[4];
            objArr[0] = getConnectionId();
            objArr[1] = getIdAsString();
            objArr[2] = host ? "host" : ":authority";
            objArr[3] = value;
            throw new HpackException(stringManager.getString("stream.header.invalid", objArr));
        }
    }

    private void compareAuthority(String value) throws HpackException {
        try {
            int i = Host.parse(value);
            if (i != -1 || (value.equals(this.coyoteRequest.serverName().getString()) && this.coyoteRequest.getServerPort() == -1)) {
                if (i > -1) {
                    if (value.substring(0, i).equals(this.coyoteRequest.serverName().getString()) && Integer.parseInt(value.substring(i + 1)) == this.coyoteRequest.getServerPort()) {
                        return;
                    }
                } else {
                    return;
                }
            }
            throw new HpackException(sm.getString("stream.host.inconsistent", getConnectionId(), getIdAsString(), value, this.coyoteRequest.serverName().getString(), Integer.toString(this.coyoteRequest.getServerPort())));
        } catch (IllegalArgumentException e) {
            throw new HpackException(sm.getString("stream.header.invalid", getConnectionId(), getIdAsString(), "host", value));
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void setHeaderException(StreamException streamException) {
        if (this.headerException == null) {
            this.headerException = streamException;
        }
    }

    @Override // org.apache.coyote.http2.HpackDecoder.HeaderEmitter
    public void validateHeaders() throws StreamException {
        if (this.headerException == null) {
        } else {
            throw this.headerException;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean receivedEndOfHeaders() throws ConnectionException {
        if (this.coyoteRequest.method().isNull() || this.coyoteRequest.scheme().isNull() || (!this.coyoteRequest.method().equals("CONNECT") && this.coyoteRequest.requestURI().isNull())) {
            throw new ConnectionException(sm.getString("stream.header.required", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR);
        }
        if (this.cookieHeader != null) {
            this.coyoteRequest.getMimeHeaders().addValue("cookie").setString(this.cookieHeader.toString());
        }
        return this.headerState == 2 || this.headerState == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeHeaders() throws IOException {
        boolean endOfStream = this.streamOutputBuffer.hasNoBody() && this.coyoteResponse.getTrailerFields() == null;
        this.handler.writeHeaders(this, 0, this.coyoteResponse.getMimeHeaders(), endOfStream, 1024);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addOutputFilter(OutputFilter filter) {
        this.http2OutputBuffer.addFilter(filter);
    }

    final void writeTrailers() throws IOException {
        Supplier<Map<String, String>> supplier = this.coyoteResponse.getTrailerFields();
        if (supplier == null) {
            return;
        }
        MimeHeaders mimeHeaders = this.coyoteResponse.getMimeHeaders();
        mimeHeaders.recycle();
        Map<String, String> headerMap = supplier.get();
        if (headerMap == null) {
            headerMap = Collections.emptyMap();
        }
        for (Map.Entry<String, String> headerEntry : headerMap.entrySet()) {
            MessageBytes mb = mimeHeaders.addValue(headerEntry.getKey());
            mb.setString(headerEntry.getValue());
        }
        this.handler.writeHeaders(this, 0, mimeHeaders, true, 1024);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeAck() throws IOException {
        this.handler.writeHeaders(this, 0, ACK_HEADERS, false, 64);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractStream
    public final String getConnectionId() {
        return this.handler.getConnectionId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    public final ByteBuffer getInputByteBuffer() {
        if (this.inputBuffer == null) {
            return ZERO_LENGTH_BYTEBUFFER;
        }
        return this.inputBuffer.getInBuffer();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receivedStartOfHeaders(boolean headersEndStream) throws Http2Exception {
        if (this.headerState == 0) {
            this.headerState = 1;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxHeaderCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxHeaderSize());
        } else if (this.headerState == 1 || this.headerState == 2) {
            if (headersEndStream) {
                this.headerState = 3;
                this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getProtocol().getMaxTrailerCount());
                this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getProtocol().getMaxTrailerSize());
            } else {
                throw new ConnectionException(sm.getString("stream.trailerHeader.noEndOfStream", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR);
            }
        }
        this.state.receivedStartOfHeaders();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.coyote.http2.AbstractNonZeroStream
    public final void receivedData(int payloadSize) throws Http2Exception {
        this.contentLengthReceived += payloadSize;
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived > contentLengthHeader) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdAsString(), Long.valueOf(contentLengthHeader), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void receivedEndOfStream() throws ConnectionException {
        if (isContentLengthInconsistent()) {
            throw new ConnectionException(sm.getString("stream.header.contentLength", getConnectionId(), getIdAsString(), Long.valueOf(this.coyoteRequest.getContentLengthLong()), Long.valueOf(this.contentLengthReceived)), Http2Error.PROTOCOL_ERROR);
        }
        this.state.receivedEndOfStream();
        if (this.inputBuffer != null) {
            this.inputBuffer.notifyEof();
        }
    }

    final boolean isContentLengthInconsistent() {
        long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1 && this.contentLengthReceived != contentLengthHeader) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void sentHeaders() {
        this.state.sentHeaders();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void sentEndOfStream() {
        this.streamOutputBuffer.endOfStreamSent = true;
        this.state.sentEndOfStream();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isReadyForWrite() {
        return this.streamOutputBuffer.isReady();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean flush(boolean block) throws IOException {
        return this.streamOutputBuffer.flush(block);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final StreamInputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final HttpOutputBuffer getOutputBuffer() {
        return this.http2OutputBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void sentPushPromise() {
        this.state.sentPushPromise();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isActive() {
        return this.state.isActive();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean canWrite() {
        return this.state.canWrite();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void closeIfIdle() {
        this.state.closeIfIdle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isInputFinished() {
        return !this.state.isFrameTypePermitted(FrameType.DATA);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void close(Http2Exception http2Exception) {
        if (http2Exception instanceof StreamException) {
            try {
                StreamException se = (StreamException) http2Exception;
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("stream.reset.send", getConnectionId(), getIdAsString(), se.getError()));
                }
                this.handler.sendStreamReset(this.state, se);
                cancelAllocationRequests();
                if (this.inputBuffer != null) {
                    this.inputBuffer.swallowUnread();
                }
            } catch (IOException ioe) {
                ConnectionException ce = new ConnectionException(sm.getString("stream.reset.fail", getConnectionId(), getIdAsString()), Http2Error.PROTOCOL_ERROR, ioe);
                this.handler.closeConnection(ce);
            }
        } else {
            this.handler.closeConnection(http2Exception);
        }
        recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void recycle() {
        int remaining;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("stream.recycle", getConnectionId(), getIdAsString()));
        }
        ByteBuffer inputByteBuffer = getInputByteBuffer();
        if (inputByteBuffer == null) {
            remaining = 0;
        } else {
            remaining = inputByteBuffer.remaining();
        }
        this.handler.replaceStream(this, new RecycledStream(getConnectionId(), getIdentifier(), this.state, remaining));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isPushSupported() {
        return this.handler.getRemoteSettings().getEnablePush();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void push(Request request) throws IOException {
        if (!isPushSupported() || getIdAsInt() % 2 == 0) {
            return;
        }
        request.getMimeHeaders().addValue(":method").duplicate(request.method());
        request.getMimeHeaders().addValue(":scheme").duplicate(request.scheme());
        StringBuilder path = new StringBuilder(request.requestURI().toString());
        if (!request.queryString().isNull()) {
            path.append('?');
            path.append(request.queryString().toString());
        }
        request.getMimeHeaders().addValue(":path").setString(path.toString());
        if ((!request.scheme().equals("http") || request.getServerPort() != 80) && (!request.scheme().equals("https") || request.getServerPort() != 443)) {
            request.getMimeHeaders().addValue(":authority").setString(request.serverName().getString() + ":" + request.getServerPort());
        } else {
            request.getMimeHeaders().addValue(":authority").duplicate(request.serverName());
        }
        push(this.handler, request, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTrailerFieldsReady() {
        return !this.state.canRead();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTrailerFieldsSupported() {
        return !this.streamOutputBuffer.endOfStreamSent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StreamException getResetException() {
        return this.streamOutputBuffer.reset;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWindowUpdateSizeToWrite(int increment) {
        int result;
        int threshold = this.handler.getProtocol().getOverheadWindowUpdateThreshold();
        synchronized (this.pendingWindowUpdateForStreamLock) {
            if (increment > threshold) {
                result = increment + this.pendingWindowUpdateForStream;
                this.pendingWindowUpdateForStream = 0;
            } else {
                this.pendingWindowUpdateForStream += increment;
                if (this.pendingWindowUpdateForStream > threshold) {
                    result = this.pendingWindowUpdateForStream;
                    this.pendingWindowUpdateForStream = 0;
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }

    public int getUrgency() {
        return this.urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public boolean getIncremental() {
        return this.incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    private static void push(Http2UpgradeHandler handler, Request request, Stream stream) throws IOException {
        if (org.apache.coyote.Constants.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(new PrivilegedPush(handler, request, stream));
                return;
            } catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new IOException(ex);
            }
        }
        handler.push(request, stream);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream$PrivilegedPush.class */
    public static class PrivilegedPush implements PrivilegedExceptionAction<Void> {
        private final Http2UpgradeHandler handler;
        private final Request request;
        private final Stream stream;

        PrivilegedPush(Http2UpgradeHandler handler, Request request, Stream stream) {
            this.handler = handler;
            this.request = request;
            this.stream = stream;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public Void run() throws IOException {
            this.handler.push(this.request, this.stream);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream$StreamOutputBuffer.class */
    public class StreamOutputBuffer implements HttpOutputBuffer, WriteBuffer.Sink {
        private boolean dataLeft;
        private final Lock writeLock = new ReentrantLock();
        private final ByteBuffer buffer = ByteBuffer.allocate(8192);
        private final WriteBuffer writeBuffer = new WriteBuffer(32768);
        private volatile long written = 0;
        private int streamReservation = 0;
        private volatile boolean closed = false;
        private volatile StreamException reset = null;
        private volatile boolean endOfStreamSent = false;

        StreamOutputBuffer() {
        }

        @Override // org.apache.coyote.OutputBuffer
        public final int doWrite(ByteBuffer chunk) throws IOException {
            this.writeLock.lock();
            try {
                if (this.closed) {
                    throw new IOException(Stream.sm.getString("stream.closed", Stream.this.getConnectionId(), Stream.this.getIdAsString()));
                }
                int result = chunk.remaining();
                if (this.writeBuffer.isEmpty()) {
                    int chunkLimit = chunk.limit();
                    while (true) {
                        if (chunk.remaining() <= 0) {
                            break;
                        }
                        int thisTime = Math.min(this.buffer.remaining(), chunk.remaining());
                        chunk.limit(chunk.position() + thisTime);
                        this.buffer.put(chunk);
                        chunk.limit(chunkLimit);
                        if (chunk.remaining() > 0 && !this.buffer.hasRemaining()) {
                            if (flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) {
                                this.writeBuffer.add(chunk);
                                this.dataLeft = true;
                                break;
                            }
                        }
                    }
                } else {
                    this.writeBuffer.add(chunk);
                }
                this.written += result;
                this.writeLock.unlock();
                return result;
            } catch (Throwable th) {
                this.writeLock.unlock();
                throw th;
            }
        }

        final boolean flush(boolean block) throws IOException {
            this.writeLock.lock();
            try {
                boolean dataInBuffer = this.buffer.position() > 0;
                boolean flushed = false;
                if (dataInBuffer) {
                    dataInBuffer = flush(false, block);
                    flushed = true;
                }
                if (dataInBuffer) {
                    this.dataLeft = true;
                } else if (this.writeBuffer.isEmpty()) {
                    if (flushed) {
                        this.dataLeft = false;
                    } else {
                        this.dataLeft = flush(false, block);
                    }
                } else {
                    this.dataLeft = this.writeBuffer.write(this, block);
                }
                boolean z = this.dataLeft;
                this.writeLock.unlock();
                return z;
            } catch (Throwable th) {
                this.writeLock.unlock();
                throw th;
            }
        }

        private boolean flush(boolean writeInProgress, boolean block) throws IOException {
            this.writeLock.lock();
            try {
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug(Stream.sm.getString("stream.outputBuffer.flush.debug", Stream.this.getConnectionId(), Stream.this.getIdAsString(), Integer.toString(this.buffer.position()), Boolean.toString(writeInProgress), Boolean.toString(this.closed)));
                }
                if (this.buffer.position() == 0) {
                    if (this.closed && !this.endOfStreamSent) {
                        Stream.this.handler.writeBody(Stream.this, this.buffer, 0, Stream.this.coyoteResponse.getTrailerFields() == null);
                    }
                    return false;
                }
                this.buffer.flip();
                int left = this.buffer.remaining();
                while (left > 0) {
                    if (this.streamReservation == 0) {
                        this.streamReservation = Stream.this.reserveWindowSize(left, block);
                        if (this.streamReservation == 0) {
                            this.buffer.compact();
                            this.writeLock.unlock();
                            return true;
                        }
                    }
                    while (this.streamReservation > 0) {
                        int connectionReservation = Stream.this.handler.reserveWindowSize(Stream.this, this.streamReservation, block);
                        if (connectionReservation == 0) {
                            this.buffer.compact();
                            this.writeLock.unlock();
                            return true;
                        }
                        Stream.this.handler.writeBody(Stream.this, this.buffer, connectionReservation, !writeInProgress && this.closed && left == connectionReservation && Stream.this.coyoteResponse.getTrailerFields() == null);
                        this.streamReservation -= connectionReservation;
                        left -= connectionReservation;
                    }
                }
                this.buffer.clear();
                this.writeLock.unlock();
                return false;
            } finally {
                this.writeLock.unlock();
            }
        }

        final boolean isReady() {
            this.writeLock.lock();
            try {
                if ((Stream.this.getWindowSize() <= 0 || !Stream.this.allocationManager.isWaitingForStream()) && (Stream.this.handler.getWindowSize() <= 0 || !Stream.this.allocationManager.isWaitingForConnection())) {
                    if (!this.dataLeft) {
                        return true;
                    }
                }
                return false;
            } finally {
                this.writeLock.unlock();
            }
        }

        @Override // org.apache.coyote.OutputBuffer
        public final long getBytesWritten() {
            return this.written;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public final void end() throws IOException {
            if (this.reset != null) {
                throw new CloseNowException(this.reset);
            }
            if (!this.closed) {
                this.closed = true;
                flush(true);
                Stream.this.writeTrailers();
            }
        }

        final boolean hasNoBody() {
            return this.written == 0 && this.closed;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public void flush() throws IOException {
            flush(Stream.this.getCoyoteResponse().getWriteListener() == null);
        }

        @Override // org.apache.tomcat.util.net.WriteBuffer.Sink
        public boolean writeFromBuffer(ByteBuffer src, boolean blocking) throws IOException {
            this.writeLock.lock();
            try {
                int chunkLimit = src.limit();
                while (src.remaining() > 0) {
                    int thisTime = Math.min(this.buffer.remaining(), src.remaining());
                    src.limit(src.position() + thisTime);
                    this.buffer.put(src);
                    src.limit(chunkLimit);
                    if (flush(false, blocking)) {
                        return true;
                    }
                }
                this.writeLock.unlock();
                return false;
            } finally {
                this.writeLock.unlock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream$StreamInputBuffer.class */
    public abstract class StreamInputBuffer implements InputBuffer {
        abstract void receiveReset();

        abstract void swallowUnread() throws IOException;

        abstract void notifyEof();

        abstract ByteBuffer getInBuffer();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract void onDataAvailable() throws IOException;

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract boolean isReadyForRead();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract boolean isRequestBodyFullyRead();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract void insertReplayedBody(ByteChunk byteChunk);

        StreamInputBuffer() {
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream$StandardStreamInputBuffer.class */
    class StandardStreamInputBuffer extends StreamInputBuffer {
        private final Lock readStateLock;
        private byte[] outBuffer;
        private volatile ByteBuffer inBuffer;
        private volatile boolean readInterest;
        private volatile boolean closed;
        private boolean resetReceived;

        StandardStreamInputBuffer() {
            super();
            this.readStateLock = new ReentrantLock();
        }

        @Override // org.apache.coyote.InputBuffer
        public final int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException {
            ensureBuffersExist();
            ByteBuffer tmpInBuffer = this.inBuffer;
            if (tmpInBuffer == null) {
                return -1;
            }
            synchronized (tmpInBuffer) {
                if (this.inBuffer == null) {
                    return -1;
                }
                boolean canRead = false;
                while (this.inBuffer.position() == 0) {
                    boolean z = Stream.this.isActive() && !Stream.this.isInputFinished();
                    canRead = z;
                    if (!z) {
                        break;
                    }
                    try {
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug(Stream.sm.getString("stream.inputBuffer.empty"));
                        }
                        long readTimeout = Stream.this.handler.getProtocol().getStreamReadTimeout();
                        if (readTimeout < 0) {
                            this.inBuffer.wait();
                        } else {
                            this.inBuffer.wait(readTimeout);
                        }
                        if (this.resetReceived) {
                            throw new IOException(Stream.sm.getString("stream.inputBuffer.reset"));
                        }
                        if (this.inBuffer.position() == 0 && Stream.this.isActive() && !Stream.this.isInputFinished()) {
                            String msg = Stream.sm.getString("stream.inputBuffer.readTimeout");
                            StreamException se = new StreamException(msg, Http2Error.ENHANCE_YOUR_CALM, Stream.this.getIdAsInt());
                            Stream.this.coyoteResponse.setError();
                            Stream.this.streamOutputBuffer.reset = se;
                            throw new CloseNowException(msg, se);
                        }
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    int written = this.inBuffer.remaining();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.copy", Integer.toString(written)));
                    }
                    this.inBuffer.get(this.outBuffer, 0, written);
                    this.inBuffer.clear();
                    applicationBufferHandler.setByteBuffer(ByteBuffer.wrap(this.outBuffer, 0, written));
                    Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
                    return written;
                }
                if (!canRead) {
                    return -1;
                }
                throw new IllegalStateException();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final boolean isReadyForRead() {
            ensureBuffersExist();
            this.readStateLock.lock();
            try {
                if (available() > 0) {
                    return true;
                }
                if (!isRequestBodyFullyRead()) {
                    this.readInterest = true;
                }
                return false;
            } finally {
                this.readStateLock.unlock();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final boolean isRequestBodyFullyRead() {
            boolean z;
            this.readStateLock.lock();
            try {
                if (this.inBuffer == null || this.inBuffer.position() == 0) {
                    if (Stream.this.isInputFinished()) {
                        z = true;
                        return z;
                    }
                }
                z = false;
                return z;
            } finally {
                this.readStateLock.unlock();
            }
        }

        @Override // org.apache.coyote.InputBuffer
        public final int available() {
            this.readStateLock.lock();
            try {
                if (this.inBuffer == null) {
                    return 0;
                }
                return this.inBuffer.position();
            } finally {
                this.readStateLock.unlock();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void onDataAvailable() throws IOException {
            this.readStateLock.lock();
            try {
                if (this.closed) {
                    swallowUnread();
                } else if (this.readInterest) {
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.dispatch"));
                    }
                    this.readInterest = false;
                    Stream.this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                    Stream.this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
                } else {
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug(Stream.sm.getString("stream.inputBuffer.signal"));
                    }
                    synchronized (this.inBuffer) {
                        this.inBuffer.notifyAll();
                    }
                }
            } finally {
                this.readStateLock.unlock();
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final ByteBuffer getInBuffer() {
            ensureBuffersExist();
            return this.inBuffer;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void insertReplayedBody(ByteChunk body) {
            this.readStateLock.lock();
            try {
                this.inBuffer = ByteBuffer.wrap(body.getBytes(), body.getOffset(), body.getLength());
            } finally {
                this.readStateLock.unlock();
            }
        }

        private void ensureBuffersExist() {
            if (this.inBuffer == null && !this.closed) {
                int size = Stream.this.handler.getLocalSettings().getInitialWindowSize();
                this.readStateLock.lock();
                try {
                    if (this.inBuffer == null && !this.closed) {
                        this.inBuffer = ByteBuffer.allocate(size);
                        this.outBuffer = new byte[size];
                    }
                } finally {
                    this.readStateLock.unlock();
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void receiveReset() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.resetReceived = true;
                    this.inBuffer.notifyAll();
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void notifyEof() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        final void swallowUnread() throws IOException {
            int unreadByteCount;
            this.readStateLock.lock();
            try {
                this.closed = true;
                if (this.inBuffer != null) {
                    synchronized (this.inBuffer) {
                        unreadByteCount = this.inBuffer.position();
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug(Stream.sm.getString("stream.inputBuffer.swallowUnread", Integer.valueOf(unreadByteCount)));
                        }
                        if (unreadByteCount > 0) {
                            this.inBuffer.position(0);
                            this.inBuffer.limit(this.inBuffer.limit() - unreadByteCount);
                        }
                    }
                    if (unreadByteCount > 0) {
                        Stream.this.handler.onSwallowedDataFramePayload(Stream.this.getIdAsInt(), unreadByteCount);
                    }
                }
            } finally {
                this.readStateLock.unlock();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Stream$SavedRequestStreamInputBuffer.class */
    class SavedRequestStreamInputBuffer extends StreamInputBuffer {
        private final SavedRequestInputFilter inputFilter;

        SavedRequestStreamInputBuffer(SavedRequestInputFilter inputFilter) {
            super();
            this.inputFilter = inputFilter;
        }

        @Override // org.apache.coyote.InputBuffer
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            return this.inputFilter.doRead(handler);
        }

        @Override // org.apache.coyote.InputBuffer
        public int available() {
            return this.inputFilter.available();
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void receiveReset() {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void swallowUnread() throws IOException {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void notifyEof() {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        ByteBuffer getInBuffer() {
            return null;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void onDataAvailable() throws IOException {
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        boolean isReadyForRead() {
            return true;
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        boolean isRequestBodyFullyRead() {
            return this.inputFilter.isFinished();
        }

        @Override // org.apache.coyote.http2.Stream.StreamInputBuffer
        void insertReplayedBody(ByteChunk body) {
        }
    }
}
