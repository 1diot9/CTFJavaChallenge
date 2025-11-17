package org.apache.catalina.connector;

import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Globals;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Constants;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.C2BConverter;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/OutputBuffer.class */
public class OutputBuffer extends Writer {
    private static final StringManager sm = StringManager.getManager((Class<?>) OutputBuffer.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private final int defaultBufferSize;
    private ByteBuffer bb;
    private final CharBuffer cb;
    protected C2BConverter conv;
    private org.apache.coyote.Response coyoteResponse;
    private final Map<Charset, C2BConverter> encoders = new HashMap();
    private boolean initial = true;
    private long bytesWritten = 0;
    private long charsWritten = 0;
    private volatile boolean closed = false;
    private boolean doFlush = false;
    private volatile boolean suspended = false;

    public OutputBuffer(int size) {
        this.defaultBufferSize = size;
        this.bb = ByteBuffer.allocate(size);
        clear(this.bb);
        this.cb = CharBuffer.allocate(size);
        clear(this.cb);
    }

    public void setResponse(org.apache.coyote.Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
    }

    public boolean isSuspended() {
        return this.suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void recycle() {
        this.initial = true;
        this.bytesWritten = 0L;
        this.charsWritten = 0L;
        if (this.bb.capacity() > 16 * this.defaultBufferSize) {
            this.bb = ByteBuffer.allocate(this.defaultBufferSize);
        }
        clear(this.bb);
        clear(this.cb);
        this.closed = false;
        this.suspended = false;
        this.doFlush = false;
        if (this.conv != null) {
            this.conv.recycle();
            this.conv = null;
        }
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed || this.suspended) {
            return;
        }
        if (this.cb.remaining() > 0) {
            flushCharBuffer();
        }
        if (!this.coyoteResponse.isCommitted() && this.coyoteResponse.getContentLengthLong() == -1 && !this.coyoteResponse.isCommitted()) {
            this.coyoteResponse.setContentLength(this.bb.remaining());
        }
        if (this.coyoteResponse.getStatus() == 101) {
            doFlush(true);
        } else {
            doFlush(false);
        }
        this.closed = true;
        Request req = (Request) this.coyoteResponse.getRequest().getNote(1);
        req.inputBuffer.close();
        this.coyoteResponse.action(ActionCode.CLOSE, null);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        doFlush(true);
    }

    protected void doFlush(boolean realFlush) throws IOException {
        if (this.suspended) {
            return;
        }
        try {
            this.doFlush = true;
            if (this.initial) {
                this.coyoteResponse.sendHeaders();
                this.initial = false;
            }
            if (this.cb.remaining() > 0) {
                flushCharBuffer();
            }
            if (this.bb.remaining() > 0) {
                flushByteBuffer();
            }
            if (realFlush) {
                this.coyoteResponse.action(ActionCode.CLIENT_FLUSH, null);
                if (this.coyoteResponse.isExceptionPresent()) {
                    throw new ClientAbortException(this.coyoteResponse.getErrorException());
                }
            }
        } finally {
            this.doFlush = false;
        }
    }

    public void realWriteBytes(ByteBuffer buf) throws IOException {
        if (!this.closed && buf.remaining() > 0) {
            try {
                this.coyoteResponse.doWrite(buf);
            } catch (CloseNowException e) {
                this.closed = true;
                throw e;
            } catch (IOException e2) {
                this.coyoteResponse.setErrorException(e2);
                throw new ClientAbortException(e2);
            }
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        writeBytes(b, off, len);
    }

    public void write(ByteBuffer from) throws IOException {
        if (this.suspended) {
            return;
        }
        writeBytes(from);
    }

    private void writeBytes(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            return;
        }
        append(b, off, len);
        this.bytesWritten += len;
        if (this.doFlush) {
            flushByteBuffer();
        }
    }

    private void writeBytes(ByteBuffer from) throws IOException {
        if (this.closed) {
            return;
        }
        int remaining = from.remaining();
        append(from);
        this.bytesWritten += remaining;
        if (this.doFlush) {
            flushByteBuffer();
        }
    }

    public void writeByte(int b) throws IOException {
        if (this.suspended) {
            return;
        }
        if (isFull(this.bb)) {
            flushByteBuffer();
        }
        transfer((byte) b, this.bb);
        this.bytesWritten++;
    }

    public void realWriteChars(CharBuffer from) throws IOException {
        while (from.remaining() > 0) {
            this.conv.convert(from, this.bb);
            if (this.bb.remaining() != 0) {
                if (from.remaining() > 0) {
                    flushByteBuffer();
                } else if (this.conv.isUndeflow() && this.bb.limit() > this.bb.capacity() - 4) {
                    flushByteBuffer();
                }
            } else {
                return;
            }
        }
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        if (this.suspended) {
            return;
        }
        if (isFull(this.cb)) {
            flushCharBuffer();
        }
        transfer((char) c, this.cb);
        this.charsWritten++;
    }

    @Override // java.io.Writer
    public void write(char[] c) throws IOException {
        if (this.suspended) {
            return;
        }
        write(c, 0, c.length);
    }

    @Override // java.io.Writer
    public void write(char[] c, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        append(c, off, len);
        this.charsWritten += len;
    }

    @Override // java.io.Writer
    public void write(String s, int off, int len) throws IOException {
        if (this.suspended) {
            return;
        }
        if (s == null) {
            throw new NullPointerException(sm.getString("outputBuffer.writeNull"));
        }
        int sOff = off;
        int sEnd = off + len;
        while (sOff < sEnd) {
            int n = transfer(s, sOff, sEnd - sOff, this.cb);
            sOff += n;
            if (sOff < sEnd && isFull(this.cb)) {
                flushCharBuffer();
            }
        }
        this.charsWritten += len;
    }

    @Override // java.io.Writer
    public void write(String s) throws IOException {
        if (this.suspended) {
            return;
        }
        if (s == null) {
            s = "null";
        }
        write(s, 0, s.length());
    }

    public void checkConverter() throws IOException {
        if (this.conv != null) {
            return;
        }
        Charset charset = this.coyoteResponse.getCharset();
        if (charset == null) {
            if (this.coyoteResponse.getCharacterEncoding() != null) {
                B2CConverter.getCharset(this.coyoteResponse.getCharacterEncoding());
            }
            charset = Constants.DEFAULT_BODY_CHARSET;
        }
        this.conv = this.encoders.get(charset);
        if (this.conv == null) {
            this.conv = createConverter(charset);
            this.encoders.put(charset, this.conv);
        }
    }

    private static C2BConverter createConverter(Charset charset) throws IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                return (C2BConverter) AccessController.doPrivileged(new PrivilegedCreateConverter(charset));
            } catch (PrivilegedActionException ex) {
                Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                throw new IOException(ex);
            }
        }
        return new C2BConverter(charset);
    }

    public long getContentWritten() {
        return this.bytesWritten + this.charsWritten;
    }

    public boolean isNew() {
        return this.bytesWritten == 0 && this.charsWritten == 0;
    }

    public void setBufferSize(int size) {
        if (size > this.bb.capacity()) {
            this.bb = ByteBuffer.allocate(size);
            clear(this.bb);
        }
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean resetWriterStreamFlags) {
        clear(this.bb);
        clear(this.cb);
        this.bytesWritten = 0L;
        this.charsWritten = 0L;
        if (resetWriterStreamFlags) {
            if (this.conv != null) {
                this.conv.recycle();
            }
            this.conv = null;
        }
        this.initial = true;
    }

    public int getBufferSize() {
        return this.bb.capacity();
    }

    public boolean isReady() {
        return this.coyoteResponse.isReady();
    }

    public void setWriteListener(WriteListener listener) {
        this.coyoteResponse.setWriteListener(listener);
    }

    public boolean isBlocking() {
        return this.coyoteResponse.getWriteListener() == null;
    }

    public void checkRegisterForWrite() {
        this.coyoteResponse.checkRegisterForWrite();
    }

    public void append(byte[] src, int off, int len) throws IOException {
        if (this.bb.remaining() == 0) {
            appendByteArray(src, off, len);
            return;
        }
        int n = transfer(src, off, len, this.bb);
        int len2 = len - n;
        int off2 = off + n;
        if (len2 > 0 && isFull(this.bb)) {
            flushByteBuffer();
            appendByteArray(src, off2, len2);
        }
    }

    public void append(char[] src, int off, int len) throws IOException {
        if (len <= this.cb.capacity() - this.cb.limit()) {
            transfer(src, off, len, this.cb);
            return;
        }
        if (len + this.cb.limit() < 2 * this.cb.capacity()) {
            int n = transfer(src, off, len, this.cb);
            flushCharBuffer();
            transfer(src, off + n, len - n, this.cb);
        } else {
            flushCharBuffer();
            realWriteChars(CharBuffer.wrap(src, off, len));
        }
    }

    public void append(ByteBuffer from) throws IOException {
        if (this.bb.remaining() == 0) {
            appendByteBuffer(from);
            return;
        }
        transfer(from, this.bb);
        if (from.hasRemaining() && isFull(this.bb)) {
            flushByteBuffer();
            appendByteBuffer(from);
        }
    }

    private void appendByteArray(byte[] src, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        int limit = this.bb.capacity();
        while (len > limit) {
            realWriteBytes(ByteBuffer.wrap(src, off, limit));
            len -= limit;
            off += limit;
        }
        if (len > 0) {
            transfer(src, off, len, this.bb);
        }
    }

    private void appendByteBuffer(ByteBuffer from) throws IOException {
        if (from.remaining() == 0) {
            return;
        }
        int limit = this.bb.capacity();
        int fromLimit = from.limit();
        while (from.remaining() > limit) {
            from.limit(from.position() + limit);
            realWriteBytes(from.slice());
            from.position(from.limit());
            from.limit(fromLimit);
        }
        if (from.remaining() > 0) {
            transfer(from, this.bb);
        }
    }

    private void flushByteBuffer() throws IOException {
        realWriteBytes(this.bb.slice());
        clear(this.bb);
    }

    private void flushCharBuffer() throws IOException {
        realWriteChars(this.cb.slice());
        clear(this.cb);
    }

    private void transfer(byte b, ByteBuffer to) {
        toWriteMode(to);
        to.put(b);
        toReadMode(to);
    }

    private void transfer(char b, CharBuffer to) {
        toWriteMode(to);
        to.put(b);
        toReadMode(to);
    }

    private int transfer(byte[] buf, int off, int len, ByteBuffer to) {
        toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(buf, off, max);
        }
        toReadMode(to);
        return max;
    }

    private int transfer(char[] buf, int off, int len, CharBuffer to) {
        toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(buf, off, max);
        }
        toReadMode(to);
        return max;
    }

    private int transfer(String s, int off, int len, CharBuffer to) {
        toWriteMode(to);
        int max = Math.min(len, to.remaining());
        if (max > 0) {
            to.put(s, off, off + max);
        }
        toReadMode(to);
        return max;
    }

    private void transfer(ByteBuffer from, ByteBuffer to) {
        toWriteMode(to);
        int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        toReadMode(to);
    }

    private void clear(Buffer buffer) {
        buffer.rewind().limit(0);
    }

    private boolean isFull(Buffer buffer) {
        return buffer.limit() == buffer.capacity();
    }

    private void toReadMode(Buffer buffer) {
        buffer.limit(buffer.position()).reset();
    }

    private void toWriteMode(Buffer buffer) {
        buffer.mark().position(buffer.limit()).limit(buffer.capacity());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/OutputBuffer$PrivilegedCreateConverter.class */
    public static class PrivilegedCreateConverter implements PrivilegedExceptionAction<C2BConverter> {
        private final Charset charset;

        PrivilegedCreateConverter(Charset charset) {
            this.charset = charset;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedExceptionAction
        public C2BConverter run() throws IOException {
            return new C2BConverter(this.charset);
        }
    }
}
