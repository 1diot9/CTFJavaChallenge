package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.IntPredicate;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferWrapper.class */
public class DataBufferWrapper implements DataBuffer {
    private final DataBuffer delegate;

    public DataBufferWrapper(DataBuffer delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    public DataBuffer dataBuffer() {
        return this.delegate;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBufferFactory factory() {
        return this.delegate.factory();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        return this.delegate.indexOf(predicate, fromIndex);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        return this.delegate.lastIndexOf(predicate, fromIndex);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.delegate.readableByteCount();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.delegate.writableByteCount();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.delegate.capacity();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer capacity(int capacity) {
        return this.delegate.capacity(capacity);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer ensureCapacity(int capacity) {
        return this.delegate.ensureCapacity(capacity);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer ensureWritable(int capacity) {
        return this.delegate.ensureWritable(capacity);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.delegate.readPosition();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer readPosition(int readPosition) {
        return this.delegate.readPosition(readPosition);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.delegate.writePosition();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer writePosition(int writePosition) {
        return this.delegate.writePosition(writePosition);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        return this.delegate.getByte(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        return this.delegate.read();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer read(byte[] destination) {
        return this.delegate.read(destination);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer read(byte[] destination, int offset, int length) {
        return this.delegate.read(destination, offset, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(byte b) {
        return this.delegate.write(b);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(byte[] source) {
        return this.delegate.write(source);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(byte[] source, int offset, int length) {
        return this.delegate.write(source, offset, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(DataBuffer... buffers) {
        return this.delegate.write(buffers);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(ByteBuffer... buffers) {
        return this.delegate.write(buffers);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer write(CharSequence charSequence, Charset charset) {
        return this.delegate.write(charSequence, charset);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer slice(int index, int length) {
        return this.delegate.slice(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public DataBuffer retainedSlice(int index, int length) {
        return this.delegate.retainedSlice(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer split(int index) {
        return this.delegate.split(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer() {
        return this.delegate.asByteBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer asByteBuffer(int index, int length) {
        return this.delegate.asByteBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer toByteBuffer() {
        return this.delegate.toByteBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    @Deprecated
    public ByteBuffer toByteBuffer(int index, int length) {
        return this.delegate.toByteBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public void toByteBuffer(ByteBuffer dest) {
        this.delegate.toByteBuffer(dest);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length) {
        this.delegate.toByteBuffer(srcPos, dest, destPos, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator readableByteBuffers() {
        return this.delegate.readableByteBuffers();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DataBuffer.ByteBufferIterator writableByteBuffers() {
        return this.delegate.writableByteBuffers();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream() {
        return this.delegate.asInputStream();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream(boolean releaseOnClose) {
        return this.delegate.asInputStream(releaseOnClose);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public OutputStream asOutputStream() {
        return this.delegate.asOutputStream();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(Charset charset) {
        return this.delegate.toString(charset);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public String toString(int index, int length, Charset charset) {
        return this.delegate.toString(index, length, charset);
    }
}
