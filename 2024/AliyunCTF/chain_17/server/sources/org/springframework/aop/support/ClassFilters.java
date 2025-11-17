package org.springframework.aop.support;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ClassFilters.class */
public abstract class ClassFilters {
    public static ClassFilter union(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter union(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }

    public static ClassFilter intersection(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter intersection(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }

    public static ClassFilter negate(ClassFilter classFilter) {
        Assert.notNull(classFilter, "ClassFilter must not be null");
        return new NegateClassFilter(classFilter);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ClassFilters$UnionClassFilter.class */
    private static class UnionClassFilter implements ClassFilter, Serializable {
        private final ClassFilter[] filters;

        UnionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (filter.matches(clazz)) {
                    return true;
                }
            }
            return false;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof UnionClassFilter) {
                    UnionClassFilter that = (UnionClassFilter) other;
                    if (ObjectUtils.nullSafeEquals(this.filters, that.filters)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(this.filters);
        }

        public String toString() {
            return getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ClassFilters$IntersectionClassFilter.class */
    private static class IntersectionClassFilter implements ClassFilter, Serializable {
        private final ClassFilter[] filters;

        IntersectionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            for (ClassFilter filter : this.filters) {
                if (!filter.matches(clazz)) {
                    return false;
                }
            }
            return true;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof IntersectionClassFilter) {
                    IntersectionClassFilter that = (IntersectionClassFilter) other;
                    if (ObjectUtils.nullSafeEquals(this.filters, that.filters)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(this.filters);
        }

        public String toString() {
            return getClass().getName() + ": " + Arrays.toString(this.filters);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/ClassFilters$NegateClassFilter.class */
    private static class NegateClassFilter implements ClassFilter, Serializable {
        private final ClassFilter original;

        NegateClassFilter(ClassFilter original) {
            this.original = original;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            return !this.original.matches(clazz);
        }

        public boolean equals(Object other) {
            if (this != other) {
                if (other instanceof NegateClassFilter) {
                    NegateClassFilter that = (NegateClassFilter) other;
                    if (this.original.equals(that.original)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(getClass(), this.original);
        }

        public String toString() {
            return "Negate " + this.original;
        }
    }
}
