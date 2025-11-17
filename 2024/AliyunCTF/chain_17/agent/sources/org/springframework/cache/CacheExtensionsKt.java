package org.springframework.cache;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.Cache;

/* compiled from: CacheExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u001c\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u0017\u0010��\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\u0002\u001a(\u0010��\u001a\u0004\u0018\u0001H\u0005\"\n\b��\u0010\u0005\u0018\u0001*\u00020\u0004*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0086\b¢\u0006\u0002\u0010\u0006\u001a\u001f\u0010\u0007\u001a\u00020\b*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0086\u0002¨\u0006\n"}, d2 = {BeanUtil.PREFIX_GETTER_GET, "Lorg/springframework/cache/Cache$ValueWrapper;", "Lorg/springframework/cache/Cache;", "key", "", "T", "(Lorg/springframework/cache/Cache;Ljava/lang/Object;)Ljava/lang/Object;", "set", "", "value", "spring-context"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/CacheExtensionsKt.class */
public final class CacheExtensionsKt {
    public static final /* synthetic */ <T> T get(Cache cache, Object obj) {
        Intrinsics.checkNotNullParameter(cache, "<this>");
        Intrinsics.checkNotNullParameter(obj, "key");
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) cache.get(obj, Object.class);
    }

    @Nullable
    /* renamed from: get, reason: collision with other method in class */
    public static final Cache.ValueWrapper m2249get(@NotNull Cache $this$get, @NotNull Object key) {
        Intrinsics.checkNotNullParameter($this$get, "<this>");
        Intrinsics.checkNotNullParameter(key, "key");
        return $this$get.get(key);
    }

    public static final void set(@NotNull Cache $this$set, @NotNull Object key, @Nullable Object value) {
        Intrinsics.checkNotNullParameter($this$set, "<this>");
        Intrinsics.checkNotNullParameter(key, "key");
        $this$set.put(key, value);
    }
}
