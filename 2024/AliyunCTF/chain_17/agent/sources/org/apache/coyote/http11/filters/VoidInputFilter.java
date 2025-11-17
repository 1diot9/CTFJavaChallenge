package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/VoidInputFilter.class */
public class VoidInputFilter implements InputFilter {
    protected static final String ENCODING_NAME = "void";
    protected static final ByteChunk ENCODING = new ByteChunk();

    static {
        ENCODING.setBytes(ENCODING_NAME.getBytes(StandardCharsets.ISO_8859_1), 0, ENCODING_NAME.length());
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        return -1;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
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
        return 0;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return true;
    }
}
