package org.apache.coyote.http2;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http2.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/Http2OutputBuffer.class */
public class Http2OutputBuffer implements HttpOutputBuffer {
    private final Response coyoteResponse;
    private HttpOutputBuffer next;

    public void addFilter(OutputFilter filter) {
        filter.setBuffer(this.next);
        this.next = filter;
    }

    public Http2OutputBuffer(Response coyoteResponse, Stream.StreamOutputBuffer streamOutputBuffer) {
        this.coyoteResponse = coyoteResponse;
        this.next = streamOutputBuffer;
    }

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (!this.coyoteResponse.isCommitted()) {
            this.coyoteResponse.sendHeaders();
        }
        return this.next.doWrite(chunk);
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        return this.next.getBytesWritten();
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void end() throws IOException {
        this.next.end();
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void flush() throws IOException {
        this.next.flush();
    }
}
