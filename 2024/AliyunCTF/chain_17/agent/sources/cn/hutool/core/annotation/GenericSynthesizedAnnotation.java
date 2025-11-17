package cn.hutool.core.annotation;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/GenericSynthesizedAnnotation.class */
public class GenericSynthesizedAnnotation<R, T extends Annotation> implements SynthesizedAnnotation {
    private final R root;
    private final T annotation;
    private final Map<String, AnnotationAttribute> attributeMethodCaches = new HashMap();
    private final int verticalDistance;
    private final int horizontalDistance;

    /* JADX INFO: Access modifiers changed from: protected */
    public GenericSynthesizedAnnotation(R root, T annotation, int verticalDistance, int horizontalDistance) {
        this.root = root;
        this.annotation = annotation;
        this.verticalDistance = verticalDistance;
        this.horizontalDistance = horizontalDistance;
        this.attributeMethodCaches.putAll(loadAttributeMethods());
    }

    protected Map<String, AnnotationAttribute> loadAttributeMethods() {
        return (Map) Stream.of((Object[]) ClassUtil.getDeclaredMethods(this.annotation.annotationType())).filter(AnnotationUtil::isAttributeMethod).collect(Collectors.toMap((v0) -> {
            return v0.getName();
        }, method -> {
            return new CacheableAnnotationAttribute(this.annotation, method);
        }));
    }

    public boolean hasAttribute(String attributeName) {
        return this.attributeMethodCaches.containsKey(attributeName);
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public boolean hasAttribute(String attributeName, Class<?> returnType) {
        return Opt.ofNullable(this.attributeMethodCaches.get(attributeName)).filter(method -> {
            return ClassUtil.isAssignable(returnType, method.getAttributeType());
        }).isPresent();
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public Map<String, AnnotationAttribute> getAttributes() {
        return this.attributeMethodCaches;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public void setAttribute(String attributeName, AnnotationAttribute attribute) {
        this.attributeMethodCaches.put(attributeName, attribute);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public void replaceAttribute(String attributeName, UnaryOperator<AnnotationAttribute> operator) {
        AnnotationAttribute old = this.attributeMethodCaches.get(attributeName);
        if (ObjectUtil.isNotNull(old)) {
            this.attributeMethodCaches.put(attributeName, operator.apply(old));
        }
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public Object getAttributeValue(String attributeName) {
        return Opt.ofNullable(this.attributeMethodCaches.get(attributeName)).map((v0) -> {
            return v0.getValue();
        }).get();
    }

    @Override // cn.hutool.core.annotation.Hierarchical
    public R getRoot() {
        return this.root;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation
    public T getAnnotation() {
        return this.annotation;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation, cn.hutool.core.annotation.Hierarchical
    public int getVerticalDistance() {
        return this.verticalDistance;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAnnotation, cn.hutool.core.annotation.Hierarchical
    public int getHorizontalDistance() {
        return this.horizontalDistance;
    }

    @Override // java.lang.annotation.Annotation
    public Class<? extends Annotation> annotationType() {
        return this.annotation.annotationType();
    }

    @Override // cn.hutool.core.annotation.AnnotationAttributeValueProvider
    public Object getAttributeValue(String attributeName, Class<?> attributeType) {
        return Opt.ofNullable(this.attributeMethodCaches.get(attributeName)).filter(method -> {
            return ClassUtil.isAssignable(attributeType, method.getAttributeType());
        }).map((v0) -> {
            return v0.getValue();
        }).get();
    }
}
