package org.springframework.aot.hint;

import java.util.Arrays;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.aot.hint.TypeHint;

/* compiled from: ReflectionHintsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��*\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n��\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0017\u0010��\u001a\u0004\u0018\u00010\u0001\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u0003H\u0086\b\u001a6\u0010\u0004\u001a\n \u0005*\u0004\u0018\u00010\u00030\u0003\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0014\b\b\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007H\u0086\bø\u0001��\u001a6\u0010\u0004\u001a\n \u0005*\u0004\u0018\u00010\u00030\u0003\"\u0006\b��\u0010\u0002\u0018\u0001*\u00020\u00032\u0012\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\f0\u000b\"\u00020\fH\u0086\b¢\u0006\u0002\u0010\r\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u000e"}, d2 = {"getTypeHint", "Lorg/springframework/aot/hint/TypeHint;", "T", "Lorg/springframework/aot/hint/ReflectionHints;", "registerType", "kotlin.jvm.PlatformType", "typeHint", "Lkotlin/Function1;", "Lorg/springframework/aot/hint/TypeHint$Builder;", "", "memberCategories", "", "Lorg/springframework/aot/hint/MemberCategory;", "(Lorg/springframework/aot/hint/ReflectionHints;[Lorg/springframework/aot/hint/MemberCategory;)Lorg/springframework/aot/hint/ReflectionHints;", "spring-core"})
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ReflectionHintsExtensionsKt.class */
public final class ReflectionHintsExtensionsKt {
    public static final /* synthetic */ <T> TypeHint getTypeHint(ReflectionHints $this$getTypeHint) {
        Intrinsics.checkNotNullParameter($this$getTypeHint, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$getTypeHint.getTypeHint(Object.class);
    }

    public static final /* synthetic */ <T> ReflectionHints registerType(ReflectionHints $this$registerType, final Function1<? super TypeHint.Builder, Unit> function1) {
        Intrinsics.checkNotNullParameter($this$registerType, "<this>");
        Intrinsics.checkNotNullParameter(function1, "typeHint");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$registerType.registerType(Object.class, new Consumer() { // from class: org.springframework.aot.hint.ReflectionHintsExtensionsKt$registerType$1
            @Override // java.util.function.Consumer
            public final void accept(@NotNull TypeHint.Builder p0) {
                Intrinsics.checkNotNullParameter(p0, "p0");
                function1.invoke(p0);
            }
        });
    }

    public static final /* synthetic */ <T> ReflectionHints registerType(ReflectionHints $this$registerType, MemberCategory... memberCategories) {
        Intrinsics.checkNotNullParameter($this$registerType, "<this>");
        Intrinsics.checkNotNullParameter(memberCategories, "memberCategories");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$registerType.registerType(Object.class, (MemberCategory[]) Arrays.copyOf(memberCategories, memberCategories.length));
    }
}
