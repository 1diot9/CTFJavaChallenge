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
import org.springframework.aot.hint.JdkProxyHint;

/* compiled from: JdkProxyHintExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u0014\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a3\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001*\u00020\u00012\u001a\u0010��\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00040\u0003\"\u0006\u0012\u0002\b\u00030\u0004¢\u0006\u0002\u0010\u0005¨\u0006\u0006"}, d2 = {"proxiedInterfaces", "Lorg/springframework/aot/hint/JdkProxyHint$Builder;", "kotlin.jvm.PlatformType", "", "Lkotlin/reflect/KClass;", "(Lorg/springframework/aot/hint/JdkProxyHint$Builder;[Lkotlin/reflect/KClass;)Lorg/springframework/aot/hint/JdkProxyHint$Builder;", "spring-core"})
@SourceDebugExtension({"SMAP\nJdkProxyHintExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JdkProxyHintExtensions.kt\norg/springframework/aot/hint/JdkProxyHintExtensionsKt\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,29:1\n11065#2:30\n11400#2,3:31\n37#3,2:34\n*S KotlinDebug\n*F\n+ 1 JdkProxyHintExtensions.kt\norg/springframework/aot/hint/JdkProxyHintExtensionsKt\n*L\n28#1:30\n28#1:31,3\n28#1:34,2\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/JdkProxyHintExtensionsKt.class */
public final class JdkProxyHintExtensionsKt {
    public static final JdkProxyHint.Builder proxiedInterfaces(@NotNull JdkProxyHint.Builder $this$proxiedInterfaces, @NotNull KClass<?>... proxiedInterfaces) {
        Intrinsics.checkNotNullParameter($this$proxiedInterfaces, "<this>");
        Intrinsics.checkNotNullParameter(proxiedInterfaces, "proxiedInterfaces");
        Collection destination$iv$iv = new ArrayList(proxiedInterfaces.length);
        for (KClass<?> kClass : proxiedInterfaces) {
            destination$iv$iv.add(JvmClassMappingKt.getJavaClass(kClass));
        }
        Collection $this$toTypedArray$iv = (List) destination$iv$iv;
        Class[] clsArr = (Class[]) $this$toTypedArray$iv.toArray(new Class[0]);
        return $this$proxiedInterfaces.proxiedInterfaces((Class<?>[]) Arrays.copyOf(clsArr, clsArr.length));
    }
}
