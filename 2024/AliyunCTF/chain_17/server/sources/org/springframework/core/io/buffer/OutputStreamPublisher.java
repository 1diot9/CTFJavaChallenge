package org.springframework.core.io.buffer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/OutputStreamPublisher.class */
public final class OutputStreamPublisher implements Publisher<DataBuffer> {
    private final Consumer<OutputStream> outputStreamConsumer;
    private final DataBufferFactory bufferFactory;
    private final Executor executor;
    private final int chunkSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OutputStreamPublisher(Consumer<OutputStream> outputStreamConsumer, DataBufferFactory bufferFactory, Executor executor, int chunkSize) {
        this.outputStreamConsumer = outputStreamConsumer;
        this.bufferFactory = bufferFactory;
        this.executor = executor;
        this.chunkSize = chunkSize;
    }

    @Override // org.reactivestreams.Publisher
    public void subscribe(Subscriber<? super DataBuffer> subscriber) {
        Objects.requireNonNull(subscriber, "Subscriber must not be null");
        OutputStreamSubscription subscription = new OutputStreamSubscription(subscriber, this.outputStreamConsumer, this.bufferFactory, this.chunkSize);
        subscriber.onSubscribe(subscription);
        Executor executor = this.executor;
        Objects.requireNonNull(subscription);
        executor.execute(subscription::invokeHandler);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/OutputStreamPublisher$OutputStreamSubscription.class */
    private static final class OutputStreamSubscription extends OutputStream implements Subscription {
        private static final Object READY = new Object();
        private final Subscriber<? super DataBuffer> actual;
        private final Consumer<OutputStream> outputStreamHandler;
        private final DataBufferFactory bufferFactory;
        private final int chunkSize;
        private final AtomicLong requested = new AtomicLong();
        private final AtomicReference<Object> parkedThread = new AtomicReference<>();

        @Nullable
        private volatile Throwable error;
        private long produced;

        OutputStreamSubscription(Subscriber<? super DataBuffer> actual, Consumer<OutputStream> outputStreamConsumer, DataBufferFactory bufferFactory, int chunkSize) {
            this.actual = actual;
            this.outputStreamHandler = outputStreamConsumer;
            this.bufferFactory = bufferFactory;
            this.chunkSize = chunkSize;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            checkDemandAndAwaitIfNeeded();
            DataBuffer next = this.bufferFactory.allocateBuffer(1);
            next.write((byte) b);
            this.actual.onNext(next);
            this.produced++;
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            checkDemandAndAwaitIfNeeded();
            DataBuffer next = this.bufferFactory.allocateBuffer(len);
            next.write(b, off, len);
            this.actual.onNext(next);
            this.produced++;
        }

        private void checkDemandAndAwaitIfNeeded() throws IOException {
            long r = this.requested.get();
            if (isTerminated(r) || isCancelled(r)) {
                throw new IOException("Subscription has been terminated");
            }
            long p = this.produced;
            if (p == r) {
                if (p > 0) {
                    r = tryProduce(p);
                    this.produced = 0L;
                }
                while (!isTerminated(r) && !isCancelled(r)) {
                    if (r != 0) {
                        return;
                    }
                    await();
                    r = this.requested.get();
                }
                throw new IOException("Subscription has been terminated");
            }
        }

        private void invokeHandler() {
            try {
                OutputStream outputStream = new BufferedOutputStream(this, this.chunkSize);
                try {
                    this.outputStreamHandler.accept(outputStream);
                    outputStream.close();
                    long previousState = tryTerminate();
                    if (isCancelled(previousState)) {
                        return;
                    }
                    if (isTerminated(previousState)) {
                        this.actual.onError(this.error);
                    } else {
                        this.actual.onComplete();
                    }
                } finally {
                }
            } catch (Exception ex) {
                long previousState2 = tryTerminate();
                if (isCancelled(previousState2)) {
                    return;
                }
                if (isTerminated(previousState2)) {
                    this.actual.onError(this.error);
                } else {
                    this.actual.onError(ex);
                }
            }
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            if (n > 0) {
                if (addCap(n) == 0) {
                    resume();
                    return;
                }
                return;
            }
            this.error = new IllegalArgumentException("request should be a positive number");
            long previousState = tryTerminate();
            if (isTerminated(previousState) || isCancelled(previousState) || previousState > 0) {
                return;
            }
            resume();
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            long previousState = tryCancel();
            if (isCancelled(previousState) || previousState > 0) {
                return;
            }
            resume();
        }

        private void await() {
            Thread toUnpark = Thread.currentThread();
            while (true) {
                Object current = this.parkedThread.get();
                if (current != READY) {
                    if (current != null && current != toUnpark) {
                        throw new IllegalStateException("Only one (Virtual)Thread can await!");
                    }
                    if (this.parkedThread.compareAndSet(null, toUnpark)) {
                        LockSupport.park();
                    }
                } else {
                    this.parkedThread.lazySet(null);
                    return;
                }
            }
        }

        private void resume() {
            Object old;
            if (this.parkedThread.get() != READY && (old = this.parkedThread.getAndSet(READY)) != READY) {
                LockSupport.unpark((Thread) old);
            }
        }

        private long tryCancel() {
            long r;
            do {
                r = this.requested.get();
                if (isCancelled(r)) {
                    return r;
                }
            } while (!this.requested.compareAndSet(r, Long.MIN_VALUE));
            return r;
        }

        private long tryTerminate() {
            long r;
            do {
                r = this.requested.get();
                if (isCancelled(r) || isTerminated(r)) {
                    return r;
                }
            } while (!this.requested.compareAndSet(r, -1L));
            return r;
        }

        private long tryProduce(long n) {
            long current;
            long update;
            do {
                current = this.requested.get();
                if (isTerminated(current) || isCancelled(current)) {
                    return current;
                }
                if (current == Long.MAX_VALUE) {
                    return Long.MAX_VALUE;
                }
                update = current - n;
                if (update < 0) {
                    update = 0;
                }
            } while (!this.requested.compareAndSet(current, update));
            return update;
        }

        private long addCap(long n) {
            long r;
            long u;
            do {
                r = this.requested.get();
                if (isTerminated(r) || isCancelled(r) || r == Long.MAX_VALUE) {
                    return r;
                }
                u = addCap(r, n);
            } while (!this.requested.compareAndSet(r, u));
            return r;
        }

        private static boolean isTerminated(long state) {
            return state == -1;
        }

        private static boolean isCancelled(long state) {
            return state == Long.MIN_VALUE;
        }

        private static long addCap(long a, long b) {
            long res = a + b;
            if (res < 0) {
                return Long.MAX_VALUE;
            }
            return res;
        }
    }
}
