package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import org.springframework.aop.ClassFilter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/annotation/AnnotationClassFilter.class */
public class AnnotationClassFilter implements ClassFilter {
    private final Class<? extends Annotation> annotationType;
    private final boolean checkInherited;

    public AnnotationClassFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationClassFilter(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return this.checkInherited ? AnnotatedElementUtils.hasAnnotation(clazz, this.annotationType) : clazz.isAnnotationPresent(this.annotationType);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof AnnotationClassFilter) {
                AnnotationClassFilter otherCf = (AnnotationClassFilter) other;
                if (!this.annotationType.equals(otherCf.annotationType) || this.checkInherited != otherCf.checkInherited) {
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
