package org.springframework.web.service.invoker;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: HttpServiceProxyFactoryExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0010\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0004¨\u0006\u0005"}, d2 = {"createClient", "T", "", "Lorg/springframework/web/service/invoker/HttpServiceProxyFactory;", "(Lorg/springframework/web/service/invoker/HttpServiceProxyFactory;)Ljava/lang/Object;", "spring-web"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceProxyFactoryExtensionsKt.class */
public final class HttpServiceProxyFactoryExtensionsKt {
    public static final /* synthetic */ <T> T createClient(HttpServiceProxyFactory httpServiceProxyFactory) {
        Intrinsics.checkNotNullParameter(httpServiceProxyFactory, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) httpServiceProxyFactory.createClient(Object.class);
        Intrinsics.checkNotNullExpressionValue(t, "createClient(T::class.java)");
        return t;
    }
}
