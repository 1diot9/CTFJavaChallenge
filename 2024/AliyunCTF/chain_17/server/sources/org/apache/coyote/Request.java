package org.apache.coyote;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletConnection;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.parser.MediaType;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/Request.class */
public final class Request {
    private static final int INITIAL_COOKIE_SIZE = 4;
    private int remotePort;
    private int localPort;
    private Response response;
    private volatile ActionHook hook;
    volatile ReadListener listener;
    private static final StringManager sm = StringManager.getManager((Class<?>) Request.class);
    private static final AtomicLong requestIdGenerator = new AtomicLong(0);
    private int serverPort = -1;
    private final MessageBytes serverNameMB = MessageBytes.newInstance();
    private final MessageBytes schemeMB = MessageBytes.newInstance();
    private final MessageBytes methodMB = MessageBytes.newInstance();
    private final MessageBytes uriMB = MessageBytes.newInstance();
    private final MessageBytes decodedUriMB = MessageBytes.newInstance();
    private final MessageBytes queryMB = MessageBytes.newInstance();
    private final MessageBytes protoMB = MessageBytes.newInstance();
    private volatile String requestId = Long.toString(requestIdGenerator.getAndIncrement());
    private final MessageBytes remoteAddrMB = MessageBytes.newInstance();
    private final MessageBytes peerAddrMB = MessageBytes.newInstance();
    private final MessageBytes localNameMB = MessageBytes.newInstance();
    private final MessageBytes remoteHostMB = MessageBytes.newInstance();
    private final MessageBytes localAddrMB = MessageBytes.newInstance();
    private final MimeHeaders headers = new MimeHeaders();
    private final Map<String, String> trailerFields = new HashMap();
    private final Map<String, String> pathParameters = new HashMap();
    private final Object[] notes = new Object[32];
    private InputBuffer inputBuffer = null;
    private final UDecoder urlDecoder = new UDecoder();
    private long contentLength = -1;
    private MessageBytes contentTypeMB = null;
    private Charset charset = null;
    private String characterEncoding = null;
    private boolean expectation = false;
    private final ServerCookies serverCookies = new ServerCookies(4);
    private final Parameters parameters = new Parameters();
    private final MessageBytes remoteUser = MessageBytes.newInstance();
    private boolean remoteUserNeedsAuthorization = false;
    private final MessageBytes authType = MessageBytes.newInstance();
    private final HashMap<String, Object> attributes = new HashMap<>();
    private long bytesRead = 0;
    private long startTimeNanos = -1;
    private long threadId = 0;
    private int available = 0;
    private final RequestInfo reqProcessorMX = new RequestInfo(this);
    private boolean sendfile = true;
    private Exception errorException = null;
    private boolean fireListener = false;
    private boolean registeredForRead = false;
    private final Object nonBlockingStateLock = new Object();
    private final AtomicBoolean allDataReadEventSent = new AtomicBoolean(false);

    public Request() {
        this.parameters.setQuery(this.queryMB);
        this.parameters.setURLDecoder(this.urlDecoder);
    }

    public ReadListener getReadListener() {
        return this.listener;
    }

