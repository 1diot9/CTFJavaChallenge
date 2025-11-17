package cn.hutool.core.annotation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.ForestMap;
import cn.hutool.core.map.LinkedForestMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AliasAnnotationPostProcessor.class */
public class AliasAnnotationPostProcessor implements SynthesizedAnnotationPostProcessor {
    @Override // cn.hutool.core.annotation.SynthesizedAnnotationPostProcessor
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotationPostProcessor
    public void process(SynthesizedAnnotation synthesizedAnnotation, AnnotationSynthesizer synthesizer) {
        Map<String, AnnotationAttribute> attributeMap = synthesizedAnnotation.getAttributes();
        ForestMap<String, AnnotationAttribute> attributeAliasMappings = new LinkedForestMap<>(false);
        attributeMap.forEach((attributeName, attribute) -> {
            String alias = (String) Opt.ofNullable(attribute.getAnnotation(Alias.class)).map((v0) -> {
                return v0.value();
            }).orElse(null);
            if (ObjectUtil.isNull(alias)) {
                return;
            }
            AnnotationAttribute aliasAttribute = (AnnotationAttribute) attributeMap.get(alias);
            Assert.notNull(aliasAttribute, "no method for alias: [{}]", alias);
            attributeAliasMappings.putLinkedNodes(alias, aliasAttribute, attributeName, attribute);
        });
        attributeMap.forEach((attributeName2, attribute2) -> {
            Opt ofNullable = Opt.ofNullable(attributeName2);
            attributeAliasMappings.getClass();
            AnnotationAttribute resolvedAttribute = (AnnotationAttribute) ofNullable.map((v1) -> {
                return r1.getRootNode(v1);
            }).map((v0) -> {
                return v0.getValue();
            }).orElse(attribute2);
            Assert.isTrue(ObjectUtil.isNull(resolvedAttribute) || ClassUtil.isAssignable(attribute2.getAttributeType(), resolvedAttribute.getAttributeType()), "return type of the root alias method [{}] is inconsistent with the original [{}]", resolvedAttribute.getClass(), attribute2.getAttributeType());
            if (attribute2 != resolvedAttribute) {
                attributeMap.put(attributeName2, new ForceAliasedAnnotationAttribute(attribute2, resolvedAttribute));
            }
        });
        synthesizedAnnotation.setAttributes(attributeMap);
    }
}
