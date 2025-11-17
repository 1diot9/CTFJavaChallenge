package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/IdentityInputFilter.class */
public class IdentityInputFilter implements InputFilter, ApplicationBufferHandler {
    protected static final String ENCODING_NAME = "identity";
    protected long contentLength = -1;
    protected long remaining = 0;
    protected InputBuffer buffer;
    protected ByteBuffer tempRead;
    private final int maxSwallowSize;
    private static final StringManager sm = StringManager.getManager((Class<?>) IdentityInputFilter.class);
    protected static final ByteChunk ENCODING = new ByteChunk();

    static {
        ENCODING.setBytes("identity".getBytes(StandardCharsets.ISO_8859_1), 0, "identity".length());
    }

    public IdentityInputFilter(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        int result = -1;
        if (this.contentLength >= 0) {
            if (this.remaining > 0) {
                int nRead = this.buffer.doRead(handler);
                if (nRead > this.remaining) {
                    handler.getByteBuffer().limit(handler.getByteBuffer().position() + ((int) this.remaining));
                    result = (int) this.remaining;
                } else {
                    result = nRead;
                }
                if (nRead > 0) {
                    this.remaining -= nRead;
                }
            } else {
                if (handler.getByteBuffer() != null) {
                    handler.getByteBuffer().position(0).limit(0);
                }
                result = -1;
            }
        }
        return result;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
        this.contentLength = request.getContentLengthLong();
        this.remaining = this.contentLength;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public long end() throws IOException {
        boolean maxSwallowSizeExceeded = this.maxSwallowSize > -1 && this.remaining > ((long) this.maxSwallowSize);
        long swallowed = 0;
        while (this.remaining > 0) {
            int nread = this.buffer.doRead(this);
            this.tempRead = null;
            if (nread > 0) {
                swallowed += nread;
                this.remaining -= nread;
                if (maxSwallowSizeExceeded && swallowed > this.maxSwallowSize) {
                    throw new IOException(sm.getString("inputFilter.maxSwallow"));
                }
            } else {
                this.remaining = 0L;
            }
        }
        return -this.remaining;
    }

    @Override // org.apache.coyote.InputBuffer
    public int available() {
        return this.buffer.available();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
        this.contentLength = -1L;
        this.remaining = 0L;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return this.contentLength > -1 && this.remaining <= 0;
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
