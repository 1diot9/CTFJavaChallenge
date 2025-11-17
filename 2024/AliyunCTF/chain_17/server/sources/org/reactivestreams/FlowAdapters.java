package org.reactivestreams;

import java.util.Objects;
import java.util.concurrent.Flow;

/* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters.class */
public final class FlowAdapters {
    private FlowAdapters() {
        throw new IllegalStateException("No instances!");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.reactivestreams.Publisher] */
    public static <T> Publisher<T> toPublisher(Flow.Publisher<? extends T> publisher) {
        ReactivePublisherFromFlow reactivePublisherFromFlow;
        Objects.requireNonNull(publisher, "flowPublisher");
        if (publisher instanceof FlowPublisherFromReactive) {
            reactivePublisherFromFlow = ((FlowPublisherFromReactive) publisher).reactiveStreams;
        } else if (publisher instanceof Publisher) {
            reactivePublisherFromFlow = (Publisher) publisher;
        } else {
            reactivePublisherFromFlow = new ReactivePublisherFromFlow(publisher);
        }
        return reactivePublisherFromFlow;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [java.util.concurrent.Flow$Publisher] */
    public static <T> Flow.Publisher<T> toFlowPublisher(Publisher<? extends T> publisher) {
        FlowPublisherFromReactive flowPublisherFromReactive;
        Objects.requireNonNull(publisher, "reactiveStreamsPublisher");
        if (publisher instanceof ReactivePublisherFromFlow) {
            flowPublisherFromReactive = ((ReactivePublisherFromFlow) publisher).flow;
        } else if (publisher instanceof Flow.Publisher) {
            flowPublisherFromReactive = (Flow.Publisher) publisher;
        } else {
            flowPublisherFromReactive = new FlowPublisherFromReactive(publisher);
        }
        return flowPublisherFromReactive;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.reactivestreams.Processor] */
    public static <T, U> Processor<T, U> toProcessor(Flow.Processor<? super T, ? extends U> processor) {
        ReactiveToFlowProcessor reactiveToFlowProcessor;
        Objects.requireNonNull(processor, "flowProcessor");
        if (processor instanceof FlowToReactiveProcessor) {
            reactiveToFlowProcessor = ((FlowToReactiveProcessor) processor).reactiveStreams;
        } else if (processor instanceof Processor) {
            reactiveToFlowProcessor = (Processor) processor;
        } else {
            reactiveToFlowProcessor = new ReactiveToFlowProcessor(processor);
        }
        return reactiveToFlowProcessor;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [java.util.concurrent.Flow$Processor] */
    public static <T, U> Flow.Processor<T, U> toFlowProcessor(Processor<? super T, ? extends U> processor) {
        FlowToReactiveProcessor flowToReactiveProcessor;
        Objects.requireNonNull(processor, "reactiveStreamsProcessor");
        if (processor instanceof ReactiveToFlowProcessor) {
            flowToReactiveProcessor = ((ReactiveToFlowProcessor) processor).flow;
        } else if (processor instanceof Flow.Processor) {
            flowToReactiveProcessor = (Flow.Processor) processor;
        } else {
            flowToReactiveProcessor = new FlowToReactiveProcessor(processor);
        }
        return flowToReactiveProcessor;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [java.util.concurrent.Flow$Subscriber] */
    public static <T> Flow.Subscriber<T> toFlowSubscriber(Subscriber<T> subscriber) {
        FlowToReactiveSubscriber flowToReactiveSubscriber;
        Objects.requireNonNull(subscriber, "reactiveStreamsSubscriber");
        if (subscriber instanceof ReactiveToFlowSubscriber) {
            flowToReactiveSubscriber = ((ReactiveToFlowSubscriber) subscriber).flow;
        } else if (subscriber instanceof Flow.Subscriber) {
            flowToReactiveSubscriber = (Flow.Subscriber) subscriber;
        } else {
            flowToReactiveSubscriber = new FlowToReactiveSubscriber(subscriber);
        }
        return flowToReactiveSubscriber;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.reactivestreams.Subscriber] */
    public static <T> Subscriber<T> toSubscriber(Flow.Subscriber<T> subscriber) {
        ReactiveToFlowSubscriber reactiveToFlowSubscriber;
        Objects.requireNonNull(subscriber, "flowSubscriber");
        if (subscriber instanceof FlowToReactiveSubscriber) {
            reactiveToFlowSubscriber = ((FlowToReactiveSubscriber) subscriber).reactiveStreams;
        } else if (subscriber instanceof Subscriber) {
            reactiveToFlowSubscriber = (Subscriber) subscriber;
        } else {
            reactiveToFlowSubscriber = new ReactiveToFlowSubscriber(subscriber);
        }
        return reactiveToFlowSubscriber;
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$FlowToReactiveSubscription.class */
    static final class FlowToReactiveSubscription implements Flow.Subscription {
        final Subscription reactiveStreams;

        public FlowToReactiveSubscription(Subscription reactive) {
            this.reactiveStreams = reactive;
        }

        @Override // java.util.concurrent.Flow.Subscription
        public void request(long n) {
            this.reactiveStreams.request(n);
        }

        @Override // java.util.concurrent.Flow.Subscription
        public void cancel() {
            this.reactiveStreams.cancel();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$ReactiveToFlowSubscription.class */
    static final class ReactiveToFlowSubscription implements Subscription {
        final Flow.Subscription flow;

        public ReactiveToFlowSubscription(Flow.Subscription flow) {
            this.flow = flow;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.flow.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.flow.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$FlowToReactiveSubscriber.class */
    public static final class FlowToReactiveSubscriber<T> implements Flow.Subscriber<T> {
        final Subscriber<? super T> reactiveStreams;

        public FlowToReactiveSubscriber(Subscriber<? super T> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onSubscribe(Flow.Subscription subscription) {
            this.reactiveStreams.onSubscribe(subscription == null ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onNext(T item) {
            this.reactiveStreams.onNext(item);
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onError(Throwable throwable) {
            this.reactiveStreams.onError(throwable);
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$ReactiveToFlowSubscriber.class */
    public static final class ReactiveToFlowSubscriber<T> implements Subscriber<T> {
        final Flow.Subscriber<? super T> flow;

        public ReactiveToFlowSubscriber(Flow.Subscriber<? super T> flow) {
            this.flow = flow;
        }

        @Override // org.reactivestreams.Subscriber
        public void onSubscribe(Subscription subscription) {
            this.flow.onSubscribe(subscription == null ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T item) {
            this.flow.onNext(item);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable throwable) {
            this.flow.onError(throwable);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.flow.onComplete();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$ReactiveToFlowProcessor.class */
    static final class ReactiveToFlowProcessor<T, U> implements Processor<T, U> {
        final Flow.Processor<? super T, ? extends U> flow;

        public ReactiveToFlowProcessor(Flow.Processor<? super T, ? extends U> flow) {
            this.flow = flow;
        }

        @Override // org.reactivestreams.Subscriber
        public void onSubscribe(Subscription subscription) {
            this.flow.onSubscribe(subscription == null ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            this.flow.onNext(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            this.flow.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            this.flow.onComplete();
        }

        @Override // org.reactivestreams.Publisher
        public void subscribe(Subscriber<? super U> s) {
            this.flow.subscribe(s == null ? null : new FlowToReactiveSubscriber(s));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$FlowToReactiveProcessor.class */
    static final class FlowToReactiveProcessor<T, U> implements Flow.Processor<T, U> {
        final Processor<? super T, ? extends U> reactiveStreams;

        public FlowToReactiveProcessor(Processor<? super T, ? extends U> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onSubscribe(Flow.Subscription subscription) {
            this.reactiveStreams.onSubscribe(subscription == null ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onNext(T t) {
            this.reactiveStreams.onNext(t);
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onError(Throwable t) {
            this.reactiveStreams.onError(t);
        }

        @Override // java.util.concurrent.Flow.Subscriber
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }

        @Override // java.util.concurrent.Flow.Publisher
        public void subscribe(Flow.Subscriber<? super U> s) {
            this.reactiveStreams.subscribe(s == null ? null : new ReactiveToFlowSubscriber(s));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$ReactivePublisherFromFlow.class */
    static final class ReactivePublisherFromFlow<T> implements Publisher<T> {
        final Flow.Publisher<? extends T> flow;

        public ReactivePublisherFromFlow(Flow.Publisher<? extends T> flowPublisher) {
            this.flow = flowPublisher;
        }

        @Override // org.reactivestreams.Publisher
        public void subscribe(Subscriber<? super T> reactive) {
            this.flow.subscribe(reactive == null ? null : new FlowToReactiveSubscriber(reactive));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/FlowAdapters$FlowPublisherFromReactive.class */
    static final class FlowPublisherFromReactive<T> implements Flow.Publisher<T> {
        final Publisher<? extends T> reactiveStreams;

        public FlowPublisherFromReactive(Publisher<? extends T> reactivePublisher) {
            this.reactiveStreams = reactivePublisher;
        }

        @Override // java.util.concurrent.Flow.Publisher
        public void subscribe(Flow.Subscriber<? super T> flow) {
            this.reactiveStreams.subscribe(flow == null ? null : new ReactiveToFlowSubscriber(flow));
        }
    }
}
