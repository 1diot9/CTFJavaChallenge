package org.apache.catalina.connector;

import jakarta.servlet.ReadListener;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.security.SecurityUtil;
import org.apache.coyote.ActionCode;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/InputBuffer.class */
public class InputBuffer extends Reader implements ByteChunk.ByteInputChannel, ApplicationBufferHandler {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public final int INITIAL_STATE = 0;
    public final int CHAR_STATE = 1;
    public final int BYTE_STATE = 2;
    private ByteBuffer bb;
    private CharBuffer cb;
    private int state;
    private boolean closed;
    protected B2CConverter conv;
    private org.apache.coyote.Request coyoteRequest;
    private int markPos;
    private int readLimit;
    private final int size;
    protected static final StringManager sm = StringManager.getManager((Class<?>) InputBuffer.class);
    private static final Log log = LogFactory.getLog((Class<?>) InputBuffer.class);
    private static final Map<Charset, SynchronizedStack<B2CConverter>> encoders = new ConcurrentHashMap();

    public InputBuffer() {
        this(8192);
    }

    public InputBuffer(int size) {
        this.INITIAL_STATE = 0;
        this.CHAR_STATE = 1;
        this.BYTE_STATE = 2;
        this.state = 0;
        this.closed = false;
        this.markPos = -1;
        this.size = size;
        this.bb = ByteBuffer.allocate(size);
        clear(this.bb);
        this.cb = CharBuffer.allocate(size);
        clear(this.cb);
        this.readLimit = size;
    }

    public void setRequest(org.apache.coyote.Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
    }

    public void recycle() {
        this.state = 0;
        if (this.cb.capacity() > this.size) {
            this.cb = CharBuffer.allocate(this.size);
            clear(this.cb);
        } else {
            clear(this.cb);
        }
        this.readLimit = this.size;
        this.markPos = -1;
        clear(this.bb);
        this.closed = false;
        if (this.conv != null) {
            this.conv.recycle();
            encoders.get(this.conv.getCharset()).push(this.conv);
            this.conv = null;
        }
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
    }

    public int available() {
        int available = availableInThisBuffer();
        if (available == 0) {
            this.coyoteRequest.action(ActionCode.AVAILABLE, Boolean.valueOf(this.coyoteRequest.getReadListener() != null));
            available = this.coyoteRequest.getAvailable() > 0 ? 1 : 0;
        }
        return available;
    }

