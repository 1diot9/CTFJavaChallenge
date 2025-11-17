package org.springframework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationAwareOrderComparator.class */
public class AnnotationAwareOrderComparator extends OrderComparator {
    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.OrderComparator
    @Nullable
    public Integer findOrder(Object obj) {
        Integer order = super.findOrder(obj);
        if (order != null) {
            return order;
        }
        return findOrderFromAnnotation(obj);
    }

    @Nullable
    private Integer findOrderFromAnnotation(Object obj) {
        AnnotatedElement annotatedElement;
        if (obj instanceof AnnotatedElement) {
            AnnotatedElement ae = (AnnotatedElement) obj;
            annotatedElement = ae;
        } else {
            annotatedElement = obj.getClass();
        }
        AnnotatedElement element = annotatedElement;
        MergedAnnotations annotations = MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
        Integer order = OrderUtils.getOrderFromAnnotations(element, annotations);
        if (order == null && (obj instanceof DecoratingProxy)) {
            DecoratingProxy decoratingProxy = (DecoratingProxy) obj;
            return findOrderFromAnnotation(decoratingProxy.getDecoratedClass());
        }
        return order;
    }

    @Override // org.springframework.core.OrderComparator
    @Nullable
    public Integer getPriority(Object obj) {
        if (obj instanceof Class) {
            Class<?> clazz = (Class) obj;
            return OrderUtils.getPriority(clazz);
        }
        Integer priority = OrderUtils.getPriority(obj.getClass());
        if (priority == null && (obj instanceof DecoratingProxy)) {
            DecoratingProxy decoratingProxy = (DecoratingProxy) obj;
            return getPriority(decoratingProxy.getDecoratedClass());
        }
        return priority;
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            Object[] objects = (Object[]) value;
            sort(objects);
        } else if (value instanceof List) {
            List<?> list = (List) value;
            sort(list);
        }
    }
}
