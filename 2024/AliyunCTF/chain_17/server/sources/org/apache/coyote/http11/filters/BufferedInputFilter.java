package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/BufferedInputFilter.class */
public class BufferedInputFilter implements InputFilter, ApplicationBufferHandler {
    private static final String ENCODING_NAME = "buffered";
    private ByteChunk buffered;
    private ByteBuffer tempRead;
    private InputBuffer buffer;
    private boolean hasRead = false;
    private final int maxSwallowSize;
    private static final StringManager sm = StringManager.getManager((Class<?>) BufferedInputFilter.class);
    private static final ByteChunk ENCODING = new ByteChunk();

    static {
        ENCODING.setBytes(ENCODING_NAME.getBytes(StandardCharsets.ISO_8859_1), 0, ENCODING_NAME.length());
    }

    public BufferedInputFilter(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public void setLimit(int limit) {
        if (this.buffered == null) {
            this.buffered = new ByteChunk();
            this.buffered.setLimit(limit);
        }
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
        try {
            if (this.buffered.getLimit() == 0) {
                long swallowed = 0;
                while (true) {
                    int read = this.buffer.doRead(this);
                    if (read < 0) {
                        break;
                    }
                    swallowed += read;
                    if (this.maxSwallowSize > -1 && swallowed > this.maxSwallowSize) {
                        throw new IOException(sm.getString("bufferedInputFilter.maxSwallowSize"));
                    }
                }
            } else {
                while (this.buffer.doRead(this) >= 0) {
                    this.buffered.append(this.tempRead);
                    this.tempRead = null;
                }
            }
        } catch (IOException | BufferOverflowException ioe) {
            throw new IllegalStateException(sm.getString("bufferedInputFilter.bodySize", ioe.getMessage()));
        }
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (isFinished()) {
            return -1;
        }
        handler.setByteBuffer(ByteBuffer.wrap(this.buffered.getBuffer(), this.buffered.getStart(), this.buffered.getLength()));
        this.hasRead = true;
        return this.buffered.getLength();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
        if (this.buffered != null) {
            if (this.buffered.getBuffer() != null && this.buffered.getBuffer().length > 65536) {
                this.buffered = null;
            } else {
                this.buffered.recycle();
            }
        }
        this.hasRead = false;
        this.buffer = null;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public long end() throws IOException {
        return 0L;
    }

    @Override // org.apache.coyote.InputBuffer
    public int available() {
        int available = this.buffered.getLength();
        if (available == 0) {
            return this.buffer.available();
        }
        return available;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return this.hasRead || this.buffered.getLength() <= 0;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.tempRead = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.tempRead;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
    }
}
