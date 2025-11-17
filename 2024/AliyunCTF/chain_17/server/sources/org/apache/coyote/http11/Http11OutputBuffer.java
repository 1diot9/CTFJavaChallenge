package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.coyote.ActionCode;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.Response;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11OutputBuffer.class */
public class Http11OutputBuffer implements HttpOutputBuffer {
    protected static final StringManager sm = StringManager.getManager((Class<?>) Http11OutputBuffer.class);
    protected final Response response;
    protected final ByteBuffer headerBuffer;
    protected SocketWrapperBase<?> socketWrapper;
    private volatile boolean ackSent = false;
    protected long byteCount = 0;
    protected OutputFilter[] filterLibrary = new OutputFilter[0];
    protected OutputFilter[] activeFilters = new OutputFilter[0];
    protected int lastActiveFilter = -1;
    protected boolean responseFinished = false;
    protected HttpOutputBuffer outputStreamOutputBuffer = new SocketOutputBuffer();

    /* JADX INFO: Access modifiers changed from: protected */
    public Http11OutputBuffer(Response response, int headerBufferSize) {
        this.response = response;
        this.headerBuffer = ByteBuffer.allocate(headerBufferSize);
    }

    public void addFilter(OutputFilter filter) {
        OutputFilter[] newFilterLibrary = (OutputFilter[]) Arrays.copyOf(this.filterLibrary, this.filterLibrary.length + 1);
        newFilterLibrary[this.filterLibrary.length] = filter;
        this.filterLibrary = newFilterLibrary;
        this.activeFilters = new OutputFilter[this.filterLibrary.length];
    }

    public OutputFilter[] getFilters() {
        return this.filterLibrary;
    }

    public void addActiveFilter(OutputFilter filter) {
        if (this.lastActiveFilter == -1) {
            filter.setBuffer(this.outputStreamOutputBuffer);
        } else {
            for (int i = 0; i <= this.lastActiveFilter; i++) {
                if (this.activeFilters[i] == filter) {
                    return;
                }
            }
            filter.setBuffer(this.activeFilters[this.lastActiveFilter]);
        }
        OutputFilter[] outputFilterArr = this.activeFilters;
        int i2 = this.lastActiveFilter + 1;
        this.lastActiveFilter = i2;
        outputFilterArr[i2] = filter;
        filter.setResponse(this.response);
    }

    @Override // org.apache.coyote.OutputBuffer
    public int doWrite(ByteBuffer chunk) throws IOException {
        if (!this.response.isCommitted()) {
            this.response.action(ActionCode.COMMIT, null);
        }
        if (this.lastActiveFilter == -1) {
            return this.outputStreamOutputBuffer.doWrite(chunk);
        }
        return this.activeFilters[this.lastActiveFilter].doWrite(chunk);
    }

