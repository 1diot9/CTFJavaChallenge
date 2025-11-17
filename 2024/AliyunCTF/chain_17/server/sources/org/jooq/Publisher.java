package org.jooq;

import java.util.concurrent.Flow;
import org.reactivestreams.FlowAdapters;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Publisher.class */
public interface Publisher<T> extends org.reactivestreams.Publisher<T>, Flow.Publisher<T> {
    @Override // java.util.concurrent.Flow.Publisher
    default void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscribe(FlowAdapters.toSubscriber(subscriber));
    }
}
