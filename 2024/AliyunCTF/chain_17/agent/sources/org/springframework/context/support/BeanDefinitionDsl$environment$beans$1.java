package org.springframework.context.support;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.ConfigurableEnvironment;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 48)
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/BeanDefinitionDsl$environment$beans$1.class */
/* synthetic */ class BeanDefinitionDsl$environment$beans$1 extends FunctionReferenceImpl implements Function1<ConfigurableEnvironment, Boolean> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionDsl$environment$beans$1(Object receiver) {
        super(1, receiver, Function1.class, "invoke", "invoke(Ljava/lang/Object;)Ljava/lang/Object;", 0);
    }

    @NotNull
    public final Boolean invoke(@NotNull ConfigurableEnvironment p0) {
        Intrinsics.checkNotNullParameter(p0, "p0");
        return (Boolean) ((Function1) this.receiver).invoke(p0);
    }
}
