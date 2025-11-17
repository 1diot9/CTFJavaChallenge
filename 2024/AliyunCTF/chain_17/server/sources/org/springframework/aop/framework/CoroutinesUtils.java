package org.springframework.aop.framework;

import kotlin.coroutines.Continuation;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import kotlinx.coroutines.reactor.MonoKt;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CoroutinesUtils.class */
abstract class CoroutinesUtils {
    CoroutinesUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object asFlow(Object publisher) {
        return ReactiveFlowKt.asFlow((Publisher) publisher);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Object awaitSingleOrNull(@Nullable Object value, Object continuation) {
        Mono justOrEmpty;
        if (value instanceof Mono) {
            Mono mono = (Mono) value;
            justOrEmpty = mono;
        } else {
            justOrEmpty = Mono.justOrEmpty(value);
        }
        return MonoKt.awaitSingleOrNull(justOrEmpty, (Continuation) continuation);
    }
}
