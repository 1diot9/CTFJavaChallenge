package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers.class */
public abstract class MethodMatchers {
    public static MethodMatcher union(MethodMatcher mm1, MethodMatcher mm2) {
        return ((mm1 instanceof IntroductionAwareMethodMatcher) || (mm2 instanceof IntroductionAwareMethodMatcher)) ? new UnionIntroductionAwareMethodMatcher(mm1, mm2) : new UnionMethodMatcher(mm1, mm2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MethodMatcher union(MethodMatcher mm1, ClassFilter cf1, MethodMatcher mm2, ClassFilter cf2) {
        if ((mm1 instanceof IntroductionAwareMethodMatcher) || (mm2 instanceof IntroductionAwareMethodMatcher)) {
            return new ClassFilterAwareUnionIntroductionAwareMethodMatcher(mm1, cf1, mm2, cf2);
        }
        return new ClassFilterAwareUnionMethodMatcher(mm1, cf1, mm2, cf2);
    }

    public static MethodMatcher intersection(MethodMatcher mm1, MethodMatcher mm2) {
        return ((mm1 instanceof IntroductionAwareMethodMatcher) || (mm2 instanceof IntroductionAwareMethodMatcher)) ? new IntersectionIntroductionAwareMethodMatcher(mm1, mm2) : new IntersectionMethodMatcher(mm1, mm2);
    }

    public static MethodMatcher negate(MethodMatcher methodMatcher) {
        Assert.notNull(methodMatcher, "MethodMatcher must not be null");
        return new NegateMethodMatcher(methodMatcher);
    }

    public static boolean matches(MethodMatcher mm, Method method, Class<?> targetClass, boolean hasIntroductions) {
        Assert.notNull(mm, "MethodMatcher must not be null");
        if (mm instanceof IntroductionAwareMethodMatcher) {
            IntroductionAwareMethodMatcher iamm = (IntroductionAwareMethodMatcher) mm;
            return iamm.matches(method, targetClass, hasIntroductions);
        }
        return mm.matches(method, targetClass);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$UnionMethodMatcher.class */
    private static class UnionMethodMatcher implements MethodMatcher, Serializable {
        protected final MethodMatcher mm1;
        protected final MethodMatcher mm2;

        public UnionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            Assert.notNull(mm1, "First MethodMatcher must not be null");
            Assert.notNull(mm2, "Second MethodMatcher must not be null");
            this.mm1 = mm1;
            this.mm2 = mm2;
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return (matchesClass1(targetClass) && this.mm1.matches(method, targetClass)) || (matchesClass2(targetClass) && this.mm2.matches(method, targetClass));
        }

        protected boolean matchesClass1(Class<?> targetClass) {
            return true;
        }

        protected boolean matchesClass2(Class<?> targetClass) {
            return true;
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean isRuntime() {
            return this.mm1.isRuntime() || this.mm2.isRuntime();
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return this.mm1.matches(method, targetClass, args) || this.mm2.matches(method, targetClass, args);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof UnionMethodMatcher) {
                    UnionMethodMatcher that = (UnionMethodMatcher) other;
                    if (!this.mm1.equals(that.mm1) || !this.mm2.equals(that.mm2)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (37 * this.mm1.hashCode()) + this.mm2.hashCode();
        }

        public String toString() {
            return getClass().getName() + ": " + this.mm1 + ", " + this.mm2;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$UnionIntroductionAwareMethodMatcher.class */
    private static class UnionIntroductionAwareMethodMatcher extends UnionMethodMatcher implements IntroductionAwareMethodMatcher {
        public UnionIntroductionAwareMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            super(mm1, mm2);
        }

        @Override // org.springframework.aop.IntroductionAwareMethodMatcher
        public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
            return (matchesClass1(targetClass) && MethodMatchers.matches(this.mm1, method, targetClass, hasIntroductions)) || (matchesClass2(targetClass) && MethodMatchers.matches(this.mm2, method, targetClass, hasIntroductions));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$ClassFilterAwareUnionMethodMatcher.class */
    private static class ClassFilterAwareUnionMethodMatcher extends UnionMethodMatcher {
        private final ClassFilter cf1;
        private final ClassFilter cf2;

        public ClassFilterAwareUnionMethodMatcher(MethodMatcher mm1, ClassFilter cf1, MethodMatcher mm2, ClassFilter cf2) {
            super(mm1, mm2);
            this.cf1 = cf1;
            this.cf2 = cf2;
        }

        @Override // org.springframework.aop.support.MethodMatchers.UnionMethodMatcher
        protected boolean matchesClass1(Class<?> targetClass) {
            return this.cf1.matches(targetClass);
        }

        @Override // org.springframework.aop.support.MethodMatchers.UnionMethodMatcher
        protected boolean matchesClass2(Class<?> targetClass) {
            return this.cf2.matches(targetClass);
        }

        @Override // org.springframework.aop.support.MethodMatchers.UnionMethodMatcher
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!super.equals(other)) {
                return false;
            }
            ClassFilter otherCf1 = ClassFilter.TRUE;
            ClassFilter otherCf2 = ClassFilter.TRUE;
            if (other instanceof ClassFilterAwareUnionMethodMatcher) {
                ClassFilterAwareUnionMethodMatcher cfa = (ClassFilterAwareUnionMethodMatcher) other;
                otherCf1 = cfa.cf1;
                otherCf2 = cfa.cf2;
            }
            return this.cf1.equals(otherCf1) && this.cf2.equals(otherCf2);
        }

        @Override // org.springframework.aop.support.MethodMatchers.UnionMethodMatcher
        public int hashCode() {
            return super.hashCode();
        }

        @Override // org.springframework.aop.support.MethodMatchers.UnionMethodMatcher
        public String toString() {
            return getClass().getName() + ": " + this.cf1 + ", " + this.mm1 + ", " + this.cf2 + ", " + this.mm2;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$ClassFilterAwareUnionIntroductionAwareMethodMatcher.class */
    private static class ClassFilterAwareUnionIntroductionAwareMethodMatcher extends ClassFilterAwareUnionMethodMatcher implements IntroductionAwareMethodMatcher {
        public ClassFilterAwareUnionIntroductionAwareMethodMatcher(MethodMatcher mm1, ClassFilter cf1, MethodMatcher mm2, ClassFilter cf2) {
            super(mm1, cf1, mm2, cf2);
        }

        @Override // org.springframework.aop.IntroductionAwareMethodMatcher
        public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
            return (matchesClass1(targetClass) && MethodMatchers.matches(this.mm1, method, targetClass, hasIntroductions)) || (matchesClass2(targetClass) && MethodMatchers.matches(this.mm2, method, targetClass, hasIntroductions));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$IntersectionMethodMatcher.class */
    private static class IntersectionMethodMatcher implements MethodMatcher, Serializable {
        protected final MethodMatcher mm1;
        protected final MethodMatcher mm2;

        public IntersectionMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            Assert.notNull(mm1, "First MethodMatcher must not be null");
            Assert.notNull(mm2, "Second MethodMatcher must not be null");
            this.mm1 = mm1;
            this.mm2 = mm2;
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return this.mm1.matches(method, targetClass) && this.mm2.matches(method, targetClass);
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean isRuntime() {
            return this.mm1.isRuntime() || this.mm2.isRuntime();
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            boolean aMatches = this.mm1.isRuntime() ? this.mm1.matches(method, targetClass, args) : this.mm1.matches(method, targetClass);
            boolean bMatches = this.mm2.isRuntime() ? this.mm2.matches(method, targetClass, args) : this.mm2.matches(method, targetClass);
            return aMatches && bMatches;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof IntersectionMethodMatcher) {
                    IntersectionMethodMatcher that = (IntersectionMethodMatcher) other;
                    if (!this.mm1.equals(that.mm1) || !this.mm2.equals(that.mm2)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (37 * this.mm1.hashCode()) + this.mm2.hashCode();
        }

        public String toString() {
            return getClass().getName() + ": " + this.mm1 + ", " + this.mm2;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$IntersectionIntroductionAwareMethodMatcher.class */
    private static class IntersectionIntroductionAwareMethodMatcher extends IntersectionMethodMatcher implements IntroductionAwareMethodMatcher {
        public IntersectionIntroductionAwareMethodMatcher(MethodMatcher mm1, MethodMatcher mm2) {
            super(mm1, mm2);
        }

        @Override // org.springframework.aop.IntroductionAwareMethodMatcher
        public boolean matches(Method method, Class<?> targetClass, boolean hasIntroductions) {
            return MethodMatchers.matches(this.mm1, method, targetClass, hasIntroductions) && MethodMatchers.matches(this.mm2, method, targetClass, hasIntroductions);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/MethodMatchers$NegateMethodMatcher.class */
    private static class NegateMethodMatcher implements MethodMatcher, Serializable {
        private final MethodMatcher original;

        NegateMethodMatcher(MethodMatcher original) {
            this.original = original;
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass) {
            return !this.original.matches(method, targetClass);
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean isRuntime() {
            return this.original.isRuntime();
        }

        @Override // org.springframework.aop.MethodMatcher
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return !this.original.matches(method, targetClass, args);
        }

        public boolean equals(Object other) {
            if (this != other) {
                if (other instanceof NegateMethodMatcher) {
                    NegateMethodMatcher that = (NegateMethodMatcher) other;
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
