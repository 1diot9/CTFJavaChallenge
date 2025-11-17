package org.springframework.aot.hint;

import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.springframework.aot.hint.JavaSerializationHint;

/* compiled from: SerializationHintsExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 176)
@SourceDebugExtension({"SMAP\nSerializationHintsExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SerializationHintsExtensions.kt\norg/springframework/aot/hint/SerializationHintsExtensionsKt$registerType$2\n*L\n1#1,30:1\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/SerializationHintsExtensionsKt$registerType$2.class */
public /* synthetic */ class SerializationHintsExtensionsKt$registerType$2 implements Consumer {
    final /* synthetic */ Function1<JavaSerializationHint.Builder, Unit> $tmp0;

    /* JADX WARN: Multi-variable type inference failed */
    public SerializationHintsExtensionsKt$registerType$2(Function1<? super JavaSerializationHint.Builder, Unit> $tmp0) {
        this.$tmp0 = $tmp0;
    }

    @Override // java.util.function.Consumer
    public final void accept(@NotNull JavaSerializationHint.Builder p0) {
        Intrinsics.checkNotNullParameter(p0, "p0");
        this.$tmp0.invoke(p0);
    }
}
