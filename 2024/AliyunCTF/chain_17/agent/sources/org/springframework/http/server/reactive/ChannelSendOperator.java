package org.springframework.http.server.reactive;

import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ChannelSendOperator.class */
public class ChannelSendOperator<T> extends Mono<Void> implements Scannable {
    private final Function<Publisher<T>, Publisher<Void>> writeFunction;
    private final Flux<T> source;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ChannelSendOperator$State.class */
    private enum State {
        NEW,
        FIRST_SIGNAL_RECEIVED,
        EMITTING_CACHED_SIGNALS,
        READY_TO_WRITE
    }

    public ChannelSendOperator(Publisher<? extends T> source, Function<Publisher<T>, Publisher<Void>> writeFunction) {
        this.source = Flux.from(source);
        this.writeFunction = writeFunction;
    }

    @Nullable
    public Object scanUnsafe(Scannable.Attr key) {
        if (key == Scannable.Attr.PREFETCH) {
            return Integer.MAX_VALUE;
        }
        if (key == Scannable.Attr.PARENT) {
            return this.source;
        }
        return null;
    }

    public void subscribe(CoreSubscriber<? super Void> actual) {
        this.source.subscribe(new WriteBarrier(actual));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ChannelSendOperator$WriteBarrier.class */
    public class WriteBarrier implements CoreSubscriber<T>, Subscription, Publisher<T> {
        private final ChannelSendOperator<T>.WriteCompletionBarrier writeCompletionBarrier;

        @Nullable
        private Subscription subscription;

        @Nullable
        private T item;

        @Nullable
        private Throwable error;
        private long demandBeforeReadyToWrite;

        @Nullable
        private Subscriber<? super T> writeSubscriber;
        private boolean completed = false;
        private State state = State.NEW;

        WriteBarrier(CoreSubscriber<? super Void> completionSubscriber) {
            this.writeCompletionBarrier = new WriteCompletionBarrier(completionSubscriber, this);
        }

        public final void onSubscribe(Subscription s) {
            if (Operators.validate(this.subscription, s)) {
                this.subscription = s;
                this.writeCompletionBarrier.connect();
                s.request(1L);
            }
        }

        public final void onNext(T item) {
            if (this.state == State.READY_TO_WRITE) {
                requiredWriteSubscriber().onNext(item);
                return;
            }
            synchronized (this) {
                if (this.state == State.READY_TO_WRITE) {
                    requiredWriteSubscriber().onNext(item);
                } else if (this.state == State.NEW) {
                    this.item = item;
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    try {
                        Publisher<Void> result = ChannelSendOperator.this.writeFunction.apply(this);
                        result.subscribe(this.writeCompletionBarrier);
                    } catch (Throwable ex) {
                        this.writeCompletionBarrier.onError(ex);
                    }
                } else {
                    if (this.subscription != null) {
                        this.subscription.cancel();
                    }
                    this.writeCompletionBarrier.onError(new IllegalStateException("Unexpected item."));
                }
            }
        }

        private Subscriber<? super T> requiredWriteSubscriber() {
            Assert.state(this.writeSubscriber != null, "No write subscriber");
            return this.writeSubscriber;
        }

        public final void onError(Throwable ex) {
            if (this.state == State.READY_TO_WRITE) {
                requiredWriteSubscriber().onError(ex);
                return;
            }
            synchronized (this) {
                if (this.state == State.READY_TO_WRITE) {
                    requiredWriteSubscriber().onError(ex);
                } else if (this.state == State.NEW) {
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    this.writeCompletionBarrier.onError(ex);
                } else {
                    this.error = ex;
                }
            }
        }

        public final void onComplete() {
            if (this.state == State.READY_TO_WRITE) {
                requiredWriteSubscriber().onComplete();
                return;
            }
            synchronized (this) {
                if (this.state == State.READY_TO_WRITE) {
                    requiredWriteSubscriber().onComplete();
                } else if (this.state == State.NEW) {
                    this.completed = true;
                    this.state = State.FIRST_SIGNAL_RECEIVED;
                    try {
                        Publisher<Void> result = ChannelSendOperator.this.writeFunction.apply(this);
                        result.subscribe(this.writeCompletionBarrier);
                    } catch (Throwable ex) {
                        this.writeCompletionBarrier.onError(ex);
                    }
                } else {
                    this.completed = true;
                }
            }
        }

        public Context currentContext() {
            return this.writeCompletionBarrier.currentContext();
        }

        public void request(long n) {
            Subscription s = this.subscription;
            if (s == null) {
                return;
            }
            if (this.state == State.READY_TO_WRITE) {
                s.request(n);
                return;
            }
            synchronized (this) {
                if (this.writeSubscriber != null) {
                    if (this.state == State.EMITTING_CACHED_SIGNALS) {
                        this.demandBeforeReadyToWrite = n;
                        return;
                    }
                    try {
                        this.state = State.EMITTING_CACHED_SIGNALS;
                        if (emitCachedSignals()) {
                            return;
                        }
                        n = (n + this.demandBeforeReadyToWrite) - 1;
                        if (n == 0) {
                            this.state = State.READY_TO_WRITE;
                            return;
                        }
                        this.state = State.READY_TO_WRITE;
                    } finally {
                        this.state = State.READY_TO_WRITE;
                    }
                }
                s.request(n);
            }
        }

        private boolean emitCachedSignals() {
            if (this.error != null) {
                try {
                    requiredWriteSubscriber().onError(this.error);
                    return true;
                } finally {
                    releaseCachedItem();
                }
            }
            T item = this.item;
            this.item = null;
            if (item != null) {
                requiredWriteSubscriber().onNext(item);
            }
            if (this.completed) {
                requiredWriteSubscriber().onComplete();
                return true;
            }
            return false;
        }

        public void cancel() {
            Subscription s = this.subscription;
            if (s != null) {
                this.subscription = null;
                try {
                    s.cancel();
                } finally {
                    releaseCachedItem();
                }
            }
        }

        private void releaseCachedItem() {
            synchronized (this) {
                Object item = this.item;
                if (item instanceof DataBuffer) {
                    DataBuffer dataBuffer = (DataBuffer) item;
                    DataBufferUtils.release(dataBuffer);
                }
                this.item = null;
            }
        }

        public void subscribe(Subscriber<? super T> writeSubscriber) {
            synchronized (this) {
                Assert.state(this.writeSubscriber == null, "Only one write subscriber supported");
                this.writeSubscriber = writeSubscriber;
                if (this.error != null || this.completed) {
                    this.writeSubscriber.onSubscribe(Operators.emptySubscription());
                    emitCachedSignals();
                } else {
                    this.writeSubscriber.onSubscribe(this);
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ChannelSendOperator$WriteCompletionBarrier.class */
    private class WriteCompletionBarrier implements CoreSubscriber<Void>, Subscription {
        private final CoreSubscriber<? super Void> completionSubscriber;
        private final ChannelSendOperator<T>.WriteBarrier writeBarrier;

        @Nullable
        private Subscription subscription;

        public WriteCompletionBarrier(CoreSubscriber<? super Void> subscriber, ChannelSendOperator<T>.WriteBarrier writeBarrier) {
            this.completionSubscriber = subscriber;
            this.writeBarrier = writeBarrier;
        }

        public void connect() {
            this.completionSubscriber.onSubscribe(this);
        }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            try {
                this.completionSubscriber.onError(ex);
            } finally {
                this.writeBarrier.releaseCachedItem();
            }
        }

        public void onComplete() {
            this.completionSubscriber.onComplete();
        }

        public Context currentContext() {
            return this.completionSubscriber.currentContext();
        }

        public void request(long n) {
        }

        public void cancel() {
            this.writeBarrier.cancel();
            Subscription subscription = this.subscription;
            if (subscription != null) {
                subscription.cancel();
            }
        }
    }
}
