package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import java.util.function.BinaryOperator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AliasLinkAnnotationPostProcessor.class */
public class AliasLinkAnnotationPostProcessor extends AbstractLinkAnnotationPostProcessor {
    private static final RelationType[] PROCESSED_RELATION_TYPES = {RelationType.ALIAS_FOR, RelationType.FORCE_ALIAS_FOR};

    @Override // cn.hutool.core.annotation.SynthesizedAnnotationPostProcessor
    public int order() {
        return -2147483646;
    }

    @Override // cn.hutool.core.annotation.AbstractLinkAnnotationPostProcessor
    protected RelationType[] processTypes() {
        return PROCESSED_RELATION_TYPES;
    }

    @Override // cn.hutool.core.annotation.AbstractLinkAnnotationPostProcessor
    protected void processLinkedAttribute(AnnotationSynthesizer synthesizer, Link annotation, SynthesizedAnnotation originalAnnotation, AnnotationAttribute originalAttribute, SynthesizedAnnotation linkedAnnotation, AnnotationAttribute linkedAttribute) {
        checkAliasRelation(annotation, originalAttribute, linkedAttribute);
        if (RelationType.ALIAS_FOR.equals(annotation.type())) {
            wrappingLinkedAttribute(synthesizer, originalAttribute, linkedAttribute, AliasedAnnotationAttribute::new);
        } else {
            wrappingLinkedAttribute(synthesizer, originalAttribute, linkedAttribute, ForceAliasedAnnotationAttribute::new);
        }
    }

    private void wrappingLinkedAttribute(AnnotationSynthesizer synthesizer, AnnotationAttribute originalAttribute, AnnotationAttribute aliasAttribute, BinaryOperator<AnnotationAttribute> wrapping) {
        if (!aliasAttribute.isWrapped()) {
            processAttribute(synthesizer, originalAttribute, aliasAttribute, wrapping);
        } else {
            AbstractWrappedAnnotationAttribute wrapper = (AbstractWrappedAnnotationAttribute) aliasAttribute;
            wrapper.getAllLinkedNonWrappedAttributes().forEach(t -> {
                processAttribute(synthesizer, originalAttribute, t, wrapping);
            });
        }
    }

    private void processAttribute(AnnotationSynthesizer synthesizer, AnnotationAttribute originalAttribute, AnnotationAttribute target, BinaryOperator<AnnotationAttribute> wrapping) {
        Opt ofNullable = Opt.ofNullable(target.getAnnotationType());
        synthesizer.getClass();
        ofNullable.map(synthesizer::getSynthesizedAnnotation).ifPresent(t -> {
            t.replaceAttribute(target.getAttributeName(), old -> {
                return (AnnotationAttribute) wrapping.apply(old, originalAttribute);
            });
        });
    }

    private void checkAliasRelation(Link annotation, AnnotationAttribute originalAttribute, AnnotationAttribute linkedAttribute) {
        checkLinkedAttributeNotNull(originalAttribute, linkedAttribute, annotation);
        checkAttributeType(originalAttribute, linkedAttribute);
        checkCircularDependency(originalAttribute, linkedAttribute);
    }

    private void checkCircularDependency(AnnotationAttribute original, AnnotationAttribute alias) {
        checkLinkedSelf(original, alias);
        Link annotation = getLinkAnnotation(alias, RelationType.ALIAS_FOR, RelationType.FORCE_ALIAS_FOR);
        if (ObjectUtil.isNull(annotation)) {
            return;
        }
        Class<?> aliasAnnotationType = getLinkedAnnotationType(annotation, alias.getAnnotationType());
        if (ObjectUtil.notEqual(aliasAnnotationType, original.getAnnotationType())) {
            return;
        }
        Assert.notEquals(annotation.attribute(), original.getAttributeName(), "circular reference between the alias attribute [{}] and the original attribute [{}]", alias.getAttribute(), original.getAttribute());
    }
}
