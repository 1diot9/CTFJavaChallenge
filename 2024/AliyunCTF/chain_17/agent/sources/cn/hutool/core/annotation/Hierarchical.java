package cn.hutool.core.annotation;

import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical.class */
public interface Hierarchical extends Comparable<Hierarchical> {
    public static final Comparator<Hierarchical> DEFAULT_HIERARCHICAL_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getVerticalDistance();
    }).thenComparing((v0) -> {
        return v0.getHorizontalDistance();
    });

    Object getRoot();

    int getVerticalDistance();

    int getHorizontalDistance();

    @Override // java.lang.Comparable
    default int compareTo(Hierarchical o) {
        return DEFAULT_HIERARCHICAL_COMPARATOR.compare(this, o);
    }

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical$Selector.class */
    public interface Selector {
        public static final Selector NEAREST_AND_OLDEST_PRIORITY = new NearestAndOldestPrioritySelector();
        public static final Selector NEAREST_AND_NEWEST_PRIORITY = new NearestAndNewestPrioritySelector();
        public static final Selector FARTHEST_AND_OLDEST_PRIORITY = new FarthestAndOldestPrioritySelector();
        public static final Selector FARTHEST_AND_NEWEST_PRIORITY = new FarthestAndNewestPrioritySelector();

        <T extends Hierarchical> T choose(T t, T t2);

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical$Selector$NearestAndOldestPrioritySelector.class */
        public static class NearestAndOldestPrioritySelector implements Selector {
            @Override // cn.hutool.core.annotation.Hierarchical.Selector
            public <T extends Hierarchical> T choose(T oldAnnotation, T newAnnotation) {
                return newAnnotation.getVerticalDistance() < oldAnnotation.getVerticalDistance() ? newAnnotation : oldAnnotation;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical$Selector$NearestAndNewestPrioritySelector.class */
        public static class NearestAndNewestPrioritySelector implements Selector {
            @Override // cn.hutool.core.annotation.Hierarchical.Selector
            public <T extends Hierarchical> T choose(T oldAnnotation, T newAnnotation) {
                return newAnnotation.getVerticalDistance() <= oldAnnotation.getVerticalDistance() ? newAnnotation : oldAnnotation;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical$Selector$FarthestAndOldestPrioritySelector.class */
        public static class FarthestAndOldestPrioritySelector implements Selector {
            @Override // cn.hutool.core.annotation.Hierarchical.Selector
            public <T extends Hierarchical> T choose(T oldAnnotation, T newAnnotation) {
                return newAnnotation.getVerticalDistance() > oldAnnotation.getVerticalDistance() ? newAnnotation : oldAnnotation;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/Hierarchical$Selector$FarthestAndNewestPrioritySelector.class */
        public static class FarthestAndNewestPrioritySelector implements Selector {
            @Override // cn.hutool.core.annotation.Hierarchical.Selector
            public <T extends Hierarchical> T choose(T oldAnnotation, T newAnnotation) {
                return newAnnotation.getVerticalDistance() >= oldAnnotation.getVerticalDistance() ? newAnnotation : oldAnnotation;
            }
        }
    }
}
