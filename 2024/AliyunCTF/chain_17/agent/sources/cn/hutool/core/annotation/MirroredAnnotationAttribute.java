package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/MirroredAnnotationAttribute.class */
public class MirroredAnnotationAttribute extends AbstractWrappedAnnotationAttribute {
    public MirroredAnnotationAttribute(AnnotationAttribute origin, AnnotationAttribute linked) {
        super(origin, linked);
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Object getValue() {
        boolean originIsDefault = this.original.isValueEquivalentToDefaultValue();
        boolean targetIsDefault = this.linked.isValueEquivalentToDefaultValue();
        Object originValue = this.original.getValue();
        Object targetValue = this.linked.getValue();
        if (originIsDefault != targetIsDefault) {
            return originIsDefault ? targetValue : originValue;
        }
        Assert.equals(originValue, targetValue, "the values of attributes [{}] and [{}] that mirror each other are different: [{}] <==> [{}]", this.original.getAttribute(), this.linked.getAttribute(), originValue, targetValue);
        return originValue;
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute, cn.hutool.core.annotation.AnnotationAttribute
    public boolean isValueEquivalentToDefaultValue() {
        return this.original.isValueEquivalentToDefaultValue() && this.linked.isValueEquivalentToDefaultValue();
    }
}
