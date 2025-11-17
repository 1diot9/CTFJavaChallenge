package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/IdentityOutputFilter.class */
public class IdentityOutputFilter implements OutputFilter {
    protected long contentLength = -1;
    protected long remaining = 0;
    protected HttpOutputBuffer buffer;

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        int result;
        if (this.contentLength < 0) {
            int result2 = chunk.remaining();
            this.buffer.doWrite(chunk);
            result = result2 - chunk.remaining();
        } else if (this.remaining > 0) {
            result = chunk.remaining();
            if (result > this.remaining) {
                chunk.limit(chunk.position() + ((int) this.remaining));
                result = (int) this.remaining;
                this.remaining = 0L;
            } else {
                this.remaining -= result;
            }
            this.buffer.doWrite(chunk);
        } else {
            chunk.position(0);
            chunk.limit(0);
            result = -1;
        }
        return result;
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setResponse(Response response) {
        this.contentLength = response.getContentLengthLong();
        this.remaining = this.contentLength;
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setBuffer(HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void flush() throws IOException {
        this.buffer.flush();
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void end() throws IOException {
        this.buffer.end();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void recycle() {
        this.contentLength = -1L;
        this.remaining = 0L;
    }
}
