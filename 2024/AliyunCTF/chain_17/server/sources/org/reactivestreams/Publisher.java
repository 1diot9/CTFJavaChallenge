package org.reactivestreams;

/* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/Publisher.class */
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
