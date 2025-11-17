package org.springframework.util.concurrent;

import reactor.core.publisher.Mono;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/concurrent/MonoToListenableFutureAdapter.class */
public class MonoToListenableFutureAdapter<T> extends CompletableToListenableFutureAdapter<T> {
    public MonoToListenableFutureAdapter(Mono<T> mono) {
        super(mono.toFuture());
    }
}
