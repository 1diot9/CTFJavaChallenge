package org.springframework.context.support;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.jvm.functions.Function11;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.springframework.context.support.BeanDefinitionDsl;

/* compiled from: BeanDefinitionDsl.kt */
@Metadata(mv = {1, 7, 0}, k = 3, xi = 176, d1 = {"��\f\n\u0002\b\u0003\n\u0002\u0010��\n\u0002\b\u0003\u0010��\u001a\n \u0002*\u0004\u0018\u0001H\u0001H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0003H\n¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"}, d2 = {"<anonymous>", "T", "kotlin.jvm.PlatformType", "", BeanUtil.PREFIX_GETTER_GET, "()Ljava/lang/Object;", "org/springframework/context/support/BeanDefinitionDsl$bean$1"})
@SourceDebugExtension({"SMAP\nBeanDefinitionDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl$bean$1\n+ 2 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl\n+ 3 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl$BeanSupplierContext\n*L\n1#1,1214:1\n704#2:1215\n1161#3,4:1216\n*S KotlinDebug\n*F\n+ 1 BeanDefinitionDsl.kt\norg/springframework/context/support/BeanDefinitionDsl\n*L\n704#1:1216,4\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/BeanDefinitionDsl$bean$$inlined$bean$12.class */
public final class BeanDefinitionDsl$bean$$inlined$bean$12<T> implements Supplier {
    final /* synthetic */ BeanDefinitionDsl this$0;
    final /* synthetic */ Function11 $f$inlined;

    public BeanDefinitionDsl$bean$$inlined$bean$12(BeanDefinitionDsl $receiver, Function11 function11) {
        this.this$0 = $receiver;
        this.$f$inlined = function11;
    }

    @Override // java.util.function.Supplier
    public final T get() {
        BeanDefinitionDsl.BeanSupplierContext beanSupplierContext = new BeanDefinitionDsl.BeanSupplierContext(this.this$0.getContext());
        Function11 function11 = this.$f$inlined;
        GenericApplicationContext context = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "A");
        Object bean = context.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean, "context.getBean(T::class.java)");
        GenericApplicationContext context2 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "B");
        Object bean2 = context2.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean2, "context.getBean(T::class.java)");
        GenericApplicationContext context3 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "C");
        Object bean3 = context3.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean3, "context.getBean(T::class.java)");
        GenericApplicationContext context4 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "D");
        Object bean4 = context4.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean4, "context.getBean(T::class.java)");
        GenericApplicationContext context5 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "E");
        Object bean5 = context5.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean5, "context.getBean(T::class.java)");
        GenericApplicationContext context6 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "F");
        Object bean6 = context6.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean6, "context.getBean(T::class.java)");
        GenericApplicationContext context7 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "G");
        Object bean7 = context7.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean7, "context.getBean(T::class.java)");
        GenericApplicationContext context8 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "H");
        Object bean8 = context8.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean8, "context.getBean(T::class.java)");
        GenericApplicationContext context9 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "I");
        Object bean9 = context9.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean9, "context.getBean(T::class.java)");
        GenericApplicationContext context10 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "J");
        Object bean10 = context10.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean10, "context.getBean(T::class.java)");
        GenericApplicationContext context11 = beanSupplierContext.getContext();
        Intrinsics.reifiedOperationMarker(4, "K");
        Object bean11 = context11.getBean(Object.class);
        Intrinsics.checkNotNullExpressionValue(bean11, "context.getBean(T::class.java)");
        return (T) function11.invoke(bean, bean2, bean3, bean4, bean5, bean6, bean7, bean8, bean9, bean10, bean11);
    }
}
