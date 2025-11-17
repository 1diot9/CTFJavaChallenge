package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.databind.ser.SerializerCache;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.h2.api.ErrorCode;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/BufferRecycler.class */
public class BufferRecycler {
    public static final int BYTE_READ_IO_BUFFER = 0;
    public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
    public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
    public static final int BYTE_BASE64_CODEC_BUFFER = 3;
    public static final int CHAR_TOKEN_BUFFER = 0;
    public static final int CHAR_CONCAT_BUFFER = 1;
    public static final int CHAR_TEXT_BUFFER = 2;
    public static final int CHAR_NAME_COPY_BUFFER = 3;
    private static final int[] BYTE_BUFFER_LENGTHS = {ErrorCode.ERROR_OPENING_DATABASE_1, ErrorCode.ERROR_OPENING_DATABASE_1, 2000, 2000};
    private static final int[] CHAR_BUFFER_LENGTHS = {SerializerCache.DEFAULT_MAX_CACHED, SerializerCache.DEFAULT_MAX_CACHED, 200, 200};
    protected final AtomicReferenceArray<byte[]> _byteBuffers;
    protected final AtomicReferenceArray<char[]> _charBuffers;

    public BufferRecycler() {
        this(4, 4);
    }

    protected BufferRecycler(int bbCount, int cbCount) {
        this._byteBuffers = new AtomicReferenceArray<>(bbCount);
        this._charBuffers = new AtomicReferenceArray<>(cbCount);
    }

    public final byte[] allocByteBuffer(int ix) {
        return allocByteBuffer(ix, 0);
    }

    public byte[] allocByteBuffer(int ix, int minSize) {
        int DEF_SIZE = byteBufferLength(ix);
        if (minSize < DEF_SIZE) {
            minSize = DEF_SIZE;
        }
        byte[] buffer = this._byteBuffers.getAndSet(ix, null);
        if (buffer == null || buffer.length < minSize) {
            buffer = balloc(minSize);
        }
        return buffer;
    }

    public void releaseByteBuffer(int ix, byte[] buffer) {
        this._byteBuffers.set(ix, buffer);
    }

    public final char[] allocCharBuffer(int ix) {
        return allocCharBuffer(ix, 0);
    }

    public char[] allocCharBuffer(int ix, int minSize) {
        int DEF_SIZE = charBufferLength(ix);
        if (minSize < DEF_SIZE) {
            minSize = DEF_SIZE;
        }
        char[] buffer = this._charBuffers.getAndSet(ix, null);
        if (buffer == null || buffer.length < minSize) {
            buffer = calloc(minSize);
        }
        return buffer;
    }

    public void releaseCharBuffer(int ix, char[] buffer) {
        this._charBuffers.set(ix, buffer);
    }

    protected int byteBufferLength(int ix) {
        return BYTE_BUFFER_LENGTHS[ix];
    }

    protected int charBufferLength(int ix) {
        return CHAR_BUFFER_LENGTHS[ix];
    }

    protected byte[] balloc(int size) {
        return new byte[size];
    }

    protected char[] calloc(int size) {
        return new char[size];
    }
}
