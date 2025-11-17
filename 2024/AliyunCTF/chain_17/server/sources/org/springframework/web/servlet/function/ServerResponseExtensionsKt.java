package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.servlet.function.ServerResponse;

/* compiled from: ServerResponseExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0016\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a.\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001\"\n\b��\u0010\u0003\u0018\u0001*\u00020\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u0002H\u0003H\u0086\b¢\u0006\u0002\u0010\u0007¨\u0006\b"}, d2 = {"bodyWithType", "Lorg/springframework/web/servlet/function/ServerResponse;", "kotlin.jvm.PlatformType", "T", "", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", "body", "(Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;Ljava/lang/Object;)Lorg/springframework/web/servlet/function/ServerResponse;", "spring-webmvc"})
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/ServerResponseExtensionsKt.class */
public final class ServerResponseExtensionsKt {
    public static final /* synthetic */ <T> ServerResponse bodyWithType(ServerResponse.BodyBuilder $this$bodyWithType, T t) {
        Intrinsics.checkNotNullParameter($this$bodyWithType, "<this>");
        Intrinsics.checkNotNullParameter(t, "body");
        Intrinsics.needClassReification();
        return $this$bodyWithType.body(t, new ParameterizedTypeReference<T>() { // from class: org.springframework.web.servlet.function.ServerResponseExtensionsKt$bodyWithType$1
        });
    }
}
