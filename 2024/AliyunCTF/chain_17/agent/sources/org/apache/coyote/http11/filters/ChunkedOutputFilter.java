package org.apache.coyote.http11.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.tomcat.util.buf.HexUtils;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/ChunkedOutputFilter.class */
public class ChunkedOutputFilter implements OutputFilter {
    private static final byte[] LAST_CHUNK_BYTES = {48, 13, 10};
    private static final byte[] CRLF_BYTES = {13, 10};
    private static final byte[] END_CHUNK_BYTES = {48, 13, 10, 13, 10};
    private static final Set<String> disallowedTrailerFieldNames = new HashSet();
    protected HttpOutputBuffer buffer;
    protected final ByteBuffer chunkHeader = ByteBuffer.allocate(10);
    protected final ByteBuffer lastChunk = ByteBuffer.wrap(LAST_CHUNK_BYTES);
    protected final ByteBuffer crlfChunk = ByteBuffer.wrap(CRLF_BYTES);
    protected final ByteBuffer endChunk = ByteBuffer.wrap(END_CHUNK_BYTES);
    private Response response;

    static {
        disallowedTrailerFieldNames.add("age");
        disallowedTrailerFieldNames.add("cache-control");
        disallowedTrailerFieldNames.add("content-length");
        disallowedTrailerFieldNames.add("content-encoding");
        disallowedTrailerFieldNames.add("content-range");
        disallowedTrailerFieldNames.add("content-type");
        disallowedTrailerFieldNames.add("date");
        disallowedTrailerFieldNames.add("expires");
        disallowedTrailerFieldNames.add("location");
        disallowedTrailerFieldNames.add("retry-after");
        disallowedTrailerFieldNames.add("trailer");
        disallowedTrailerFieldNames.add("transfer-encoding");
        disallowedTrailerFieldNames.add("vary");
        disallowedTrailerFieldNames.add("warning");
    }

    public ChunkedOutputFilter() {
        this.chunkHeader.put(8, (byte) 13);
        this.chunkHeader.put(9, (byte) 10);
    }

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        int result = chunk.remaining();
        if (result <= 0) {
            return 0;
        }
        int pos = calculateChunkHeader(result);
        this.chunkHeader.position(pos).limit(10);
        this.buffer.doWrite(this.chunkHeader);
        this.buffer.doWrite(chunk);
        this.chunkHeader.position(8).limit(10);
        this.buffer.doWrite(this.chunkHeader);
        return result;
    }

    private int calculateChunkHeader(int len) {
        int pos = 8;
        int current = len;
        while (current > 0) {
            int digit = current % 16;
            current /= 16;
            pos--;
            this.chunkHeader.put(pos, HexUtils.getHex(digit));
        }
        return pos;
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void setResponse(Response response) {
        this.response = response;
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
        Supplier<Map<String, String>> trailerFieldsSupplier = this.response.getTrailerFields();
        Map<String, String> trailerFields = null;
        if (trailerFieldsSupplier != null) {
            trailerFields = trailerFieldsSupplier.get();
        }
        if (trailerFields == null) {
            this.buffer.doWrite(this.endChunk);
            this.endChunk.position(0).limit(this.endChunk.capacity());
        } else {
            this.buffer.doWrite(this.lastChunk);
            this.lastChunk.position(0).limit(this.lastChunk.capacity());
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.ISO_8859_1);
            try {
                for (Map.Entry<String, String> trailerField : trailerFields.entrySet()) {
                    if (!disallowedTrailerFieldNames.contains(trailerField.getKey().toLowerCase(Locale.ENGLISH))) {
                        osw.write(trailerField.getKey());
                        osw.write(58);
                        osw.write(32);
                        osw.write(trailerField.getValue());
                        osw.write("\r\n");
                    }
                }
                osw.close();
                this.buffer.doWrite(ByteBuffer.wrap(baos.toByteArray()));
                this.buffer.doWrite(this.crlfChunk);
                this.crlfChunk.position(0).limit(this.crlfChunk.capacity());
            } catch (Throwable th) {
                try {
                    osw.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        this.buffer.end();
    }

    @Override // org.apache.coyote.http11.OutputFilter
    public void recycle() {
        this.response = null;
    }
}
