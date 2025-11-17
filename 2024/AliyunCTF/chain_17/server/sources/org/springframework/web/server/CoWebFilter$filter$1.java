package org.springframework.web.server;

import java.util.Map;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.reactor.MonoKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/* compiled from: CoWebFilter.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
@DebugMetadata(f = "CoWebFilter.kt", l = {40}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "org.springframework.web.server.CoWebFilter$filter$1")
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/CoWebFilter$filter$1.class */
final class CoWebFilter$filter$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ CoWebFilter this$0;
    final /* synthetic */ ServerWebExchange $exchange;
    final /* synthetic */ WebFilterChain $chain;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public CoWebFilter$filter$1(CoWebFilter this$0, ServerWebExchange $exchange, WebFilterChain $chain, Continuation<? super CoWebFilter$filter$1> $completion) {
        super(2, $completion);
        this.this$0 = this$0;
        this.$exchange = $exchange;
        this.$chain = $chain;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
        return new CoWebFilter$filter$1(this.this$0, this.$exchange, this.$chain, $completion);
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super Unit> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                CoWebFilter coWebFilter = this.this$0;
                ServerWebExchange serverWebExchange = this.$exchange;
                final WebFilterChain webFilterChain = this.$chain;
                this.label = 1;
                if (coWebFilter.filter(serverWebExchange, new CoWebFilterChain() { // from class: org.springframework.web.server.CoWebFilter$filter$1.1
                    @Override // org.springframework.web.server.CoWebFilterChain
                    @Nullable
                    public Object filter(@NotNull ServerWebExchange exchange, @NotNull Continuation<? super Unit> $completion) {
                        Map<String, Object> attributes = exchange.getAttributes();
                        Intrinsics.checkNotNullExpressionValue(attributes, "exchange.attributes");
                        attributes.put(CoWebFilter.COROUTINE_CONTEXT_ATTRIBUTE, $completion.getContext().minusKey(Job.Key));
                        Mono<Void> filter = WebFilterChain.this.filter(exchange);
                        Intrinsics.checkNotNullExpressionValue(filter, "chain.filter(exchange)");
                        Object awaitSingleOrNull = MonoKt.awaitSingleOrNull(filter, $completion);
                        return awaitSingleOrNull == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? awaitSingleOrNull : Unit.INSTANCE;
                    }
                }, (Continuation) this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                break;
            case 1:
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }
}
