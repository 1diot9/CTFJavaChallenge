package cn.hutool.core.annotation;

import cn.hutool.core.annotation.scanner.AnnotationScanner;
import cn.hutool.core.annotation.scanner.MetaAnnotationScanner;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/GenericSynthesizedAggregateAnnotation.class */
public class GenericSynthesizedAggregateAnnotation extends AbstractAnnotationSynthesizer<List<Annotation>> implements SynthesizedAggregateAnnotation {
    private final Object root;
    private final int verticalDistance;
    private final int horizontalDistance;
    private final SynthesizedAnnotationAttributeProcessor attributeProcessor;

    public GenericSynthesizedAggregateAnnotation(Annotation... source) {
        this(Arrays.asList(source), new MetaAnnotationScanner());
    }

    public GenericSynthesizedAggregateAnnotation(List<Annotation> source, AnnotationScanner annotationScanner) {
        this(source, SynthesizedAnnotationSelector.NEAREST_AND_OLDEST_PRIORITY, new CacheableSynthesizedAnnotationAttributeProcessor(), Arrays.asList(SynthesizedAnnotationPostProcessor.ALIAS_ANNOTATION_POST_PROCESSOR, SynthesizedAnnotationPostProcessor.MIRROR_LINK_ANNOTATION_POST_PROCESSOR, SynthesizedAnnotationPostProcessor.ALIAS_LINK_ANNOTATION_POST_PROCESSOR), annotationScanner);
    }

    public GenericSynthesizedAggregateAnnotation(List<Annotation> source, SynthesizedAnnotationSelector annotationSelector, SynthesizedAnnotationAttributeProcessor attributeProcessor, Collection<SynthesizedAnnotationPostProcessor> annotationPostProcessors, AnnotationScanner annotationScanner) {
        this(null, 0, 0, source, annotationSelector, attributeProcessor, annotationPostProcessors, annotationScanner);
    }

    GenericSynthesizedAggregateAnnotation(Object root, int verticalDistance, int horizontalDistance, List<Annotation> source, SynthesizedAnnotationSelector annotationSelector, SynthesizedAnnotationAttributeProcessor attributeProcessor, Collection<SynthesizedAnnotationPostProcessor> annotationPostProcessors, AnnotationScanner annotationScanner) {
        super(source, annotationSelector, annotationPostProcessors, annotationScanner);
        Assert.notNull(attributeProcessor, "attributeProcessor must not null", new Object[0]);
        this.root = ObjectUtil.defaultIfNull((GenericSynthesizedAggregateAnnotation) root, this);
        this.verticalDistance = verticalDistance;
        this.horizontalDistance = horizontalDistance;
        this.attributeProcessor = attributeProcessor;
    }

    @Override // cn.hutool.core.annotation.Hierarchical
    public Object getRoot() {
        return this.root;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAggregateAnnotation, cn.hutool.core.annotation.Hierarchical
    public int getVerticalDistance() {
        return this.verticalDistance;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAggregateAnnotation, cn.hutool.core.annotation.Hierarchical
    public int getHorizontalDistance() {
        return this.horizontalDistance;
    }

    @Override // cn.hutool.core.annotation.AbstractAnnotationSynthesizer
    protected Map<Class<? extends Annotation>, SynthesizedAnnotation> loadAnnotations() {
        Map<Class<? extends Annotation>, SynthesizedAnnotation> annotationMap = new LinkedHashMap<>();
        for (int i = 0; i < ((List) this.source).size(); i++) {
            Annotation sourceAnnotation = (Annotation) ((List) this.source).get(i);
            Assert.isFalse(AnnotationUtil.isSynthesizedAnnotation(sourceAnnotation), "source [{}] has been synthesized", new Object[0]);
            annotationMap.put(sourceAnnotation.annotationType(), new MetaAnnotation(sourceAnnotation, sourceAnnotation, 0, i));
            Assert.isTrue(this.annotationScanner.support(sourceAnnotation.annotationType()), "annotation scanner [{}] cannot support scan [{}]", this.annotationScanner, sourceAnnotation.annotationType());
            this.annotationScanner.scan((index, annotation) -> {
                SynthesizedAnnotation oldAnnotation = (SynthesizedAnnotation) annotationMap.get(annotation.annotationType());
                MetaAnnotation metaAnnotation = new MetaAnnotation(sourceAnnotation, annotation, index.intValue() + 1, annotationMap.size());
                if (ObjectUtil.isNull(oldAnnotation)) {
                    annotationMap.put(annotation.annotationType(), metaAnnotation);
                } else {
                    annotationMap.put(annotation.annotationType(), this.annotationSelector.choose(oldAnnotation, metaAnnotation));
                }
            }, sourceAnnotation.annotationType(), null);
        }
        return annotationMap;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAggregateAnnotation
    public SynthesizedAnnotationAttributeProcessor getAnnotationAttributeProcessor() {
        return this.attributeProcessor;
    }

    @Override // cn.hutool.core.annotation.SynthesizedAggregateAnnotation, cn.hutool.core.annotation.AnnotationAttributeValueProvider
    public Object getAttributeValue(String attributeName, Class<?> attributeType) {
        return this.attributeProcessor.getAttributeValue(attributeName, attributeType, this.synthesizedAnnotationMap.values());
    }

    @Override // cn.hutool.core.annotation.SynthesizedAggregateAnnotation
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        Opt ofNullable = Opt.ofNullable(annotationType);
        Map<Class<? extends Annotation>, SynthesizedAnnotation> map = this.synthesizedAnnotationMap;
        map.getClass();
        Opt map2 = ofNullable.map((v1) -> {
            return r1.get(v1);
        }).map((v0) -> {
            return v0.getAnnotation();
        });
        annotationType.getClass();
        return (T) map2.map((v1) -> {
            return r1.cast(v1);
        }).orElse(null);
    }

    @Override // cn.hutool.core.annotation.AggregateAnnotation
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.synthesizedAnnotationMap.containsKey(annotationType);
    }

    @Override // cn.hutool.core.annotation.AggregateAnnotation
    public Annotation[] getAnnotations() {
        return (Annotation[]) this.synthesizedAnnotationMap.values().stream().map((v0) -> {
            return v0.getAnnotation();
        }).toArray(x$0 -> {
            return new Annotation[x$0];
        });
    }

    @Override // cn.hutool.core.annotation.AbstractAnnotationSynthesizer
    public <T extends Annotation> T synthesize(Class<T> cls, SynthesizedAnnotation synthesizedAnnotation) {
        return (T) SynthesizedAnnotationProxy.create(cls, this, synthesizedAnnotation);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/GenericSynthesizedAggregateAnnotation$MetaAnnotation.class */
    public static class MetaAnnotation extends GenericSynthesizedAnnotation<Annotation, Annotation> {
        protected MetaAnnotation(Annotation root, Annotation annotation, int verticalDistance, int horizontalDistance) {
            super(root, annotation, verticalDistance, horizontalDistance);
        }
    }
}
