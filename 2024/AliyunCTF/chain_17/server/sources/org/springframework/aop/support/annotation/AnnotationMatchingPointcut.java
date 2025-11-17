package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/annotation/AnnotationMatchingPointcut.class */
public class AnnotationMatchingPointcut implements Pointcut {
    private final ClassFilter classFilter;
    private final MethodMatcher methodMatcher;

    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
        this(classAnnotationType, false);
    }

    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType, boolean checkInherited) {
        this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
        this.methodMatcher = MethodMatcher.TRUE;
    }

    public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType, @Nullable Class<? extends Annotation> methodAnnotationType) {
        this(classAnnotationType, methodAnnotationType, false);
    }

    public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType, @Nullable Class<? extends Annotation> methodAnnotationType, boolean checkInherited) {
        Assert.isTrue((classAnnotationType == null && methodAnnotationType == null) ? false : true, "Either Class annotation type or Method annotation type needs to be specified (or both)");
        if (classAnnotationType != null) {
            this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
        } else {
            this.classFilter = new AnnotationCandidateClassFilter(methodAnnotationType);
        }
        if (methodAnnotationType != null) {
            this.methodMatcher = new AnnotationMethodMatcher(methodAnnotationType, checkInherited);
        } else {
            this.methodMatcher = MethodMatcher.TRUE;
        }
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AnnotationMatchingPointcut) {
                AnnotationMatchingPointcut otherPointcut = (AnnotationMatchingPointcut) other;
                if (!this.classFilter.equals(otherPointcut.classFilter) || !this.methodMatcher.equals(otherPointcut.methodMatcher)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.classFilter.hashCode() * 37) + this.methodMatcher.hashCode();
    }

    public String toString() {
        return "AnnotationMatchingPointcut: " + this.classFilter + ", " + this.methodMatcher;
    }

    public static AnnotationMatchingPointcut forClassAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut(annotationType);
    }

    public static AnnotationMatchingPointcut forMethodAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut((Class<? extends Annotation>) null, annotationType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/annotation/AnnotationMatchingPointcut$AnnotationCandidateClassFilter.class */
    private static class AnnotationCandidateClassFilter implements ClassFilter {
        private final Class<? extends Annotation> annotationType;

        AnnotationCandidateClassFilter(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            return AnnotationUtils.isCandidateClass(clazz, this.annotationType);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof AnnotationCandidateClassFilter) {
                    AnnotationCandidateClassFilter that = (AnnotationCandidateClassFilter) other;
                    if (this.annotationType.equals(that.annotationType)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.annotationType.hashCode();
        }

        public String toString() {
            return getClass().getName() + ": " + this.annotationType;
        }
    }
}
