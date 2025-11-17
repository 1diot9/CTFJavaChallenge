package cn.hutool.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/WrappedAnnotationAttribute.class */
public interface WrappedAnnotationAttribute extends AnnotationAttribute {
    AnnotationAttribute getOriginal();

    AnnotationAttribute getNonWrappedOriginal();

    AnnotationAttribute getLinked();

    Collection<AnnotationAttribute> getAllLinkedNonWrappedAttributes();

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    boolean isValueEquivalentToDefaultValue();

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    default Annotation getAnnotation() {
        return getOriginal().getAnnotation();
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    default Method getAttribute() {
        return getOriginal().getAttribute();
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    default Class<?> getAttributeType() {
        return getOriginal().getAttributeType();
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    default <T extends Annotation> T getAnnotation(Class<T> cls) {
        return (T) getOriginal().getAnnotation(cls);
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    default boolean isWrapped() {
        return true;
    }
}
