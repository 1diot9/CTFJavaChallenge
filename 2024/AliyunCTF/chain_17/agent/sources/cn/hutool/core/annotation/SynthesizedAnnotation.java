package cn.hutool.core.annotation;

import cn.hutool.core.collection.CollUtil;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.UnaryOperator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotation.class */
public interface SynthesizedAnnotation extends Annotation, Hierarchical, AnnotationAttributeValueProvider {
    Annotation getAnnotation();

    int getVerticalDistance();

    int getHorizontalDistance();

    boolean hasAttribute(String str, Class<?> cls);

    Map<String, AnnotationAttribute> getAttributes();

    void setAttribute(String str, AnnotationAttribute annotationAttribute);

    void replaceAttribute(String str, UnaryOperator<AnnotationAttribute> unaryOperator);

    Object getAttributeValue(String str);

    default void setAttributes(Map<String, AnnotationAttribute> attributes) {
        if (CollUtil.isNotEmpty(attributes)) {
            attributes.forEach(this::setAttribute);
        }
    }
}
