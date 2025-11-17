package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"�� \n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a%\u0010��\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004¢\u0006\u0002\b\u0007\u001aO\u0010\b\u001a&\u0012\f\u0012\n \n*\u0004\u0018\u0001H\tH\t \n*\u0012\u0012\f\u0012\n \n*\u0004\u0018\u0001H\tH\t\u0018\u00010\u00010\u0001\"\b\b��\u0010\t*\u00020\u0002*\b\u0012\u0004\u0012\u0002H\t0\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\t0\u0001H\u0086\u0002¨\u0006\f"}, d2 = {"router", "Lorg/springframework/web/servlet/function/RouterFunction;", "Lorg/springframework/web/servlet/function/ServerResponse;", "routes", "Lkotlin/Function1;", "Lorg/springframework/web/servlet/function/RouterFunctionDsl;", "", "Lkotlin/ExtensionFunctionType;", "plus", "T", "kotlin.jvm.PlatformType", "other", "spring-webmvc"})
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RouterFunctionDslKt.class */
public final class RouterFunctionDslKt {
    @NotNull
    public static final RouterFunction<ServerResponse> router(@NotNull Function1<? super RouterFunctionDsl, Unit> routes) {
        Intrinsics.checkNotNullParameter(routes, "routes");
        return new RouterFunctionDsl(routes).build$spring_webmvc();
    }

    public static final <T extends ServerResponse> RouterFunction<T> plus(@NotNull RouterFunction<T> $this$plus, @NotNull RouterFunction<T> other) {
        Intrinsics.checkNotNullParameter($this$plus, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return $this$plus.and(other);
    }
}
