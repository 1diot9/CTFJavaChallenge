package cn.hutool.core.io;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/FastByteBuffer.class */
public class FastByteBuffer {
    private byte[][] buffers;
    private int buffersCount;
    private int currentBufferIndex;
    private byte[] currentBuffer;
    private int offset;
    private int size;
    private final int minChunkLen;

    public FastByteBuffer() {
        this(1024);
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [byte[], byte[][]] */
    public FastByteBuffer(int size) {
        this.buffers = new byte[16];
        this.currentBufferIndex = -1;
        this.minChunkLen = Math.abs(size <= 0 ? 1024 : size);
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [byte[], byte[][], java.lang.Object] */
    private void needNewBuffer(int newSize) {
        int delta = newSize - this.size;
        int newBufferSize = Math.max(this.minChunkLen, delta);
        this.currentBufferIndex++;
        this.currentBuffer = new byte[newBufferSize];
        this.offset = 0;
        if (this.currentBufferIndex >= this.buffers.length) {
            int newLen = this.buffers.length << 1;
            ?? r0 = new byte[newLen];
            System.arraycopy(this.buffers, 0, r0, 0, this.buffers.length);
            this.buffers = r0;
        }
        this.buffers[this.currentBufferIndex] = this.currentBuffer;
        this.buffersCount++;
    }

    public FastByteBuffer append(byte[] array, int off, int len) {
        int end = off + len;
        if (off < 0 || len < 0 || end > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return this;
        }
        int newSize = this.size + len;
        int remaining = len;
        if (this.currentBuffer != null) {
            int part = Math.min(remaining, this.currentBuffer.length - this.offset);
            System.arraycopy(array, end - remaining, this.currentBuffer, this.offset, part);
            remaining -= part;
            this.offset += part;
            this.size += part;
        }
        if (remaining > 0) {
            needNewBuffer(newSize);
            int part2 = Math.min(remaining, this.currentBuffer.length - this.offset);
            System.arraycopy(array, end - remaining, this.currentBuffer, this.offset, part2);
            this.offset += part2;
            this.size += part2;
        }
        return this;
    }

    public FastByteBuffer append(byte[] array) {
        return append(array, 0, array.length);
    }

    public FastByteBuffer append(byte element) {
        if (this.currentBuffer == null || this.offset == this.currentBuffer.length) {
            needNewBuffer(this.size + 1);
        }
        this.currentBuffer[this.offset] = element;
        this.offset++;
        this.size++;
        return this;
    }

    public FastByteBuffer append(FastByteBuffer buff) {
        if (buff.size == 0) {
            return this;
        }
        for (int i = 0; i < buff.currentBufferIndex; i++) {
            append(buff.buffers[i]);
        }
        append(buff.currentBuffer, 0, buff.offset);
        return this;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int index() {
        return this.currentBufferIndex;
    }

    public int offset() {
        return this.offset;
    }

    public byte[] array(int index) {
        return this.buffers[index];
    }

    public void reset() {
        this.size = 0;
        this.offset = 0;
        this.currentBufferIndex = -1;
        this.currentBuffer = null;
        this.buffersCount = 0;
    }

    public byte[] toArray() {
        int pos = 0;
        byte[] array = new byte[this.size];
        if (this.currentBufferIndex == -1) {
            return array;
        }
        for (int i = 0; i < this.currentBufferIndex; i++) {
            int len = this.buffers[i].length;
            System.arraycopy(this.buffers[i], 0, array, pos, len);
            pos += len;
        }
        System.arraycopy(this.buffers[this.currentBufferIndex], 0, array, pos, this.offset);
        return array;
    }

    public byte[] toArray(int start, int len) {
        int remaining = len;
        int pos = 0;
        byte[] array = new byte[len];
        if (len == 0) {
            return array;
        }
        int i = 0;
        while (start >= this.buffers[i].length) {
            start -= this.buffers[i].length;
            i++;
        }
        while (i < this.buffersCount) {
            byte[] buf = this.buffers[i];
            int c = Math.min(buf.length - start, remaining);
            System.arraycopy(buf, start, array, pos, c);
            pos += c;
            remaining -= c;
            if (remaining == 0) {
                break;
            }
            start = 0;
            i++;
        }
        return array;
    }

    public byte get(int index) {
        if (index >= this.size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        int ndx = 0;
        while (true) {
            byte[] b = this.buffers[ndx];
            if (index < b.length) {
                return b[index];
            }
            ndx++;
            index -= b.length;
        }
    }
}
