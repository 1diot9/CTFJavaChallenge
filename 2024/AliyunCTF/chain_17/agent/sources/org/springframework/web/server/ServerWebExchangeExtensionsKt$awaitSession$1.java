package org.springframework.web.server;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ServerWebExchangeExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 48)
@DebugMetadata(f = "ServerWebExchangeExtensions.kt", l = {60}, i = {}, s = {}, n = {}, m = "awaitSession", c = "org.springframework.web.server.ServerWebExchangeExtensionsKt")
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchangeExtensionsKt$awaitSession$1.class */
public final class ServerWebExchangeExtensionsKt$awaitSession$1 extends ContinuationImpl {
    /* synthetic */ Object result;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServerWebExchangeExtensionsKt$awaitSession$1(Continuation<? super ServerWebExchangeExtensionsKt$awaitSession$1> $completion) {
        super($completion);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        this.result = $result;
        this.label |= Integer.MIN_VALUE;
        return ServerWebExchangeExtensionsKt.awaitSession(null, (Continuation) this);
    }
}
