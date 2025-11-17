package cn.hutool.core.comparator;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.Field;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/FieldsComparator.class */
public class FieldsComparator<T> extends NullComparator<T> {
    private static final long serialVersionUID = 8649196282886500803L;

    public FieldsComparator(Class<T> beanClass, String... fieldNames) {
        this(true, beanClass, fieldNames);
    }

    public FieldsComparator(boolean nullGreater, Class<T> beanClass, String... fieldNames) {
        super(nullGreater, (a, b) -> {
            for (String fieldName : fieldNames) {
                Field field = ClassUtil.getDeclaredField(beanClass, fieldName);
                Assert.notNull(field, "Field [{}] not found in Class [{}]", fieldName, beanClass.getName());
                int compare = new FieldComparator(field).compare(a, b);
                if (0 != compare) {
                    return compare;
                }
            }
            return 0;
        });
    }
}
