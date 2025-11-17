package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DefaultDataBuffer.class */
public class DefaultDataBuffer implements DataBuffer {
    private static final int MAX_CAPACITY = Integer.MAX_VALUE;
    private static final int CAPACITY_THRESHOLD = 4194304;
    private final DefaultDataBufferFactory dataBufferFactory;
    private ByteBuffer byteBuffer;
    private int capacity;
    private int readPosition;
    private int writePosition;

    private DefaultDataBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        Assert.notNull(dataBufferFactory, "DefaultDataBufferFactory must not be null");
        Assert.notNull(byteBuffer, "ByteBuffer must not be null");
        this.dataBufferFactory = dataBufferFactory;
        ByteBuffer slice = byteBuffer.slice();
        this.byteBuffer = slice;
        this.capacity = slice.remaining();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DefaultDataBuffer fromFilledByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        DefaultDataBuffer dataBuffer = new DefaultDataBuffer(dataBufferFactory, byteBuffer);
        dataBuffer.writePosition(byteBuffer.remaining());
        return dataBuffer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DefaultDataBuffer fromEmptyByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        return new DefaultDataBuffer(dataBufferFactory, byteBuffer);
    }

    public ByteBuffer getNativeBuffer() {
        this.byteBuffer.position(this.readPosition);
        this.byteBuffer.limit(readableByteCount());
        return this.byteBuffer;
    }

    private void setNativeBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        this.capacity = byteBuffer.remaining();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.writePosition) {
            return -1;
        }
        for (int i = fromIndex; i < this.writePosition; i++) {
            byte b = this.byteBuffer.get(i);
            if (predicate.test(b)) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "IntPredicate must not be null");
        for (int i = Math.min(fromIndex, this.writePosition - 1); i >= 0; i--) {
            byte b = this.byteBuffer.get(i);
            if (predicate.test(b)) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.writePosition - this.readPosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.capacity - this.writePosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.readPosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer readPosition(int readPosition) {
        assertIndex(readPosition >= 0, "'readPosition' %d must be >= 0", Integer.valueOf(readPosition));
        assertIndex(readPosition <= this.writePosition, "'readPosition' %d must be <= %d", Integer.valueOf(readPosition), Integer.valueOf(this.writePosition));
        this.readPosition = readPosition;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.writePosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer writePosition(int writePosition) {
        assertIndex(writePosition >= this.readPosition, "'writePosition' %d must be >= %d", Integer.valueOf(writePosition), Integer.valueOf(this.readPosition));
        assertIndex(writePosition <= this.capacity, "'writePosition' %d must be <= %d", Integer.valueOf(writePosition), Integer.valueOf(this.capacity));
        this.writePosition = writePosition;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.capacity;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer capacity(int capacity) {
        setCapacity(capacity);
        return this;
    }

    private void setCapacity(int newCapacity) {
        if (newCapacity < 0) {
            throw new IllegalArgumentException(String.format("'newCapacity' %d must be 0 or higher", Integer.valueOf(newCapacity)));
        }
        int readPosition = readPosition();
        int writePosition = writePosition();
        int oldCapacity = capacity();
        if (newCapacity > oldCapacity) {
            ByteBuffer oldBuffer = this.byteBuffer;
            ByteBuffer newBuffer = allocate(newCapacity, oldBuffer.isDirect());
            oldBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            setNativeBuffer(newBuffer);
            return;
        }
        if (newCapacity < oldCapacity) {
            ByteBuffer oldBuffer2 = this.byteBuffer;
            ByteBuffer newBuffer2 = allocate(newCapacity, oldBuffer2.isDirect());
            if (readPosition < newCapacity) {
                if (writePosition > newCapacity) {
                    writePosition = newCapacity;
                    writePosition(writePosition);
                }
                oldBuffer2.position(readPosition).limit(writePosition);
                newBuffer2.position(readPosition).limit(writePosition);
                newBuffer2.put(oldBuffer2);
                newBuffer2.clear();
            } else {
                readPosition(newCapacity);
                writePosition(newCapacity);
            }
            setNativeBuffer(newBuffer2);
        }
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer ensureWritable(int length) {
        if (length > writableByteCount()) {
            int newCapacity = calculateCapacity(this.writePosition + length);
            setCapacity(newCapacity);
        }
        return this;
    }

    private static ByteBuffer allocate(int capacity, boolean direct) {
        return direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        assertIndex(index >= 0, "index %d must be >= 0", Integer.valueOf(index));
        assertIndex(index <= this.writePosition - 1, "index %d must be <= %d", Integer.valueOf(index), Integer.valueOf(this.writePosition - 1));
        return this.byteBuffer.get(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        assertIndex(this.readPosition <= this.writePosition - 1, "readPosition %d must be <= %d", Integer.valueOf(this.readPosition), Integer.valueOf(this.writePosition - 1));
        int pos = this.readPosition;
        byte b = this.byteBuffer.get(pos);
        this.readPosition = pos + 1;
        return b;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer read(byte[] destination) {
        Assert.notNull(destination, "Byte array must not be null");
        read(destination, 0, destination.length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer read(byte[] destination, int offset, int length) {
        Assert.notNull(destination, "Byte array must not be null");
        assertIndex(this.readPosition <= this.writePosition - length, "readPosition %d and length %d should be smaller than writePosition %d", Integer.valueOf(this.readPosition), Integer.valueOf(length), Integer.valueOf(this.writePosition));
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.readPosition + length;
        tmp.clear().position(this.readPosition).limit(limit);
        tmp.get(destination, offset, length);
        this.readPosition += length;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte b) {
        ensureWritable(1);
        int pos = this.writePosition;
        this.byteBuffer.put(pos, b);
        this.writePosition = pos + 1;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte[] source) {
        Assert.notNull(source, "Byte array must not be null");
        write(source, 0, source.length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte[] source, int offset, int length) {
        Assert.notNull(source, "Byte array must not be null");
        ensureWritable(length);
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + length;
        tmp.clear().position(this.writePosition).limit(limit);
        tmp.put(source, offset, length);
        this.writePosition += length;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(DataBuffer... dataBuffers) {
        if (!ObjectUtils.isEmpty((Object[]) dataBuffers)) {
            ByteBuffer[] byteBuffers = new ByteBuffer[dataBuffers.length];
            for (int i = 0; i < dataBuffers.length; i++) {
                byteBuffers[i] = ByteBuffer.allocate(dataBuffers[i].readableByteCount());
                dataBuffers[i].toByteBuffer(byteBuffers[i]);
            }
            write(byteBuffers);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(ByteBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            int capacity = Arrays.stream(buffers).mapToInt((v0) -> {
                return v0.remaining();
            }).sum();
            ensureWritable(capacity);
            Arrays.stream(buffers).forEach(this::write);
        }
        return this;
    }

    private void write(ByteBuffer source) {
        int length = source.remaining();
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + source.remaining();
        tmp.clear().position(this.writePosition).limit(limit);
        tmp.put(source);
        this.writePosition += length;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DefaultDataBuffer slice(int index, int length) {
        checkIndex(index, length);
        int oldPosition = this.byteBuffer.position();
        try {
            this.byteBuffer.position(index);
            ByteBuffer slice = this.byteBuffer.slice();
            slice.limit(length);
            SlicedDefaultDataBuffer slicedDefaultDataBuffer = new SlicedDefaultDataBuffer(slice, this.dataBufferFactory, length);
            this.byteBuffer.position(oldPosition);
            return slicedDefaultDataBuffer;
        } catch (Throwable th) {
            this.byteBuffer.position(oldPosition);
            throw th;
        }
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer split(int index) {
        checkIndex(index);
        ByteBuffer split = this.byteBuffer.duplicate().clear().position(0).limit(index).slice();
        DefaultDataBuffer result = new DefaultDataBuffer(this.dataBufferFactory, split);
        result.writePosition = Math.min(this.writePosition, index);
        result.readPosition = Math.min(this.readPosition, index);
        this.byteBuffer = this.byteBuffer.duplicate().clear().position(index).limit(this.byteBuffer.capacity()).slice();
        this.writePosition = Math.max(this.writePosition, index) - index;
        this.readPosition = Math.max(this.readPosition, index) - index;
        this.capacity = this.byteBuffer.capacity();
        return result;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer() {
        return asByteBuffer(this.readPosition, readableByteCount());
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer(int index, int length) {
        checkIndex(index, length);
        ByteBuffer duplicate = this.byteBuffer.duplicate();
        duplicate.position(index);
        duplicate.limit(index + length);
        return duplicate.slice();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer toByteBuffer(int index, int length) {
        checkIndex(index, length);
        ByteBuffer copy = allocate(length, this.byteBuffer.isDirect());
        ByteBuffer readOnly = this.byteBuffer.asReadOnlyBuffer();
        readOnly.clear().position(index).limit(index + length);
        copy.put(readOnly);
        return copy.flip();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length) {
        checkIndex(srcPos, length);
        Assert.notNull(dest, "Dest must not be null");
        dest.duplicate().clear().put(destPos, this.byteBuffer, srcPos, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator readableByteBuffers() {
        ByteBuffer readOnly = this.byteBuffer.slice(this.readPosition, readableByteCount()).asReadOnlyBuffer();
        return new ByteBufferIterator(readOnly);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator writableByteBuffers() {
        ByteBuffer slice = this.byteBuffer.slice(this.writePosition, writableByteCount());
        return new ByteBufferIterator(slice);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(int index, int length, Charset charset) {
        byte[] bytes;
        int offset;
        checkIndex(index, length);
        Assert.notNull(charset, "Charset must not be null");
        if (this.byteBuffer.hasArray()) {
            bytes = this.byteBuffer.array();
            offset = this.byteBuffer.arrayOffset() + index;
        } else {
            bytes = new byte[length];
            offset = 0;
            ByteBuffer duplicate = this.byteBuffer.duplicate();
            duplicate.clear().position(index).limit(index + length);
            duplicate.get(bytes, 0, length);
        }
        return new String(bytes, offset, length, charset);
    }

    private int calculateCapacity(int neededCapacity) {
        int newCapacity;
        Assert.isTrue(neededCapacity >= 0, "'neededCapacity' must be >= 0");
        if (neededCapacity == 4194304) {
            return 4194304;
        }
        if (neededCapacity > 4194304) {
            int newCapacity2 = (neededCapacity / 4194304) * 4194304;
            if (newCapacity2 > 2143289343) {
                newCapacity = Integer.MAX_VALUE;
            } else {
                newCapacity = newCapacity2 + 4194304;
            }
            return newCapacity;
        }
        int i = 64;
        while (true) {
            int newCapacity3 = i;
            if (newCapacity3 < neededCapacity) {
                i = newCapacity3 << 1;
            } else {
                return Math.min(newCapacity3, Integer.MAX_VALUE);
            }
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof DefaultDataBuffer) {
                DefaultDataBuffer that = (DefaultDataBuffer) other;
                if (this.readPosition != that.readPosition || this.writePosition != that.writePosition || !this.byteBuffer.equals(that.byteBuffer)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.byteBuffer.hashCode();
    }

    public String toString() {
        return String.format("DefaultDataBuffer (r: %d, w: %d, c: %d)", Integer.valueOf(this.readPosition), Integer.valueOf(this.writePosition), Integer.valueOf(this.capacity));
    }

    private void checkIndex(int index, int length) {
        checkIndex(index);
        checkLength(length);
    }

    private void checkIndex(int index) {
        assertIndex(index >= 0, "index %d must be >= 0", Integer.valueOf(index));
        assertIndex(index <= this.capacity, "index %d must be <= %d", Integer.valueOf(index), Integer.valueOf(this.capacity));
    }

    private void checkLength(int length) {
        assertIndex(length >= 0, "length %d must be >= 0", Integer.valueOf(length));
        assertIndex(length <= this.capacity, "length %d must be <= %d", Integer.valueOf(length), Integer.valueOf(this.capacity));
    }

    private void assertIndex(boolean expression, String format, Object... args) {
        if (!expression) {
            String message = String.format(format, args);
            throw new IndexOutOfBoundsException(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DefaultDataBuffer$SlicedDefaultDataBuffer.class */
    public static class SlicedDefaultDataBuffer extends DefaultDataBuffer {
        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        @Deprecated
        public /* bridge */ /* synthetic */ DataBuffer slice(int index, int length) {
            return super.slice(index, length);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(ByteBuffer[] buffers) {
            return super.write(buffers);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(DataBuffer[] dataBuffers) {
            return super.write(dataBuffers);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte[] source, int offset, int length) {
            return super.write(source, offset, length);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte[] source) {
            return super.write(source);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte b) {
            return super.write(b);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer read(byte[] destination, int offset, int length) {
            return super.read(destination, offset, length);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer read(byte[] destination) {
            return super.read(destination);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer writePosition(int writePosition) {
            return super.writePosition(writePosition);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer readPosition(int readPosition) {
            return super.readPosition(readPosition);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBufferFactory factory() {
            return super.factory();
        }

        SlicedDefaultDataBuffer(ByteBuffer byteBuffer, DefaultDataBufferFactory dataBufferFactory, int length) {
            super(dataBufferFactory, byteBuffer);
            writePosition(length);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public DefaultDataBuffer capacity(int newCapacity) {
            throw new UnsupportedOperationException("Changing the capacity of a sliced buffer is not supported");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DefaultDataBuffer$ByteBufferIterator.class */
    private static final class ByteBufferIterator implements DataBuffer.ByteBufferIterator {
        private final ByteBuffer buffer;
        private boolean hasNext = true;

        public ByteBufferIterator(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.hasNext;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public ByteBuffer next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            this.hasNext = false;
            return this.buffer;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer.ByteBufferIterator, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }
}
