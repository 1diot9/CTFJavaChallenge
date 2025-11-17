package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/CacheableAnnotationAttribute.class */
public class CacheableAnnotationAttribute implements AnnotationAttribute {
    private boolean valueInvoked;
    private Object value;
    private boolean defaultValueInvoked;
    private Object defaultValue;
    private final Annotation annotation;
    private final Method attribute;

    public CacheableAnnotationAttribute(Annotation annotation, Method attribute) {
        Assert.notNull(annotation, "annotation must not null", new Object[0]);
        Assert.notNull(attribute, "attribute must not null", new Object[0]);
        this.annotation = annotation;
        this.attribute = attribute;
        this.valueInvoked = false;
        this.defaultValueInvoked = false;
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Annotation getAnnotation() {
        return this.annotation;
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Method getAttribute() {
        return this.attribute;
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Object getValue() {
        if (!this.valueInvoked) {
            this.valueInvoked = true;
            this.value = ReflectUtil.invoke(this.annotation, this.attribute, new Object[0]);
        }
        return this.value;
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public boolean isValueEquivalentToDefaultValue() {
        if (!this.defaultValueInvoked) {
            this.defaultValue = this.attribute.getDefaultValue();
            this.defaultValueInvoked = true;
        }
        return ObjectUtil.equals(getValue(), this.defaultValue);
    }
}
