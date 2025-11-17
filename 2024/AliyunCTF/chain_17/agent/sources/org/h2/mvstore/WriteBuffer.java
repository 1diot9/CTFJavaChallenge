package org.h2.mvstore;

import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/WriteBuffer.class */
public class WriteBuffer {
    private static final int MAX_REUSE_CAPACITY = 4194304;
    private static final int MIN_GROW = 1048576;
    private ByteBuffer reuse;
    private ByteBuffer buff;

    public WriteBuffer(int i) {
        this.reuse = ByteBuffer.allocate(i);
        this.buff = this.reuse;
    }

    public WriteBuffer() {
        this(1048576);
    }

    public WriteBuffer putVarInt(int i) {
        DataUtils.writeVarInt(ensureCapacity(5), i);
        return this;
    }

    public WriteBuffer putVarLong(long j) {
        DataUtils.writeVarLong(ensureCapacity(10), j);
        return this;
    }

    public WriteBuffer putStringData(String str, int i) {
        DataUtils.writeStringData(ensureCapacity(3 * i), str, i);
        return this;
    }

    public WriteBuffer put(byte b) {
        ensureCapacity(1).put(b);
        return this;
    }

    public WriteBuffer putChar(char c) {
        ensureCapacity(2).putChar(c);
        return this;
    }

    public WriteBuffer putShort(short s) {
        ensureCapacity(2).putShort(s);
        return this;
    }

    public WriteBuffer putInt(int i) {
        ensureCapacity(4).putInt(i);
        return this;
    }

    public WriteBuffer putLong(long j) {
        ensureCapacity(8).putLong(j);
        return this;
    }

    public WriteBuffer putFloat(float f) {
        ensureCapacity(4).putFloat(f);
        return this;
    }

    public WriteBuffer putDouble(double d) {
        ensureCapacity(8).putDouble(d);
        return this;
    }

    public WriteBuffer put(byte[] bArr) {
        ensureCapacity(bArr.length).put(bArr);
        return this;
    }

    public WriteBuffer put(byte[] bArr, int i, int i2) {
        ensureCapacity(i2).put(bArr, i, i2);
        return this;
    }

    public WriteBuffer put(ByteBuffer byteBuffer) {
        ensureCapacity(byteBuffer.remaining()).put(byteBuffer);
        return this;
    }

    public WriteBuffer limit(int i) {
        ensureCapacity(i - this.buff.position()).limit(i);
        return this;
    }

    public int capacity() {
        return this.buff.capacity();
    }

    public WriteBuffer position(int i) {
        this.buff.position(i);
        return this;
    }

    public int limit() {
        return this.buff.limit();
    }

    public int position() {
        return this.buff.position();
    }

    public WriteBuffer get(byte[] bArr) {
        this.buff.get(bArr);
        return this;
    }

    public WriteBuffer putInt(int i, int i2) {
        this.buff.putInt(i, i2);
        return this;
    }

    public WriteBuffer putShort(int i, short s) {
        this.buff.putShort(i, s);
        return this;
    }

    public WriteBuffer clear() {
        if (this.buff.limit() > 4194304) {
            this.buff = this.reuse;
        } else if (this.buff != this.reuse) {
            this.reuse = this.buff;
        }
        this.buff.clear();
        return this;
    }

    public ByteBuffer getBuffer() {
        return this.buff;
    }

    private ByteBuffer ensureCapacity(int i) {
        if (this.buff.remaining() < i) {
            grow(i);
        }
        return this.buff;
    }

    private void grow(int i) {
        ByteBuffer byteBuffer = this.buff;
        int remaining = i - byteBuffer.remaining();
        int min = (int) Math.min(2147483647L, byteBuffer.capacity() + Math.max(byteBuffer.capacity() / 2, Math.max(remaining, 1048576)));
        if (min < remaining) {
            throw new OutOfMemoryError("Capacity: " + min + " needed: " + remaining);
        }
        try {
            this.buff = ByteBuffer.allocate(min);
            byteBuffer.flip();
            this.buff.put(byteBuffer);
            if (min <= 4194304) {
                this.reuse = this.buff;
            }
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Capacity: " + min);
        }
    }
}
