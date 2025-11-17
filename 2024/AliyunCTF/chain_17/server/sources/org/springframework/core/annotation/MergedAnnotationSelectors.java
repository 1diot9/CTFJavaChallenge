package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationSelectors.class */
public abstract class MergedAnnotationSelectors {
    private static final MergedAnnotationSelector<?> NEAREST = new Nearest();
    private static final MergedAnnotationSelector<?> FIRST_DIRECTLY_DECLARED = new FirstDirectlyDeclared();

    private MergedAnnotationSelectors() {
    }

    public static <A extends Annotation> MergedAnnotationSelector<A> nearest() {
        return (MergedAnnotationSelector<A>) NEAREST;
    }

    public static <A extends Annotation> MergedAnnotationSelector<A> firstDirectlyDeclared() {
        return (MergedAnnotationSelector<A>) FIRST_DIRECTLY_DECLARED;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationSelectors$Nearest.class */
    private static class Nearest implements MergedAnnotationSelector<Annotation> {
        private Nearest() {
        }

        @Override // org.springframework.core.annotation.MergedAnnotationSelector
        public boolean isBestCandidate(MergedAnnotation<Annotation> annotation) {
            return annotation.getDistance() == 0;
        }

        @Override // org.springframework.core.annotation.MergedAnnotationSelector
        public MergedAnnotation<Annotation> select(MergedAnnotation<Annotation> existing, MergedAnnotation<Annotation> candidate) {
            if (candidate.getDistance() < existing.getDistance()) {
                return candidate;
            }
            return existing;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/MergedAnnotationSelectors$FirstDirectlyDeclared.class */
    private static class FirstDirectlyDeclared implements MergedAnnotationSelector<Annotation> {
        private FirstDirectlyDeclared() {
        }

        @Override // org.springframework.core.annotation.MergedAnnotationSelector
        public boolean isBestCandidate(MergedAnnotation<Annotation> annotation) {
            return annotation.getDistance() == 0;
        }

        @Override // org.springframework.core.annotation.MergedAnnotationSelector
        public MergedAnnotation<Annotation> select(MergedAnnotation<Annotation> existing, MergedAnnotation<Annotation> candidate) {
            if (existing.getDistance() > 0 && candidate.getDistance() == 0) {
                return candidate;
            }
            return existing;
        }
    }
}
