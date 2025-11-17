package cn.hutool.core.annotation;

import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AnnotationAttribute.class */
public interface AnnotationAttribute {
    Annotation getAnnotation();

    Method getAttribute();

    boolean isValueEquivalentToDefaultValue();

    default Class<?> getAnnotationType() {
        return getAttribute().getDeclaringClass();
    }

    default String getAttributeName() {
        return getAttribute().getName();
    }

    default Object getValue() {
        return ReflectUtil.invoke(getAnnotation(), getAttribute(), new Object[0]);
    }

    default Class<?> getAttributeType() {
        return getAttribute().getReturnType();
    }

    default <T extends Annotation> T getAnnotation(Class<T> cls) {
        return (T) getAttribute().getAnnotation(cls);
    }

    default boolean isWrapped() {
        return false;
    }
}
