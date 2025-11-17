package cn.hutool.core.annotation;

import java.util.Collection;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationAttributeProcessor.class */
public interface SynthesizedAnnotationAttributeProcessor {
    <R> R getAttributeValue(String str, Class<R> cls, Collection<? extends SynthesizedAnnotation> collection);
}
