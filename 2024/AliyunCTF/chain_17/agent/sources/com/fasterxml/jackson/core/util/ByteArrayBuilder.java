package com.fasterxml.jackson.core.util;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/ByteArrayBuilder.class */
public final class ByteArrayBuilder extends OutputStream {
    public static final byte[] NO_BYTES = new byte[0];
    private static final int INITIAL_BLOCK_SIZE = 500;
    private static final int MAX_BLOCK_SIZE = 131072;
    static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
    private final BufferRecycler _bufferRecycler;
    private final LinkedList<byte[]> _pastBlocks;
    private int _pastLen;
    private byte[] _currBlock;
    private int _currBlockPtr;

    public ByteArrayBuilder() {
        this((BufferRecycler) null);
    }

    public ByteArrayBuilder(BufferRecycler br) {
        this(br, 500);
    }

    public ByteArrayBuilder(int firstBlockSize) {
        this(null, firstBlockSize);
    }

    public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
        this._pastBlocks = new LinkedList<>();
        this._bufferRecycler = br;
        this._currBlock = br == null ? new byte[firstBlockSize > 131072 ? 131072 : firstBlockSize] : br.allocByteBuffer(2);
    }

    private ByteArrayBuilder(BufferRecycler br, byte[] initialBlock, int initialLen) {
        this._pastBlocks = new LinkedList<>();
        this._bufferRecycler = null;
        this._currBlock = initialBlock;
        this._currBlockPtr = initialLen;
    }

    public static ByteArrayBuilder fromInitial(byte[] initialBlock, int length) {
        return new ByteArrayBuilder(null, initialBlock, length);
    }

    public void reset() {
        this._pastLen = 0;
        this._currBlockPtr = 0;
        if (!this._pastBlocks.isEmpty()) {
            this._pastBlocks.clear();
        }
    }

    public int size() {
        return this._pastLen + this._currBlockPtr;
    }

    public void release() {
        reset();
        if (this._bufferRecycler != null && this._currBlock != null) {
            this._bufferRecycler.releaseByteBuffer(2, this._currBlock);
            this._currBlock = null;
        }
    }

    public void append(int i) {
        if (this._currBlockPtr >= this._currBlock.length) {
            _allocMore();
        }
        byte[] bArr = this._currBlock;
        int i2 = this._currBlockPtr;
        this._currBlockPtr = i2 + 1;
        bArr[i2] = (byte) i;
    }

    public void appendTwoBytes(int b16) {
        if (this._currBlockPtr + 1 < this._currBlock.length) {
            byte[] bArr = this._currBlock;
            int i = this._currBlockPtr;
            this._currBlockPtr = i + 1;
            bArr[i] = (byte) (b16 >> 8);
            byte[] bArr2 = this._currBlock;
            int i2 = this._currBlockPtr;
            this._currBlockPtr = i2 + 1;
            bArr2[i2] = (byte) b16;
            return;
        }
        append(b16 >> 8);
        append(b16);
    }

    public void appendThreeBytes(int b24) {
        if (this._currBlockPtr + 2 < this._currBlock.length) {
            byte[] bArr = this._currBlock;
            int i = this._currBlockPtr;
            this._currBlockPtr = i + 1;
            bArr[i] = (byte) (b24 >> 16);
            byte[] bArr2 = this._currBlock;
            int i2 = this._currBlockPtr;
            this._currBlockPtr = i2 + 1;
            bArr2[i2] = (byte) (b24 >> 8);
            byte[] bArr3 = this._currBlock;
            int i3 = this._currBlockPtr;
            this._currBlockPtr = i3 + 1;
            bArr3[i3] = (byte) b24;
            return;
        }
        append(b24 >> 16);
        append(b24 >> 8);
        append(b24);
    }

    public void appendFourBytes(int b32) {
        if (this._currBlockPtr + 3 < this._currBlock.length) {
            byte[] bArr = this._currBlock;
            int i = this._currBlockPtr;
            this._currBlockPtr = i + 1;
            bArr[i] = (byte) (b32 >> 24);
            byte[] bArr2 = this._currBlock;
            int i2 = this._currBlockPtr;
            this._currBlockPtr = i2 + 1;
            bArr2[i2] = (byte) (b32 >> 16);
            byte[] bArr3 = this._currBlock;
            int i3 = this._currBlockPtr;
            this._currBlockPtr = i3 + 1;
            bArr3[i3] = (byte) (b32 >> 8);
            byte[] bArr4 = this._currBlock;
            int i4 = this._currBlockPtr;
            this._currBlockPtr = i4 + 1;
            bArr4[i4] = (byte) b32;
            return;
        }
        append(b32 >> 24);
        append(b32 >> 16);
        append(b32 >> 8);
        append(b32);
    }

    public byte[] toByteArray() {
        int totalLen = this._pastLen + this._currBlockPtr;
        if (totalLen == 0) {
            return NO_BYTES;
        }
        byte[] result = new byte[totalLen];
        int offset = 0;
        Iterator<byte[]> it = this._pastBlocks.iterator();
        while (it.hasNext()) {
            byte[] block = it.next();
            int len = block.length;
            System.arraycopy(block, 0, result, offset, len);
            offset += len;
        }
        System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
        int offset2 = offset + this._currBlockPtr;
        if (offset2 != totalLen) {
            throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset2 + " bytes");
        }
        if (!this._pastBlocks.isEmpty()) {
            reset();
        }
        return result;
    }

    public byte[] resetAndGetFirstSegment() {
        reset();
        return this._currBlock;
    }

    public byte[] finishCurrentSegment() {
        _allocMore();
        return this._currBlock;
    }

    public byte[] completeAndCoalesce(int lastBlockLength) {
        this._currBlockPtr = lastBlockLength;
        return toByteArray();
    }

    public byte[] getCurrentSegment() {
        return this._currBlock;
    }

    public void setCurrentSegmentLength(int len) {
        this._currBlockPtr = len;
    }

    public int getCurrentSegmentLength() {
        return this._currBlockPtr;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        while (true) {
            int max = this._currBlock.length - this._currBlockPtr;
            int toCopy = Math.min(max, len);
            if (toCopy > 0) {
                System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
                off += toCopy;
                this._currBlockPtr += toCopy;
                len -= toCopy;
            }
            if (len > 0) {
                _allocMore();
            } else {
                return;
            }
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        append(b);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() {
    }

    private void _allocMore() {
        int newPastLen = this._pastLen + this._currBlock.length;
        if (newPastLen < 0) {
            throw new IllegalStateException("Maximum Java array size (2GB) exceeded by `ByteArrayBuilder`");
        }
        this._pastLen = newPastLen;
        int newSize = Math.max(this._pastLen >> 1, 1000);
        if (newSize > 131072) {
            newSize = 131072;
        }
        this._pastBlocks.add(this._currBlock);
        this._currBlock = new byte[newSize];
        this._currBlockPtr = 0;
    }
}
