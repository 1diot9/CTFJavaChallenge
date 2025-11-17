package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.support.ClassFilters;
import org.springframework.aop.support.DelegatePerTargetObjectIntroductionInterceptor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/DeclareParentsAdvisor.class */
public class DeclareParentsAdvisor implements IntroductionAdvisor {
    private final Advice advice;
    private final Class<?> introducedInterface;
    private final ClassFilter typePatternClassFilter;

    public DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, Class<?> defaultImpl) {
        this(interfaceType, typePattern, (IntroductionInterceptor) new DelegatePerTargetObjectIntroductionInterceptor(defaultImpl, interfaceType));
    }

    public DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, Object delegateRef) {
        this(interfaceType, typePattern, (IntroductionInterceptor) new DelegatingIntroductionInterceptor(delegateRef));
    }

    private DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, IntroductionInterceptor interceptor) {
        this.advice = interceptor;
        this.introducedInterface = interfaceType;
        ClassFilter typePatternFilter = new TypePatternClassFilter(typePattern);
        ClassFilter exclusion = clazz -> {
            return !this.introducedInterface.isAssignableFrom(clazz);
        };
        this.typePatternClassFilter = ClassFilters.intersection(typePatternFilter, exclusion);
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public ClassFilter getClassFilter() {
        return this.typePatternClassFilter;
    }

    @Override // org.springframework.aop.IntroductionAdvisor
    public void validateInterfaces() throws IllegalArgumentException {
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        return this.advice;
    }

    @Override // org.springframework.aop.IntroductionInfo
    public Class<?>[] getInterfaces() {
        return new Class[]{this.introducedInterface};
    }
}
