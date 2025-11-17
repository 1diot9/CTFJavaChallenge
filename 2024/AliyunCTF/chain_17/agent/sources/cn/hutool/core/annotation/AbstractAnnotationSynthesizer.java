package cn.hutool.core.annotation;

import cn.hutool.core.annotation.scanner.AnnotationScanner;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AbstractAnnotationSynthesizer.class */
public abstract class AbstractAnnotationSynthesizer<T> implements AnnotationSynthesizer {
    protected final T source;
    protected final Map<Class<? extends Annotation>, SynthesizedAnnotation> synthesizedAnnotationMap;
    private final Map<Class<? extends Annotation>, Annotation> synthesizedProxyAnnotations;
    protected final SynthesizedAnnotationSelector annotationSelector;
    protected final Collection<SynthesizedAnnotationPostProcessor> postProcessors;
    protected final AnnotationScanner annotationScanner;

    protected abstract Map<Class<? extends Annotation>, SynthesizedAnnotation> loadAnnotations();

    protected abstract <A extends Annotation> A synthesize(Class<A> cls, SynthesizedAnnotation synthesizedAnnotation);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAnnotationSynthesizer(T source, SynthesizedAnnotationSelector annotationSelector, Collection<SynthesizedAnnotationPostProcessor> annotationPostProcessors, AnnotationScanner annotationScanner) {
        Assert.notNull(source, "source must not null", new Object[0]);
        Assert.notNull(annotationSelector, "annotationSelector must not null", new Object[0]);
        Assert.notNull(annotationPostProcessors, "annotationPostProcessors must not null", new Object[0]);
        Assert.notNull(annotationPostProcessors, "annotationScanner must not null", new Object[0]);
        this.source = source;
        this.annotationSelector = annotationSelector;
        this.annotationScanner = annotationScanner;
        this.postProcessors = CollUtil.unmodifiable(CollUtil.sort(annotationPostProcessors, Comparator.comparing((v0) -> {
            return v0.order();
        })));
        this.synthesizedProxyAnnotations = new LinkedHashMap();
        this.synthesizedAnnotationMap = MapUtil.unmodifiable(loadAnnotations());
        annotationPostProcessors.forEach(processor -> {
            this.synthesizedAnnotationMap.values().forEach(synthesized -> {
                processor.process(synthesized, this);
            });
        });
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public T getSource() {
        return this.source;
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public SynthesizedAnnotationSelector getAnnotationSelector() {
        return this.annotationSelector;
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public Collection<SynthesizedAnnotationPostProcessor> getAnnotationPostProcessors() {
        return this.postProcessors;
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public SynthesizedAnnotation getSynthesizedAnnotation(Class<?> annotationType) {
        return this.synthesizedAnnotationMap.get(annotationType);
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public Map<Class<? extends Annotation>, SynthesizedAnnotation> getAllSynthesizedAnnotation() {
        return this.synthesizedAnnotationMap;
    }

    @Override // cn.hutool.core.annotation.AnnotationSynthesizer
    public <A extends Annotation> A synthesize(Class<A> annotationType) {
        return (A) this.synthesizedProxyAnnotations.computeIfAbsent(annotationType, type -> {
            SynthesizedAnnotation synthesizedAnnotation = this.synthesizedAnnotationMap.get(annotationType);
            if (ObjectUtil.isNull(synthesizedAnnotation)) {
                return null;
            }
            return synthesize(annotationType, synthesizedAnnotation);
        });
    }
}
