package cn.hutool.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AnnotationSynthesizer.class */
public interface AnnotationSynthesizer {
    Object getSource();

    SynthesizedAnnotationSelector getAnnotationSelector();

    Collection<SynthesizedAnnotationPostProcessor> getAnnotationPostProcessors();

    SynthesizedAnnotation getSynthesizedAnnotation(Class<?> cls);

    Map<Class<? extends Annotation>, SynthesizedAnnotation> getAllSynthesizedAnnotation();

    <T extends Annotation> T synthesize(Class<T> cls);
}
