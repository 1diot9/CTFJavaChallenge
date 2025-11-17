package org.h2.store;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/DataReader.class */
public class DataReader extends Reader {
    private final InputStream in;

    public DataReader(InputStream inputStream) {
        this.in = inputStream;
    }

    public byte readByte() throws IOException {
        int read = this.in.read();
        if (read < 0) {
            throw new FastEOFException();
        }
        return (byte) read;
    }

    public int readVarInt() throws IOException {
        byte readByte = readByte();
        if (readByte >= 0) {
            return readByte;
        }
        int i = readByte & Byte.MAX_VALUE;
        byte readByte2 = readByte();
        if (readByte2 >= 0) {
            return i | (readByte2 << 7);
        }
        int i2 = i | ((readByte2 & Byte.MAX_VALUE) << 7);
        byte readByte3 = readByte();
        if (readByte3 >= 0) {
            return i2 | (readByte3 << 14);
        }
        int i3 = i2 | ((readByte3 & Byte.MAX_VALUE) << 14);
        byte readByte4 = readByte();
        if (readByte4 >= 0) {
            return i3 | (readByte4 << 21);
        }
        return i3 | ((readByte4 & Byte.MAX_VALUE) << 21) | (readByte() << 28);
    }

    private char readChar() throws IOException {
        int readByte = readByte() & 255;
        if (readByte < 128) {
            return (char) readByte;
        }
        if (readByte >= 224) {
            return (char) (((readByte & 15) << 12) + ((readByte() & 63) << 6) + (readByte() & 63));
        }
        return (char) (((readByte & 31) << 6) + (readByte() & 63));
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    @Override // java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return 0;
        }
        int i3 = 0;
        while (i3 < i2) {
            try {
                cArr[i + i3] = readChar();
                i3++;
            } catch (EOFException e) {
                if (i3 == 0) {
                    return -1;
                }
                return i3;
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/DataReader$FastEOFException.class */
    public static class FastEOFException extends EOFException {
        private static final long serialVersionUID = 1;

        FastEOFException() {
        }

        @Override // java.lang.Throwable
        public synchronized Throwable fillInStackTrace() {
            return null;
        }
    }
}
