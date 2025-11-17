package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AbstractLinkAnnotationPostProcessor.class */
public abstract class AbstractLinkAnnotationPostProcessor implements SynthesizedAnnotationPostProcessor {
    protected abstract RelationType[] processTypes();

    protected abstract void processLinkedAttribute(AnnotationSynthesizer annotationSynthesizer, Link link, SynthesizedAnnotation synthesizedAnnotation, AnnotationAttribute annotationAttribute, SynthesizedAnnotation synthesizedAnnotation2, AnnotationAttribute annotationAttribute2);

    @Override // cn.hutool.core.annotation.SynthesizedAnnotationPostProcessor
    public void process(SynthesizedAnnotation synthesizedAnnotation, AnnotationSynthesizer synthesizer) {
        Map<String, AnnotationAttribute> attributeMap = new HashMap<>(synthesizedAnnotation.getAttributes());
        attributeMap.forEach((originalAttributeName, originalAttribute) -> {
            Link link = getLinkAnnotation(originalAttribute, processTypes());
            if (ObjectUtil.isNull(link)) {
                return;
            }
            SynthesizedAnnotation linkedAnnotation = getLinkedAnnotation(link, synthesizer, synthesizedAnnotation.annotationType());
            if (ObjectUtil.isNull(linkedAnnotation)) {
                return;
            }
            AnnotationAttribute linkedAttribute = linkedAnnotation.getAttributes().get(link.attribute());
            processLinkedAttribute(synthesizer, link, synthesizedAnnotation, synthesizedAnnotation.getAttributes().get(originalAttributeName), linkedAnnotation, linkedAttribute);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Link getLinkAnnotation(AnnotationAttribute attribute, RelationType... relationTypes) {
        return (Link) Opt.ofNullable(attribute).map(t -> {
            return (Link) AnnotationUtil.getSynthesizedAnnotation(attribute.getAttribute(), Link.class);
        }).filter(a -> {
            return ArrayUtil.contains(relationTypes, a.type());
        }).get();
    }

    protected SynthesizedAnnotation getLinkedAnnotation(Link annotation, AnnotationSynthesizer synthesizer, Class<? extends Annotation> defaultType) {
        Class<?> targetAnnotationType = getLinkedAnnotationType(annotation, defaultType);
        return synthesizer.getSynthesizedAnnotation(targetAnnotationType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getLinkedAnnotationType(Link annotation, Class<?> defaultType) {
        return ObjectUtil.equals(annotation.annotation(), Annotation.class) ? defaultType : annotation.annotation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkAttributeType(AnnotationAttribute original, AnnotationAttribute alias) {
        Assert.equals(original.getAttributeType(), alias.getAttributeType(), "return type of the linked attribute [{}] is inconsistent with the original [{}]", original.getAttribute(), alias.getAttribute());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkLinkedSelf(AnnotationAttribute original, AnnotationAttribute linked) {
        boolean linkSelf = original == linked || ObjectUtil.equals(original.getAttribute(), linked.getAttribute());
        Assert.isFalse(linkSelf, "cannot link self [{}]", original.getAttribute());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkLinkedAttributeNotNull(AnnotationAttribute original, AnnotationAttribute linkedAttribute, Link annotation) {
        Assert.notNull(linkedAttribute, "cannot find linked attribute [{}] of original [{}] in [{}]", original.getAttribute(), annotation.attribute(), getLinkedAnnotationType(annotation, original.getAnnotationType()));
    }
}
