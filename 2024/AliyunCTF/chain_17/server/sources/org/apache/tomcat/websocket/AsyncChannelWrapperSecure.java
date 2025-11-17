package org.apache.tomcat.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure.class */
public class AsyncChannelWrapperSecure implements AsyncChannelWrapper {
    private static final StringManager sm = StringManager.getManager((Class<?>) AsyncChannelWrapperSecure.class);
    private static final ByteBuffer DUMMY = ByteBuffer.allocate(16921);
    private final AsynchronousSocketChannel socketChannel;
    private final SSLEngine sslEngine;
    private final ByteBuffer socketReadBuffer;
    private final ByteBuffer socketWriteBuffer;
    private final Log log = LogFactory.getLog((Class<?>) AsyncChannelWrapperSecure.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(2, new SecureIOThreadFactory());
    private AtomicBoolean writing = new AtomicBoolean(false);
    private AtomicBoolean reading = new AtomicBoolean(false);

    public AsyncChannelWrapperSecure(AsynchronousSocketChannel socketChannel, SSLEngine sslEngine) {
        this.socketChannel = socketChannel;
        this.sslEngine = sslEngine;
        int socketBufferSize = sslEngine.getSession().getPacketBufferSize();
        this.socketReadBuffer = ByteBuffer.allocateDirect(socketBufferSize);
        this.socketWriteBuffer = ByteBuffer.allocateDirect(socketBufferSize);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Integer> read(ByteBuffer dst) {
        WrapperFuture<Integer, Void> future = new WrapperFuture<>();
        if (!this.reading.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentRead"));
        }
        ReadTask readTask = new ReadTask(dst, future);
        this.executor.execute(readTask);
        return future;
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public <B, A extends B> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, B> handler) {
        WrapperFuture<Integer, B> future = new WrapperFuture<>(handler, attachment);
        if (!this.reading.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentRead"));
        }
        ReadTask readTask = new ReadTask(dst, future);
        this.executor.execute(readTask);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Integer> write(ByteBuffer src) {
        WrapperFuture<Long, Void> inner = new WrapperFuture<>();
        if (!this.writing.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentWrite"));
        }
        WriteTask writeTask = new WriteTask(new ByteBuffer[]{src}, 0, 1, inner);
        this.executor.execute(writeTask);
        Future<Integer> future = new LongToIntegerFuture(inner);
        return future;
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public <B, A extends B> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, B> handler) {
        WrapperFuture<Long, B> future = new WrapperFuture<>(handler, attachment);
        if (!this.writing.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentWrite"));
        }
        WriteTask writeTask = new WriteTask(srcs, offset, length, future);
        this.executor.execute(writeTask);
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public void close() {
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            this.log.info(sm.getString("asyncChannelWrapperSecure.closeFail"));
        }
        this.executor.shutdownNow();
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public Future<Void> handshake() throws SSLException {
        WrapperFuture<Void, Void> wFuture = new WrapperFuture<>();
        Thread t = new WebSocketSslHandshakeThread(wFuture);
        t.start();
        return wFuture;
    }

    @Override // org.apache.tomcat.websocket.AsyncChannelWrapper
    public SocketAddress getLocalAddress() throws IOException {
        return this.socketChannel.getLocalAddress();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$WriteTask.class */
    private class WriteTask implements Runnable {
        private final ByteBuffer[] srcs;
        private final int offset;
        private final int length;
        private final WrapperFuture<Long, ?> future;

        WriteTask(ByteBuffer[] srcs, int offset, int length, WrapperFuture<Long, ?> future) {
            this.srcs = srcs;
            this.future = future;
            this.offset = offset;
            this.length = length;
        }

        @Override // java.lang.Runnable
        public void run() {
            long written = 0;
            try {
                for (int i = this.offset; i < this.offset + this.length; i++) {
                    ByteBuffer src = this.srcs[i];
                    while (src.hasRemaining()) {
                        AsyncChannelWrapperSecure.this.socketWriteBuffer.clear();
                        SSLEngineResult r = AsyncChannelWrapperSecure.this.sslEngine.wrap(src, AsyncChannelWrapperSecure.this.socketWriteBuffer);
                        written += r.bytesConsumed();
                        SSLEngineResult.Status s = r.getStatus();
                        if (s != SSLEngineResult.Status.OK && s != SSLEngineResult.Status.BUFFER_OVERFLOW) {
                            throw new IllegalStateException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.statusWrap"));
                        }
                        if (r.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            Runnable runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            while (runnable != null) {
                                runnable.run();
                                runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            }
                        }
                        AsyncChannelWrapperSecure.this.socketWriteBuffer.flip();
                        int toWrite = r.bytesProduced();
                        while (toWrite > 0) {
                            Future<Integer> f = AsyncChannelWrapperSecure.this.socketChannel.write(AsyncChannelWrapperSecure.this.socketWriteBuffer);
                            Integer socketWrite = f.get();
                            toWrite -= socketWrite.intValue();
                        }
                    }
                }
                if (AsyncChannelWrapperSecure.this.writing.compareAndSet(true, false)) {
                    this.future.complete(Long.valueOf(written));
                } else {
                    this.future.fail(new IllegalStateException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.wrongStateWrite")));
                }
            } catch (Exception e) {
                AsyncChannelWrapperSecure.this.writing.set(false);
                this.future.fail(e);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$ReadTask.class */
    private class ReadTask implements Runnable {
        private final ByteBuffer dest;
        private final WrapperFuture<Integer, ?> future;

        ReadTask(ByteBuffer dest, WrapperFuture<Integer, ?> future) {
            this.dest = dest;
            this.future = future;
        }

        @Override // java.lang.Runnable
        public void run() {
            int read = 0;
            boolean forceRead = false;
            while (read == 0) {
                try {
                    AsyncChannelWrapperSecure.this.socketReadBuffer.compact();
                    if (forceRead) {
                        forceRead = false;
                        Future<Integer> f = AsyncChannelWrapperSecure.this.socketChannel.read(AsyncChannelWrapperSecure.this.socketReadBuffer);
                        Integer socketRead = f.get();
                        if (socketRead.intValue() == -1) {
                            throw new EOFException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.eof"));
                        }
                    }
                    AsyncChannelWrapperSecure.this.socketReadBuffer.flip();
                    if (AsyncChannelWrapperSecure.this.socketReadBuffer.hasRemaining()) {
                        SSLEngineResult r = AsyncChannelWrapperSecure.this.sslEngine.unwrap(AsyncChannelWrapperSecure.this.socketReadBuffer, this.dest);
                        read += r.bytesProduced();
                        SSLEngineResult.Status s = r.getStatus();
                        if (s != SSLEngineResult.Status.OK) {
                            if (s == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                if (read == 0) {
                                    forceRead = true;
                                }
                            } else if (s == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                                if (AsyncChannelWrapperSecure.this.reading.compareAndSet(true, false)) {
                                    throw new ReadBufferOverflowException(AsyncChannelWrapperSecure.this.sslEngine.getSession().getApplicationBufferSize());
                                }
                                this.future.fail(new IllegalStateException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.wrongStateRead")));
                            } else {
                                throw new IllegalStateException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.statusUnwrap"));
                            }
                        }
                        if (r.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            Runnable runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            while (runnable != null) {
                                runnable.run();
                                runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            }
                        }
                    } else {
                        forceRead = true;
                    }
                } catch (EOFException | InterruptedException | RuntimeException | ExecutionException | SSLException | ReadBufferOverflowException e) {
                    AsyncChannelWrapperSecure.this.reading.set(false);
                    this.future.fail(e);
                    return;
                }
            }
            if (AsyncChannelWrapperSecure.this.reading.compareAndSet(true, false)) {
                this.future.complete(Integer.valueOf(read));
            } else {
                this.future.fail(new IllegalStateException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.wrongStateRead")));
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$WebSocketSslHandshakeThread.class */
    private class WebSocketSslHandshakeThread extends Thread {
        private final WrapperFuture<Void, Void> hFuture;
        private SSLEngineResult.HandshakeStatus handshakeStatus;
        private SSLEngineResult.Status resultStatus;

        WebSocketSslHandshakeThread(WrapperFuture<Void, Void> hFuture) {
            this.hFuture = hFuture;
        }

        /* JADX WARN: Failed to find 'out' block for switch in B:6:0x0045. Please report as an issue. */
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                AsyncChannelWrapperSecure.this.sslEngine.beginHandshake();
                AsyncChannelWrapperSecure.this.socketReadBuffer.position(AsyncChannelWrapperSecure.this.socketReadBuffer.limit());
                this.handshakeStatus = AsyncChannelWrapperSecure.this.sslEngine.getHandshakeStatus();
                this.resultStatus = SSLEngineResult.Status.OK;
                boolean handshaking = true;
                while (handshaking) {
                    switch (AnonymousClass1.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[this.handshakeStatus.ordinal()]) {
                        case 1:
                            AsyncChannelWrapperSecure.this.socketWriteBuffer.clear();
                            checkResult(AsyncChannelWrapperSecure.this.sslEngine.wrap(AsyncChannelWrapperSecure.DUMMY, AsyncChannelWrapperSecure.this.socketWriteBuffer), true);
                            AsyncChannelWrapperSecure.this.socketWriteBuffer.flip();
                            Future<Integer> fWrite = AsyncChannelWrapperSecure.this.socketChannel.write(AsyncChannelWrapperSecure.this.socketWriteBuffer);
                            fWrite.get();
                        case 2:
                            AsyncChannelWrapperSecure.this.socketReadBuffer.compact();
                            if (AsyncChannelWrapperSecure.this.socketReadBuffer.position() == 0 || this.resultStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                Future<Integer> fRead = AsyncChannelWrapperSecure.this.socketChannel.read(AsyncChannelWrapperSecure.this.socketReadBuffer);
                                fRead.get();
                            }
                            AsyncChannelWrapperSecure.this.socketReadBuffer.flip();
                            checkResult(AsyncChannelWrapperSecure.this.sslEngine.unwrap(AsyncChannelWrapperSecure.this.socketReadBuffer, AsyncChannelWrapperSecure.DUMMY), false);
                            break;
                        case 3:
                            while (true) {
                                Runnable r = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                                if (r != null) {
                                    r.run();
                                } else {
                                    this.handshakeStatus = AsyncChannelWrapperSecure.this.sslEngine.getHandshakeStatus();
                                }
                            }
                        case 4:
                            handshaking = false;
                        case 5:
                        case 6:
                            throw new SSLException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.unexpectedHandshakeState", this.handshakeStatus));
                    }
                }
                this.hFuture.complete(null);
            } catch (Exception e) {
                this.hFuture.fail(e);
            }
        }

        private void checkResult(SSLEngineResult result, boolean wrap) throws SSLException {
            this.handshakeStatus = result.getHandshakeStatus();
            this.resultStatus = result.getStatus();
            if (this.resultStatus != SSLEngineResult.Status.OK && (wrap || this.resultStatus != SSLEngineResult.Status.BUFFER_UNDERFLOW)) {
                throw new SSLException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.check.notOk", this.resultStatus));
            }
            if (wrap && result.bytesConsumed() != 0) {
                throw new SSLException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.check.wrap"));
            }
            if (!wrap && result.bytesProduced() != 0) {
                throw new SSLException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.check.unwrap"));
            }
        }
    }

    /* renamed from: org.apache.tomcat.websocket.AsyncChannelWrapperSecure$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus = new int[SSLEngineResult.HandshakeStatus.values().length];

        static {
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_WRAP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_UNWRAP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_TASK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.FINISHED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_UNWRAP_AGAIN.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$WrapperFuture.class */
    private static class WrapperFuture<T, A> implements Future<T> {
        private final CompletionHandler<T, A> handler;
        private final A attachment;
        private volatile T result;
        private volatile Throwable throwable;
        private CountDownLatch completionLatch;

        WrapperFuture() {
            this(null, null);
        }

        WrapperFuture(CompletionHandler<T, A> handler, A attachment) {
            this.result = null;
            this.throwable = null;
            this.completionLatch = new CountDownLatch(1);
            this.handler = handler;
            this.attachment = attachment;
        }

        public void complete(T result) {
            this.result = result;
            this.completionLatch.countDown();
            if (this.handler != null) {
                this.handler.completed(result, this.attachment);
            }
        }

        public void fail(Throwable t) {
            this.throwable = t;
            this.completionLatch.countDown();
            if (this.handler != null) {
                this.handler.failed(this.throwable, this.attachment);
            }
        }

        @Override // java.util.concurrent.Future
        public final boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override // java.util.concurrent.Future
        public final boolean isCancelled() {
            return false;
        }

        @Override // java.util.concurrent.Future
        public final boolean isDone() {
            return this.completionLatch.getCount() > 0;
        }

        @Override // java.util.concurrent.Future
        public T get() throws InterruptedException, ExecutionException {
            this.completionLatch.await();
            if (this.throwable != null) {
                throw new ExecutionException(this.throwable);
            }
            return this.result;
        }

        @Override // java.util.concurrent.Future
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            boolean latchResult = this.completionLatch.await(timeout, unit);
            if (!latchResult) {
                throw new TimeoutException();
            }
            if (this.throwable != null) {
                throw new ExecutionException(this.throwable);
            }
            return this.result;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$LongToIntegerFuture.class */
    private static final class LongToIntegerFuture implements Future<Integer> {
        private final Future<Long> wrapped;

        LongToIntegerFuture(Future<Long> wrapped) {
            this.wrapped = wrapped;
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.wrapped.cancel(mayInterruptIfRunning);
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            return this.wrapped.isCancelled();
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            return this.wrapped.isDone();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get() throws InterruptedException, ExecutionException {
            Long result = this.wrapped.get();
            if (result.longValue() > 2147483647L) {
                throw new ExecutionException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.tooBig", result), null);
            }
            return Integer.valueOf(result.intValue());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            Long result = this.wrapped.get(timeout, unit);
            if (result.longValue() > 2147483647L) {
                throw new ExecutionException(AsyncChannelWrapperSecure.sm.getString("asyncChannelWrapperSecure.tooBig", result), null);
            }
            return Integer.valueOf(result.intValue());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/AsyncChannelWrapperSecure$SecureIOThreadFactory.class */
    private static class SecureIOThreadFactory implements ThreadFactory {
        private AtomicInteger count = new AtomicInteger(0);

        private SecureIOThreadFactory() {
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("WebSocketClient-SecureIO-" + this.count.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
