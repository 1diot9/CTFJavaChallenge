package org.springframework.aot.hint;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ResourceHintsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\n\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u001d\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001\"\u0006\b��\u0010\u0003\u0018\u0001*\u00020\u0001H\u0086\b¨\u0006\u0004"}, d2 = {"registerType", "Lorg/springframework/aot/hint/ResourceHints;", "kotlin.jvm.PlatformType", "T", "spring-core"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ResourceHintsExtensionsKt.class */
public final class ResourceHintsExtensionsKt {
    public static final /* synthetic */ <T> ResourceHints registerType(ResourceHints $this$registerType) {
        Intrinsics.checkNotNullParameter($this$registerType, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$registerType.registerType(Object.class);
    }
}
