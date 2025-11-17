package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/annotation/AnnotationFilter.class */
public interface AnnotationFilter {
    public static final AnnotationFilter PLAIN = packages("java.lang", "org.springframework.lang");
    public static final AnnotationFilter JAVA = packages("java", "javax");
    public static final AnnotationFilter ALL = new AnnotationFilter() { // from class: org.springframework.core.annotation.AnnotationFilter.1
        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(Annotation annotation) {
            return true;
        }

        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(Class<?> type) {
            return true;
        }

        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(String typeName) {
            return true;
        }

        public String toString() {
            return "All annotations filtered";
        }
    };

    @Deprecated
    public static final AnnotationFilter NONE = new AnnotationFilter() { // from class: org.springframework.core.annotation.AnnotationFilter.2
        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(Annotation annotation) {
            return false;
        }

        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(Class<?> type) {
            return false;
        }

        @Override // org.springframework.core.annotation.AnnotationFilter
        public boolean matches(String typeName) {
            return false;
        }

        public String toString() {
            return "No annotation filtering";
        }
    };

    boolean matches(String typeName);

    default boolean matches(Annotation annotation) {
        return matches(annotation.annotationType());
    }

    default boolean matches(Class<?> type) {
        return matches(type.getName());
    }

    static AnnotationFilter packages(String... packages) {
        return new PackagesAnnotationFilter(packages);
    }
}
