package org.springframework.http.server.reactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerWriteProcessor.class */
public abstract class AbstractListenerWriteProcessor<T> implements Processor<T, Void> {
    protected static final Log rsWriteLogger = LogDelegateFactory.getHiddenLog((Class<?>) AbstractListenerWriteProcessor.class);
    private final AtomicReference<State> state;

    @Nullable
    private Subscription subscription;

    @Nullable
    private volatile T currentData;
    private volatile boolean sourceCompleted;
    private volatile boolean readyToCompleteAfterLastWrite;
    private final WriteResultPublisher resultPublisher;
    private final String logPrefix;

    protected abstract boolean isDataEmpty(T data);

    protected abstract boolean isWritePossible();

    protected abstract boolean write(T data) throws IOException;

    protected abstract void discardData(T data);

    public AbstractListenerWriteProcessor() {
        this("");
    }

    public AbstractListenerWriteProcessor(String logPrefix) {
        this.state = new AtomicReference<>(State.UNSUBSCRIBED);
        this.resultPublisher = new WriteResultPublisher(logPrefix + "[WP] ", this::cancelAndSetCompleted);
        this.logPrefix = StringUtils.hasText(logPrefix) ? logPrefix : "";
    }

    public String getLogPrefix() {
        return this.logPrefix;
    }

    public final void onSubscribe(Subscription subscription) {
        this.state.get().onSubscribe(this, subscription);
    }

    public final void onNext(T data) {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "onNext: " + data.getClass().getSimpleName());
        }
        this.state.get().onNext(this, data);
    }

    public final void onError(Throwable ex) {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "onError: " + ex + " [" + state + "]");
        }
        state.onError(this, ex);
    }

    public final void onComplete() {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "onComplete [" + state + "]");
        }
        state.onComplete(this);
    }

    public final void onWritePossible() {
        State state = this.state.get();
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "onWritePossible [" + state + "]");
        }
        state.onWritePossible(this);
    }

    public void cancel() {
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "cancel [" + this.state + "]");
        }
        if (this.subscription != null) {
            this.subscription.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelAndSetCompleted() {
        State prev;
        cancel();
        do {
            prev = this.state.get();
            if (prev == State.COMPLETED) {
                return;
            }
        } while (!this.state.compareAndSet(prev, State.COMPLETED));
        if (rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + prev + " -> " + this.state);
        }
        if (prev != State.WRITING) {
            discardCurrentData();
        }
    }

    public final void subscribe(Subscriber<? super Void> subscriber) {
        this.resultPublisher.subscribe(subscriber);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void dataReceived(T data) {
        T prev = this.currentData;
        if (prev != null) {
            discardData(data);
            cancel();
            onError(new IllegalStateException("Received new data while current not processed yet."));
        }
        this.currentData = data;
    }

    @Deprecated
    protected void writingPaused() {
    }

    protected void writingComplete() {
    }

    protected void writingFailed(Throwable ex) {
    }

    private boolean changeState(State oldState, State newState) {
        boolean result = this.state.compareAndSet(oldState, newState);
        if (result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + oldState + " -> " + newState);
        }
        return result;
    }

    private void changeStateToReceived(State oldState) {
        if (changeState(oldState, State.RECEIVED)) {
            writeIfPossible();
        }
    }

    private void changeStateToComplete(State oldState) {
        if (changeState(oldState, State.COMPLETED)) {
            discardCurrentData();
            writingComplete();
            this.resultPublisher.publishComplete();
            return;
        }
        this.state.get().onComplete(this);
    }

    private void writeIfPossible() {
        boolean result = isWritePossible();
        if (!result && rsWriteLogger.isTraceEnabled()) {
            rsWriteLogger.trace(getLogPrefix() + "isWritePossible false");
        }
        if (result) {
            onWritePossible();
        }
    }

    private void discardCurrentData() {
        T data = this.currentData;
        this.currentData = null;
        if (data != null) {
            discardData(data);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/AbstractListenerWriteProcessor$State.class */
    public enum State {
        UNSUBSCRIBED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.1
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
                Assert.notNull(subscription, "Subscription must not be null");
                if (processor.changeState(this, REQUESTED)) {
                    ((AbstractListenerWriteProcessor) processor).subscription = subscription;
                    subscription.request(1L);
                } else {
                    super.onSubscribe(processor, subscription);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                processor.changeStateToComplete(this);
            }
        },
        REQUESTED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.2
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
                if (processor.isDataEmpty(data)) {
                    Assert.state(((AbstractListenerWriteProcessor) processor).subscription != null, "No subscription");
                    ((AbstractListenerWriteProcessor) processor).subscription.request(1L);
                } else {
                    processor.dataReceived(data);
                    processor.changeStateToReceived(this);
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor) processor).readyToCompleteAfterLastWrite = true;
                processor.changeStateToReceived(this);
            }
        },
        RECEIVED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.3
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
                if (((AbstractListenerWriteProcessor) processor).readyToCompleteAfterLastWrite) {
                    processor.changeStateToComplete(RECEIVED);
                    return;
                }
                if (processor.changeState(this, WRITING)) {
                    T data = ((AbstractListenerWriteProcessor) processor).currentData;
                    Assert.state(data != null, "No data");
                    try {
                        if (processor.write(data)) {
                            if (processor.changeState(WRITING, REQUESTED)) {
                                ((AbstractListenerWriteProcessor) processor).currentData = null;
                                if (((AbstractListenerWriteProcessor) processor).sourceCompleted) {
                                    ((AbstractListenerWriteProcessor) processor).readyToCompleteAfterLastWrite = true;
                                    processor.changeStateToReceived(REQUESTED);
                                } else {
                                    processor.writingPaused();
                                    Assert.state(((AbstractListenerWriteProcessor) processor).subscription != null, "No subscription");
                                    ((AbstractListenerWriteProcessor) processor).subscription.request(1L);
                                }
                            }
                        } else {
                            processor.changeStateToReceived(WRITING);
                        }
                    } catch (IOException ex) {
                        processor.writingFailed(ex);
                    }
                }
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor) processor).sourceCompleted = true;
                if (((AbstractListenerWriteProcessor) processor).state.get() == State.REQUESTED) {
                    processor.changeStateToComplete(State.REQUESTED);
                }
            }
        },
        WRITING { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.4
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
                ((AbstractListenerWriteProcessor) processor).sourceCompleted = true;
                if (((AbstractListenerWriteProcessor) processor).state.get() == State.REQUESTED) {
                    processor.changeStateToComplete(State.REQUESTED);
                }
            }
        },
        COMPLETED { // from class: org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State.5
            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            }

            @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor.State
            public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            }
        };

        public <T> void onSubscribe(AbstractListenerWriteProcessor<T> processor, Subscription subscription) {
            subscription.cancel();
        }

        public <T> void onNext(AbstractListenerWriteProcessor<T> processor, T data) {
            processor.discardData(data);
            processor.cancel();
            processor.onError(new IllegalStateException("Illegal onNext without demand"));
        }

        public <T> void onError(AbstractListenerWriteProcessor<T> processor, Throwable ex) {
            if (processor.changeState(this, COMPLETED)) {
                processor.discardCurrentData();
                processor.writingComplete();
                ((AbstractListenerWriteProcessor) processor).resultPublisher.publishError(ex);
                return;
            }
            ((AbstractListenerWriteProcessor) processor).state.get().onError(processor, ex);
        }

        public <T> void onComplete(AbstractListenerWriteProcessor<T> processor) {
            throw new IllegalStateException(toString());
        }

        public <T> void onWritePossible(AbstractListenerWriteProcessor<T> processor) {
        }
    }
}
