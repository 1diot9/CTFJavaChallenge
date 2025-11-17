package org.springframework.http.client.reactive;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.io.Content;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.io.buffer.TouchableDataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyClientHttpConnector.class */
public class JettyClientHttpConnector implements ClientHttpConnector {
    private final HttpClient httpClient;
    private DataBufferFactory bufferFactory;

    public JettyClientHttpConnector() {
        this(new HttpClient());
    }

    public JettyClientHttpConnector(HttpClient httpClient) {
        this(httpClient, (JettyResourceFactory) null);
    }

    public JettyClientHttpConnector(HttpClient httpClient, @Nullable JettyResourceFactory resourceFactory) {
        this.bufferFactory = DefaultDataBufferFactory.sharedInstance;
        Assert.notNull(httpClient, "HttpClient is required");
        if (resourceFactory != null) {
            httpClient.setExecutor(resourceFactory.getExecutor());
            httpClient.setByteBufferPool(resourceFactory.getByteBufferPool());
            httpClient.setScheduler(resourceFactory.getScheduler());
        }
        this.httpClient = httpClient;
    }

    @Deprecated
    public JettyClientHttpConnector(JettyResourceFactory resourceFactory, @Nullable Consumer<HttpClient> customizer) {
        this(new HttpClient(), resourceFactory);
        if (customizer != null) {
            customizer.accept(this.httpClient);
        }
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        this.bufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        if (!uri.isAbsolute()) {
            return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
        }
        if (!this.httpClient.isStarted()) {
            try {
                this.httpClient.start();
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        }
        Request jettyRequest = this.httpClient.newRequest(uri).method(method.toString());
        JettyClientHttpRequest request = new JettyClientHttpRequest(jettyRequest, this.bufferFactory);
        return requestCallback.apply(request).then(execute(request));
    }

    private Mono<ClientHttpResponse> execute(JettyClientHttpRequest request) {
        return Mono.fromDirect(request.toReactiveRequest().response((reactiveResponse, chunkPublisher) -> {
            Flux<DataBuffer> content = Flux.from(chunkPublisher).map(this::toDataBuffer);
            return Mono.just(new JettyClientHttpResponse(reactiveResponse, content));
        }));
    }

    private DataBuffer toDataBuffer(Content.Chunk chunk) {
        DataBuffer delegate = this.bufferFactory.wrap(chunk.getByteBuffer());
        return new JettyDataBuffer(delegate, chunk);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyClientHttpConnector$JettyDataBuffer.class */
    private static final class JettyDataBuffer implements PooledDataBuffer {
        private final DataBuffer delegate;
        private final Content.Chunk chunk;
        private final AtomicInteger refCount = new AtomicInteger(1);

        public JettyDataBuffer(DataBuffer delegate, Content.Chunk chunk) {
            Assert.notNull(delegate, "Delegate must not be null");
            Assert.notNull(chunk, "Chunk must not be null");
            this.delegate = delegate;
            this.chunk = chunk;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public boolean isAllocated() {
            return this.refCount.get() > 0;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public PooledDataBuffer retain() {
            DataBuffer dataBuffer = this.delegate;
            if (dataBuffer instanceof PooledDataBuffer) {
                PooledDataBuffer pooledDelegate = (PooledDataBuffer) dataBuffer;
                pooledDelegate.retain();
            }
            this.chunk.retain();
            this.refCount.getAndUpdate(c -> {
                if (c != 0) {
                    return c + 1;
                }
                return 0;
            });
            return this;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer
        public boolean release() {
            DataBuffer dataBuffer = this.delegate;
            if (dataBuffer instanceof PooledDataBuffer) {
                PooledDataBuffer pooledDelegate = (PooledDataBuffer) dataBuffer;
                pooledDelegate.release();
            }
            this.chunk.release();
            int refCount = this.refCount.updateAndGet(c -> {
                if (c != 0) {
                    return c - 1;
                }
                throw new IllegalStateException("already released " + this);
            });
            return refCount == 0;
        }

        @Override // org.springframework.core.io.buffer.PooledDataBuffer, org.springframework.core.io.buffer.TouchableDataBuffer
        public PooledDataBuffer touch(Object hint) {
            DataBuffer dataBuffer = this.delegate;
            if (dataBuffer instanceof TouchableDataBuffer) {
                TouchableDataBuffer touchableDelegate = (TouchableDataBuffer) dataBuffer;
                touchableDelegate.touch(hint);
            }
            return this;
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
            this.delegate.capacity(capacity);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer ensureWritable(int capacity) {
            this.delegate.ensureWritable(capacity);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int readPosition() {
            return this.delegate.readPosition();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer readPosition(int readPosition) {
            this.delegate.readPosition(readPosition);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public int writePosition() {
            return this.delegate.writePosition();
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer writePosition(int writePosition) {
            this.delegate.writePosition(writePosition);
            return this;
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
            this.delegate.read(destination);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer read(byte[] destination, int offset, int length) {
            this.delegate.read(destination, offset, length);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte b) {
            this.delegate.write(b);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte[] source) {
            this.delegate.write(source);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(byte[] source, int offset, int length) {
            this.delegate.write(source, offset, length);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(DataBuffer... buffers) {
            this.delegate.write(buffers);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer write(ByteBuffer... buffers) {
            this.delegate.write(buffers);
            return this;
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        @Deprecated
        public DataBuffer slice(int index, int length) {
            DataBuffer delegateSlice = this.delegate.slice(index, length);
            this.chunk.retain();
            return new JettyDataBuffer(delegateSlice, this.chunk);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer split(int index) {
            DataBuffer delegateSplit = this.delegate.split(index);
            this.chunk.retain();
            return new JettyDataBuffer(delegateSplit, this.chunk);
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
        public ByteBuffer toByteBuffer(int index, int length) {
            return this.delegate.toByteBuffer(index, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public void toByteBuffer(int srcPos, ByteBuffer dest, int destPos, int length) {
            this.delegate.toByteBuffer(srcPos, dest, destPos, length);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer.ByteBufferIterator readableByteBuffers() {
            DataBuffer.ByteBufferIterator delegateIterator = this.delegate.readableByteBuffers();
            return new JettyByteBufferIterator(delegateIterator, this.chunk);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public DataBuffer.ByteBufferIterator writableByteBuffers() {
            DataBuffer.ByteBufferIterator delegateIterator = this.delegate.writableByteBuffers();
            return new JettyByteBufferIterator(delegateIterator, this.chunk);
        }

        @Override // org.springframework.core.io.buffer.DataBuffer
        public String toString(int index, int length, Charset charset) {
            return this.delegate.toString(index, length, charset);
        }

        /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/JettyClientHttpConnector$JettyDataBuffer$JettyByteBufferIterator.class */
        private static final class JettyByteBufferIterator implements DataBuffer.ByteBufferIterator {
            private final DataBuffer.ByteBufferIterator delegate;
            private final Content.Chunk chunk;

            public JettyByteBufferIterator(DataBuffer.ByteBufferIterator delegate, Content.Chunk chunk) {
                Assert.notNull(delegate, "Delegate must not be null");
                Assert.notNull(chunk, "Chunk must not be null");
                this.delegate = delegate;
                this.chunk = chunk;
                this.chunk.retain();
            }

            @Override // org.springframework.core.io.buffer.DataBuffer.ByteBufferIterator, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
                this.delegate.close();
                this.chunk.release();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public ByteBuffer next() {
                return this.delegate.next();
            }
        }
    }
}
