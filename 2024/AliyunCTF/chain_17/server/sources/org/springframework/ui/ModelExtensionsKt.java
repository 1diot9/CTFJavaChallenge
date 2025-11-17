package org.springframework.ui;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ModelExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0018\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010��\n��\u001a\u001f\u0010��\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0086\u0002¨\u0006\u0007"}, d2 = {"set", "", "Lorg/springframework/ui/Model;", "attributeName", "", "attributeValue", "", "spring-context"})
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/ModelExtensionsKt.class */
public final class ModelExtensionsKt {
    public static final void set(@NotNull Model $this$set, @NotNull String attributeName, @Nullable Object attributeValue) {
        Intrinsics.checkNotNullParameter($this$set, "<this>");
        Intrinsics.checkNotNullParameter(attributeName, "attributeName");
        $this$set.addAttribute(attributeName, attributeValue);
    }
}
