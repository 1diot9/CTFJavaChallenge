package org.springframework.web.server;

import java.security.Principal;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.reactor.MonoKt;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ServerWebExchange;

/* compiled from: ServerWebExchangeExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��<\n��\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\u001a\u001e\u0010��\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\u0086@¢\u0006\u0002\u0010\u0004\u001a\u001e\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00060\u0001*\u00020\u0003H\u0086@¢\u0006\u0002\u0010\u0004\u001a\u001c\u0010\u0007\u001a\u0002H\b\"\b\b��\u0010\b*\u00020\t*\u00020\u0003H\u0086@¢\u0006\u0002\u0010\u0004\u001a\u0012\u0010\n\u001a\u00020\u000b*\u00020\u0003H\u0086@¢\u0006\u0002\u0010\u0004\u001a-\u0010\f\u001a\u00020\r*\u00020\r2\u001c\u0010\u000e\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u000f¢\u0006\u0002\u0010\u0012¨\u0006\u0013"}, d2 = {"awaitFormData", "Lorg/springframework/util/MultiValueMap;", "", "Lorg/springframework/web/server/ServerWebExchange;", "(Lorg/springframework/web/server/ServerWebExchange;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "awaitMultipartData", "Lorg/springframework/http/codec/multipart/Part;", "awaitPrincipal", "T", "Ljava/security/Principal;", "awaitSession", "Lorg/springframework/web/server/WebSession;", "principal", "Lorg/springframework/web/server/ServerWebExchange$Builder;", "supplier", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lorg/springframework/web/server/ServerWebExchange$Builder;Lkotlin/jvm/functions/Function1;)Lorg/springframework/web/server/ServerWebExchange$Builder;", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/ServerWebExchangeExtensionsKt.class */
public final class ServerWebExchangeExtensionsKt {
    /* JADX WARN: Removed duplicated region for block: B:15:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0058  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object awaitFormData(@org.jetbrains.annotations.NotNull org.springframework.web.server.ServerWebExchange r5, @org.jetbrains.annotations.NotNull kotlin.coroutines.Continuation<? super org.springframework.util.MultiValueMap<java.lang.String, java.lang.String>> r6) {
        /*
            r0 = r6
            boolean r0 = r0 instanceof org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitFormData$1
            if (r0 == 0) goto L27
            r0 = r6
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitFormData$1 r0 = (org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitFormData$1) r0
            r9 = r0
            r0 = r9
            int r0 = r0.label
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r0 & r1
            if (r0 == 0) goto L27
            r0 = r9
            r1 = r0
            int r1 = r1.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            int r1 = r1 - r2
            r0.label = r1
            goto L31
        L27:
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitFormData$1 r0 = new org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitFormData$1
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            r9 = r0
        L31:
            r0 = r9
            java.lang.Object r0 = r0.result
            r8 = r0
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r10 = r0
            r0 = r9
            int r0 = r0.label
            switch(r0) {
                case 0: goto L58;
                case 1: goto L7e;
                default: goto L8a;
            }
        L58:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r5
            reactor.core.publisher.Mono r0 = r0.getFormData()
            r7 = r0
            r0 = r7
            java.lang.String r1 = "this.formData"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            r0 = r7
            r1 = r9
            r2 = r9
            r3 = 1
            r2.label = r3
            java.lang.Object r0 = kotlinx.coroutines.reactor.MonoKt.awaitSingle(r0, r1)
            r1 = r0
            r2 = r10
            if (r1 != r2) goto L83
            r1 = r10
            return r1
        L7e:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r8
        L83:
            r1 = r0
            java.lang.String r2 = "this.formData.awaitSingle()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            return r0
        L8a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.server.ServerWebExchangeExtensionsKt.awaitFormData(org.springframework.web.server.ServerWebExchange, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0058  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object awaitMultipartData(@org.jetbrains.annotations.NotNull org.springframework.web.server.ServerWebExchange r5, @org.jetbrains.annotations.NotNull kotlin.coroutines.Continuation<? super org.springframework.util.MultiValueMap<java.lang.String, org.springframework.http.codec.multipart.Part>> r6) {
        /*
            r0 = r6
            boolean r0 = r0 instanceof org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitMultipartData$1
            if (r0 == 0) goto L27
            r0 = r6
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitMultipartData$1 r0 = (org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitMultipartData$1) r0
            r9 = r0
            r0 = r9
            int r0 = r0.label
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r0 & r1
            if (r0 == 0) goto L27
            r0 = r9
            r1 = r0
            int r1 = r1.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            int r1 = r1 - r2
            r0.label = r1
            goto L31
        L27:
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitMultipartData$1 r0 = new org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitMultipartData$1
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            r9 = r0
        L31:
            r0 = r9
            java.lang.Object r0 = r0.result
            r8 = r0
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r10 = r0
            r0 = r9
            int r0 = r0.label
            switch(r0) {
                case 0: goto L58;
                case 1: goto L7e;
                default: goto L8a;
            }
        L58:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r5
            reactor.core.publisher.Mono r0 = r0.getMultipartData()
            r7 = r0
            r0 = r7
            java.lang.String r1 = "this.multipartData"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            r0 = r7
            r1 = r9
            r2 = r9
            r3 = 1
            r2.label = r3
            java.lang.Object r0 = kotlinx.coroutines.reactor.MonoKt.awaitSingle(r0, r1)
            r1 = r0
            r2 = r10
            if (r1 != r2) goto L83
            r1 = r10
            return r1
        L7e:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r8
        L83:
            r1 = r0
            java.lang.String r2 = "this.multipartData.awaitSingle()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            return r0
        L8a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.server.ServerWebExchangeExtensionsKt.awaitMultipartData(org.springframework.web.server.ServerWebExchange, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0058  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T extends java.security.Principal> java.lang.Object awaitPrincipal(@org.jetbrains.annotations.NotNull org.springframework.web.server.ServerWebExchange r5, @org.jetbrains.annotations.NotNull kotlin.coroutines.Continuation<? super T> r6) {
        /*
            r0 = r6
            boolean r0 = r0 instanceof org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitPrincipal$1
            if (r0 == 0) goto L27
            r0 = r6
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitPrincipal$1 r0 = (org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitPrincipal$1) r0
            r9 = r0
            r0 = r9
            int r0 = r0.label
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r0 & r1
            if (r0 == 0) goto L27
            r0 = r9
            r1 = r0
            int r1 = r1.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            int r1 = r1 - r2
            r0.label = r1
            goto L31
        L27:
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitPrincipal$1 r0 = new org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitPrincipal$1
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            r9 = r0
        L31:
            r0 = r9
            java.lang.Object r0 = r0.result
            r8 = r0
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r10 = r0
            r0 = r9
            int r0 = r0.label
            switch(r0) {
                case 0: goto L58;
                case 1: goto L7e;
                default: goto L8a;
            }
        L58:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r5
            reactor.core.publisher.Mono r0 = r0.getPrincipal()
            r7 = r0
            r0 = r7
            java.lang.String r1 = "this.getPrincipal<T>()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            r0 = r7
            r1 = r9
            r2 = r9
            r3 = 1
            r2.label = r3
            java.lang.Object r0 = kotlinx.coroutines.reactor.MonoKt.awaitSingle(r0, r1)
            r1 = r0
            r2 = r10
            if (r1 != r2) goto L83
            r1 = r10
            return r1
        L7e:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r8
        L83:
            r1 = r0
            java.lang.String r2 = "this.getPrincipal<T>().awaitSingle()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            return r0
        L8a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.server.ServerWebExchangeExtensionsKt.awaitPrincipal(org.springframework.web.server.ServerWebExchange, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0058  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object awaitSession(@org.jetbrains.annotations.NotNull org.springframework.web.server.ServerWebExchange r5, @org.jetbrains.annotations.NotNull kotlin.coroutines.Continuation<? super org.springframework.web.server.WebSession> r6) {
        /*
            r0 = r6
            boolean r0 = r0 instanceof org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitSession$1
            if (r0 == 0) goto L27
            r0 = r6
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitSession$1 r0 = (org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitSession$1) r0
            r9 = r0
            r0 = r9
            int r0 = r0.label
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r0 & r1
            if (r0 == 0) goto L27
            r0 = r9
            r1 = r0
            int r1 = r1.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            int r1 = r1 - r2
            r0.label = r1
            goto L31
        L27:
            org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitSession$1 r0 = new org.springframework.web.server.ServerWebExchangeExtensionsKt$awaitSession$1
            r1 = r0
            r2 = r6
            r1.<init>(r2)
            r9 = r0
        L31:
            r0 = r9
            java.lang.Object r0 = r0.result
            r8 = r0
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r10 = r0
            r0 = r9
            int r0 = r0.label
            switch(r0) {
                case 0: goto L58;
                case 1: goto L7e;
                default: goto L8a;
            }
        L58:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r5
            reactor.core.publisher.Mono r0 = r0.getSession()
            r7 = r0
            r0 = r7
            java.lang.String r1 = "this.session"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            r0 = r7
            r1 = r9
            r2 = r9
            r3 = 1
            r2.label = r3
            java.lang.Object r0 = kotlinx.coroutines.reactor.MonoKt.awaitSingle(r0, r1)
            r1 = r0
            r2 = r10
            if (r1 != r2) goto L83
            r1 = r10
            return r1
        L7e:
            r0 = r8
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r8
        L83:
            r1 = r0
            java.lang.String r2 = "this.session.awaitSingle()"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
            return r0
        L8a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.server.ServerWebExchangeExtensionsKt.awaitSession(org.springframework.web.server.ServerWebExchange, kotlin.coroutines.Continuation):java.lang.Object");
    }

    @NotNull
    public static final ServerWebExchange.Builder principal(@NotNull ServerWebExchange.Builder $this$principal, @NotNull Function1<? super Continuation<? super Principal>, ? extends Object> supplier) {
        Intrinsics.checkNotNullParameter($this$principal, "<this>");
        Intrinsics.checkNotNullParameter(supplier, "supplier");
        ServerWebExchange.Builder principal = $this$principal.principal(MonoKt.mono(Dispatchers.getUnconfined(), new ServerWebExchangeExtensionsKt$principal$1(supplier, null)));
        Intrinsics.checkNotNullExpressionValue(principal, "supplier: suspend () -> …d) { supplier.invoke() })");
        return principal;
    }
}
