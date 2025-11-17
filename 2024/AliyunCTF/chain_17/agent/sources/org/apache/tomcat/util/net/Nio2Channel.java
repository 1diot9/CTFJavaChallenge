package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Channel.class */
public class Nio2Channel implements AsynchronousByteChannel {
    protected final SocketBufferHandler bufHandler;
    protected AsynchronousSocketChannel sc = null;
    protected SocketWrapperBase<Nio2Channel> socketWrapper = null;
    private ApplicationBufferHandler appReadBufHandler;
    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    private static final Future<Boolean> DONE = new Future<Boolean>() { // from class: org.apache.tomcat.util.net.Nio2Channel.1
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
        public Boolean get() throws InterruptedException, ExecutionException {
            return Boolean.TRUE;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return Boolean.TRUE;
        }
    };
    private static final Future<Integer> DONE_INT = new Future<Integer>() { // from class: org.apache.tomcat.util.net.Nio2Channel.2
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
        public Integer get() throws InterruptedException, ExecutionException {
            return -1;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return -1;
        }
    };
    static final Nio2Channel CLOSED_NIO2_CHANNEL = new Nio2Channel(SocketBufferHandler.EMPTY) { // from class: org.apache.tomcat.util.net.Nio2Channel.3
        @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.Channel
        public boolean isOpen() {
            return false;
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socket) throws IOException {
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public void free() {
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        protected ApplicationBufferHandler getAppReadBufHandler() {
            return ApplicationBufferHandler.EMPTY;
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousByteChannel
        public Future<Integer> read(ByteBuffer dst) {
            return Nio2Channel.DONE_INT;
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousByteChannel
        public Future<Integer> write(ByteBuffer src) {
            return Nio2Channel.DONE_INT;
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override // org.apache.tomcat.util.net.Nio2Channel
        public String toString() {
            return "Closed Nio2Channel";
        }
    };

    public Nio2Channel(SocketBufferHandler bufHandler) {
        this.bufHandler = bufHandler;
    }

    public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socketWrapper) throws IOException {
        this.sc = channel;
        this.socketWrapper = socketWrapper;
        this.bufHandler.reset();
    }

    public void free() {
        this.bufHandler.free();
    }

    SocketWrapperBase<Nio2Channel> getSocketWrapper() {
        return this.socketWrapper;
    }

    @Override // java.nio.channels.AsynchronousChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.sc.close();
    }

    public void close(boolean force) throws IOException {
        if (isOpen() || force) {
            close();
        }
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.sc.isOpen();
    }

    public SocketBufferHandler getBufHandler() {
        return this.bufHandler;
    }

    public AsynchronousSocketChannel getIOChannel() {
        return this.sc;
    }

    public boolean isClosing() {
        return false;
    }

    public boolean isHandshakeComplete() {
        return true;
    }

    public int handshake() throws IOException {
        return 0;
    }

    public String toString() {
        return super.toString() + ":" + this.sc;
    }

    @Override // java.nio.channels.AsynchronousByteChannel
    public Future<Integer> read(ByteBuffer dst) {
        return this.sc.read(dst);
    }

    @Override // java.nio.channels.AsynchronousByteChannel
    public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
        read(dst, 0L, TimeUnit.MILLISECONDS, attachment, handler);
    }

    public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.sc.read(dst, timeout, unit, attachment, handler);
    }

    public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        this.sc.read(dsts, offset, length, timeout, unit, attachment, handler);
    }

    @Override // java.nio.channels.AsynchronousByteChannel
    public Future<Integer> write(ByteBuffer src) {
        return this.sc.write(src);
    }

    @Override // java.nio.channels.AsynchronousByteChannel
    public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
        write(src, 0L, TimeUnit.MILLISECONDS, attachment, handler);
    }

    public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.sc.write(src, timeout, unit, attachment, handler);
    }

    public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        this.sc.write(srcs, offset, length, timeout, unit, attachment, handler);
    }

    public Future<Boolean> flush() {
        return DONE;
    }

    public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        this.appReadBufHandler = handler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ApplicationBufferHandler getAppReadBufHandler() {
        return this.appReadBufHandler;
    }
}
