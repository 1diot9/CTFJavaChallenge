package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Operators;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerReadPublisher.class */
public abstract class AbstractListenerReadPublisher<T> implements Publisher<T> {
    private final AtomicReference<State> state;
    private volatile long demand;

    @Nullable
    private volatile Subscriber<? super T> subscriber;
    private volatile boolean completionPending;

    @Nullable
    private volatile Throwable errorPending;
    private final String logPrefix;
    protected static Log rsReadLogger = LogDelegateFactory.getHiddenLog((Class<?>) AbstractListenerReadPublisher.class);
    static final DataBuffer EMPTY_BUFFER = DefaultDataBufferFactory.sharedInstance.allocateBuffer(0);
    private static final AtomicLongFieldUpdater<AbstractListenerReadPublisher> DEMAND_FIELD_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractListenerReadPublisher.class, "demand");

    protected abstract void checkOnDataAvailable();

    @Nullable
    protected abstract T read() throws IOException;

    protected abstract void readingPaused();

    protected abstract void discardData();

    public AbstractListenerReadPublisher() {
        this("");
    }

    public AbstractListenerReadPublisher(String logPrefix) {
        this.state = new AtomicReference<>(State.UNSUBSCRIBED);
        this.logPrefix = logPrefix;
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    @Override // org.reactivestreams.Publisher
    public void subscribe(Subscriber<? super T> subscriber) {
        this.state.get().subscribe(this, subscriber);
    }

    public final void onDataAvailable() {
        rsReadLogger.trace(getLogPrefix() + "onDataAvailable");
        this.state.get().onDataAvailable(this);
    }

    public void onAllDataRead() {
        State state = this.state.get();
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace(getLogPrefix() + "onAllDataRead [" + state + "]");
        }
        state.onAllDataRead(this);
    }

    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
        }
        state.onError(this, ex);
    }

    private boolean readAndPublish() throws IOException {
        while (true) {
            long r = this.demand;
            if (r > 0 && this.state.get() != State.COMPLETED) {
                T data = read();
                if (data == EMPTY_BUFFER) {
                    if (rsReadLogger.isTraceEnabled()) {
                        rsReadLogger.trace(getLogPrefix() + "0 bytes read, trying again");
                    }
                } else if (data != null) {
                    if (r != Long.MAX_VALUE) {
                        DEMAND_FIELD_UPDATER.addAndGet(this, -1L);
                    }
                    Subscriber<? super T> subscriber = this.subscriber;
                    Assert.state(subscriber != null, "No subscriber");
                    if (rsReadLogger.isTraceEnabled()) {
                        rsReadLogger.trace(getLogPrefix() + "Publishing " + data.getClass().getSimpleName());
                    }
                    subscriber.onNext(data);
                } else {
                    if (rsReadLogger.isTraceEnabled()) {
                        rsReadLogger.trace(getLogPrefix() + "No more to read");
                        return true;
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace(getLogPrefix() + oldState + " -> " + newState);
        }
        return result;
    }

    private void changeToDemandState(State oldState) {
        if (changeState(oldState, State.DEMAND) && oldState != State.READING) {
            checkOnDataAvailable();
        }
    }

    private boolean handlePendingCompletionOrError() {
        State state = this.state.get();
        if (state == State.DEMAND || state == State.NO_DEMAND) {
            if (this.completionPending) {
                rsReadLogger.trace(getLogPrefix() + "Processing pending completion");
                this.state.get().onAllDataRead(this);
                return true;
            }
            Throwable ex = this.errorPending;
            if (ex != null) {
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace(getLogPrefix() + "Processing pending completion with error: " + ex);
                }
                this.state.get().onError(this, ex);
                return true;
            }
            return false;
        }
        return false;
    }

    private Subscription createSubscription() {
        return new ReadSubscription();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerReadPublisher$ReadSubscription.class */
    public final class ReadSubscription implements Subscription {
        private ReadSubscription() {
        }

        @Override // org.reactivestreams.Subscription
        public final void request(long n) {
            if (AbstractListenerReadPublisher.rsReadLogger.isTraceEnabled()) {
                AbstractListenerReadPublisher.rsReadLogger.trace(AbstractListenerReadPublisher.this.getLogPrefix() + "request " + (n != Long.MAX_VALUE ? Long.valueOf(n) : "Long.MAX_VALUE"));
            }
            AbstractListenerReadPublisher.this.state.get().request(AbstractListenerReadPublisher.this, n);
        }

        @Override // org.reactivestreams.Subscription
        public final void cancel() {
            State state = AbstractListenerReadPublisher.this.state.get();
            if (AbstractListenerReadPublisher.rsReadLogger.isTraceEnabled()) {
                AbstractListenerReadPublisher.rsReadLogger.trace(AbstractListenerReadPublisher.this.getLogPrefix() + "cancel [" + state + "]");
            }
            state.cancel(AbstractListenerReadPublisher.this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerReadPublisher$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.1
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
                Assert.notNull(publisher, "Publisher must not be null");
                Assert.notNull(subscriber, "Subscriber must not be null");
                if (publisher.changeState(this, SUBSCRIBING)) {
                    Subscription subscription = publisher.createSubscription();
                    ((AbstractListenerReadPublisher) publisher).subscriber = subscriber;
                    subscriber.onSubscribe(subscription);
                    publisher.changeState(SUBSCRIBING, NO_DEMAND);
                    publisher.handlePendingCompletionOrError();
                    return;
                }
                throw new IllegalStateException("Failed to transition to SUBSCRIBING, subscriber: " + subscriber);
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher) publisher).completionPending = true;
                publisher.handlePendingCompletionOrError();
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher) publisher).errorPending = ex;
                publisher.handlePendingCompletionOrError();
            }
        },
        SUBSCRIBING { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.2
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate(n)) {
                    Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
                    publisher.changeToDemandState(this);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher) publisher).completionPending = true;
                publisher.handlePendingCompletionOrError();
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher) publisher).errorPending = ex;
                publisher.handlePendingCompletionOrError();
            }
        },
        NO_DEMAND { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.3
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate(n)) {
                    Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
                    publisher.changeToDemandState(this);
                }
            }
        },
        DEMAND { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.4
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate(n)) {
                    Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
                    publisher.changeToDemandState(NO_DEMAND);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {
                if (publisher.changeState(this, READING)) {
                    try {
                        boolean demandAvailable = publisher.readAndPublish();
                        if (demandAvailable) {
                            publisher.changeToDemandState(READING);
                            publisher.handlePendingCompletionOrError();
                        } else {
                            publisher.readingPaused();
                            if (publisher.changeState(READING, NO_DEMAND) && !publisher.handlePendingCompletionOrError()) {
                                long r = ((AbstractListenerReadPublisher) publisher).demand;
                                if (r > 0) {
                                    publisher.changeToDemandState(NO_DEMAND);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        publisher.onError(ex);
                    }
                }
            }
        },
        READING { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.5
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
                if (Operators.validate(n)) {
                    Operators.addCap(AbstractListenerReadPublisher.DEMAND_FIELD_UPDATER, publisher, n);
                    publisher.changeToDemandState(NO_DEMAND);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
                ((AbstractListenerReadPublisher) publisher).completionPending = true;
                publisher.handlePendingCompletionOrError();
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable ex) {
                ((AbstractListenerReadPublisher) publisher).errorPending = ex;
                publisher.handlePendingCompletionOrError();
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.AbstractListenerReadPublisher.State.6
            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void cancel(AbstractListenerReadPublisher<T> publisher) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher.State
            <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {
            }
        };

        <T> void subscribe(AbstractListenerReadPublisher<T> publisher, Subscriber<? super T> subscriber) {
            throw new IllegalStateException(toString());
        }

        <T> void request(AbstractListenerReadPublisher<T> publisher, long n) {
            throw new IllegalStateException(toString());
        }

        <T> void cancel(AbstractListenerReadPublisher<T> publisher) {
            if (publisher.changeState(this, COMPLETED)) {
                publisher.discardData();
            } else {
                ((AbstractListenerReadPublisher) publisher).state.get().cancel(publisher);
            }
        }

        <T> void onDataAvailable(AbstractListenerReadPublisher<T> publisher) {
        }

        <T> void onAllDataRead(AbstractListenerReadPublisher<T> publisher) {
            if (publisher.changeState(this, COMPLETED)) {
                Subscriber<? super T> s = ((AbstractListenerReadPublisher) publisher).subscriber;
                if (s != null) {
                    s.onComplete();
                    return;
                }
                return;
            }
            ((AbstractListenerReadPublisher) publisher).state.get().onAllDataRead(publisher);
        }

        <T> void onError(AbstractListenerReadPublisher<T> publisher, Throwable t) {
            if (publisher.changeState(this, COMPLETED)) {
                publisher.discardData();
                Subscriber<? super T> s = ((AbstractListenerReadPublisher) publisher).subscriber;
                if (s != null) {
                    s.onError(t);
                    return;
                }
                return;
            }
            ((AbstractListenerReadPublisher) publisher).state.get().onError(publisher, t);
        }
    }
}