    public void setReadListener(ReadListener listener) {
        if (listener == null) {
            throw new NullPointerException(sm.getString("request.nullReadListener"));
        }
        if (getReadListener() != null) {
            throw new IllegalStateException(sm.getString("request.readListenerSet"));
        }
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.ASYNC_IS_ASYNC, result);
        if (!result.get()) {
            throw new IllegalStateException(sm.getString("request.notAsync"));
        }
        this.listener = listener;
        if (!isFinished() && isReady()) {
            synchronized (this.nonBlockingStateLock) {
                this.registeredForRead = true;
                this.fireListener = true;
            }
            action(ActionCode.DISPATCH_READ, null);
            if (!isRequestThread()) {
                action(ActionCode.DISPATCH_EXECUTE, null);
            }
        }
    }

    public boolean isReady() {
        synchronized (this.nonBlockingStateLock) {
            if (this.registeredForRead) {
                this.fireListener = true;
                return false;
            }
            boolean ready = checkRegisterForRead();
            this.fireListener = !ready;
            return ready;
        }
    }

    private boolean checkRegisterForRead() {
        AtomicBoolean ready = new AtomicBoolean(false);
        synchronized (this.nonBlockingStateLock) {
            if (!this.registeredForRead) {
                action(ActionCode.NB_READ_INTEREST, ready);
                this.registeredForRead = !ready.get();
            }
        }
        return ready.get();
    }

    public void onDataAvailable() throws IOException {
        boolean fire = false;
        synchronized (this.nonBlockingStateLock) {
            this.registeredForRead = false;
            if (this.fireListener) {
                this.fireListener = false;
                fire = true;
            }
        }
        if (fire) {
            this.listener.onDataAvailable();
        }
    }

    public boolean sendAllDataReadEvent() {
        return this.allDataReadEventSent.compareAndSet(false, true);
    }

    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    public boolean isTrailerFieldsReady() {
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.IS_TRAILER_FIELDS_READY, result);
        return result.get();
    }

    public Map<String, String> getTrailerFields() {
        return this.trailerFields;
    }

    public UDecoder getURLDecoder() {
        return this.urlDecoder;
    }

    public MessageBytes scheme() {
        return this.schemeMB;
    }

    public MessageBytes method() {
        return this.methodMB;
    }

    public MessageBytes requestURI() {
        return this.uriMB;
    }

    public MessageBytes decodedURI() {
        return this.decodedUriMB;
    }

    public MessageBytes queryString() {
        return this.queryMB;
    }

    public MessageBytes protocol() {
        return this.protoMB;
    }

    public MessageBytes serverName() {
        return this.serverNameMB;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public MessageBytes remoteAddr() {
        return this.remoteAddrMB;
    }

    public MessageBytes peerAddr() {
        return this.peerAddrMB;
    }

    public MessageBytes remoteHost() {
        return this.remoteHostMB;
    }

    public MessageBytes localName() {
        return this.localNameMB;
    }

    public MessageBytes localAddr() {
        return this.localAddrMB;
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public void setRemotePort(int port) {
        this.remotePort = port;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public String getCharacterEncoding() {
        if (this.characterEncoding == null) {
            this.characterEncoding = getCharsetFromContentType(getContentType());
        }
        return this.characterEncoding;
    }

    public Charset getCharset() throws UnsupportedEncodingException {
        if (this.charset == null) {
            getCharacterEncoding();
            if (this.characterEncoding != null) {
                this.charset = B2CConverter.getCharset(this.characterEncoding);
            }
        }
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
        this.characterEncoding = charset.name();
    }

    public void setContentLength(long len) {
        this.contentLength = len;
    }

    public int getContentLength() {
        long length = getContentLengthLong();
        if (length < 2147483647L) {
            return (int) length;
        }
        return -1;
    }

    public long getContentLengthLong() {
        if (this.contentLength > -1) {
            return this.contentLength;
        }
        MessageBytes clB = this.headers.getUniqueValue("content-length");
        this.contentLength = (clB == null || clB.isNull()) ? -1L : clB.getLong();
        return this.contentLength;
    }

    public String getContentType() {
        contentType();
        if (this.contentTypeMB == null || this.contentTypeMB.isNull()) {
            return null;
        }
        return this.contentTypeMB.toString();
    }

    public void setContentType(String type) {
        this.contentTypeMB.setString(type);
    }

    public MessageBytes contentType() {
        if (this.contentTypeMB == null) {
            this.contentTypeMB = this.headers.getValue("content-type");
        }
        return this.contentTypeMB;
    }

    public void setContentType(MessageBytes mb) {
        this.contentTypeMB = mb;
    }

    public String getHeader(String name) {
        return this.headers.getHeader(name);
    }

    public void setExpectation(boolean expectation) {
        this.expectation = expectation;
    }

    public boolean hasExpectation() {
        return this.expectation;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        this.response = response;
        response.setRequest(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHook(ActionHook hook) {
        this.hook = hook;
    }

    public void action(ActionCode actionCode, Object param) {
        if (this.hook != null) {
            if (param == null) {
                this.hook.action(actionCode, this);
            } else {
                this.hook.action(actionCode, param);
            }
        }
    }

    public ServerCookies getCookies() {
        return this.serverCookies;
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public void addPathParameter(String name, String value) {
        this.pathParameters.put(name, value);
    }

    public String getPathParameter(String name) {
        return this.pathParameters.get(name);
    }

    public void setAttribute(String name, Object o) {
        this.attributes.put(name, o);
    }

    public HashMap<String, Object> getAttributes() {
        return this.attributes;
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public MessageBytes getRemoteUser() {
        return this.remoteUser;
    }

    public boolean getRemoteUserNeedsAuthorization() {
        return this.remoteUserNeedsAuthorization;
    }

    public void setRemoteUserNeedsAuthorization(boolean remoteUserNeedsAuthorization) {
        this.remoteUserNeedsAuthorization = remoteUserNeedsAuthorization;
    }

    public MessageBytes getAuthType() {
        return this.authType;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public boolean getSendfile() {
        return this.sendfile;
    }

    public void setSendfile(boolean sendfile) {
        this.sendfile = sendfile;
    }

    public boolean isFinished() {
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.REQUEST_BODY_FULLY_READ, result);
        return result.get();
    }

    public boolean getSupportsRelativeRedirects() {
        if (protocol().equals("") || protocol().equals(org.apache.coyote.http11.Constants.HTTP_10)) {
            return false;
        }
        return true;
    }

    public InputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    public void setInputBuffer(InputBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
    }

    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (getBytesRead() == 0 && !this.response.isCommitted()) {
            action(ActionCode.ACK, ContinueResponseTiming.ON_REQUEST_BODY_READ);
        }
        int n = this.inputBuffer.doRead(handler);
        if (n > 0) {
            this.bytesRead += n;
        }
        return n;
    }

    public void setErrorException(Exception ex) {
        this.errorException = ex;
    }

    public Exception getErrorException() {
        return this.errorException;
    }

    public boolean isExceptionPresent() {
        return this.errorException != null;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getProtocolRequestId() {
        AtomicReference<String> ref = new AtomicReference<>();
        this.hook.action(ActionCode.PROTOCOL_REQUEST_ID, ref);
        return ref.get();
    }

    public ServletConnection getServletConnection() {
        AtomicReference<ServletConnection> ref = new AtomicReference<>();
        this.hook.action(ActionCode.SERVLET_CONNECTION, ref);
        return ref.get();
    }

    public String toString() {
        return "R( " + requestURI().toString() + ")";
    }

    public long getStartTime() {
        return System.currentTimeMillis() - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.startTimeNanos);
    }

    @Deprecated
    public void setStartTime(long startTime) {
    }

    public long getStartTimeNanos() {
        return this.startTimeNanos;
    }

    public void setStartTimeNanos(long startTimeNanos) {
        this.startTimeNanos = startTimeNanos;
    }

    public long getThreadId() {
        return this.threadId;
    }

    public void clearRequestThread() {
        this.threadId = 0L;
    }

    public void setRequestThread() {
        Thread t = Thread.currentThread();
        this.threadId = t.getId();
        getRequestProcessor().setWorkerThreadName(t.getName());
    }

    public boolean isRequestThread() {
        return Thread.currentThread().getId() == this.threadId;
    }

    public void setNote(int pos, Object value) {
        this.notes[pos] = value;
    }

    public Object getNote(int pos) {
        return this.notes[pos];
    }

    public void recycle() {
        this.bytesRead = 0L;
        this.contentLength = -1L;
        this.contentTypeMB = null;
        this.charset = null;
        this.characterEncoding = null;
        this.expectation = false;
        this.headers.recycle();
        this.trailerFields.clear();
        this.serverNameMB.recycle();
        this.serverPort = -1;
        this.localAddrMB.recycle();
        this.localNameMB.recycle();
        this.localPort = -1;
        this.peerAddrMB.recycle();
        this.remoteAddrMB.recycle();
        this.remoteHostMB.recycle();
        this.remotePort = -1;
        this.available = 0;
        this.sendfile = true;
        if (this.startTimeNanos != -1) {
            this.requestId = Long.toHexString(requestIdGenerator.getAndIncrement());
        }
        this.serverCookies.recycle();
        this.parameters.recycle();
        this.pathParameters.clear();
        this.uriMB.recycle();
        this.decodedUriMB.recycle();
        this.queryMB.recycle();
        this.methodMB.recycle();
        this.protoMB.recycle();
        this.schemeMB.recycle();
        this.remoteUser.recycle();
        this.remoteUserNeedsAuthorization = false;
        this.authType.recycle();
        this.attributes.clear();
        this.errorException = null;
        this.listener = null;
        synchronized (this.nonBlockingStateLock) {
            this.fireListener = false;
            this.registeredForRead = false;
        }
        this.allDataReadEventSent.set(false);
        this.startTimeNanos = -1L;
        this.threadId = 0L;
    }

    public void updateCounters() {
        this.reqProcessorMX.updateCounters();
    }

    public RequestInfo getRequestProcessor() {
        return this.reqProcessorMX;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }

    public boolean isProcessing() {
        return this.reqProcessorMX.getStage() == 3;
    }

    private static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        MediaType mediaType = null;
        try {
            mediaType = MediaType.parseMediaType(new StringReader(contentType));
        } catch (IOException e) {
        }
        if (mediaType != null) {
            return mediaType.getCharset();
        }
        return null;
    }
}
