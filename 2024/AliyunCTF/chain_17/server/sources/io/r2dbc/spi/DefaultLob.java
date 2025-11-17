package io.r2dbc.spi;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/DefaultLob.class */
class DefaultLob<T> {
    private static final AtomicIntegerFieldUpdater<DefaultLob<?>> ATOMIC_DISCARD = AtomicIntegerFieldUpdater.newUpdater(DefaultLob.class, "discarded");
    private static final AtomicIntegerFieldUpdater<DefaultLob<?>> ATOMIC_CONSUMED = AtomicIntegerFieldUpdater.newUpdater(DefaultLob.class, "consumed");
    private static final int NOT_DISCARDED = 0;
    private static final int DISCARDED = 1;
    private static final int NOT_CONSUMED = 0;
    private static final int CONSUMED = 1;
    private final Publisher<T> p;
    private volatile int discarded = 0;
    private volatile int consumed = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultLob(Publisher<T> p) {
        this.p = p;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Publisher<T> stream() {
        return subscriber -> {
            if (ATOMIC_DISCARD.get(this) == 1) {
                subscriber.onError(new IllegalStateException("Source stream was already released"));
            } else if (!ATOMIC_CONSUMED.compareAndSet(this, 0, 1)) {
                subscriber.onError(new IllegalStateException("Source stream was already consumed"));
            } else {
                this.p.subscribe(subscriber);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Publisher<Void> discard() {
        return subscriber -> {
            final AtomicBoolean atomicBoolean = new AtomicBoolean();
            if (!ATOMIC_DISCARD.compareAndSet(this, 0, 1)) {
                subscriber.onError(new IllegalStateException("Source stream was already released"));
                return;
            }
            try {
                this.p.subscribe(new Subscriber<T>() { // from class: io.r2dbc.spi.DefaultLob.1
                    @Override // org.reactivestreams.Subscriber
                    public void onSubscribe(Subscription s) {
                        s.cancel();
                        if (atomicBoolean.compareAndSet(false, true)) {
                            subscriber.onComplete();
                        }
                    }

                    @Override // org.reactivestreams.Subscriber
                    public void onNext(T t) {
                    }

                    @Override // org.reactivestreams.Subscriber
                    public void onError(Throwable t) {
                        if (atomicBoolean.compareAndSet(false, true)) {
                            subscriber.onError(new IllegalStateException("Resource release has failed", t));
                        }
                    }

                    @Override // org.reactivestreams.Subscriber
                    public void onComplete() {
                        if (atomicBoolean.compareAndSet(false, true)) {
                            subscriber.onComplete();
                        }
                    }
                });
            } catch (Exception e) {
                if (atomicBoolean.compareAndSet(false, true)) {
                    subscriber.onError(new IllegalStateException("Resource release has failed", e));
                }
            }
        };
    }
}