    @Override // org.apache.coyote.OutputBuffer
    public long getBytesWritten() {
        if (this.lastActiveFilter == -1) {
            return this.outputStreamOutputBuffer.getBytesWritten();
        }
        return this.activeFilters[this.lastActiveFilter].getBytesWritten();
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void flush() throws IOException {
        if (this.lastActiveFilter == -1) {
            this.outputStreamOutputBuffer.flush();
        } else {
            this.activeFilters[this.lastActiveFilter].flush();
        }
    }

    @Override // org.apache.coyote.http11.HttpOutputBuffer
    public void end() throws IOException {
        if (this.responseFinished) {
            return;
        }
        if (this.lastActiveFilter == -1) {
            this.outputStreamOutputBuffer.end();
        } else {
            this.activeFilters[this.lastActiveFilter].end();
        }
        this.responseFinished = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetHeaderBuffer() {
        this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
    }

    public void recycle() {
        nextRequest();
        this.socketWrapper = null;
    }

    public void nextRequest() {
        for (int i = 0; i <= this.lastActiveFilter; i++) {
            this.activeFilters[i].recycle();
        }
        this.response.recycle();
        this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
        this.lastActiveFilter = -1;
        this.ackSent = false;
        this.responseFinished = false;
        this.byteCount = 0L;
    }

    public void init(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    public void sendAck() throws IOException {
        if (!this.response.isCommitted() && !this.ackSent) {
            this.ackSent = true;
            this.socketWrapper.write(isBlocking(), Constants.ACK_BYTES, 0, Constants.ACK_BYTES.length);
            if (flushBuffer(true)) {
                throw new IOException(sm.getString("iob.failedwrite.ack"));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void commit() throws IOException {
        this.response.setCommitted(true);
        if (this.headerBuffer.position() > 0) {
            this.headerBuffer.flip();
            try {
                SocketWrapperBase<?> socketWrapper = this.socketWrapper;
                if (socketWrapper != null) {
                    socketWrapper.write(isBlocking(), this.headerBuffer);
                    return;
                }
                throw new CloseNowException(sm.getString("iob.failedwrite"));
            } finally {
                this.headerBuffer.position(0).limit(this.headerBuffer.capacity());
            }
        }
    }

    public void sendStatus() {
        write(Constants.HTTP_11_BYTES);
        this.headerBuffer.put((byte) 32);
        int status = this.response.getStatus();
        switch (status) {
            case 200:
                write(Constants._200_BYTES);
                break;
            case 400:
                write(Constants._400_BYTES);
                break;
            case 404:
                write(Constants._404_BYTES);
                break;
            default:
                write(status);
                break;
        }
        this.headerBuffer.put((byte) 32);
        this.headerBuffer.put((byte) 13).put((byte) 10);
    }

    public void sendHeader(MessageBytes name, MessageBytes value) {
        write(name);
        this.headerBuffer.put((byte) 58).put((byte) 32);
        write(value);
        this.headerBuffer.put((byte) 13).put((byte) 10);
    }

    public void endHeaders() {
        this.headerBuffer.put((byte) 13).put((byte) 10);
    }

    private void write(MessageBytes mb) {
        if (mb.getType() != 2) {
            mb.toBytes();
            ByteChunk bc = mb.getByteChunk();
            byte[] buffer = bc.getBuffer();
            for (int i = bc.getOffset(); i < bc.getLength(); i++) {
                if ((buffer[i] > -1 && buffer[i] <= 31 && buffer[i] != 9) || buffer[i] == Byte.MAX_VALUE) {
                    buffer[i] = 32;
                }
            }
        }
        write(mb.getByteChunk());
    }

    private void write(ByteChunk bc) {
        int length = bc.getLength();
        checkLengthBeforeWrite(length);
        this.headerBuffer.put(bc.getBytes(), bc.getStart(), length);
    }

    public void write(byte[] b) {
        checkLengthBeforeWrite(b.length);
        this.headerBuffer.put(b);
    }

    private void write(int value) {
        String s = Integer.toString(value);
        int len = s.length();
        checkLengthBeforeWrite(len);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            this.headerBuffer.put((byte) c);
        }
    }

    private void checkLengthBeforeWrite(int length) {
        if (this.headerBuffer.position() + length + 4 > this.headerBuffer.capacity()) {
            throw new HeadersTooLargeException(sm.getString("iob.responseheadertoolarge.error"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flushBuffer(boolean block) throws IOException {
        return this.socketWrapper.flush(block);
    }

    protected final boolean isBlocking() {
        return this.response.getWriteListener() == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isReady() {
        boolean result = !hasDataToWrite();
        if (!result) {
            this.socketWrapper.registerWriteInterest();
        }
        return result;
    }

    public boolean hasDataToWrite() {
        return this.socketWrapper.hasDataToWrite();
    }

    public void registerWriteInterest() {
        this.socketWrapper.registerWriteInterest();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isChunking() {
        for (int i = 0; i < this.lastActiveFilter; i++) {
            if (this.activeFilters[i] == this.filterLibrary[1]) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11OutputBuffer$SocketOutputBuffer.class */
    protected class SocketOutputBuffer implements HttpOutputBuffer {
        protected SocketOutputBuffer() {
        }

        @Override // org.apache.coyote.OutputBuffer
        public int doWrite(ByteBuffer chunk) throws IOException {
            try {
                int len = chunk.remaining();
                SocketWrapperBase<?> socketWrapper = Http11OutputBuffer.this.socketWrapper;
                if (socketWrapper != null) {
                    socketWrapper.write(Http11OutputBuffer.this.isBlocking(), chunk);
                    int len2 = len - chunk.remaining();
                    Http11OutputBuffer.this.byteCount += len2;
                    return len2;
                }
                throw new CloseNowException(Http11OutputBuffer.sm.getString("iob.failedwrite"));
            } catch (IOException ioe) {
                Http11OutputBuffer.this.response.action(ActionCode.CLOSE_NOW, ioe);
                throw ioe;
            }
        }

        @Override // org.apache.coyote.OutputBuffer
        public long getBytesWritten() {
            return Http11OutputBuffer.this.byteCount;
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public void end() throws IOException {
            Http11OutputBuffer.this.socketWrapper.flush(true);
        }

        @Override // org.apache.coyote.http11.HttpOutputBuffer
        public void flush() throws IOException {
            Http11OutputBuffer.this.socketWrapper.flush(Http11OutputBuffer.this.isBlocking());
        }
    }
}
