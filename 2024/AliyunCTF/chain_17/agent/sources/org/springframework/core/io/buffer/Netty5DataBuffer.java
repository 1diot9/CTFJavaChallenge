package org.springframework.core.io.buffer;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferComponent;
import io.netty5.buffer.ByteCursor;
import io.netty5.buffer.ComponentIterator;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/Netty5DataBuffer.class */
public final class Netty5DataBuffer implements CloseableDataBuffer, TouchableDataBuffer {
    private final Buffer buffer;
    private final Netty5DataBufferFactory dataBufferFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Netty5DataBuffer(Buffer buffer, Netty5DataBufferFactory dataBufferFactory) {
        Assert.notNull(buffer, "Buffer must not be null");
        Assert.notNull(dataBufferFactory, "Netty5DataBufferFactory must not be null");
        this.buffer = buffer;
        this.dataBufferFactory = dataBufferFactory;
    }

    public Buffer getNativeBuffer() {
        return this.buffer;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.buffer.writerOffset()) {
            return -1;
        }
        int length = this.buffer.writerOffset() - fromIndex;
        ByteCursor openCursor = this.buffer.openCursor(fromIndex, length);
        IntPredicate negate = predicate.negate();
        Objects.requireNonNull(negate);
        int bytes = openCursor.process((v1) -> {
            return r1.test(v1);
        });
        if (bytes == -1) {
            return -1;
        }
        return fromIndex + bytes;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            return -1;
        }
        ByteCursor openCursor = this.buffer.openCursor(0, Math.min(fromIndex, this.buffer.writerOffset() - 1) + 1);
        IntPredicate negate = predicate.negate();
        Objects.requireNonNull(negate);
        return openCursor.process((v1) -> {
            return r1.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.buffer.readableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.buffer.writableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.buffer.readerOffset();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer readPosition(int readPosition) {
        this.buffer.readerOffset(readPosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.buffer.writerOffset();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer writePosition(int writePosition) {
        this.buffer.writerOffset(writePosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        return this.buffer.getByte(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public Netty5DataBuffer capacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(String.format("'newCapacity' %d must be higher than 0", Integer.valueOf(capacity)));
        }
        int diff = capacity - capacity();
        if (diff > 0) {
            this.buffer.ensureWritable(this.buffer.writableBytes() + diff);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer ensureWritable(int capacity) {
        Assert.isTrue(capacity >= 0, "Capacity must be >= 0");
        this.buffer.ensureWritable(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        return this.buffer.readByte();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer read(byte[] destination) {
        return read(destination, 0, destination.length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer read(byte[] destination, int offset, int length) {
        this.buffer.readBytes(destination, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer write(byte b) {
        this.buffer.writeByte(b);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer write(byte[] source) {
        this.buffer.writeBytes(source);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer write(byte[] source, int offset, int length) {
        this.buffer.writeBytes(source, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer write(DataBuffer... dataBuffers) {
        if (!ObjectUtils.isEmpty((Object[]) dataBuffers)) {
            if (hasNetty5DataBuffers(dataBuffers)) {
                Buffer[] nativeBuffers = new Buffer[dataBuffers.length];
                for (int i = 0; i < dataBuffers.length; i++) {
                    nativeBuffers[i] = ((Netty5DataBuffer) dataBuffers[i]).getNativeBuffer();
                }
                return write(nativeBuffers);
            }
            ByteBuffer[] byteBuffers = new ByteBuffer[dataBuffers.length];
            for (int i2 = 0; i2 < dataBuffers.length; i2++) {
                byteBuffers[i2] = ByteBuffer.allocate(dataBuffers[i2].readableByteCount());
                dataBuffers[i2].toByteBuffer(byteBuffers[i2]);
            }
            return write(byteBuffers);
        }
        return this;
    }

    private static boolean hasNetty5DataBuffers(DataBuffer[] buffers) {
        for (DataBuffer buffer : buffers) {
            if (!(buffer instanceof Netty5DataBuffer)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public Netty5DataBuffer write(ByteBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            for (ByteBuffer buffer : buffers) {
                this.buffer.writeBytes(buffer);
            }
        }
        return this;
    }

    public Netty5DataBuffer write(Buffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            for (Buffer buffer : buffers) {
                this.buffer.writeBytes(buffer);
            }
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(CharSequence charSequence, Charset charset) {
        Assert.notNull(charSequence, "CharSequence must not be null");
        Assert.notNull(charset, "Charset must not be null");
        this.buffer.writeCharSequence(charSequence, charset);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer slice(int index, int length) {
        Buffer copy = this.buffer.copy(index, length);
        return new Netty5DataBuffer(copy, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer split(int index) {
        Buffer split = this.buffer.split(index);
        return new Netty5DataBuffer(split, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer() {
        return toByteBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer(int index, int length) {
        return toByteBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer toByteBuffer(int index, int length) {
        ByteBuffer allocate;
        if (this.buffer.isDirect()) {
            allocate = ByteBuffer.allocateDirect(length);
        } else {
            allocate = ByteBuffer.allocate(length);
        }
        ByteBuffer copy = allocate;
        this.buffer.copyInto(index, copy, 0, length);
        return copy;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length) {
        this.buffer.copyInto(srcPos, dest, destPos, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator readableByteBuffers() {
        return new BufferComponentIterator(this.buffer.forEachComponent(), true);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator writableByteBuffers() {
        return new BufferComponentIterator(this.buffer.forEachComponent(), false);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return this.buffer.toString(charset);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(int index, int length, Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        byte[] data = new byte[length];
        this.buffer.copyInto(index, data, 0, length);
        return new String(data, 0, length, charset);
    }

    @Override // org.springframework.core.io.buffer.TouchableDataBuffer
    public Netty5DataBuffer touch(Object hint) {
        this.buffer.touch(hint);
        return this;
    }

    @Override // org.springframework.core.io.buffer.CloseableDataBuffer, java.lang.AutoCloseable
    public void close() {
        this.buffer.close();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof Netty5DataBuffer) {
                Netty5DataBuffer that = (Netty5DataBuffer) other;
                if (this.buffer.equals(that.buffer)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.buffer.hashCode();
    }

    public String toString() {
        return this.buffer.toString();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/Netty5DataBuffer$BufferComponentIterator.class */
    private static final class BufferComponentIterator<T extends BufferComponent & ComponentIterator.Next> implements DataBuffer.ByteBufferIterator {
        private final ComponentIterator<T> delegate;
        private final boolean readable;

        @Nullable
        private T next;

        public BufferComponentIterator(ComponentIterator<T> componentIterator, boolean z) {
            Assert.notNull(componentIterator, "Delegate must not be null");
            this.delegate = componentIterator;
            this.readable = z;
            this.next = z ? (T) this.delegate.firstReadable() : (T) this.delegate.firstWritable();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ByteBuffer next() {
            ByteBuffer writableBuffer;
            if (this.next != null) {
                if (this.readable) {
                    writableBuffer = this.next.readableBuffer();
                    this.next = (T) this.next.nextReadable();
                } else {
                    writableBuffer = this.next.writableBuffer();
                    this.next = (T) this.next.nextWritable();
                }
                return writableBuffer;
            }
            throw new NoSuchElementException();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer.ByteBufferIterator, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.delegate.close();
        }
    }
}
