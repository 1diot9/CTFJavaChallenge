package org.apache.coyote.http11.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/GzipOutputFilter.class */
public class GzipOutputFilter implements OutputFilter {
    protected static final Log log = LogFactory.getLog((Class<?>) GzipOutputFilter.class);
    protected HttpOutputBuffer buffer;
    protected GZIPOutputStream compressionStream = null;
    protected final OutputStream fakeOutputStream = new FakeOutputStream();

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        int len = chunk.remaining();
        if (chunk.hasArray()) {
            this.compressionStream.write(chunk.array(), chunk.arrayOffset() + chunk.position(), len);
            chunk.position(chunk.position() + len);
        } else {
            byte[] bytes = new byte[len];
            chunk.get(bytes);
            this.compressionStream.write(bytes, 0, len);
        }
        return len;
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void flush() throws IOException {
        if (this.compressionStream != null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Flushing the compression stream!");
                }
                this.compressionStream.flush();
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Ignored exception while flushing gzip filter", e);
                }
            }
        }
        this.buffer.flush();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setResponse(Response response) {
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setBuffer(HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void end() throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        this.compressionStream.finish();
        this.compressionStream.close();
        this.buffer.end();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void recycle() {
        this.compressionStream = null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/GzipOutputFilter$FakeOutputStream.class */
    protected class FakeOutputStream extends OutputStream {
        protected final ByteBuffer outputChunk = ByteBuffer.allocate(1);

        protected FakeOutputStream() {
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.outputChunk.put(0, (byte) (b & 255));
            GzipOutputFilter.this.buffer.doWrite(this.outputChunk);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            GzipOutputFilter.this.buffer.doWrite(ByteBuffer.wrap(b, off, len));
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }
    }
}
