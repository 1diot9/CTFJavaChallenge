package org.springframework.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/OrderComparator.class */
public class OrderComparator implements Comparator<Object> {
    public static final OrderComparator INSTANCE = new OrderComparator();

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/OrderComparator$OrderSourceProvider.class */
    public interface OrderSourceProvider {
        @Nullable
        Object getOrderSource(Object obj);
    }

    public Comparator<Object> withSourceProvider(OrderSourceProvider sourceProvider) {
        return (o1, o2) -> {
            return doCompare(o1, o2, sourceProvider);
        };
    }

    @Override // java.util.Comparator
    public int compare(@Nullable Object o1, @Nullable Object o2) {
        return doCompare(o1, o2, null);
    }

    private int doCompare(@Nullable Object o1, @Nullable Object o2, @Nullable OrderSourceProvider sourceProvider) {
        boolean p1 = o1 instanceof PriorityOrdered;
        boolean p2 = o2 instanceof PriorityOrdered;
        if (p1 && !p2) {
            return -1;
        }
        if (p2 && !p1) {
            return 1;
        }
        int i1 = getOrder(o1, sourceProvider);
        int i2 = getOrder(o2, sourceProvider);
        return Integer.compare(i1, i2);
    }

    private int getOrder(@Nullable Object obj, @Nullable OrderSourceProvider sourceProvider) {
        Object orderSource;
        Integer order = null;
        if (obj != null && sourceProvider != null && (orderSource = sourceProvider.getOrderSource(obj)) != null) {
            if (orderSource.getClass().isArray()) {
                for (Object source : ObjectUtils.toObjectArray(orderSource)) {
                    order = findOrder(source);
                    if (order != null) {
                        break;
                    }
                }
            } else {
                order = findOrder(orderSource);
            }
        }
        return order != null ? order.intValue() : getOrder(obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getOrder(@Nullable Object obj) {
        Integer order;
        if (obj != null && (order = findOrder(obj)) != null) {
            return order.intValue();
        }
        return Integer.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Integer findOrder(Object obj) {
        if (!(obj instanceof Ordered)) {
            return null;
        }
        Ordered ordered = (Ordered) obj;
        return Integer.valueOf(ordered.getOrder());
    }

    @Nullable
    public Integer getPriority(Object obj) {
        return null;
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
