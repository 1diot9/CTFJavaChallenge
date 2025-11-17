package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/NettyDataBuffer.class */
public class NettyDataBuffer implements PooledDataBuffer {
    private ByteBuf byteBuf;
    private final NettyDataBufferFactory dataBufferFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NettyDataBuffer(ByteBuf byteBuf, NettyDataBufferFactory dataBufferFactory) {
        Assert.notNull(byteBuf, "ByteBuf must not be null");
        Assert.notNull(dataBufferFactory, "NettyDataBufferFactory must not be null");
        this.byteBuf = byteBuf;
        this.dataBufferFactory = dataBufferFactory;
    }

    public ByteBuf getNativeBuffer() {
        return this.byteBuf;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.byteBuf.writerIndex()) {
            return -1;
        }
        int length = this.byteBuf.writerIndex() - fromIndex;
        IntPredicate negate = predicate.negate();
        Objects.requireNonNull(negate);
        return this.byteBuf.forEachByte(fromIndex, length, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            return -1;
        }
        int fromIndex2 = Math.min(fromIndex, this.byteBuf.writerIndex() - 1);
        IntPredicate negate = predicate.negate();
        Objects.requireNonNull(negate);
        return this.byteBuf.forEachByteDesc(0, fromIndex2 + 1, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.byteBuf.readableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.byteBuf.writableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.byteBuf.readerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer readPosition(int readPosition) {
        this.byteBuf.readerIndex(readPosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.byteBuf.writerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer writePosition(int writePosition) {
        this.byteBuf.writerIndex(writePosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        return this.byteBuf.getByte(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.byteBuf.capacity();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public NettyDataBuffer capacity(int capacity) {
        this.byteBuf.capacity(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer ensureWritable(int capacity) {
        this.byteBuf.ensureWritable(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        return this.byteBuf.readByte();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination) {
        this.byteBuf.readBytes(destination);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination, int offset, int length) {
        this.byteBuf.readBytes(destination, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte b) {
        this.byteBuf.writeByte(b);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source) {
        this.byteBuf.writeBytes(source);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source, int offset, int length) {
        this.byteBuf.writeBytes(source, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(DataBuffer... dataBuffers) {
        if (!ObjectUtils.isEmpty((Object[]) dataBuffers)) {
            if (hasNettyDataBuffers(dataBuffers)) {
                ByteBuf[] nativeBuffers = new ByteBuf[dataBuffers.length];
                for (int i = 0; i < dataBuffers.length; i++) {
                    nativeBuffers[i] = ((NettyDataBuffer) dataBuffers[i]).getNativeBuffer();
                }
                write(nativeBuffers);
            } else {
                ByteBuffer[] byteBuffers = new ByteBuffer[dataBuffers.length];
                for (int i2 = 0; i2 < dataBuffers.length; i2++) {
                    byteBuffers[i2] = ByteBuffer.allocate(dataBuffers[i2].readableByteCount());
                    dataBuffers[i2].toByteBuffer(byteBuffers[i2]);
                }
                write(byteBuffers);
            }
        }
        return this;
    }

    private static boolean hasNettyDataBuffers(DataBuffer[] buffers) {
        for (DataBuffer buffer : buffers) {
            if (!(buffer instanceof NettyDataBuffer)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(ByteBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            for (ByteBuffer buffer : buffers) {
                this.byteBuf.writeBytes(buffer);
            }
        }
        return this;
    }

    public NettyDataBuffer write(ByteBuf... byteBufs) {
        if (!ObjectUtils.isEmpty((Object[]) byteBufs)) {
            for (ByteBuf byteBuf : byteBufs) {
                this.byteBuf.writeBytes(byteBuf);
            }
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(CharSequence charSequence, Charset charset) {
        Assert.notNull(charSequence, "CharSequence must not be null");
        Assert.notNull(charset, "Charset must not be null");
        if (StandardCharsets.UTF_8.equals(charset)) {
            ByteBufUtil.writeUtf8(this.byteBuf, charSequence);
        } else if (StandardCharsets.US_ASCII.equals(charset)) {
            ByteBufUtil.writeAscii(this.byteBuf, charSequence);
        } else {
            return super.write(charSequence, charset);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public NettyDataBuffer slice(int index, int length) {
        ByteBuf slice = this.byteBuf.slice(index, length);
        return new NettyDataBuffer(slice, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public NettyDataBuffer retainedSlice(int index, int length) {
        ByteBuf slice = this.byteBuf.retainedSlice(index, length);
        return new NettyDataBuffer(slice, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer split(int index) {
        ByteBuf split = this.byteBuf.retainedSlice(0, index);
        int writerIndex = this.byteBuf.writerIndex();
        int readerIndex = this.byteBuf.readerIndex();
        split.writerIndex(Math.min(writerIndex, index));
        split.readerIndex(Math.min(readerIndex, index));
        this.byteBuf = this.byteBuf.slice(index, this.byteBuf.capacity() - index);
        this.byteBuf.writerIndex(Math.max(writerIndex, index) - index);
        this.byteBuf.readerIndex(Math.max(readerIndex, index) - index);
        return new NettyDataBuffer(split, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer() {
        return this.byteBuf.nioBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer(int index, int length) {
        return this.byteBuf.nioBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer toByteBuffer(int index, int length) {
        ByteBuffer allocate;
        if (this.byteBuf.isDirect()) {
            allocate = ByteBuffer.allocateDirect(length);
        } else {
            allocate = ByteBuffer.allocate(length);
        }
        ByteBuffer result = allocate;
        this.byteBuf.getBytes(index, result);
        return result.flip();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length) {
        Assert.notNull(dest, "Dest must not be null");
        dest.duplicate().clear().put(destPos, this.byteBuf.nioBuffer(srcPos, length), 0, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator readableByteBuffers() {
        ByteBuffer[] readable = this.byteBuf.nioBuffers(this.byteBuf.readerIndex(), this.byteBuf.readableBytes());
        return new ByteBufferIterator(readable, true);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator writableByteBuffers() {
        ByteBuffer[] writable = this.byteBuf.nioBuffers(this.byteBuf.writerIndex(), this.byteBuf.writableBytes());
        return new ByteBufferIterator(writable, false);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return this.byteBuf.toString(charset);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(int index, int length, Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return this.byteBuf.toString(index, length, charset);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean isAllocated() {
        return this.byteBuf.refCnt() > 0;
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public PooledDataBuffer retain() {
        return new NettyDataBuffer(this.byteBuf.retain(), this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer, org.springframework.core.io.buffer.TouchableDataBuffer
    public PooledDataBuffer touch(Object hint) {
        this.byteBuf.touch(hint);
        return this;
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean release() {
        return this.byteBuf.release();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof NettyDataBuffer) {
                NettyDataBuffer that = (NettyDataBuffer) other;
                if (this.byteBuf.equals(that.byteBuf)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    public String toString() {
        return this.byteBuf.toString();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/NettyDataBuffer$ByteBufferIterator.class */
    private static final class ByteBufferIterator implements DataBuffer.ByteBufferIterator {
        private final ByteBuffer[] byteBuffers;
        private final boolean readOnly;
        private int cursor = 0;

        public ByteBufferIterator(ByteBuffer[] byteBuffers, boolean readOnly) {
            this.byteBuffers = byteBuffers;
            this.readOnly = readOnly;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor < this.byteBuffers.length;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ByteBuffer next() {
            int index = this.cursor;
            if (index < this.byteBuffers.length) {
                this.cursor = index + 1;
                ByteBuffer next = this.byteBuffers[index];
                return this.readOnly ? next.asReadOnlyBuffer() : next;
            }
            throw new NoSuchElementException();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer.ByteBufferIterator, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }
}
