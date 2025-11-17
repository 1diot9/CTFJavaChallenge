package org.springframework.core.io.buffer;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Iterator;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBuffer.class */
public interface DataBuffer {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBuffer$ByteBufferIterator.class */
    public interface ByteBufferIterator extends Iterator<ByteBuffer>, Closeable {
        @Override // java.io.Closeable, java.lang.AutoCloseable
        void close();
    }

    DataBufferFactory factory();

    int indexOf(IntPredicate predicate, int fromIndex);

    int lastIndexOf(IntPredicate predicate, int fromIndex);

    int readableByteCount();

    int writableByteCount();

    int capacity();

    @Deprecated(since = "6.0")
    DataBuffer capacity(int capacity);

    DataBuffer ensureWritable(int capacity);

    int readPosition();

    DataBuffer readPosition(int readPosition);

    int writePosition();

    DataBuffer writePosition(int writePosition);

    byte getByte(int index);

    byte read();

    DataBuffer read(byte[] destination);

    DataBuffer read(byte[] destination, int offset, int length);

    DataBuffer write(byte b);

    DataBuffer write(byte[] source);

    DataBuffer write(byte[] source, int offset, int length);

    DataBuffer write(DataBuffer... buffers);

    DataBuffer write(ByteBuffer... buffers);

    @Deprecated(since = "6.0")
    DataBuffer slice(int index, int length);

    DataBuffer split(int index);

    @Deprecated(since = "6.0")
    ByteBuffer asByteBuffer();

    @Deprecated(since = "6.0")
    ByteBuffer asByteBuffer(int index, int length);

    @Deprecated(since = "6.0.5")
    ByteBuffer toByteBuffer(int index, int length);

    void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length);

    ByteBufferIterator readableByteBuffers();

    ByteBufferIterator writableByteBuffers();

    String toString(int index, int length, Charset charset);

    @Deprecated(since = "6.0")
    default DataBuffer ensureCapacity(int capacity) {
        return ensureWritable(capacity);
    }

    default DataBuffer write(CharSequence charSequence, Charset charset) {
        CoderResult cr;
        Assert.notNull(charSequence, "CharSequence must not be null");
        Assert.notNull(charset, "Charset must not be null");
        if (!charSequence.isEmpty()) {
            CharsetEncoder encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            CharBuffer src = CharBuffer.wrap(charSequence);
            int averageSize = (int) Math.ceil(src.remaining() * encoder.averageBytesPerChar());
            ensureWritable(averageSize);
            while (true) {
                if (src.hasRemaining()) {
                    ByteBufferIterator iterator = writableByteBuffers();
                    try {
                        Assert.state(iterator.hasNext(), "No ByteBuffer available");
                        ByteBuffer dest = iterator.next();
                        cr = encoder.encode(src, dest, true);
                        if (cr.isUnderflow()) {
                            cr = encoder.flush(dest);
                        }
                        writePosition(writePosition() + dest.position());
                        if (iterator != null) {
                            iterator.close();
                        }
                    } catch (Throwable th) {
                        if (iterator != null) {
                            try {
                                iterator.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } else {
                    cr = CoderResult.UNDERFLOW;
                }
                if (cr.isUnderflow()) {
                    break;
                }
                if (cr.isOverflow()) {
                    int maxSize = (int) Math.ceil(src.remaining() * encoder.maxBytesPerChar());
                    ensureWritable(maxSize);
                }
            }
        }
        return this;
    }

    @Deprecated(since = "6.0")
    default DataBuffer retainedSlice(int index, int length) {
        return DataBufferUtils.retain(slice(index, length));
    }

    @Deprecated(since = "6.0.5")
    default ByteBuffer toByteBuffer() {
        return toByteBuffer(readPosition(), readableByteCount());
    }

    default void toByteBuffer(ByteBuffer dest) {
        toByteBuffer(readPosition(), dest, dest.position(), readableByteCount());
    }

    default InputStream asInputStream() {
        return new DataBufferInputStream(this, false);
    }

    default InputStream asInputStream(boolean releaseOnClose) {
        return new DataBufferInputStream(this, releaseOnClose);
    }

    default OutputStream asOutputStream() {
        return new DataBufferOutputStream(this);
    }

    default String toString(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        return toString(readPosition(), readableByteCount(), charset);
    }
}
