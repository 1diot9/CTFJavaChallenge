package cn.hutool.core.comparator;

import cn.hutool.core.bean.BeanUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/PropertyComparator.class */
public class PropertyComparator<T> extends FuncComparator<T> {
    private static final long serialVersionUID = 9157326766723846313L;

    public PropertyComparator(String property) {
        this(property, true);
    }

    public PropertyComparator(String property, boolean isNullGreater) {
        super(isNullGreater, bean -> {
            return (Comparable) BeanUtil.getProperty(bean, property);
        });
    }
}