    private int availableInThisBuffer() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        } else if (this.state == 1) {
            available = this.cb.remaining();
        }
        return available;
    }

    public void setReadListener(ReadListener listener) {
        this.coyoteRequest.setReadListener(listener);
    }

    public boolean isFinished() {
        int available = 0;
        if (this.state == 2) {
            available = this.bb.remaining();
        } else if (this.state == 1) {
            available = this.cb.remaining();
        }
        if (available > 0) {
            return false;
        }
        return this.coyoteRequest.isFinished();
    }

    public boolean isReady() {
        if (this.coyoteRequest.getReadListener() == null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("inputBuffer.requiresNonBlocking"));
                return false;
            }
            return false;
        }
        if (isFinished()) {
            if (!this.coyoteRequest.isRequestThread()) {
                this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
                return false;
            }
            return false;
        }
        if (availableInThisBuffer() > 0) {
            return true;
        }
        return this.coyoteRequest.isReady();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isBlocking() {
        return this.coyoteRequest.getReadListener() == null;
    }

    @Override // org.apache.tomcat.util.buf.ByteChunk.ByteInputChannel
    public int realReadBytes() throws IOException {
        if (this.closed) {
            return -1;
        }
        if (this.state == 0) {
            this.state = 2;
        }
        try {
            return this.coyoteRequest.doRead(this);
        } catch (BadRequestException bre) {
            handleReadException(bre);
            throw bre;
        } catch (IOException ioe) {
            handleReadException(ioe);
            throw new ClientAbortException(ioe);
        }
    }

    private void handleReadException(Exception e) throws IOException {
        this.coyoteRequest.setErrorException(e);
        Request request = (Request) this.coyoteRequest.getNote(1);
        Response response = request.getResponse();
        request.setAttribute("jakarta.servlet.error.exception", e);
        if (e instanceof SocketTimeoutException) {
            response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT);
        } else {
            response.sendError(400);
        }
    }

    public int readByte() throws IOException {
        throwIfClosed();
        if (checkByteBufferEof()) {
            return -1;
        }
        return this.bb.get() & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        throwIfClosed();
        if (checkByteBufferEof()) {
            return -1;
        }
        int n = Math.min(len, this.bb.remaining());
        this.bb.get(b, off, n);
        return n;
    }

    public int read(ByteBuffer to) throws IOException {
        throwIfClosed();
        if (checkByteBufferEof()) {
            return -1;
        }
        int n = Math.min(to.remaining(), this.bb.remaining());
        int orgLimit = this.bb.limit();
        this.bb.limit(this.bb.position() + n);
        to.put(this.bb);
        this.bb.limit(orgLimit);
        to.limit(to.position()).position(to.position() - n);
        return n;
    }

    public int realReadChars() throws IOException {
        checkConverter();
        boolean eof = false;
        if (this.bb.remaining() <= 0) {
            int nRead = realReadBytes();
            if (nRead < 0) {
                eof = true;
            }
        }
        if (this.markPos == -1) {
            clear(this.cb);
        } else {
            makeSpace(this.bb.remaining());
            if (this.cb.capacity() - this.cb.limit() == 0 && this.bb.remaining() != 0) {
                clear(this.cb);
                this.markPos = -1;
            }
        }
        this.state = 1;
        this.conv.convert(this.bb, this.cb, this, eof);
        if (this.cb.remaining() == 0 && eof) {
            return -1;
        }
        return this.cb.remaining();
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        throwIfClosed();
        if (checkCharBufferEof()) {
            return -1;
        }
        return this.cb.get();
    }

    @Override // java.io.Reader
    public int read(char[] cbuf) throws IOException {
        throwIfClosed();
        return read(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int off, int len) throws IOException {
        throwIfClosed();
        if (checkCharBufferEof()) {
            return -1;
        }
        int n = Math.min(len, this.cb.remaining());
        this.cb.get(cbuf, off, n);
        return n;
    }

    @Override // java.io.Reader
    public long skip(long n) throws IOException {
        throwIfClosed();
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        long nRead = 0;
        while (nRead < n) {
            if (this.cb.remaining() >= n) {
                this.cb.position(this.cb.position() + ((int) n));
                nRead = n;
            } else {
                nRead += this.cb.remaining();
                this.cb.position(this.cb.limit());
                int nb = realReadChars();
                if (nb < 0) {
                    break;
                }
            }
        }
        return nRead;
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        throwIfClosed();
        if (this.state == 0) {
            this.state = 1;
        }
        return available() > 0;
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.Reader
    public void mark(int readAheadLimit) throws IOException {
        throwIfClosed();
        if (this.cb.remaining() <= 0) {
            clear(this.cb);
        } else if (this.cb.capacity() > 2 * this.size && this.cb.remaining() < this.cb.position()) {
            this.cb.compact();
            this.cb.flip();
        }
        this.readLimit = this.cb.position() + readAheadLimit + this.size;
        this.markPos = this.cb.position();
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        throwIfClosed();
        if (this.state == 1) {
            if (this.markPos < 0) {
                clear(this.cb);
                this.markPos = -1;
                IOException ioe = new IOException();
                this.coyoteRequest.setErrorException(ioe);
                throw ioe;
            }
            this.cb.position(this.markPos);
            return;
        }
        clear(this.bb);
    }

    private void throwIfClosed() throws IOException {
        if (this.closed) {
            IOException ioe = new IOException(sm.getString("inputBuffer.streamClosed"));
            this.coyoteRequest.setErrorException(ioe);
            throw ioe;
        }
    }

    public void checkConverter() throws IOException {
        if (this.conv != null) {
            return;
        }
        Charset charset = this.coyoteRequest.getCharset();
        if (charset == null) {
            charset = Constants.DEFAULT_BODY_CHARSET;
        }
        SynchronizedStack<B2CConverter> stack = encoders.get(charset);
        if (stack == null) {
            encoders.putIfAbsent(charset, new SynchronizedStack<>());
            stack = encoders.get(charset);
        }
        this.conv = stack.pop();
        if (this.conv == null) {
            this.conv = createConverter(charset);
        }
    }

    private static B2CConverter createConverter(Charset charset) throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return (B2CConverter) AccessController.doPrivileged(new PrivilegedCreateConverter(charset));
            } catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new IOException(e);
            }
        }
        return new B2CConverter(charset);
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.bb = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.bb;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
    }

    private boolean checkByteBufferEof() throws IOException {
        if (this.bb.remaining() == 0) {
            int n = realReadBytes();
            if (n < 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean checkCharBufferEof() throws IOException {
        if (this.cb.remaining() == 0) {
            int n = realReadChars();
            if (n < 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void clear(Buffer buffer) {
        buffer.rewind().limit(0);
    }

    private void makeSpace(int count) {
        int desiredSize = this.cb.limit() + count;
        if (desiredSize > this.readLimit) {
            desiredSize = this.readLimit;
        }
        if (desiredSize <= this.cb.capacity()) {
            return;
        }
        int newSize = 2 * this.cb.capacity();
        if (desiredSize >= newSize) {
            newSize = (2 * this.cb.capacity()) + count;
        }
        if (newSize > this.readLimit) {
            newSize = this.readLimit;
        }
        CharBuffer tmp = CharBuffer.allocate(newSize);
        int oldPosition = this.cb.position();
        this.cb.position(0);
        tmp.put(this.cb);
        tmp.flip();
        tmp.position(oldPosition);
        this.cb = tmp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/InputBuffer$PrivilegedCreateConverter.class */
    public static class PrivilegedCreateConverter implements PrivilegedExceptionAction<B2CConverter> {
        private final Charset charset;

        PrivilegedCreateConverter(Charset charset) {
            this.charset = charset;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public B2CConverter run() throws IOException {
            return new B2CConverter(this.charset);
        }
    }
}
