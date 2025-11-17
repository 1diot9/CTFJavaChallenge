package org.springframework.context.support;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import org.springframework.context.support.BeanDefinitionDsl;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 176, d1 = {"��\f\n\u0002\b\u0003\n\u0002\u0010��\n\u0002\b\u0002\u0010��\u001a\n \u0002*\u0004\u0018\u0001H\u0001H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0003H\n¢\u0006\u0004\b\u0004\u0010\u0005"}, d2 = {"<anonymous>", "T", "kotlin.jvm.PlatformType", "", BeanUtil.PREFIX_GETTER_GET, "()Ljava/lang/Object;"})
@SourceDebugExtension({"SMAP\nBeanDefinitionDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl$bean$1\n*L\n1#1,1214:1\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/BeanDefinitionDsl$bean$1.class */
public final class BeanDefinitionDsl$bean$1<T> implements Supplier {
    final /* synthetic */ Function1<BeanDefinitionDsl.BeanSupplierContext, T> $function;
    final /* synthetic */ BeanDefinitionDsl this$0;

    /* JADX WARN: Multi-variable type inference failed */
    public BeanDefinitionDsl$bean$1(Function1<? super BeanDefinitionDsl.BeanSupplierContext, ? extends T> $function, BeanDefinitionDsl this$0) {
        this.$function = $function;
        this.this$0 = this$0;
    }

    @Override // java.util.function.Supplier
    public final T get() {
        return (T) this.$function.invoke(new BeanDefinitionDsl.BeanSupplierContext(this.this$0.getContext()));
    }
}
