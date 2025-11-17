package cn.hutool.core.annotation;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AliasedAnnotationAttribute.class */
public class AliasedAnnotationAttribute extends AbstractWrappedAnnotationAttribute {
    /* JADX INFO: Access modifiers changed from: protected */
    public AliasedAnnotationAttribute(AnnotationAttribute origin, AnnotationAttribute linked) {
        super(origin, linked);
    }

    @Override // cn.hutool.core.annotation.AnnotationAttribute
    public Object getValue() {
        return this.linked.isValueEquivalentToDefaultValue() ? super.getValue() : this.linked.getValue();
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute, cn.hutool.core.annotation.AnnotationAttribute
    public boolean isValueEquivalentToDefaultValue() {
        return this.linked.isValueEquivalentToDefaultValue() && this.original.isValueEquivalentToDefaultValue();
    }
}
