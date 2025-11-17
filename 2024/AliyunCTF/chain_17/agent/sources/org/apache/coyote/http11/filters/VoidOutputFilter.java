package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/VoidOutputFilter.class */
public class VoidOutputFilter implements OutputFilter {
    private HttpOutputBuffer buffer = null;

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        return chunk.remaining();
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        return 0L;
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setResponse(Response response) {
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setBuffer(HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void flush() throws IOException {
        this.buffer.flush();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void recycle() {
        this.buffer = null;
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void end() throws IOException {
        this.buffer.end();
    }
}
