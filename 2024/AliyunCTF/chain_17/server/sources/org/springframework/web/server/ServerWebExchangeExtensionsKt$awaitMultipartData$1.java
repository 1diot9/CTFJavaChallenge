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
@DebugMetadata(f = "ServerWebExchangeExtensions.kt", l = {42}, i = {}, s = {}, n = {}, m = "awaitMultipartData", c = "org.springframework.web.server.ServerWebExchangeExtensionsKt")
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchangeExtensionsKt$awaitMultipartData$1.class */
public final class ServerWebExchangeExtensionsKt$awaitMultipartData$1 extends ContinuationImpl {
    /* synthetic */ Object result;
    int label;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ServerWebExchangeExtensionsKt$awaitMultipartData$1(Continuation<? super ServerWebExchangeExtensionsKt$awaitMultipartData$1> $completion) {
        super($completion);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        this.result = $result;
        this.label |= Integer.MIN_VALUE;
        return ServerWebExchangeExtensionsKt.awaitMultipartData(null, (Continuation) this);
    }
}
