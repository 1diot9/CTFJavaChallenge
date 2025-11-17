package org.reactivestreams;

/* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/Subscriber.class */
public interface Subscriber<T> {
    void onSubscribe(Subscription subscription);

    void onNext(T t);

    void onError(Throwable th);

    void onComplete();
}
