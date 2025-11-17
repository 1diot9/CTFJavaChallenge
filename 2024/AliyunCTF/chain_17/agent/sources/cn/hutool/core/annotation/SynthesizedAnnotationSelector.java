package cn.hutool.core.annotation;

import cn.hutool.core.annotation.Hierarchical;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationSelector.class */
public interface SynthesizedAnnotationSelector {
    public static final SynthesizedAnnotationSelector NEAREST_AND_OLDEST_PRIORITY = new NearestAndOldestPrioritySelector();
    public static final SynthesizedAnnotationSelector NEAREST_AND_NEWEST_PRIORITY = new NearestAndNewestPrioritySelector();
    public static final SynthesizedAnnotationSelector FARTHEST_AND_OLDEST_PRIORITY = new FarthestAndOldestPrioritySelector();
    public static final SynthesizedAnnotationSelector FARTHEST_AND_NEWEST_PRIORITY = new FarthestAndNewestPrioritySelector();

    <T extends SynthesizedAnnotation> T choose(T t, T t2);

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationSelector$NearestAndOldestPrioritySelector.class */
    public static class NearestAndOldestPrioritySelector implements SynthesizedAnnotationSelector {
        @Override // cn.hutool.core.annotation.SynthesizedAnnotationSelector
        public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
            return (T) Hierarchical.Selector.NEAREST_AND_OLDEST_PRIORITY.choose(oldAnnotation, newAnnotation);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationSelector$NearestAndNewestPrioritySelector.class */
    public static class NearestAndNewestPrioritySelector implements SynthesizedAnnotationSelector {
        @Override // cn.hutool.core.annotation.SynthesizedAnnotationSelector
        public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
            return (T) Hierarchical.Selector.NEAREST_AND_NEWEST_PRIORITY.choose(oldAnnotation, newAnnotation);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationSelector$FarthestAndOldestPrioritySelector.class */
    public static class FarthestAndOldestPrioritySelector implements SynthesizedAnnotationSelector {
        @Override // cn.hutool.core.annotation.SynthesizedAnnotationSelector
        public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
            return (T) Hierarchical.Selector.FARTHEST_AND_OLDEST_PRIORITY.choose(oldAnnotation, newAnnotation);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/SynthesizedAnnotationSelector$FarthestAndNewestPrioritySelector.class */
    public static class FarthestAndNewestPrioritySelector implements SynthesizedAnnotationSelector {
        @Override // cn.hutool.core.annotation.SynthesizedAnnotationSelector
        public <T extends SynthesizedAnnotation> T choose(T oldAnnotation, T newAnnotation) {
            return (T) Hierarchical.Selector.FARTHEST_AND_NEWEST_PRIORITY.choose(oldAnnotation, newAnnotation);
        }
    }
}
