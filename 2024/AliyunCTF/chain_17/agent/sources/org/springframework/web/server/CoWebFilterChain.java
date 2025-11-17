package org.springframework.web.server;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CoWebFilter.kt */
@Metadata(mv = {1, 7, 0}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018��2\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H¦@¢\u0006\u0002\u0010\u0006¨\u0006\u0007"}, d2 = {"Lorg/springframework/web/server/CoWebFilterChain;", "", "filter", "", "exchange", "Lorg/springframework/web/server/ServerWebExchange;", "(Lorg/springframework/web/server/ServerWebExchange;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/CoWebFilterChain.class */
public interface CoWebFilterChain {
    @Nullable
    Object filter(@NotNull ServerWebExchange exchange, @NotNull Continuation<? super Unit> $completion);
}
