package cn.hutool.core.annotation;

import java.lang.annotation.Annotation;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAggregateAnnotation.class */
public interface SynthesizedAggregateAnnotation extends AggregateAnnotation, Hierarchical, AnnotationSynthesizer, AnnotationAttributeValueProvider {
    <T extends Annotation> T getAnnotation(Class<T> cls);

    SynthesizedAnnotationAttributeProcessor getAnnotationAttributeProcessor();

    Object getAttributeValue(String str, Class<?> cls);

    default int getVerticalDistance() {
        return 0;
    }

    default int getHorizontalDistance() {
        return 0;
    }

    @Override // java.lang.annotation.Annotation
    default Class<? extends Annotation> annotationType() {
        return getClass();
    }
}
