package org.springframework.beans.support;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/support/PropertyComparator.class */
public class PropertyComparator<T> implements Comparator<T> {
    protected final Log logger = LogFactory.getLog(getClass());
    private final SortDefinition sortDefinition;

    public PropertyComparator(SortDefinition sortDefinition) {
        this.sortDefinition = sortDefinition;
    }

    public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
        this.sortDefinition = new MutableSortDefinition(property, ignoreCase, ascending);
    }

    public final SortDefinition getSortDefinition() {
        return this.sortDefinition;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Comparator
    public int compare(T o1, T o2) {
        int result;
        int compareTo;
        Object v1 = getPropertyValue(o1);
        Object v2 = getPropertyValue(o2);
        if (this.sortDefinition.isIgnoreCase() && (v1 instanceof String)) {
            String text1 = (String) v1;
            if (v2 instanceof String) {
                String text2 = (String) v2;
                v1 = text1.toLowerCase();
                v2 = text2.toLowerCase();
            }
        }
        if (v1 != null) {
            if (v2 != null) {
                try {
                    compareTo = ((Comparable) v1).compareTo(v2);
                } catch (RuntimeException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Could not sort objects [" + o1 + "] and [" + o2 + "]", ex);
                        return 0;
                    }
                    return 0;
                }
            } else {
                compareTo = -1;
            }
            result = compareTo;
        } else {
            result = v2 != null ? 1 : 0;
        }
        return this.sortDefinition.isAscending() ? result : -result;
    }

    @Nullable
    private Object getPropertyValue(Object obj) {
        try {
            BeanWrapperImpl beanWrapper = new BeanWrapperImpl(false);
            beanWrapper.setWrappedInstance(obj);
            return beanWrapper.getPropertyValue(this.sortDefinition.getProperty());
        } catch (BeansException ex) {
            this.logger.debug("PropertyComparator could not access property - treating as null for sorting", ex);
            return null;
        }
    }

    public static void sort(List<?> source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            source.sort(new PropertyComparator(sortDefinition));
        }
    }

    public static void sort(Object[] source, SortDefinition sortDefinition) throws BeansException {
        if (StringUtils.hasText(sortDefinition.getProperty())) {
            Arrays.sort(source, new PropertyComparator(sortDefinition));
        }
    }
}
