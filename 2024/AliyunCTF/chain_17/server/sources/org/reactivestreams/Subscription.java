package org.reactivestreams;

/* loaded from: server.jar:BOOT-INF/lib/reactive-streams-1.0.4.jar:org/reactivestreams/Subscription.class */
public interface Subscription {
    void request(long j);

    void cancel();
}
