package org.springframework.web.server;

import java.security.Principal;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.asm.TypeReference;

/* compiled from: ServerWebExchangeExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010��\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "Ljava/security/Principal;", "Lkotlinx/coroutines/CoroutineScope;"})
@DebugMetadata(f = "ServerWebExchangeExtensions.kt", l = {TypeReference.CONSTRUCTOR_REFERENCE}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "org.springframework.web.server.ServerWebExchangeExtensionsKt$principal$1")
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchangeExtensionsKt$principal$1.class */
final class ServerWebExchangeExtensionsKt$principal$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Principal>, Object> {
    int label;
    final /* synthetic */ Function1<Continuation<? super Principal>, Object> $supplier;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ServerWebExchangeExtensionsKt$principal$1(Function1<? super Continuation<? super Principal>, ? extends Object> $supplier, Continuation<? super ServerWebExchangeExtensionsKt$principal$1> $completion) {
        super(2, $completion);
        this.$supplier = $supplier;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
        return new ServerWebExchangeExtensionsKt$principal$1(this.$supplier, $completion);
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super Principal> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                Function1<Continuation<? super Principal>, Object> function1 = this.$supplier;
                this.label = 1;
                Object invoke = function1.invoke(this);
                return invoke == coroutine_suspended ? coroutine_suspended : invoke;
            case 1:
                ResultKt.throwOnFailure($result);
                return $result;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
}
