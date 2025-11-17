package cn.hutool.core.annotation;

import cn.hutool.core.comparator.CompareUtil;
import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationPostProcessor.class */
public interface SynthesizedAnnotationPostProcessor extends Comparable<SynthesizedAnnotationPostProcessor> {
    public static final AliasAnnotationPostProcessor ALIAS_ANNOTATION_POST_PROCESSOR = new AliasAnnotationPostProcessor();
    public static final MirrorLinkAnnotationPostProcessor MIRROR_LINK_ANNOTATION_POST_PROCESSOR = new MirrorLinkAnnotationPostProcessor();
    public static final AliasLinkAnnotationPostProcessor ALIAS_LINK_ANNOTATION_POST_PROCESSOR = new AliasLinkAnnotationPostProcessor();

    void process(SynthesizedAnnotation synthesizedAnnotation, AnnotationSynthesizer annotationSynthesizer);

    default int order() {
        return Integer.MAX_VALUE;
    }

    @Override // java.lang.Comparable
    default int compareTo(SynthesizedAnnotationPostProcessor o) {
        return CompareUtil.compare(this, o, (Comparator<SynthesizedAnnotationPostProcessor>) Comparator.comparing((v0) -> {
            return v0.order();
        }));
    }
}
