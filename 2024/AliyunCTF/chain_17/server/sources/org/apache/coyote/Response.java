package org.apache.coyote;

import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.MediaType;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/Response.class */
public final class Response {
    private static final StringManager sm = StringManager.getManager((Class<?>) Response.class);
    private static final Log log = LogFactory.getLog((Class<?>) Response.class);
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    OutputBuffer outputBuffer;
    volatile ActionHook hook;
    Request req;
    volatile WriteListener listener;
    int status = 200;
    String message = null;
    final MimeHeaders headers = new MimeHeaders();
    private Supplier<Map<String, String>> trailerFieldsSupplier = null;
    final Object[] notes = new Object[32];
    volatile boolean committed = false;
    String contentType = null;
    String contentLanguage = null;
    Charset charset = null;
    String characterEncoding = null;
    long contentLength = -1;
    private Locale locale = DEFAULT_LOCALE;
    private long contentWritten = 0;
    private long commitTimeNanos = -1;
    private Exception errorException = null;
    private final AtomicInteger errorState = new AtomicInteger(0);
    private boolean fireListener = false;
    private boolean registeredForWrite = false;
    private final Object nonBlockingStateLock = new Object();

    public Request getRequest() {
        return this.req;
    }

    public void setRequest(Request req) {
        this.req = req;
    }

