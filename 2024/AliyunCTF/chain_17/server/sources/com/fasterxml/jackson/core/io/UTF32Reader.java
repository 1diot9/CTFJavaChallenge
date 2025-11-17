package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.jooq.types.UShort;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/UTF32Reader.class */
public class UTF32Reader extends Reader {
    protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
    protected static final char NC = 0;
    protected final IOContext _context;
    protected InputStream _in;
    protected byte[] _buffer;
    protected int _ptr;
    protected int _length;
    protected final boolean _bigEndian;
    protected char _surrogate = 0;
    protected int _charCount;
    protected int _byteCount;
    protected final boolean _managedBuffers;
    protected char[] _tmpBuf;

    public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
        this._context = ctxt;
        this._in = in;
        this._buffer = buf;
        this._ptr = ptr;
        this._length = len;
        this._bigEndian = isBigEndian;
        this._managedBuffers = in != null;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream in = this._in;
        if (in != null) {
            this._in = null;
            freeBuffers();
            in.close();
        }
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this._tmpBuf == null) {
            this._tmpBuf = new char[1];
        }
        if (read(this._tmpBuf, 0, 1) < 1) {
            return -1;
        }
        return this._tmpBuf[0];
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int start, int len) throws IOException {
        int lo;
        int hi;
        if (this._buffer == null) {
            return -1;
        }
        if (len < 1) {
            return len;
        }
        if (start < 0 || start + len > cbuf.length) {
            reportBounds(cbuf, start, len);
        }
        int outPtr = start;
        int outEnd = len + start;
        if (this._surrogate != 0) {
            outPtr++;
            cbuf[outPtr] = this._surrogate;
            this._surrogate = (char) 0;
        } else {
            int left = this._length - this._ptr;
            if (left < 4 && !loadMore(left)) {
                if (left == 0) {
                    return -1;
                }
                reportUnexpectedEOF(this._length - this._ptr, 4);
            }
        }
        int lastValidInputStart = this._length - 4;
        while (true) {
            if (outPtr >= outEnd || this._ptr > lastValidInputStart) {
                break;
            }
            int ptr = this._ptr;
            if (this._bigEndian) {
                hi = (this._buffer[ptr] << 8) | (this._buffer[ptr + 1] & 255);
                lo = ((this._buffer[ptr + 2] & 255) << 8) | (this._buffer[ptr + 3] & 255);
            } else {
                lo = (this._buffer[ptr] & 255) | ((this._buffer[ptr + 1] & 255) << 8);
                hi = (this._buffer[ptr + 2] & 255) | (this._buffer[ptr + 3] << 8);
            }
            this._ptr += 4;
            if (hi != 0) {
                int hi2 = hi & UShort.MAX_VALUE;
                int ch2 = ((hi2 - 1) << 16) | lo;
                if (hi2 > 16) {
                    reportInvalid(ch2, outPtr - start, String.format(" (above 0x%08x)", Integer.valueOf(LAST_VALID_UNICODE_CHAR)));
                }
                int i = outPtr;
                outPtr++;
                cbuf[i] = (char) (GeneratorBase.SURR1_FIRST + (ch2 >> 10));
                lo = 56320 | (ch2 & FastDoubleMath.DOUBLE_EXPONENT_BIAS);
                if (outPtr >= outEnd) {
                    this._surrogate = (char) ch2;
                    break;
                }
            }
            int i2 = outPtr;
            outPtr++;
            cbuf[i2] = (char) lo;
        }
        int actualLen = outPtr - start;
        this._charCount += actualLen;
        return actualLen;
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        int bytePos = this._byteCount + gotBytes;
        int charPos = this._charCount;
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        int bytePos = (this._byteCount + this._ptr) - 1;
        int charPos = this._charCount + offset;
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }

    private boolean loadMore(int available) throws IOException {
        if (this._in == null || this._buffer == null) {
            return false;
        }
        this._byteCount += this._length - available;
        if (available > 0) {
            if (this._ptr > 0) {
                System.arraycopy(this._buffer, this._ptr, this._buffer, 0, available);
                this._ptr = 0;
            }
            this._length = available;
        } else {
            this._ptr = 0;
            int count = this._in.read(this._buffer);
            if (count < 1) {
                this._length = 0;
                if (count < 0) {
                    if (this._managedBuffers) {
                        freeBuffers();
                        return false;
                    }
                    return false;
                }
                reportStrangeStream();
            }
            this._length = count;
        }
        while (this._length < 4) {
            int count2 = this._in.read(this._buffer, this._length, this._buffer.length - this._length);
            if (count2 < 1) {
                if (count2 < 0) {
                    if (this._managedBuffers) {
                        freeBuffers();
                    }
                    reportUnexpectedEOF(this._length, 4);
                }
                reportStrangeStream();
            }
            this._length += count2;
        }
        return true;
    }

    private void freeBuffers() {
        byte[] buf = this._buffer;
        if (buf != null) {
            this._buffer = null;
            if (this._context != null) {
                this._context.releaseReadIOBuffer(buf);
            }
        }
    }

    private void reportBounds(char[] cbuf, int start, int len) throws IOException {
        throw new ArrayIndexOutOfBoundsException(String.format("read(buf,%d,%d), cbuf[%d]", Integer.valueOf(start), Integer.valueOf(len), Integer.valueOf(cbuf.length)));
    }

    private void reportStrangeStream() throws IOException {
        throw new IOException("Strange I/O stream, returned 0 bytes on read");
    }
}
