package org.springframework.beans.factory;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

/* compiled from: BeanFactoryExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��&\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\u001a\u001e\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\u0004\u001a2\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\"\u00020\u0002H\u0086\b¢\u0006\u0002\u0010\u0007\u001a&\u0010��\u001a\u0002H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u0086\b¢\u0006\u0002\u0010\n\u001a\u001f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00010\f\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¨\u0006\r"}, d2 = {"getBean", "T", "", "Lorg/springframework/beans/factory/BeanFactory;", "(Lorg/springframework/beans/factory/BeanFactory;)Ljava/lang/Object;", "args", "", "(Lorg/springframework/beans/factory/BeanFactory;[Ljava/lang/Object;)Ljava/lang/Object;", "name", "", "(Lorg/springframework/beans/factory/BeanFactory;Ljava/lang/String;)Ljava/lang/Object;", "getBeanProvider", "Lorg/springframework/beans/factory/ObjectProvider;", "spring-beans"})
@SourceDebugExtension({"SMAP\nBeanFactoryExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BeanFactoryExtensions.kt\norg/springframework/beans/factory/BeanFactoryExtensionsKt\n*L\n1#1,66:1\n64#1:67\n64#1:68\n*S KotlinDebug\n*F\n+ 1 BeanFactoryExtensions.kt\norg/springframework/beans/factory/BeanFactoryExtensionsKt\n*L\n30#1:67\n53#1:68\n*E\n"})
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/BeanFactoryExtensionsKt.class */
public final class BeanFactoryExtensionsKt {
    public static final /* synthetic */ <T> T getBean(BeanFactory $this$getBean) {
        Intrinsics.checkNotNullParameter($this$getBean, "<this>");
        Intrinsics.needClassReification();
        ObjectProvider<T> beanProvider = $this$getBean.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<T>() { // from class: org.springframework.beans.factory.BeanFactoryExtensionsKt$getBean$$inlined$getBeanProvider$1
        }.getType()));
        Intrinsics.checkNotNullExpressionValue(beanProvider, "getBeanProvider(Resolvab…Reference<T>() {}).type))");
        T object = beanProvider.getObject();
        Intrinsics.checkNotNullExpressionValue(object, "getBeanProvider<T>().getObject()");
        return object;
    }

    public static final /* synthetic */ <T> T getBean(BeanFactory beanFactory, String str) {
        Intrinsics.checkNotNullParameter(beanFactory, "<this>");
        Intrinsics.checkNotNullParameter(str, "name");
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) beanFactory.getBean(str, Object.class);
        Intrinsics.checkNotNullExpressionValue(t, "getBean(name, T::class.java)");
        return t;
    }

    public static final /* synthetic */ <T> T getBean(BeanFactory $this$getBean, Object... args) {
        Intrinsics.checkNotNullParameter($this$getBean, "<this>");
        Intrinsics.checkNotNullParameter(args, "args");
        Intrinsics.needClassReification();
        ObjectProvider<T> beanProvider = $this$getBean.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<T>() { // from class: org.springframework.beans.factory.BeanFactoryExtensionsKt$getBean$$inlined$getBeanProvider$2
        }.getType()));
        Intrinsics.checkNotNullExpressionValue(beanProvider, "getBeanProvider(Resolvab…Reference<T>() {}).type))");
        T object = beanProvider.getObject(Arrays.copyOf(args, args.length));
        Intrinsics.checkNotNullExpressionValue(object, "getBeanProvider<T>().getObject(*args)");
        return object;
    }

    public static final /* synthetic */ <T> ObjectProvider<T> getBeanProvider(BeanFactory $this$getBeanProvider) {
        Intrinsics.checkNotNullParameter($this$getBeanProvider, "<this>");
        Intrinsics.needClassReification();
        ObjectProvider<T> beanProvider = $this$getBeanProvider.getBeanProvider(ResolvableType.forType(new ParameterizedTypeReference<T>() { // from class: org.springframework.beans.factory.BeanFactoryExtensionsKt$getBeanProvider$1
        }.getType()));
        Intrinsics.checkNotNullExpressionValue(beanProvider, "getBeanProvider(Resolvab…Reference<T>() {}).type))");
        return beanProvider;
    }
}
