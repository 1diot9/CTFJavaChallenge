package cn.hutool.core.annotation;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AnnotationAttributeValueProvider.class */
public interface AnnotationAttributeValueProvider {
    Object getAttributeValue(String str, Class<?> cls);
}
