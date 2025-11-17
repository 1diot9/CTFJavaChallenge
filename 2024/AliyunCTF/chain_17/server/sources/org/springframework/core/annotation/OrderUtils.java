package org.springframework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/OrderUtils.class */
public abstract class OrderUtils {
    private static final String JAVAX_PRIORITY_ANNOTATION = "jakarta.annotation.Priority";
    private static final Object NOT_ANNOTATED = new Object();
    static final Map<AnnotatedElement, Object> orderCache = new ConcurrentReferenceHashMap(64);

    public static int getOrder(Class<?> type, int defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order.intValue() : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
        Integer order = getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type) {
        return getOrder((AnnotatedElement) type);
    }

    @Nullable
    public static Integer getOrder(AnnotatedElement element) {
        return getOrderFromAnnotations(element, MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Integer getOrderFromAnnotations(AnnotatedElement element, MergedAnnotations annotations) {
        if (!(element instanceof Class)) {
            return findOrder(annotations);
        }
        Object cached = orderCache.get(element);
        if (cached != null) {
            if (!(cached instanceof Integer)) {
                return null;
            }
            Integer integer = (Integer) cached;
            return integer;
        }
        Integer result = findOrder(annotations);
        orderCache.put(element, result != null ? result : NOT_ANNOTATED);
        return result;
    }

    @Nullable
    private static Integer findOrder(MergedAnnotations annotations) {
        MergedAnnotation<Order> orderAnnotation = annotations.get(Order.class);
        if (orderAnnotation.isPresent()) {
            return Integer.valueOf(orderAnnotation.getInt("value"));
        }
        MergedAnnotation<?> priorityAnnotation = annotations.get(JAVAX_PRIORITY_ANNOTATION);
        if (priorityAnnotation.isPresent()) {
            return Integer.valueOf(priorityAnnotation.getInt("value"));
        }
        return null;
    }

    @Nullable
    public static Integer getPriority(Class<?> type) {
        return (Integer) MergedAnnotations.from(type, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(JAVAX_PRIORITY_ANNOTATION).getValue("value", Integer.class).orElse(null);
    }
}
