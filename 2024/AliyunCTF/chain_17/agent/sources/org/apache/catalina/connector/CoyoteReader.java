package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteReader.class */
public class CoyoteReader extends BufferedReader {
    private static final char[] LINE_SEP = {'\r', '\n'};
    private static final int MAX_LINE_LENGTH = 4096;
    protected InputBuffer ib;
    protected char[] lineBuffer;

    public CoyoteReader(InputBuffer ib) {
        super(ib, 1);
        this.lineBuffer = null;
        this.ib = ib;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.ib = null;
    }

    @Override // java.io.BufferedReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.ib.close();
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read() throws IOException {
        return this.ib.read();
    }

    @Override // java.io.Reader
    public int read(char[] cbuf) throws IOException {
        return this.ib.read(cbuf, 0, cbuf.length);
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.ib.read(cbuf, off, len);
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public long skip(long n) throws IOException {
        return this.ib.skip(n);
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public boolean ready() throws IOException {
        return this.ib.ready();
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public void mark(int readAheadLimit) throws IOException {
        this.ib.mark(readAheadLimit);
    }

    @Override // java.io.BufferedReader, java.io.Reader
    public void reset() throws IOException {
        this.ib.reset();
    }

    @Override // java.io.BufferedReader
    public String readLine() throws IOException {
        String result;
        char nextchar;
        if (this.lineBuffer == null) {
            this.lineBuffer = new char[4096];
        }
        int pos = 0;
        int end = -1;
        int skip = -1;
        StringBuilder aggregator = null;
        while (end < 0) {
            mark(4096);
            while (pos < 4096 && end < 0) {
                int nRead = read(this.lineBuffer, pos, 4096 - pos);
                if (nRead < 0) {
                    if (pos == 0 && aggregator == null) {
                        return null;
                    }
                    end = pos;
                    skip = pos;
                }
                for (int i = pos; i < pos + nRead && end < 0; i++) {
                    if (this.lineBuffer[i] == LINE_SEP[0]) {
                        end = i;
                        skip = i + 1;
                        if (i == (pos + nRead) - 1) {
                            nextchar = (char) read();
                        } else {
                            nextchar = this.lineBuffer[i + 1];
                        }
                        if (nextchar == LINE_SEP[1]) {
                            skip++;
                        }
                    } else if (this.lineBuffer[i] == LINE_SEP[1]) {
                        end = i;
                        skip = i + 1;
                    }
                }
                if (nRead > 0) {
                    pos += nRead;
                }
            }
            if (end < 0) {
                if (aggregator == null) {
                    aggregator = new StringBuilder();
                }
                aggregator.append(this.lineBuffer);
                pos = 0;
            } else {
                reset();
                skip(skip);
            }
        }
        if (aggregator == null) {
            result = new String(this.lineBuffer, 0, end);
        } else {
            aggregator.append(this.lineBuffer, 0, end);
            result = aggregator.toString();
        }
        return result;
    }
}
