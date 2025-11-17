package org.springframework.aot.hint;

import java.io.Serializable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.aot.hint.JavaSerializationHint;

/* compiled from: SerializationHintsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��\u001e\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n��\u001a:\u0010��\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001\"\n\b��\u0010\u0003\u0018\u0001*\u00020\u0004*\u00020\u00012\u0014\b\n\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006H\u0086\bø\u0001��\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\t"}, d2 = {"registerType", "Lorg/springframework/aot/hint/SerializationHints;", "kotlin.jvm.PlatformType", "T", "Ljava/io/Serializable;", "serializationHint", "Lkotlin/Function1;", "Lorg/springframework/aot/hint/JavaSerializationHint$Builder;", "", "spring-core"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/SerializationHintsExtensionsKt.class */
public final class SerializationHintsExtensionsKt {
    public static /* synthetic */ SerializationHints registerType$default(SerializationHints $this$registerType_u24default, Function1 serializationHint, int i, Object obj) {
        if ((i & 1) != 0) {
            serializationHint = new Function1<JavaSerializationHint.Builder, Unit>() { // from class: org.springframework.aot.hint.SerializationHintsExtensionsKt$registerType$1
                public final void invoke(@NotNull JavaSerializationHint.Builder it) {
                    Intrinsics.checkNotNullParameter(it, "it");
                }

                public /* bridge */ /* synthetic */ Object invoke(Object p1) {
                    invoke((JavaSerializationHint.Builder) p1);
                    return Unit.INSTANCE;
                }
            };
        }
        Intrinsics.checkNotNullParameter($this$registerType_u24default, "<this>");
        Intrinsics.checkNotNullParameter(serializationHint, "serializationHint");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$registerType_u24default.registerType(Serializable.class, new SerializationHintsExtensionsKt$registerType$2(serializationHint));
    }

    public static final /* synthetic */ <T extends Serializable> SerializationHints registerType(SerializationHints $this$registerType, Function1<? super JavaSerializationHint.Builder, Unit> function1) {
        Intrinsics.checkNotNullParameter($this$registerType, "<this>");
        Intrinsics.checkNotNullParameter(function1, "serializationHint");
        Intrinsics.reifiedOperationMarker(4, "T");
        return $this$registerType.registerType(Serializable.class, new SerializationHintsExtensionsKt$registerType$2(function1));
    }
}
