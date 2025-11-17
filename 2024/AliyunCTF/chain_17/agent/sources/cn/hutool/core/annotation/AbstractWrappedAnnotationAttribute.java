package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AbstractWrappedAnnotationAttribute.class */
public abstract class AbstractWrappedAnnotationAttribute implements WrappedAnnotationAttribute {
    protected final AnnotationAttribute original;
    protected final AnnotationAttribute linked;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractWrappedAnnotationAttribute(AnnotationAttribute original, AnnotationAttribute linked) {
        Assert.notNull(original, "target must not null", new Object[0]);
        Assert.notNull(linked, "linked must not null", new Object[0]);
        this.original = original;
        this.linked = linked;
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute
    public AnnotationAttribute getOriginal() {
        return this.original;
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute
    public AnnotationAttribute getLinked() {
        return this.linked;
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute
    public AnnotationAttribute getNonWrappedOriginal() {
        AnnotationAttribute curr = null;
        AnnotationAttribute annotationAttribute = this.original;
        while (true) {
            AnnotationAttribute next = annotationAttribute;
            if (next != null) {
                curr = next;
                annotationAttribute = next.isWrapped() ? ((WrappedAnnotationAttribute) curr).getOriginal() : null;
            } else {
                return curr;
            }
        }
    }

    @Override // cn.hutool.core.annotation.WrappedAnnotationAttribute
    public Collection<AnnotationAttribute> getAllLinkedNonWrappedAttributes() {
        List<AnnotationAttribute> leafAttributes = new ArrayList<>();
        collectLeafAttribute(this, leafAttributes);
        return leafAttributes;
    }

    private void collectLeafAttribute(AnnotationAttribute curr, List<AnnotationAttribute> leafAttributes) {
        if (ObjectUtil.isNull(curr)) {
            return;
        }
        if (!curr.isWrapped()) {
            leafAttributes.add(curr);
            return;
        }
        WrappedAnnotationAttribute wrappedAttribute = (WrappedAnnotationAttribute) curr;
        collectLeafAttribute(wrappedAttribute.getOriginal(), leafAttributes);
        collectLeafAttribute(wrappedAttribute.getLinked(), leafAttributes);
    }
}
