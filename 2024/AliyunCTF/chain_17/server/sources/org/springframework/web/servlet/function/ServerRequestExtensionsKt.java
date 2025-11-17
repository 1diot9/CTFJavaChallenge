package org.springframework.web.servlet.function;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.OptionalLong;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;

/* compiled from: ServerRequestExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��4\n��\n\u0002\u0010��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\u001a\u0014\u0010��\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001e\u0010\u0005\u001a\u0002H\u0006\"\n\b��\u0010\u0006\u0018\u0001*\u00020\u0001*\u00020\u0002H\u0086\b¢\u0006\u0002\u0010\u0007\u001a\u0011\u0010\b\u001a\u0004\u0018\u00010\t*\u00020\n¢\u0006\u0002\u0010\u000b\u001a\f\u0010\f\u001a\u0004\u0018\u00010\r*\u00020\n\u001a\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u0004*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\f\u0010\u000f\u001a\u0004\u0018\u00010\u0010*\u00020\u0002\u001a\f\u0010\u0011\u001a\u0004\u0018\u00010\u0012*\u00020\u0002¨\u0006\u0013"}, d2 = {"attributeOrNull", "", "Lorg/springframework/web/servlet/function/ServerRequest;", "name", "", "body", "T", "(Lorg/springframework/web/servlet/function/ServerRequest;)Ljava/lang/Object;", "contentLengthOrNull", "", "Lorg/springframework/web/servlet/function/ServerRequest$Headers;", "(Lorg/springframework/web/servlet/function/ServerRequest$Headers;)Ljava/lang/Long;", "contentTypeOrNull", "Lorg/springframework/http/MediaType;", "paramOrNull", "principalOrNull", "Ljava/security/Principal;", "remoteAddressOrNull", "Ljava/net/InetSocketAddress;", "spring-webmvc"})
@SourceDebugExtension({"SMAP\nServerRequestExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ServerRequestExtensions.kt\norg/springframework/web/servlet/function/ServerRequestExtensionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,81:1\n1#2:82\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerRequestExtensionsKt.class */
public final class ServerRequestExtensionsKt {
    @Nullable
    public static final InetSocketAddress remoteAddressOrNull(@NotNull ServerRequest $this$remoteAddressOrNull) {
        Intrinsics.checkNotNullParameter($this$remoteAddressOrNull, "<this>");
        return $this$remoteAddressOrNull.remoteAddress().orElse(null);
    }

    public static final /* synthetic */ <T> T body(ServerRequest serverRequest) {
        Intrinsics.checkNotNullParameter(serverRequest, "<this>");
        Intrinsics.needClassReification();
        T t = (T) serverRequest.body(new ParameterizedTypeReference<T>() { // from class: org.springframework.web.servlet.function.ServerRequestExtensionsKt$body$1
        });
        Intrinsics.checkNotNullExpressionValue(t, "body(object : ParameterizedTypeReference<T>() {})");
        return t;
    }

    @Nullable
    public static final Object attributeOrNull(@NotNull ServerRequest $this$attributeOrNull, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$attributeOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        return $this$attributeOrNull.attribute(name).orElse(null);
    }

    @Nullable
    public static final String paramOrNull(@NotNull ServerRequest $this$paramOrNull, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$paramOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        return $this$paramOrNull.param(name).orElse(null);
    }

    @Nullable
    public static final Principal principalOrNull(@NotNull ServerRequest $this$principalOrNull) {
        Intrinsics.checkNotNullParameter($this$principalOrNull, "<this>");
        return $this$principalOrNull.principal().orElse(null);
    }

    @Nullable
    public static final Long contentLengthOrNull(@NotNull ServerRequest.Headers $this$contentLengthOrNull) {
        Intrinsics.checkNotNullParameter($this$contentLengthOrNull, "<this>");
        OptionalLong it = $this$contentLengthOrNull.contentLength();
        if (it.isPresent()) {
            return Long.valueOf(it.getAsLong());
        }
        return null;
    }

    @Nullable
    public static final MediaType contentTypeOrNull(@NotNull ServerRequest.Headers $this$contentTypeOrNull) {
        Intrinsics.checkNotNullParameter($this$contentTypeOrNull, "<this>");
        return $this$contentTypeOrNull.contentType().orElse(null);
    }
}
