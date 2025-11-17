package org.springframework.context.support;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.springframework.context.support.BeanDefinitionDsl;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 176, d1 = {"��\f\n\u0002\b\u0003\n\u0002\u0010��\n\u0002\b\u0003\u0010��\u001a\n \u0002*\u0004\u0018\u0001H\u0001H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0003H\n¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"}, d2 = {"<anonymous>", "T", "kotlin.jvm.PlatformType", "", BeanUtil.PREFIX_GETTER_GET, "()Ljava/lang/Object;", "org/springframework/context/support/BeanDefinitionDsl$bean$1"})
@SourceDebugExtension({"SMAP\nBeanDefinitionDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl$bean$1\n+ 2 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl\n*L\n1#1,1214:1\n281#2:1215\n*E\n"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/BeanDefinitionDsl$bean$$inlined$bean$1.class */
public final class BeanDefinitionDsl$bean$$inlined$bean$1<T> implements Supplier {
    final /* synthetic */ BeanDefinitionDsl this$0;
    final /* synthetic */ Function0 $f$inlined;

    public BeanDefinitionDsl$bean$$inlined$bean$1(BeanDefinitionDsl $receiver, Function0 function0) {
        this.this$0 = $receiver;
        this.$f$inlined = function0;
    }

    @Override // java.util.function.Supplier
    public final T get() {
        new BeanDefinitionDsl.BeanSupplierContext(this.this$0.getContext());
        return (T) this.$f$inlined.invoke();
    }
}