    public void setOutputBuffer(OutputBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHook(ActionHook hook) {
        this.hook = hook;
    }

    public void setNote(int pos, Object value) {
        this.notes[pos] = value;
    }

    public Object getNote(int pos) {
        return this.notes[pos];
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

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCommitted() {
        return this.committed;
    }

    public void setCommitted(boolean v) {
        if (v && !this.committed) {
            this.commitTimeNanos = System.nanoTime();
        }
        this.committed = v;
    }

    public long getCommitTime() {
        return System.currentTimeMillis() - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.commitTimeNanos);
    }

    public long getCommitTimeNanos() {
        return this.commitTimeNanos;
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

    @Deprecated
    public boolean setError() {
        return this.errorState.compareAndSet(0, 1);
    }

    public boolean isError() {
        return this.errorState.get() > 0;
    }

    public boolean isErrorReportRequired() {
        return this.errorState.get() == 1;
    }

    public boolean setErrorReported() {
        return this.errorState.compareAndSet(1, 2);
    }

    public void reset() throws IllegalStateException {
        if (this.committed) {
            throw new IllegalStateException();
        }
        recycle();
    }

    public boolean containsHeader(String name) {
        return this.headers.getHeader(name) != null;
    }

    public void setHeader(String name, String value) {
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && checkSpecialHeader(name, value)) {
            return;
        }
        this.headers.setValue(name).setString(value);
    }

    public void addHeader(String name, String value) {
        addHeader(name, value, null);
    }

    public void addHeader(String name, String value, Charset charset) {
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && checkSpecialHeader(name, value)) {
            return;
        }
        MessageBytes mb = this.headers.addValue(name);
        if (charset != null) {
            mb.setCharset(charset);
        }
        mb.setString(value);
    }

    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        AtomicBoolean trailerFieldsSupported = new AtomicBoolean(false);
        action(ActionCode.IS_TRAILER_FIELDS_SUPPORTED, trailerFieldsSupported);
        if (!trailerFieldsSupported.get()) {
            throw new IllegalStateException(sm.getString("response.noTrailers.notSupported"));
        }
        this.trailerFieldsSupplier = supplier;
    }

    public Supplier<Map<String, String>> getTrailerFields() {
        return this.trailerFieldsSupplier;
    }

    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
            setContentType(value);
            return true;
        }
        if (name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
            try {
                long cL = Long.parseLong(value);
                setContentLength(cL);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public void sendHeaders() {
        action(ActionCode.COMMIT, this);
        setCommitted(true);
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            this.locale = null;
            this.contentLanguage = null;
        } else {
            this.locale = locale;
            this.contentLanguage = locale.toLanguageTag();
        }
    }

    public String getContentLanguage() {
        return this.contentLanguage;
    }

    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
        if (isCommitted()) {
            return;
        }
        if (characterEncoding == null) {
            this.charset = null;
            this.characterEncoding = null;
        } else {
            this.characterEncoding = characterEncoding;
            this.charset = B2CConverter.getCharset(characterEncoding);
        }
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setContentType(String type) {
        if (type == null) {
            this.contentType = null;
            return;
        }
        MediaType m = null;
        try {
            m = MediaType.parseMediaType(new StringReader(type));
        } catch (IOException e) {
        }
        if (m == null) {
            this.contentType = type;
            return;
        }
        this.contentType = m.toStringNoCharset();
        String charsetValue = m.getCharset();
        if (charsetValue == null) {
            this.contentType = type;
            return;
        }
        this.contentType = m.toStringNoCharset();
        String charsetValue2 = charsetValue.trim();
        if (charsetValue2.length() > 0) {
            try {
                this.charset = B2CConverter.getCharset(charsetValue2);
            } catch (UnsupportedEncodingException e2) {
                log.warn(sm.getString("response.encoding.invalid", charsetValue2), e2);
            }
        }
    }

    public void setContentTypeNoCharset(String type) {
        this.contentType = type;
    }

    public String getContentType() {
        String ret = this.contentType;
        if (ret != null && this.charset != null) {
            ret = ret + ";charset=" + this.characterEncoding;
        }
        return ret;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public int getContentLength() {
        long length = getContentLengthLong();
        if (length < 2147483647L) {
            return (int) length;
        }
        return -1;
    }

    public long getContentLengthLong() {
        return this.contentLength;
    }

    public void doWrite(ByteBuffer chunk) throws IOException {
        int len = chunk.remaining();
        this.outputBuffer.doWrite(chunk);
        this.contentWritten += len - chunk.remaining();
    }

    public void recycle() {
        this.contentType = null;
        this.contentLanguage = null;
        this.locale = DEFAULT_LOCALE;
        this.charset = null;
        this.characterEncoding = null;
        this.contentLength = -1L;
        this.status = 200;
        this.message = null;
        this.committed = false;
        this.commitTimeNanos = -1L;
        this.errorException = null;
        this.errorState.set(0);
        this.headers.recycle();
        this.trailerFieldsSupplier = null;
        this.listener = null;
        synchronized (this.nonBlockingStateLock) {
            this.fireListener = false;
            this.registeredForWrite = false;
        }
        this.contentWritten = 0L;
    }

    public long getContentWritten() {
        return this.contentWritten;
    }

    public long getBytesWritten(boolean flush) {
        if (flush) {
            action(ActionCode.CLIENT_FLUSH, this);
        }
        return this.outputBuffer.getBytesWritten();
    }

    public WriteListener getWriteListener() {
        return this.listener;
    }

    public void setWriteListener(WriteListener listener) {
        if (listener == null) {
            throw new NullPointerException(sm.getString("response.nullWriteListener"));
        }
        if (getWriteListener() != null) {
            throw new IllegalStateException(sm.getString("response.writeListenerSet"));
        }
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.ASYNC_IS_ASYNC, result);
        if (!result.get()) {
            throw new IllegalStateException(sm.getString("response.notAsync"));
        }
        this.listener = listener;
        if (isReady()) {
            synchronized (this.nonBlockingStateLock) {
                this.registeredForWrite = true;
                this.fireListener = true;
            }
            action(ActionCode.DISPATCH_WRITE, null);
            if (!this.req.isRequestThread()) {
                action(ActionCode.DISPATCH_EXECUTE, null);
            }
        }
    }

    public boolean isReady() {
        if (this.listener == null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("response.notNonBlocking"));
                return false;
            }
            return false;
        }
        synchronized (this.nonBlockingStateLock) {
            if (this.registeredForWrite) {
                this.fireListener = true;
                return false;
            }
            boolean ready = checkRegisterForWrite();
            this.fireListener = !ready;
            return ready;
        }
    }

    public boolean checkRegisterForWrite() {
        AtomicBoolean ready = new AtomicBoolean(false);
        synchronized (this.nonBlockingStateLock) {
            if (!this.registeredForWrite) {
                action(ActionCode.NB_WRITE_INTEREST, ready);
                this.registeredForWrite = !ready.get();
            }
        }
        return ready.get();
    }

    public void onWritePossible() throws IOException {
        boolean fire = false;
        synchronized (this.nonBlockingStateLock) {
            this.registeredForWrite = false;
            if (this.fireListener) {
                this.fireListener = false;
                fire = true;
            }
        }
        if (fire) {
            this.listener.onWritePossible();
        }
    }
}
