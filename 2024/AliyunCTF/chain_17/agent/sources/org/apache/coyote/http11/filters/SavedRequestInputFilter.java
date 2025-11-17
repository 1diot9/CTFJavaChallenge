package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/SavedRequestInputFilter.class */
public class SavedRequestInputFilter implements InputFilter {
    protected ByteChunk input;

    public SavedRequestInputFilter(ByteChunk input) {
        this.input = null;
        this.input = input;
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.input.getOffset() >= this.input.getEnd()) {
            return -1;
        }
        ByteBuffer byteBuffer = handler.getByteBuffer();
        byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());
        this.input.subtract(byteBuffer);
        return byteBuffer.remaining();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
        request.setContentLength(this.input.getLength());
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
        this.input = null;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public ByteChunk getEncodingName() {
        return null;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
    }

    @Override // org.apache.coyote.InputBuffer
    public int available() {
        return this.input.getLength();
    }

    @Override // org.apache.coyote.http11.InputFilter
    public long end() throws IOException {
        return 0L;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return this.input.getOffset() >= this.input.getEnd();
    }
}
