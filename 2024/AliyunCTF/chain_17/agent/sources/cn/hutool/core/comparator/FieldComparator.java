package cn.hutool.core.comparator;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.Field;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/FieldComparator.class */
public class FieldComparator<T> extends FuncComparator<T> {
    private static final long serialVersionUID = 9157326766723846313L;

    public FieldComparator(Class<T> beanClass, String fieldName) {
        this(getNonNullField(beanClass, fieldName));
    }

    public FieldComparator(Field field) {
        this(true, field);
    }

    public FieldComparator(boolean nullGreater, Field field) {
        super(nullGreater, bean -> {
            return (Comparable) ReflectUtil.getFieldValue(bean, (Field) Assert.notNull(field, "Field must be not null!", new Object[0]));
        });
    }

    private static Field getNonNullField(Class<?> beanClass, String fieldName) {
        Field field = ClassUtil.getDeclaredField(beanClass, fieldName);
        if (field == null) {
            throw new IllegalArgumentException(StrUtil.format("Field [{}] not found in Class [{}]", fieldName, beanClass.getName()));
        }
        return field;
    }
}
