package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/DefaultAdvisorChainFactory.class */
public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {
    public static final DefaultAdvisorChainFactory INSTANCE = new DefaultAdvisorChainFactory();

    @Override // org.springframework.aop.framework.AdvisorChainFactory
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass) {
        boolean match;
        AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        Advisor[] advisors = config.getAdvisors();
        List<Object> interceptorList = new ArrayList<>(advisors.length);
        Class<?> actualClass = targetClass != null ? targetClass : method.getDeclaringClass();
        Boolean hasIntroductions = null;
        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    if (mm instanceof IntroductionAwareMethodMatcher) {
                        IntroductionAwareMethodMatcher iamm = (IntroductionAwareMethodMatcher) mm;
                        if (hasIntroductions == null) {
                            hasIntroductions = Boolean.valueOf(hasMatchingIntroductions(advisors, actualClass));
                        }
                        match = iamm.matches(method, actualClass, hasIntroductions.booleanValue());
                    } else {
                        match = mm.matches(method, actualClass);
                    }
                    if (match) {
                        MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                        if (mm.isRuntime()) {
                            for (MethodInterceptor interceptor : interceptors) {
                                interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                            }
                        } else {
                            interceptorList.addAll(Arrays.asList(interceptors));
                        }
                    }
                }
            } else if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
                if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
                    Interceptor[] interceptors2 = registry.getInterceptors(advisor);
                    interceptorList.addAll(Arrays.asList(interceptors2));
                }
            } else {
                Interceptor[] interceptors3 = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors3));
            }
        }
        return interceptorList;
    }

    private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
        for (Advisor advisor : advisors) {
            if (advisor instanceof IntroductionAdvisor) {
                IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
                if (ia.getClassFilter().matches(actualClass)) {
                    return true;
                }
            }
        }
        return false;
    }
}
