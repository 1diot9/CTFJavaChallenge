package org.springframework.cache;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: CacheManagerExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0012\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n��\u001a\u0017\u0010��\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\u0002¨\u0006\u0005"}, d2 = {BeanUtil.PREFIX_GETTER_GET, "Lorg/springframework/cache/Cache;", "Lorg/springframework/cache/CacheManager;", "name", "", "spring-context"})
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/CacheManagerExtensionsKt.class */
public final class CacheManagerExtensionsKt {
    @Nullable
    public static final Cache get(@NotNull CacheManager $this$get, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$get, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        return $this$get.getCache(name);
    }
}
