package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor.class */
public abstract class AbstractListenerWriteFlushProcessor<T> implements Processor<Publisher<? extends T>, Void> {
    protected static final Log rsWriteFlushLogger = LogDelegateFactory.getHiddenLog((Class<?>) AbstractListenerWriteFlushProcessor.class);
    private final AtomicReference<State> state;

    @Nullable
    private Subscription subscription;
    private volatile boolean sourceCompleted;

    @Nullable
    private volatile AbstractListenerWriteProcessor<?> currentWriteProcessor;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    protected abstract Processor<? super T, Void> createWriteProcessor();

    protected abstract boolean isWritePossible();

    protected abstract void flush() throws IOException;

    protected abstract boolean isFlushPending();

    public AbstractListenerWriteFlushProcessor() {
        this("");
    }

    public AbstractListenerWriteFlushProcessor(String logPrefix) {
        this.state = new AtomicReference<>(State.UNSUBSCRIBED);
        this.logPrefix = logPrefix;
        this.resultPublisher = new WriteResultPublisher(logPrefix + "[WFP] ", () -> {
            cancel();
            State oldState = this.state.getAndSet(State.COMPLETED);
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace(getLogPrefix() + oldState + " -> " + this.state);
            }
            AbstractListenerWriteProcessor<?> writeProcessor = this.currentWriteProcessor;
            if (writeProcessor != null) {
                writeProcessor.cancelAndSetCompleted();
            }
            this.currentWriteProcessor = null;
        });
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    @Override // org.reactivestreams.Subscriber
    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    @Override // org.reactivestreams.Subscriber
    public final void onNext(Publisher<? extends T> publisher) {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "onNext: \"write\" Publisher");
        }
        this.state.get().onNext(this, publisher);
    }

    @Override // org.reactivestreams.Subscriber
    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
        }
        state.onError(this, ex);
    }

    @Override // org.reactivestreams.Subscriber
    public final void onComplete() {
        State state = this.state.get();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "onComplete [" + state + "]");
        }
        state.onComplete(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onFlushPossible() {
        this.state.get().onFlushPossible(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancel() {
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "cancel [" + this.state + "]");
        }
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    @Override // org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    protected void flushingFailed(Throwable t) {
        cancel();
        onError(t);
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + oldState + " -> " + newState);
        }
        return result;
    }

    private void flushIfPossible() {
        boolean result = isWritePossible();
        if (rsWriteFlushLogger.isTraceEnabled()) {
            rsWriteFlushLogger.trace(getLogPrefix() + "isWritePossible[" + result + "]");
        }
        if (result) {
            onFlushPossible();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.1
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> processor, Subscription subscription) {
                Assert.notNull(subscription, "Subscription must not be null");
                if (processor.changeState(this, REQUESTED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                if (processor.changeState(this, COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                } else {
                    ((AbstractListenerWriteFlushProcessor) processor).state.get().onComplete(processor);
                }
            }
        },
        REQUESTED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.2
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> processor, Publisher<? extends T> currentPublisher) {
                if (processor.changeState(this, RECEIVED)) {
                    Processor<? super T, Void> writeProcessor = processor.createWriteProcessor();
                    ((AbstractListenerWriteFlushProcessor) processor).currentWriteProcessor = (AbstractListenerWriteProcessor) writeProcessor;
                    currentPublisher.subscribe(writeProcessor);
                    writeProcessor.subscribe(new WriteResultSubscriber(processor));
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                if (processor.changeState(this, COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                } else {
                    ((AbstractListenerWriteFlushProcessor) processor).state.get().onComplete(processor);
                }
            }
        },
        RECEIVED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.3
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                    if (processor.changeState(this, REQUESTED)) {
                        if (((AbstractListenerWriteFlushProcessor) processor).sourceCompleted) {
                            handleSourceCompleted(processor);
                        } else {
                            Assert.state(((AbstractListenerWriteFlushProcessor) processor).subscription != null, "No subscription");
                            ((AbstractListenerWriteFlushProcessor) processor).subscription.request(1L);
                        }
                    }
                } catch (Throwable ex) {
                    processor.flushingFailed(ex);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
                ((AbstractListenerWriteFlushProcessor) processor).sourceCompleted = true;
                if (((AbstractListenerWriteFlushProcessor) processor).state.get() == State.REQUESTED) {
                    handleSourceCompleted(processor);
                }
            }

            private <T> void handleSourceCompleted(AbstractListenerWriteFlushProcessor<T> processor) {
                if (processor.isFlushPending()) {
                    processor.changeState(State.REQUESTED, State.FLUSHING);
                    processor.flushIfPossible();
                } else if (processor.changeState(State.REQUESTED, State.COMPLETED)) {
                    ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                } else {
                    ((AbstractListenerWriteFlushProcessor) processor).state.get().onComplete(processor);
                }
            }
        },
        FLUSHING { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.4
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
                try {
                    processor.flush();
                    if (processor.changeState(this, COMPLETED)) {
                        ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishComplete();
                    } else {
                        ((AbstractListenerWriteFlushProcessor) processor).state.get().onComplete(processor);
                    }
                } catch (Throwable ex) {
                    processor.flushingFailed(ex);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State.5
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable t) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor.State
            public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            }
        };

        public <T> void onSubscribe(AbstractListenerWriteFlushProcessor<T> proc, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteFlushProcessor<T> proc, Publisher<? extends T> pub) {
            throw new IllegalStateException(toString());
        }

        public <T> void onError(AbstractListenerWriteFlushProcessor<T> processor, Throwable ex) {
            if (processor.changeState(this, COMPLETED)) {
                ((AbstractListenerWriteFlushProcessor) processor).resultPublisher.publishError(ex);
            } else {
                ((AbstractListenerWriteFlushProcessor) processor).state.get().onError(processor, ex);
            }
        }

        public <T> void onComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void writeComplete(AbstractListenerWriteFlushProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void onFlushPossible(AbstractListenerWriteFlushProcessor<T> processor) {
        }

        /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerWriteFlushProcessor$State$WriteResultSubscriber.class */
        private static class WriteResultSubscriber implements Subscriber<Void> {
            private final AbstractListenerWriteFlushProcessor<?> processor;

            public WriteResultSubscriber(AbstractListenerWriteFlushProcessor<?> processor) {
                this.processor = processor;
            }

            @Override // org.reactivestreams.Subscriber
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override // org.reactivestreams.Subscriber
            public void onNext(Void aVoid) {
            }

            @Override // org.reactivestreams.Subscriber
            public void onError(Throwable ex) {
                if (AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.isTraceEnabled()) {
                    AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.trace(this.processor.getLogPrefix() + "current \"write\" Publisher failed: " + ex);
                }
                ((AbstractListenerWriteFlushProcessor) this.processor).currentWriteProcessor = null;
                this.processor.cancel();
                this.processor.onError(ex);
            }

            @Override // org.reactivestreams.Subscriber
            public void onComplete() {
                if (AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.isTraceEnabled()) {
                    AbstractListenerWriteFlushProcessor.rsWriteFlushLogger.trace(this.processor.getLogPrefix() + "current \"write\" Publisher completed");
                }
                ((AbstractListenerWriteFlushProcessor) this.processor).currentWriteProcessor = null;
                ((AbstractListenerWriteFlushProcessor) this.processor).state.get().writeComplete(this.processor);
            }

            public String toString() {
                return this.processor.getClass().getSimpleName() + "-WriteResultSubscriber";
            }
        }
    }
}
