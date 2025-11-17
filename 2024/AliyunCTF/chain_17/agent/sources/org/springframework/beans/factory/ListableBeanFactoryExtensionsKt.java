package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ListableBeanFactoryExtensions.kt */
@Metadata(mv = {1, 7, 0}, k = 2, xi = 48, d1 = {"��4\n\u0002\b\u0002\n\u0002\u0010\u001b\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0002\u001a(\u0010��\u001a\u0004\u0018\u0001H\u0001\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b¢\u0006\u0002\u0010\u0006\u001a&\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\b\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¢\u0006\u0002\u0010\t\u001a:\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\b\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u000b*\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u0086\b¢\u0006\u0002\u0010\u000f\u001a9\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H\u00010\u0011\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u000b*\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u0086\b\u001a%\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000b0\u0011\"\n\b��\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b¨\u0006\u0013"}, d2 = {"findAnnotationOnBean", "T", "", "Lorg/springframework/beans/factory/ListableBeanFactory;", "beanName", "", "(Lorg/springframework/beans/factory/ListableBeanFactory;Ljava/lang/String;)Ljava/lang/annotation/Annotation;", "getBeanNamesForAnnotation", "", "(Lorg/springframework/beans/factory/ListableBeanFactory;)[Ljava/lang/String;", "getBeanNamesForType", "", "includeNonSingletons", "", "allowEagerInit", "(Lorg/springframework/beans/factory/ListableBeanFactory;ZZ)[Ljava/lang/String;", "getBeansOfType", "", "getBeansWithAnnotation", "spring-beans"})
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/ListableBeanFactoryExtensionsKt.class */
public final class ListableBeanFactoryExtensionsKt {
    public static /* synthetic */ String[] getBeanNamesForType$default(ListableBeanFactory $this$getBeanNamesForType_u24default, boolean includeNonSingletons, boolean allowEagerInit, int i, Object obj) {
        if ((i & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((i & 2) != 0) {
            allowEagerInit = true;
        }
        Intrinsics.checkNotNullParameter($this$getBeanNamesForType_u24default, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForType = $this$getBeanNamesForType_u24default.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkNotNullExpressionValue(beanNamesForType, "getBeanNamesForType(T::c…ngletons, allowEagerInit)");
        return beanNamesForType;
    }

    public static final /* synthetic */ <T> String[] getBeanNamesForType(ListableBeanFactory $this$getBeanNamesForType, boolean includeNonSingletons, boolean allowEagerInit) {
        Intrinsics.checkNotNullParameter($this$getBeanNamesForType, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForType = $this$getBeanNamesForType.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkNotNullExpressionValue(beanNamesForType, "getBeanNamesForType(T::c…ngletons, allowEagerInit)");
        return beanNamesForType;
    }

    public static /* synthetic */ Map getBeansOfType$default(ListableBeanFactory $this$getBeansOfType_u24default, boolean includeNonSingletons, boolean allowEagerInit, int i, Object obj) {
        if ((i & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((i & 2) != 0) {
            allowEagerInit = true;
        }
        Intrinsics.checkNotNullParameter($this$getBeansOfType_u24default, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        Map beansOfType = $this$getBeansOfType_u24default.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkNotNullExpressionValue(beansOfType, "getBeansOfType(T::class.…ngletons, allowEagerInit)");
        return beansOfType;
    }

    public static final /* synthetic */ <T> Map<String, T> getBeansOfType(ListableBeanFactory $this$getBeansOfType, boolean includeNonSingletons, boolean allowEagerInit) {
        Intrinsics.checkNotNullParameter($this$getBeansOfType, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        Map<String, T> beansOfType = $this$getBeansOfType.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkNotNullExpressionValue(beansOfType, "getBeansOfType(T::class.…ngletons, allowEagerInit)");
        return beansOfType;
    }

    public static final /* synthetic */ <T extends Annotation> String[] getBeanNamesForAnnotation(ListableBeanFactory $this$getBeanNamesForAnnotation) {
        Intrinsics.checkNotNullParameter($this$getBeanNamesForAnnotation, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        String[] beanNamesForAnnotation = $this$getBeanNamesForAnnotation.getBeanNamesForAnnotation(Annotation.class);
        Intrinsics.checkNotNullExpressionValue(beanNamesForAnnotation, "getBeanNamesForAnnotation(T::class.java)");
        return beanNamesForAnnotation;
    }

    public static final /* synthetic */ <T extends Annotation> Map<String, Object> getBeansWithAnnotation(ListableBeanFactory $this$getBeansWithAnnotation) {
        Intrinsics.checkNotNullParameter($this$getBeansWithAnnotation, "<this>");
        Intrinsics.reifiedOperationMarker(4, "T");
        Map<String, Object> beansWithAnnotation = $this$getBeansWithAnnotation.getBeansWithAnnotation(Annotation.class);
        Intrinsics.checkNotNullExpressionValue(beansWithAnnotation, "getBeansWithAnnotation(T::class.java)");
        return beansWithAnnotation;
    }

    public static final /* synthetic */ <T extends Annotation> T findAnnotationOnBean(ListableBeanFactory listableBeanFactory, String str) {
        Intrinsics.checkNotNullParameter(listableBeanFactory, "<this>");
        Intrinsics.checkNotNullParameter(str, "beanName");
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) listableBeanFactory.findAnnotationOnBean(str, Annotation.class);
    }
}
