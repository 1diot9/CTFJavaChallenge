package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.util.Closeable;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/MultipartStream.class */
public class MultipartStream {
    public static final byte CR = 13;
    public static final byte LF = 10;
    public static final byte DASH = 45;
    public static final int HEADER_PART_SIZE_MAX = 10240;
    protected static final int DEFAULT_BUFSIZE = 4096;
    protected static final byte[] HEADER_SEPARATOR = {13, 10, 13, 10};
    protected static final byte[] FIELD_SEPARATOR = {13, 10};
    protected static final byte[] STREAM_TERMINATOR = {45, 45};
    protected static final byte[] BOUNDARY_PREFIX = {13, 10, 45, 45};
    private final InputStream input;
    private int boundaryLength;
    private final int keepRegion;
    private final byte[] boundary;
    private final int[] boundaryTable;
    private final int bufSize;
    private final byte[] buffer;
    private int head;
    private int tail;
    private String headerEncoding;
    private final ProgressNotifier notifier;

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/MultipartStream$ProgressNotifier.class */
    public static class ProgressNotifier {
        private final ProgressListener listener;
        private final long contentLength;
        private long bytesRead;
        private int items;

        public ProgressNotifier(ProgressListener pListener, long pContentLength) {
            this.listener = pListener;
            this.contentLength = pContentLength;
        }

        void noteBytesRead(int pBytes) {
            this.bytesRead += pBytes;
            notifyListener();
        }

        public void noteItem() {
            this.items++;
            notifyListener();
        }

        private void notifyListener() {
            if (this.listener != null) {
                this.listener.update(this.bytesRead, this.contentLength, this.items);
            }
        }
    }

    public MultipartStream(InputStream input, byte[] boundary, int bufSize, ProgressNotifier pNotifier) {
        if (boundary == null) {
            throw new IllegalArgumentException("boundary may not be null");
        }
        this.boundaryLength = boundary.length + BOUNDARY_PREFIX.length;
        if (bufSize < this.boundaryLength + 1) {
            throw new IllegalArgumentException("The buffer size specified for the MultipartStream is too small");
        }
        this.input = input;
        this.bufSize = Math.max(bufSize, this.boundaryLength * 2);
        this.buffer = new byte[this.bufSize];
        this.notifier = pNotifier;
        this.boundary = new byte[this.boundaryLength];
        this.boundaryTable = new int[this.boundaryLength + 1];
        this.keepRegion = this.boundary.length;
        System.arraycopy(BOUNDARY_PREFIX, 0, this.boundary, 0, BOUNDARY_PREFIX.length);
        System.arraycopy(boundary, 0, this.boundary, BOUNDARY_PREFIX.length, boundary.length);
        computeBoundaryTable();
        this.head = 0;
        this.tail = 0;
    }

