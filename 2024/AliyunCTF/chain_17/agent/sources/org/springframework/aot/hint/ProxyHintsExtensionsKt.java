package org.springframework.aot.hint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

/* compiled from: ProxyHintsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0016\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a3\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001*\u00020\u00012\u001a\u0010\u0003\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00050\u0004\"\u0006\u0012\u0002\b\u00030\u0005¢\u0006\u0002\u0010\u0006¨\u0006\u0007"}, d2 = {"registerJdkProxy", "Lorg/springframework/aot/hint/ProxyHints;", "kotlin.jvm.PlatformType", "proxiedInterfaces", "", "Lkotlin/reflect/KClass;", "(Lorg/springframework/aot/hint/ProxyHints;[Lkotlin/reflect/KClass;)Lorg/springframework/aot/hint/ProxyHints;", "spring-core"})
@SourceDebugExtension({"SMAP\nProxyHintsExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ProxyHintsExtensions.kt\norg/springframework/aot/hint/ProxyHintsExtensionsKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,29:1\n11065#2:30\n11400#2,3:31\n37#3,2:34\n*S KotlinDebug\n*F\n+ 1 ProxyHintsExtensions.kt\norg/springframework/aot/hint/ProxyHintsExtensionsKt\n*L\n28#1:30\n28#1:31,3\n28#1:34,2\n*E\n"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ProxyHintsExtensionsKt.class */
public final class ProxyHintsExtensionsKt {
    public static final ProxyHints registerJdkProxy(@NotNull ProxyHints $this$registerJdkProxy, @NotNull KClass<?>... proxiedInterfaces) {
        Intrinsics.checkNotNullParameter($this$registerJdkProxy, "<this>");
        Intrinsics.checkNotNullParameter(proxiedInterfaces, "proxiedInterfaces");
        Collection destination$iv$iv = new ArrayList(proxiedInterfaces.length);
        for (KClass<?> kClass : proxiedInterfaces) {
            destination$iv$iv.add(JvmClassMappingKt.getJavaClass(kClass));
        }
        Collection $this$toTypedArray$iv = (List) destination$iv$iv;
        Class[] clsArr = (Class[]) $this$toTypedArray$iv.toArray(new Class[0]);
        return $this$registerJdkProxy.registerJdkProxy((Class<?>[]) Arrays.copyOf(clsArr, clsArr.length));
    }
}
