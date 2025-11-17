package org.apache.tomcat.websocket;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperNonSecure.class */
public class AsyncChannelWrapperNonSecure implements AsyncChannelWrapper {
    private static final Future<Void> NOOP_FUTURE = new NoOpFuture();
    private final AsynchronousSocketChannel socketChannel;

    public AsyncChannelWrapperNonSecure(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Integer> read(ByteBuffer dst) {
        return this.socketChannel.read(dst);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public <B, A extends B> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, B> handler) {
        this.socketChannel.read(dst, attachment, handler);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Integer> write(ByteBuffer src) {
        return this.socketChannel.write(src);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public <B, A extends B> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, B> handler) {
        this.socketChannel.write(srcs, offset, length, timeout, unit, attachment, handler);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public void close() {
        try {
            this.socketChannel.close();
        } catch (IOException e) {
        }
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Void> handshake() {
        return NOOP_FUTURE;
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public SocketAddress getLocalAddress() throws IOException {
        return this.socketChannel.getLocalAddress();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperNonSecure$NoOpFuture.class */
    private static final class NoOpFuture implements Future<Void> {
        private NoOpFuture() {
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            return false;
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}
