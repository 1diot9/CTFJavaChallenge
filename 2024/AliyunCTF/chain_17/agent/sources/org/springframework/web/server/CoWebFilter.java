package org.springframework.web.server;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.reactor.MonoKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/* compiled from: CoWebFilter.kt */
@Metadata(mv = {1, 7, 0}, k = 1, xi = 48, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018�� \r2\u00020\u0001:\u0001\rB\u0005¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH¤@¢\u0006\u0002\u0010\tJ\u001c\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\f¨\u0006\u000e"}, d2 = {"Lorg/springframework/web/server/CoWebFilter;", "Lorg/springframework/web/server/WebFilter;", "()V", "filter", "", "exchange", "Lorg/springframework/web/server/ServerWebExchange;", "chain", "Lorg/springframework/web/server/CoWebFilterChain;", "(Lorg/springframework/web/server/ServerWebExchange;Lorg/springframework/web/server/CoWebFilterChain;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Lreactor/core/publisher/Mono;", "Ljava/lang/Void;", "Lorg/springframework/web/server/WebFilterChain;", "Companion", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/CoWebFilter.class */
public abstract class CoWebFilter implements WebFilter {

    @NotNull
    public static final Companion Companion = new Companion(null);

    @JvmField
    @NotNull
    public static final String COROUTINE_CONTEXT_ATTRIBUTE = CoWebFilter.class.getName() + ".context";

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public abstract Object filter(@NotNull ServerWebExchange exchange, @NotNull CoWebFilterChain chain, @NotNull Continuation<? super Unit> $completion);

    @Override // org.springframework.web.server.WebFilter
    @NotNull
    public final Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        Intrinsics.checkNotNullParameter(exchange, "exchange");
        Intrinsics.checkNotNullParameter(chain, "chain");
        CoroutineContext context = (CoroutineContext) exchange.getAttributes().get(COROUTINE_CONTEXT_ATTRIBUTE);
        CoroutineContext coroutineContext = context;
        if (coroutineContext == null) {
            coroutineContext = (CoroutineContext) Dispatchers.getUnconfined();
        }
        Mono<Void> then = MonoKt.mono(coroutineContext, new CoWebFilter$filter$1(this, exchange, chain, null)).then();
        Intrinsics.checkNotNullExpressionValue(then, "final override fun filte…()\n\t\t\t\t}\n\t\t\t})}.then()\n\t}");
        return then;
    }

    /* compiled from: CoWebFilter.kt */
    @Metadata(mv = {1, 7, 0}, k = 1, xi = 48, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n��¨\u0006\u0005"}, d2 = {"Lorg/springframework/web/server/CoWebFilter$Companion;", "", "()V", "COROUTINE_CONTEXT_ATTRIBUTE", "", "spring-web"})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/CoWebFilter$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
