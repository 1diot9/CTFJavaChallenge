package org.springframework.aot.hint;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.aot.hint.TypeHint;

/* compiled from: TypeHintExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\n\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u001d\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001\"\u0006\b��\u0010\u0003\u0018\u0001*\u00020\u0001H\u0086\b¨\u0006\u0004"}, d2 = {"onReachableType", "Lorg/springframework/aot/hint/TypeHint$Builder;", "kotlin.jvm.PlatformType", "T", "spring-core"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/TypeHintExtensionsKt.class */
public final class TypeHintExtensionsKt {
    public static final /* synthetic */ <T> TypeHint.Builder onReachableType(TypeHint.Builder $this$onReachableType) {
        Intrinsics.checkNotNullParameter($this$onReachableType, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$onReachableType.onReachableType(Object.class);
    }
}