    public MultipartStream(InputStream input, byte[] boundary, ProgressNotifier pNotifier) {
        this(input, boundary, 4096, pNotifier);
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    public byte readByte() throws IOException {
        if (this.head == this.tail) {
            this.head = 0;
            this.tail = this.input.read(this.buffer, this.head, this.bufSize);
            if (this.tail == -1) {
                throw new IOException("No more data is available");
            }
            if (this.notifier != null) {
                this.notifier.noteBytesRead(this.tail);
            }
        }
        byte[] bArr = this.buffer;
        int i = this.head;
        this.head = i + 1;
        return bArr[i];
    }

    public boolean readBoundary() throws FileUploadIOException, MalformedStreamException {
        boolean nextChunk;
        byte[] marker = new byte[2];
        this.head += this.boundaryLength;
        try {
            marker[0] = readByte();
            if (marker[0] == 10) {
                return true;
            }
            marker[1] = readByte();
            if (arrayequals(marker, STREAM_TERMINATOR, 2)) {
                nextChunk = false;
            } else if (arrayequals(marker, FIELD_SEPARATOR, 2)) {
                nextChunk = true;
            } else {
                throw new MalformedStreamException("Unexpected characters follow a boundary");
            }
            return nextChunk;
        } catch (FileUploadIOException e) {
            throw e;
        } catch (IOException e2) {
            throw new MalformedStreamException("Stream ended unexpectedly");
        }
    }

    public void setBoundary(byte[] boundary) throws IllegalBoundaryException {
        if (boundary.length != this.boundaryLength - BOUNDARY_PREFIX.length) {
            throw new IllegalBoundaryException("The length of a boundary token cannot be changed");
        }
        System.arraycopy(boundary, 0, this.boundary, BOUNDARY_PREFIX.length, boundary.length);
        computeBoundaryTable();
    }

    private void computeBoundaryTable() {
        int position = 2;
        int candidate = 0;
        this.boundaryTable[0] = -1;
        this.boundaryTable[1] = 0;
        while (position <= this.boundaryLength) {
            if (this.boundary[position - 1] == this.boundary[candidate]) {
                this.boundaryTable[position] = candidate + 1;
                candidate++;
                position++;
            } else if (candidate > 0) {
                candidate = this.boundaryTable[candidate];
            } else {
                this.boundaryTable[position] = 0;
                position++;
            }
        }
    }

    public String readHeaders() throws FileUploadIOException, MalformedStreamException {
        String headers;
        int i = 0;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        int size = 0;
        while (i < HEADER_SEPARATOR.length) {
            try {
                byte b = readByte();
                size++;
                if (size > 10240) {
                    throw new MalformedStreamException(String.format("Header section has more than %s bytes (maybe it is not properly terminated)", 10240));
                }
                if (b == HEADER_SEPARATOR[i]) {
                    i++;
                } else {
                    i = 0;
                }
                baos.write(b);
            } catch (FileUploadIOException e) {
                throw e;
            } catch (IOException e2) {
                throw new MalformedStreamException("Stream ended unexpectedly");
            }
        }
        if (this.headerEncoding != null) {
            try {
                headers = baos.toString(this.headerEncoding);
            } catch (UnsupportedEncodingException e3) {
                headers = baos.toString();
            }
        } else {
            headers = baos.toString();
        }
        return headers;
    }

    public int readBodyData(OutputStream output) throws MalformedStreamException, IOException {
        return (int) Streams.copy(newInputStream(), output, false);
    }

    public ItemInputStream newInputStream() {
        return new ItemInputStream();
    }

    public int discardBodyData() throws MalformedStreamException, IOException {
        return readBodyData(null);
    }

    public boolean skipPreamble() throws IOException {
        System.arraycopy(this.boundary, 2, this.boundary, 0, this.boundary.length - 2);
        this.boundaryLength = this.boundary.length - 2;
        computeBoundaryTable();
        try {
            discardBodyData();
            return readBoundary();
        } catch (MalformedStreamException e) {
            return false;
        } finally {
            System.arraycopy(this.boundary, 0, this.boundary, 2, this.boundary.length - 2);
            this.boundaryLength = this.boundary.length;
            this.boundary[0] = 13;
            this.boundary[1] = 10;
            computeBoundaryTable();
        }
    }

    public static boolean arrayequals(byte[] a, byte[] b, int count) {
        for (int i = 0; i < count; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    protected int findSeparator() {
        int bufferPos = this.head;
        int tablePos = 0;
        while (bufferPos < this.tail) {
            while (tablePos >= 0 && this.buffer[bufferPos] != this.boundary[tablePos]) {
                tablePos = this.boundaryTable[tablePos];
            }
            bufferPos++;
            tablePos++;
            if (tablePos == this.boundaryLength) {
                return bufferPos - this.boundaryLength;
            }
        }
        return -1;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/MultipartStream$MalformedStreamException.class */
    public static class MalformedStreamException extends IOException {
        private static final long serialVersionUID = 6466926458059796677L;

        public MalformedStreamException() {
        }

        public MalformedStreamException(String message) {
            super(message);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/MultipartStream$IllegalBoundaryException.class */
    public static class IllegalBoundaryException extends IOException {
        private static final long serialVersionUID = -161533165102632918L;

        public IllegalBoundaryException() {
        }

        public IllegalBoundaryException(String message) {
            super(message);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/MultipartStream$ItemInputStream.class */
    public class ItemInputStream extends InputStream implements Closeable {
        private long total;
        private int pad;
        private int pos;
        private boolean closed;
        private static final int BYTE_POSITIVE_OFFSET = 256;

        ItemInputStream() {
            findSeparator();
        }

        private void findSeparator() {
            this.pos = MultipartStream.this.findSeparator();
            if (this.pos == -1) {
                if (MultipartStream.this.tail - MultipartStream.this.head > MultipartStream.this.keepRegion) {
                    this.pad = MultipartStream.this.keepRegion;
                } else {
                    this.pad = MultipartStream.this.tail - MultipartStream.this.head;
                }
            }
        }

        public long getBytesRead() {
            return this.total;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            if (this.pos == -1) {
                return (MultipartStream.this.tail - MultipartStream.this.head) - this.pad;
            }
            return this.pos - MultipartStream.this.head;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            if (available() == 0 && makeAvailable() == 0) {
                return -1;
            }
            this.total++;
            byte[] bArr = MultipartStream.this.buffer;
            MultipartStream multipartStream = MultipartStream.this;
            int i = multipartStream.head;
            multipartStream.head = i + 1;
            byte b = bArr[i];
            if (b >= 0) {
                return b;
            }
            return b + 256;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            if (len == 0) {
                return 0;
            }
            int res = available();
            if (res == 0) {
                res = makeAvailable();
                if (res == 0) {
                    return -1;
                }
            }
            int res2 = Math.min(res, len);
            System.arraycopy(MultipartStream.this.buffer, MultipartStream.this.head, b, off, res2);
            MultipartStream.this.head += res2;
            this.total += res2;
            return res2;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable, org.apache.tomcat.util.http.fileupload.util.Closeable
        public void close() throws IOException {
            close(false);
        }

        public void close(boolean pCloseUnderlying) throws IOException {
            if (this.closed) {
                return;
            }
            if (pCloseUnderlying) {
                this.closed = true;
                MultipartStream.this.input.close();
            } else {
                while (true) {
                    int av = available();
                    if (av == 0) {
                        av = makeAvailable();
                        if (av == 0) {
                            break;
                        }
                    }
                    skip(av);
                }
            }
            this.closed = true;
        }

        @Override // java.io.InputStream
        public long skip(long bytes) throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            int av = available();
            if (av == 0) {
                av = makeAvailable();
                if (av == 0) {
                    return 0L;
                }
            }
            long res = Math.min(av, bytes);
            MultipartStream.this.head = (int) (r0.head + res);
            return res;
        }

        private int makeAvailable() throws IOException {
            int av;
            if (this.pos != -1) {
                return 0;
            }
            this.total += (MultipartStream.this.tail - MultipartStream.this.head) - this.pad;
            System.arraycopy(MultipartStream.this.buffer, MultipartStream.this.tail - this.pad, MultipartStream.this.buffer, 0, this.pad);
            MultipartStream.this.head = 0;
            MultipartStream.this.tail = this.pad;
            do {
                int bytesRead = MultipartStream.this.input.read(MultipartStream.this.buffer, MultipartStream.this.tail, MultipartStream.this.bufSize - MultipartStream.this.tail);
                if (bytesRead == -1) {
                    throw new MalformedStreamException("Stream ended unexpectedly");
                }
                if (MultipartStream.this.notifier != null) {
                    MultipartStream.this.notifier.noteBytesRead(bytesRead);
                }
                MultipartStream.this.tail += bytesRead;
                findSeparator();
                av = available();
                if (av > 0) {
                    break;
                }
            } while (this.pos == -1);
            return av;
        }

        @Override // org.apache.tomcat.util.http.fileupload.util.Closeable
        public boolean isClosed() {
            return this.closed;
        }
    }
}
