package cn.hutool.core.annotation;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/ForceAliasedAnnotationAttribute.class */
public class ForceAliasedAnnotationAttribute extends AbstractWrappedAnnotationAttribute {
    /* JADX INFO: Access modifiers changed from: protected */
    public ForceAliasedAnnotationAttribute(AnnotationAttribute origin, AnnotationAttribute linked) {
        super(origin, linked);
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Object getValue() {
        return this.linked.getValue();
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute, cn.hutool.core.annotation.AnnotationAttribute
    public boolean isValueEquivalentToDefaultValue() {
        return this.linked.isValueEquivalentToDefaultValue();
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute, cn.hutool.core.annotation.AnnotationAttribute
    public Class<?> getAttributeType() {
        return this.linked.getAttributeType();
    }
}
