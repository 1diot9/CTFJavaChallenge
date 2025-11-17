package org.springframework.aop.support;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInfo;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/DefaultIntroductionAdvisor.class */
public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable {
    private final Advice advice;
    private final Set<Class<?>> interfaces;
    private int order;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public DefaultIntroductionAdvisor(org.aopalliance.aop.Advice r5) {
        /*
            r4 = this;
            r0 = r4
            r1 = r5
            r2 = r5
            boolean r2 = r2 instanceof org.springframework.aop.IntroductionInfo
            if (r2 == 0) goto L12
            r2 = r5
            org.springframework.aop.IntroductionInfo r2 = (org.springframework.aop.IntroductionInfo) r2
            r6 = r2
            r2 = r6
            goto L13
        L12:
            r2 = 0
        L13:
            r0.<init>(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.aop.support.DefaultIntroductionAdvisor.<init>(org.aopalliance.aop.Advice):void");
    }

    public DefaultIntroductionAdvisor(Advice advice, @Nullable IntroductionInfo introductionInfo) {
        this.interfaces = new LinkedHashSet();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionInfo defines no interfaces to introduce: " + introductionInfo);
            }
            for (Class<?> ifc : introducedInterfaces) {
                addInterface(ifc);
            }
        }
    }

    public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class<?> ifc) {
        this.interfaces = new LinkedHashSet();
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        addInterface(ifc);
    }

    public void addInterface(Class<?> ifc) {
        Assert.notNull(ifc, "Interface must not be null");
        if (!ifc.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + ifc.getName() + "] must be an interface");
        }
        this.interfaces.add(ifc);
    }

    @Override // org.springframework.aop.IntroductionInfo
    public Class<?>[] getInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public void validateInterfaces() throws IllegalArgumentException {
        for (Class<?> ifc : this.interfaces) {
            Advice advice = this.advice;
            if (advice instanceof DynamicIntroductionAdvice) {
                DynamicIntroductionAdvice dynamicIntroductionAdvice = (DynamicIntroductionAdvice) advice;
                if (!dynamicIntroductionAdvice.implementsInterface(ifc)) {
                    throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] does not implement interface [" + ifc.getName() + "] specified for introduction");
                }
            }
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return true;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof DefaultIntroductionAdvisor) {
                DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor) other;
                if (!this.advice.equals(otherAdvisor.advice) || !this.interfaces.equals(otherAdvisor.interfaces)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.advice.hashCode() * 13) + this.interfaces.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": advice [" + this.advice + "]; interfaces " + ClassUtils.classNamesToString(this.interfaces);
    }
}
